/*******************************************************************************
 * Copyright (c) Microsoft Open Technologies, Inc.
 * All Rights Reserved
 * See License.txt in the project root for license information.
 ******************************************************************************/
package com.microsoft.services.sharepoint.http;

import com.google.common.util.concurrent.SettableFuture;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Runnable that executes a network operation
 */
class NetworkRunnable implements Runnable {

    HttpURLConnection mConnection = null;
    InputStream mResponseStream = null;
    Request mRequest;
    SettableFuture<Response> mFuture;
    final Object mCloseLock = new Object();


    /**
     * Initializes the network runnable
     *
     * @param request The request to execute
     * @param future  Future for the operation
     */
    public NetworkRunnable(Request request, SettableFuture<Response> future) {
        mRequest = request;
        mFuture = future;
    }


    @Override
    public void run() {
        try {
            int responseCode = -1;
            synchronized (mCloseLock) {
                if (!mFuture.isCancelled()) {
                    if (mRequest == null) {
                        mFuture.setException(new IllegalArgumentException(
                                "request"));
                        return;
                    }

                    mConnection = createHttpURLConnection(mRequest);

                    responseCode = mConnection.getResponseCode();

                    if (responseCode >= 400) {
                        this.mResponseStream = this.mConnection.getErrorStream();
                        if (this.mResponseStream == null) {
                            this.mResponseStream = this.mConnection.getInputStream();
                        }
                        if (this.mResponseStream == null) {
                            this.mResponseStream = new ByteArrayInputStream(("HTTP error" + responseCode).getBytes(StandardCharsets.UTF_8));
                        }

                    } else {
                        this.mResponseStream = this.mConnection.getInputStream();
                    }
                }
            }

            if (!mFuture.isCancelled()) {
                if (mResponseStream != null) {
                    mFuture.set(new StreamResponse(mResponseStream, responseCode, mConnection.getHeaderFields()));
                } else {
                    mFuture.set(null);
                }
            }
        } catch (Throwable e) {
            if (!mFuture.isCancelled()) {
                if (mConnection != null) {
                    mConnection.disconnect();
                }

                mFuture.setException(e);
            }
        } finally {
            closeStreamAndConnection();
        }

    }


    /**
     * Closes the stream and connection, if possible
     */
    void closeStreamAndConnection() {
        synchronized (mCloseLock) {
            if (mResponseStream != null) {
                try {
                    mResponseStream.close();
                } catch (IOException e) {
                }
            }

            if (mConnection != null) {
                mConnection.disconnect();
            }
        }
    }


    /**
     * Creates an HttpURLConnection
     *
     * @param request The request info
     * @return An HttpURLConnection to execute the request
     * @throws java.io.IOException
     */
    static HttpURLConnection createHttpURLConnection(Request request)
            throws IOException {
        URL url = new URL(request.getUrl());

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(request.getVerb());

        Map<String, String> headers = request.getHeaders();

        for (String key : headers.keySet()) {
            connection.setRequestProperty(key, headers.get(key));
        }

        if ("POST".equals(connection.getRequestMethod())) {
            connection.setDoOutput(true);

            try (OutputStream stream = connection.getOutputStream()) {
                if (request.getContentStream() != null) {
                    IOUtils.copy(request.getContentStream(), stream);
                } else if (request.getContent() != null) {
                    byte[] requestContent = request.getContent();
                    stream.write(requestContent, 0, requestContent.length);
                }
            }
        }
        return connection;
    }
}

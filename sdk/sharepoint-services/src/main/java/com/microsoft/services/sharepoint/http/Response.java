/*******************************************************************************
 * Copyright (c) Microsoft Open Technologies, Inc.
 * All Rights Reserved
 * See License.txt in the project root for license information.
 ******************************************************************************/
package com.microsoft.services.sharepoint.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Represents an HTTP Response
 */
public interface Response {

	/**
	 * Returns the response headers
	 */
    Map<String, List<String>> getHeaders();

	/**
	 * Return the header value
	 * @param headerName Header to retrieve
	 * @return The header Values
	 */
    List<String> getHeader(String headerName);

	/**
	 * Reads the response stream to the end and returns its value as a String
	 * @return The response content as a String
	 * @throws java.io.IOException
	 */
    String readToEnd() throws IOException;

	/**
	 * Reads one line from the response stream
	 * @return A line from the response stream
	 * @throws java.io.IOException
	 */
    String readLine() throws IOException;

	/**
	 * Reads the response stream to the end and returns the response bytes
	 * @return The response content as a byte array
	 * @throws java.io.IOException
	 */
    byte[] readAllBytes() throws IOException;

    /**
     * Returns the reponse stream
     * @return
     */
    InputStream getInputStream();

	/**
	 * Returns the response HTTP Status code
	 */
    int getStatus();
}

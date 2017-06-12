/*******************************************************************************
 * Copyright (c) Microsoft Open Technologies, Inc.
 * All Rights Reserved
 * See License.txt in the project root for license information.
 ******************************************************************************/
package com.microsoft.services.sharepoint;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * The Class SPContentType.
 */
public class SPContentType extends OfficeEntity {


    /**
     * List from json.
     *
     * @param json the json
     * @return the list
     * @throws JSONException the JSON exception
     */
    public static List<SPContentType> listFromJson(JSONObject json) throws JSONException {
        return listFromJson(json, SPContentType.class);
    }

    /**
     * Instantiates a new SP list.
     * <p>
     * the JSON exception
     */
    public SPContentType() {
        super();
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() {
        return getData("StringId").toString();
    }

    public String getName() {
        return getData("Name").toString();
    }

    public String getDescription() {
        return getData("Description").toString();
    }

    public boolean isHidden() {
        return Boolean.TRUE.equals(getData("Hidden").toString());
    }

    public boolean isSealed() {
        return Boolean.TRUE.equals(getData("Sealed").toString());
    }

    public String getGroup() {
        return getData("Group").toString();
    }

    public boolean isReadOnly() {
        return Boolean.TRUE.equals(getData("ReadOnly").toString());
    }
}

package com.sourcepoint.test_project;

import org.json.JSONException;
import org.json.JSONObject;

public class PropertyConfig {

    public int accountId;
    public int propertyId;
    public String propertyName;
    public String pmId;


    public PropertyConfig(JSONObject config) throws JSONException {
        accountId = config.getInt("accountId");
        propertyId = config.getInt("propertyId");
        propertyName = config.getString("propertyName");
        pmId = config.getString("pmId");
    }
}

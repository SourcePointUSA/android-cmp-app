package com.sourcepoint.gdpr_cmplibrary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class CustomJsonParser {

    static int getInt(String s, JSONObject j) throws ConsentLibException {
        try {
            return j.getInt(s);
        } catch (JSONException e) {
            throw new ConsentLibException(e, s + " miising from mesasgeJSON");
        }
    }

    static String getString(String s, JSONObject j) throws ConsentLibException {
        try {
            return j.getString(s);
        } catch (JSONException e) {
            throw new ConsentLibException(e, s + " miising from mesasgeJSON");
        }
    }

    static JSONObject getJson(String s, JSONObject j) throws ConsentLibException {
        try {
            return j.getJSONObject(s);
        } catch (JSONException e) {
            throw new ConsentLibException(e, s + " miising from mesasgeJSON");
        }
    }

    static JSONArray getJArray(String s, JSONObject j) throws ConsentLibException {
        try {
            return j.getJSONArray(s);
        } catch (JSONException e) {
            throw new ConsentLibException(e, s + " miising from mesasgeJSON");
        }
    }

    static JSONObject getJson(int i, JSONArray jArray) throws ConsentLibException {
        try {
            return jArray.getJSONObject(i);
        } catch (JSONException e) {
            throw new ConsentLibException(e, "Error tryng to get action obj from mesasgeJSON");
        }
    }

    static String getString(int i, JSONArray jArray) throws ConsentLibException {
        try {
            return jArray.getString(i);
        } catch (JSONException e) {
            throw new ConsentLibException(e, "Error tryng to get action obj from mesasgeJSON");
        }
    }

    static HashMap<String, String> getHashMap(JSONObject jCustomFields) throws ConsentLibException {
        HashMap<String, String> hMap = new HashMap<>();
        JSONArray names = jCustomFields.names();
        if (names != null){
            for(int i = 0; i < names.length(); i++) {
                String name = getString(i, names);
                hMap.put(name, getString(name, jCustomFields));
            }
        }
        return hMap;
    }
}

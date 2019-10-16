package com.sourcepoint.cmplibrary;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Simple encapsulating class for consents.
 */
abstract public class Consent {
    public final String id;
    public final String name;
    private final String type;

    Consent(String id, String name, String type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }

    @Override
    public String toString() {
        return name + "("+id+")";
    }

    @Override
    public boolean equals(Object otherConsent) {
        if (getClass() != otherConsent.getClass()) { return false; }
        return super.equals(((Consent) otherConsent).id);
    }

    JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", id).put("name", name).put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}

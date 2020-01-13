package com.sourcepoint.cmplibrary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserConsent {

    public enum ConsentStatus {
        acceptedAll,
        acceptedSome,
        acceptedNone
    }

    public ConsentStatus status;
    public ArrayList<String> acceptedVendors = new ArrayList();
    public ArrayList<String> acceptedCategories = new ArrayList();
    public JSONObject jsonConsents = new JSONObject();

    public UserConsent(JSONArray acceptedVendors, JSONArray acceptedCategories) throws JSONException {
        this.status = ConsentStatus.acceptedSome;
        this.acceptedVendors = json2StrArr(acceptedVendors);
        this.acceptedCategories = json2StrArr(acceptedCategories);
        if(this.acceptedVendors.isEmpty() && this.acceptedCategories.isEmpty())
            this.status = ConsentStatus.acceptedNone;
        setJsonConsents();
    }

    public UserConsent(JSONObject jConsent) throws JSONException, ConsentLibException {
        status = statusFromStr(jConsent.getString("status"));
        this.acceptedVendors = json2StrArr(jConsent.getJSONArray("acceptedVendors"));
        this.acceptedCategories = json2StrArr(jConsent.getJSONArray("acceptedCategories"));
        setJsonConsents();
    }

    public UserConsent(ConsentStatus status) throws JSONException {
        this.status = status;
        setJsonConsents();
    }

    private ArrayList<String> json2StrArr(JSONArray jArray) throws JSONException {
        ArrayList<String> listData = new ArrayList();
        if (jArray != null) {
            for (int i=0;i<jArray.length();i++){
                listData.add(jArray.getString(i));
            }
        }
        return listData;
    }

    private void setJsonConsents() throws JSONException {
        jsonConsents.put("status", status.name());
        jsonConsents.put("acceptedVendors", new JSONArray(acceptedVendors));
        jsonConsents.put("acceptedCategories", new JSONArray(acceptedCategories));
    }

    private ConsentStatus statusFromStr(String statusName) throws ConsentLibException {
        if(statusName.equals(ConsentStatus.acceptedAll.name())) return ConsentStatus.acceptedAll;
        if(statusName.equals(ConsentStatus.acceptedNone.name())) return ConsentStatus.acceptedNone;
        if(statusName.equals(ConsentStatus.acceptedSome.name())) return ConsentStatus.acceptedSome;
        throw new ConsentLibException("ConsentStatus string not valid.");
    }
}

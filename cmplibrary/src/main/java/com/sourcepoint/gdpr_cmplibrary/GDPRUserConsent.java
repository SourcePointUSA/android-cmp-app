package com.sourcepoint.gdpr_cmplibrary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GDPRUserConsent {

    public ArrayList<String> acceptedVendors;
    public ArrayList<String> acceptedCategories;
    public JSONObject jsonConsents = new JSONObject();
    public String consentString;

    public GDPRUserConsent() throws JSONException {
        this.acceptedVendors = new ArrayList<>();
        this.acceptedCategories = new ArrayList<>();
        this.consentString = "";
        jsonConsents.put("acceptedVendors",new JSONArray());
        jsonConsents.put("acceptedCategories",new JSONArray());
    }

    public GDPRUserConsent(JSONObject jConsent) throws JSONException {
        this.acceptedVendors = json2StrArr(jConsent.getJSONArray("acceptedVendors"));
        this.acceptedCategories = json2StrArr(jConsent.getJSONArray("acceptedCategories"));
        if(jConsent.has("euconsent") && !jConsent.isNull("euconsent")){
            consentString = jConsent.getString("euconsent");
        }
        jsonConsents = jConsent;
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

    public JSONObject getJsonConsents() {
        return jsonConsents;
    }
}

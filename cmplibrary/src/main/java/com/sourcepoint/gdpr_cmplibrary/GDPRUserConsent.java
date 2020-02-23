package com.sourcepoint.gdpr_cmplibrary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.sourcepoint.gdpr_cmplibrary.CustomJsonParser.getHashMap;

public class GDPRUserConsent {

    public ArrayList<String> acceptedVendors;
    public ArrayList<String> acceptedCategories;
    public JSONObject jsonConsents = new JSONObject();
    public String consentString;
    public HashMap<String, String> TCData;

    public GDPRUserConsent(JSONObject jConsent) throws JSONException, ConsentLibException {
        this.acceptedVendors = json2StrArr(jConsent.getJSONArray("acceptedVendors"));
        this.acceptedCategories = json2StrArr(jConsent.getJSONArray("acceptedCategories"));
        if(jConsent.has("euconsent") && !jConsent.isNull("euconsent")){
            consentString = jConsent.getString("euconsent");
        }
        if(jConsent.has("TCData") && !jConsent.isNull("TCData")){
            TCData = getHashMap(jConsent.getJSONObject("TCData"));
        }
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
        jsonConsents.put("acceptedVendors", new JSONArray(acceptedVendors));
        jsonConsents.put("acceptedCategories", new JSONArray(acceptedCategories));
    }

}

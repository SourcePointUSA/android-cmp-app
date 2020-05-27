package com.sourcepoint.gdpr_cmplibrary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.sourcepoint.gdpr_cmplibrary.CustomJsonParser.getBoolean;
import static com.sourcepoint.gdpr_cmplibrary.CustomJsonParser.getHashMap;
import static com.sourcepoint.gdpr_cmplibrary.CustomJsonParser.getJson;
import static com.sourcepoint.gdpr_cmplibrary.CustomJsonParser.getString;

public class GDPRUserConsent {

    public String uuid;
    public ArrayList<String> acceptedVendors;
    public ArrayList<String> acceptedCategories;
    public ArrayList<String> specialFeatures;
    public ArrayList<String> legIntCategories;
    public JSONObject jsonConsents = new JSONObject();
    public String consentString;
    public HashMap TCData;
    public VendorGrants vendorGrants;

    public GDPRUserConsent(){
        acceptedVendors = new ArrayList<>();
        acceptedCategories = new ArrayList<>();
        consentString = "";
        uuid = "";
        TCData = new HashMap();
    }

    public GDPRUserConsent(JSONObject jConsent, String uuid) throws JSONException, ConsentLibException {
        init(jConsent, uuid);
    }

    public GDPRUserConsent(JSONObject jConsent, String uuid, HashMap tcData) throws JSONException, ConsentLibException {
        init(jConsent, uuid);
        TCData = tcData;
    }

    private void init(JSONObject jConsent, String uuid) throws JSONException, ConsentLibException {
        this.uuid = uuid;
        this.acceptedVendors = json2StrArr(jConsent.getJSONArray("acceptedVendors"));
        this.acceptedCategories = json2StrArr(jConsent.getJSONArray("acceptedCategories"));
        this.specialFeatures = json2StrArr(jConsent.getJSONArray("specialFeatures"));
        this.legIntCategories = json2StrArr(jConsent.getJSONArray("legIntCategories"));
        if(jConsent.has("euconsent") && !jConsent.isNull("euconsent")){
            consentString = jConsent.getString("euconsent");
        }
        if(jConsent.has("TCData") && !jConsent.isNull("TCData")){
            TCData = getHashMap(jConsent.getJSONObject("TCData"));
        }
        if(jConsent.has("vendorGrants") && !jConsent.isNull("vendorGrants")){
            vendorGrants = new VendorGrants(jConsent.getJSONObject("vendorGrants"));
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

    public class VendorGrants extends HashMap<String, VendorGrants.VendorGrant> {
        VendorGrants(JSONObject jVendorGrants) throws ConsentLibException {
            super();
            JSONArray names = jVendorGrants.names();
            if (names != null){
                for(int i = 0; i < names.length(); i++) {
                    String name = getString(i, names);
                    this.put(name, new VendorGrant(getJson(name, jVendorGrants)));
                }
            }
        }

        class VendorGrant {
            public boolean vendorGrant;
            public HashMap<String, Boolean> purposeGrants;
            VendorGrant(JSONObject jVendorGrant) throws ConsentLibException {
                vendorGrant = getBoolean("vendorGrant", jVendorGrant);
                purposeGrants = getHashMap(getJson("purposeGrants", jVendorGrant));
            }
            public String toString(){
                return "{" + "vendorGrant: " + vendorGrant + ", " + "purposeGrants: " + purposeGrants + "}";
            }
        }
    }

}

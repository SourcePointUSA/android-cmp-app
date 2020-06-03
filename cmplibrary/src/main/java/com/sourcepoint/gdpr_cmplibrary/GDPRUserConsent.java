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
    public String consentString;
    public HashMap TCData;
    public VendorGrants vendorGrants;

    public GDPRUserConsent() {
        acceptedVendors = new ArrayList<>();
        acceptedCategories = new ArrayList<>();
        specialFeatures = new ArrayList<>();
        legIntCategories = new ArrayList<>();
        consentString = StoreClient.DEFAULT_EMPTY_CONSENT_STRING;
        uuid = StoreClient.DEFAULT_EMPTY_UUID;
        TCData = new HashMap();
        vendorGrants = new VendorGrants();
    }

    public GDPRUserConsent(JSONObject jConsent) throws ConsentLibException {
        init(jConsent);
    }

    public GDPRUserConsent(JSONObject jConsent, String uuid) throws ConsentLibException {
        try {
            jConsent.put("uuid", uuid);
        } catch (JSONException e) {
            throw new ConsentLibException(e, "Error parsing jConsent");
        }
        init(jConsent);
    }

    private void init(JSONObject jConsent) throws ConsentLibException {
        try {
            uuid = jConsent.getString("uuid");
            acceptedVendors = json2StrArr(jConsent.getJSONArray("acceptedVendors"));
            acceptedCategories = json2StrArr(jConsent.getJSONArray("acceptedCategories"));
            specialFeatures = json2StrArr(jConsent.getJSONArray("specialFeatures"));
            legIntCategories = json2StrArr(jConsent.getJSONArray("legIntCategories"));
            consentString = jConsent.getString("euconsent");
            TCData = getHashMap(jConsent.getJSONObject("TCData"));
            vendorGrants = new VendorGrants(jConsent.getJSONObject("grants"));
        } catch (Exception e){
            //This general catch block is meant to deal with null pointer exceptions as well
            throw new ConsentLibException(e, "Error parsing JSONObject to ConsentUser obj");
        }
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

    public JSONObject toJsonObject() throws JSONException, ConsentLibException {
        JSONObject jsonConsents = new JSONObject();
        jsonConsents.put("acceptedVendors", new JSONArray(acceptedVendors));
        jsonConsents.put("acceptedCategories", new JSONArray(acceptedCategories));
        jsonConsents.put("specialFeatures", new JSONArray(specialFeatures));
        jsonConsents.put("legIntCategories", new JSONArray(legIntCategories));
        jsonConsents.put("uuid", uuid);
        jsonConsents.put("euconsent", consentString);
        jsonConsents.put("TCData", getJson(TCData));
        jsonConsents.put("grants", vendorGrants.toJsonObject());
        return jsonConsents;
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
        VendorGrants(){ super(); }

        public JSONObject toJsonObject() throws JSONException, ConsentLibException {
            JSONObject json = new JSONObject();
            for(String key : keySet()){
                json.put(key, this.get(key).toJsonObject());
            }
            return json;
        }

        class VendorGrant {
            public boolean vendorGrant;
            public HashMap<String, Boolean> purposeGrants;
            VendorGrant(JSONObject jVendorGrant) throws ConsentLibException {
                vendorGrant = getBoolean("vendorGrant", jVendorGrant);
                purposeGrants = getHashMap(getJson("purposeGrants", jVendorGrant));
            }
            public String toString(){
                return "{" + "vendorGrant=" + vendorGrant + ", " + "purposeGrants=" + purposeGrants + "}";
            }

            public JSONObject toJsonObject() throws ConsentLibException, JSONException {
                JSONObject json = new JSONObject();
                json.put("vendorGrant", vendorGrant);
                json.put("purposeGrants" , getJson(purposeGrants));
                return json;
            }
        }
    }

}

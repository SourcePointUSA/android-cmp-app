package com.sourcepoint.gdpr_cmplibrary;

import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StoreClient {

    public static final String CONSENT_UUID_KEY = "sp.gdpr.consentUUID";

    public static final String META_DATA_KEY = "sp.gdpr.metaData";

    public static final String EU_CONSENT_KEY = "sp.gdpr.euconsent";

    public static final String USER_CONSENT_KEY = "sp.gdpr.userConsent";

    public static final String AUTH_ID_KEY = "sp.gdpr.authId";
    public static final String DEFAULT_EMPTY_UUID = "";

    private SharedPreferences.Editor editor;

    private SharedPreferences pref;

    public static final String DEFAULT_EMPTY_CONSENT_STRING = "";

    public static final String DEFAULT_META_DATA = "{}";

    public static final String DEFAULT_AUTH_ID = null;

    static final String IABTCF_KEY_PREFIX = "IABTCF_";

    StoreClient(SharedPreferences pref){
        this.editor = pref.edit();
        this.pref = pref;
    }

    public void setTCData(HashMap<String, Object> tcData){
        clearConsentData();
        for(String key : tcData.keySet()){
            if(tcData.get(key).getClass().equals(Integer.class)) editor.putInt(key, (Integer) tcData.get(key));
            if(tcData.get(key).getClass().equals(String.class)) editor.putString(key, (String) tcData.get(key));
        }
        editor.commit();
    }

    public void setConsentUuid(String consentUuid){
        editor.putString(CONSENT_UUID_KEY, consentUuid);
        editor.commit();
    }

    public void setMetaData(String  metaData){
        editor.putString(META_DATA_KEY, metaData);
        editor.commit();
    }

    public void setAuthId(String authId){
        editor.putString(AUTH_ID_KEY, authId);
        editor.commit();
    }

    public void setConsentString(String euconsent){
        editor.putString(EU_CONSENT_KEY, euconsent);
        editor.commit();
    }

    public void setUserConsents(GDPRUserConsent userConsent) throws JSONException, ConsentLibException {
        editor.putString(USER_CONSENT_KEY, userConsent.toJsonObject().toString());
        editor.commit();
    }

    public String getMetaData() {
        return pref.getString(META_DATA_KEY, DEFAULT_META_DATA);
    }

    public String getConsentUUID() {
        return pref.getString(CONSENT_UUID_KEY, DEFAULT_EMPTY_UUID);
    }

    public GDPRUserConsent getUserConsent() throws ConsentLibException {
        try {
            String uStr = pref.getString(USER_CONSENT_KEY, null);
            return uStr != null ? new GDPRUserConsent(new JSONObject(uStr)) : new GDPRUserConsent();
        } catch (Exception e) {
            throw new ConsentLibException(e, "Error trying to recover UserConsents for sharedPrefs");
        }
    }

    public String getConsentString() {
        return pref.getString(EU_CONSENT_KEY, DEFAULT_EMPTY_CONSENT_STRING);
    }

    public String getAuthId() {
        return pref.getString(AUTH_ID_KEY, DEFAULT_AUTH_ID);
    }

    public void clearAllData(){
        clearInternalData();
        clearConsentData();
    }

    public void clearInternalData(){
        editor.remove(CONSENT_UUID_KEY);
        editor.remove(META_DATA_KEY);
        editor.remove(EU_CONSENT_KEY);
        editor.remove(AUTH_ID_KEY);
        editor.commit();
    }

    public HashMap getTCData(){
        HashMap tcData = new HashMap();
        Map<String , ?> map = pref.getAll();
        for(String key : map.keySet()) if (key.startsWith(IABTCF_KEY_PREFIX))
            tcData.put(key, map.get(key));
        return tcData;
    };

    public void clearConsentData(){
        for(String key : pref.getAll().keySet()) if (key.startsWith(IABTCF_KEY_PREFIX))
            editor.remove(key);
        editor.commit();
    }
}

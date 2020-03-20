package com.sourcepoint.gdpr_cmplibrary;

import android.content.SharedPreferences;
import android.os.Build;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class StoreClient {

    /**
     * They key used to store the IAB Consent string for the user in the shared preferences
     */
    public static final String IAB_CONSENT_CONSENT_STRING = "IABConsent_ConsentString";


    public static final String TC_KEYS_KEY = "sp.gdpr.TCKeys";

    public static final String CONSENT_UUID_KEY = "sp.gdpr.consentUUID";

    public static final String META_DATA_KEY = "sp.gdpr.metaData";

    public static final String EU_CONSENT__KEY = "sp.gdpr.euconsent";

    public static final String AUTH_ID_KEY = "sp.gdpr.authId";

    private SharedPreferences.Editor editor;

    private SharedPreferences pref;

    public static final String DEFAULT_EMPTY_CONSENT_STRING = "";

    public static final String DEFAULT_META_DATA = "{}";

    public static final String DEFAULT_AUTH_ID = null;

    public static final String TC_KEYS_DELIMITER = ";";

    StoreClient(SharedPreferences pref){
        this.editor = pref.edit();
        this.pref = pref;
    }

    public void setTCData(HashMap<String, String> data){
        for(String s : data.keySet()){
            editor.putString(s, data.get(s));
        }
        editor.putString(TC_KEYS_KEY, dataSetToStr(data.keySet()));
        editor.commit();
    }

    private String dataSetToStr(Set<String> dataSet){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return String.join(TC_KEYS_DELIMITER, dataSet);
        }
        //TODO: remove this code when update min API to 26
        else {
            String result = "";
            for (String s : dataSet){
                result += s + TC_KEYS_DELIMITER;
            }
            if(result != "") result = result.substring(0, result.length() - 1);
            return result;
        }
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
        editor.putString(IAB_CONSENT_CONSENT_STRING, euconsent);
        editor.putString(EU_CONSENT__KEY, euconsent);
        editor.commit();
    }

    public String getMetaData() {
        return pref.getString(META_DATA_KEY, DEFAULT_META_DATA);
    }

    public String getConsentUUID() {
        return pref.getString(CONSENT_UUID_KEY, UUID.randomUUID().toString());
    }

    public String getConsentString() {
        return pref.getString(EU_CONSENT__KEY, DEFAULT_EMPTY_CONSENT_STRING);
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
        editor.remove(EU_CONSENT__KEY);
        editor.remove(AUTH_ID_KEY);
        editor.commit();
    }


    public void clearConsentData(){
        clearTCData();
        editor.remove(IAB_CONSENT_CONSENT_STRING);
    }

    private void clearTCData() {
        for(String s : dataSetFromStore()){
            editor.remove(s);
        }
        editor.remove(TC_KEYS_KEY);
    }

    private String[] dataSetFromStore(){
        return pref.getString(TC_KEYS_KEY, "").split(TC_KEYS_DELIMITER);

    }
}

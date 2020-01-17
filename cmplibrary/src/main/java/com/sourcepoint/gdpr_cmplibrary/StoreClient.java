package com.sourcepoint.gdpr_cmplibrary;

import android.content.SharedPreferences;

import java.util.UUID;

public class StoreClient {
    /**
     * If the user has consent data stored, reading for this key in the shared preferences will return true
     */
    private static final String IAB_CONSENT_CMP_PRESENT = "IABConsent_CMPPresent";

    /**
     * If the user is subject to GDPR, reading for this key in the shared preferences will return "1" otherwise "0"
     */
    private static final String IAB_CONSENT_SUBJECT_TO_GDPR = "IABConsent_SubjectToGDPR";

    /**
     * They key used to store the IAB Consent string for the user in the shared preferences
     */
    private static final String IAB_CONSENT_CONSENT_STRING = "IABConsent_ConsentString";

    /**
     * They key used to read and write the parsed IAB Purposes consented by the user in the shared preferences
     */
    private static final String IAB_CONSENT_PARSED_PURPOSE_CONSENTS = "IABConsent_ParsedPurposeConsents";

    /**
     * They key used to read and write the parsed IAB Vendor consented by the user in the shared preferences
     */
    private static final String IAB_CONSENT_PARSED_VENDOR_CONSENTS = "IABConsent_ParsedVendorConsents";

    private static final String CONSENT_UUID_KEY = "consentUUID";

    private static final String META_DATA_KEY = "metaData";

    private static final String EU_CONSENT__KEY = "euconsent";

    private SharedPreferences.Editor editor;

    private SharedPreferences pref;

    public static final String DEFAULT_EMPTY_CONSENT_STRING = null;

    public static final String DEFAULT_META_DATA = "{}";


    StoreClient(SharedPreferences pref){
        this.editor = pref.edit();
        this.pref = pref;
    }

    public void setConsentSubjectToGDPr(Boolean consentSubjectToGDPR){
        editor.putString(IAB_CONSENT_SUBJECT_TO_GDPR, consentSubjectToGDPR != null ? (consentSubjectToGDPR ? "1" : "0") : null);
        editor.commit();
    }

    public void setIabConsentCmpPresent(Boolean iabConsentCmpPresent){
        editor.putBoolean(IAB_CONSENT_CMP_PRESENT, iabConsentCmpPresent);
        editor.commit();
    }

    public void setIabConsentConsentString(String consentConsentString){
        editor.putString(IAB_CONSENT_CONSENT_STRING, consentConsentString);
        editor.putString(EU_CONSENT__KEY, consentConsentString);
        editor.commit();
    }

    public void setIabConsentParsedPurposeConsents(String consentParsedPurposeConsents){
        editor.putString(IAB_CONSENT_PARSED_PURPOSE_CONSENTS, consentParsedPurposeConsents);
        editor.commit();
    }

    public void setIabConsentParsedVendorConsents(String consentParsedVendorConsents){
        editor.putString(IAB_CONSENT_PARSED_VENDOR_CONSENTS, consentParsedVendorConsents);
        editor.commit();
    }

    public void setConsentUuid(String consentUuid){
        editor.putString(CONSENT_UUID_KEY, consentUuid);
        editor.putString(IAB_CONSENT_CONSENT_STRING, consentUuid);
        editor.commit();
    }

    public void setMetaData(String  metaData){
        editor.putString(META_DATA_KEY, metaData);
        editor.commit();
    }

    public void commit(){
        editor.commit();
    }

    public void clear(){
        editor.clear();
    }

    public String getMetaData() {
        return pref.getString(META_DATA_KEY, DEFAULT_META_DATA);
    }

    public String getConsentUUID() {
        return pref.getString(CONSENT_UUID_KEY, UUID.randomUUID().toString());
    }

    public String getConsentString() {
        return pref.getString(IAB_CONSENT_CONSENT_STRING, DEFAULT_EMPTY_CONSENT_STRING);
    }

    public void deleteIABConsentData(){
        editor.remove(IAB_CONSENT_CONSENT_STRING);
        editor.remove(IAB_CONSENT_PARSED_VENDOR_CONSENTS);
        editor.remove(IAB_CONSENT_PARSED_PURPOSE_CONSENTS);
        editor.remove(IAB_CONSENT_CMP_PRESENT);
        editor.remove(IAB_CONSENT_SUBJECT_TO_GDPR);
        editor.commit();
    }
}

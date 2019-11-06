package com.sourcepoint.cmplibrary;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class ConsentLibTest {

    private static final String IAB_CONSENT_CMP_PRESENT = "IABConsent_CMPPresent";
    private static final String IAB_CONSENT_SUBJECT_TO_GDPR = "IABConsent_SubjectToGDPR";
    private static final String IAB_CONSENT_CONSENT_STRING = "IABConsent_ConsentString";
    private static final String IAB_CONSENT_PARSED_PURPOSE_CONSENTS = "IABConsent_ParsedPurposeConsents";
    private static final String IAB_CONSENT_PARSED_VENDOR_CONSENTS = "IABConsent_ParsedVendorConsents";
    private static final String EU_CONSENT_KEY = "euconsent";
    private static final String CONSENT_UUID_KEY = "consentUUID";
    private SharedPreferences sharedPrefs;
    private Context context;
    private ConsentLib consentLib;

    @Before
    public void setUP(){
        consentLib = Mockito.mock(ConsentLib.class);
        context = Mockito.mock(Context.class);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application.getApplicationContext());

    }

    /*Test method for removing all consent data*/
    @Test
    public void clearAllConsentData() throws NoSuchFieldException {
        consentLib.clearAllConsentData();

        assertEquals(sharedPrefs.contains(IAB_CONSENT_CMP_PRESENT),false);
        assertEquals(sharedPrefs.contains(IAB_CONSENT_SUBJECT_TO_GDPR),false);
        assertEquals(sharedPrefs.contains(IAB_CONSENT_CONSENT_STRING),false);
        assertEquals(sharedPrefs.contains(IAB_CONSENT_PARSED_VENDOR_CONSENTS),false);
        assertEquals(sharedPrefs.contains(IAB_CONSENT_PARSED_PURPOSE_CONSENTS),false);
        assertEquals(sharedPrefs.contains(EU_CONSENT_KEY),false);
        assertEquals(sharedPrefs.contains(CONSENT_UUID_KEY),false);
    }


    @Test
    public void testIAB_CONSENT_CMP_PRESENT(){
        consentLib.clearAllConsentData();
        assertEquals(sharedPrefs.contains(IAB_CONSENT_CMP_PRESENT), false);
    }

    @Test
    public void testIAB_CONSENT_SUBJECT_TO_GDPR(){
        consentLib.clearAllConsentData();
        assertEquals(sharedPrefs.contains(IAB_CONSENT_SUBJECT_TO_GDPR) , false);
    }

    @Test
    public void testIAB_CONSENT_CONSENT_STRING(){
        consentLib.clearAllConsentData();
        assertEquals(sharedPrefs.contains(IAB_CONSENT_CONSENT_STRING) , false);
    }

    @Test
    public void testIAB_CONSENT_PARSED_PURPOSE_CONSENTS(){
        consentLib.clearAllConsentData();
        assertEquals(sharedPrefs.contains(IAB_CONSENT_PARSED_PURPOSE_CONSENTS) , false);
    }

    @Test
    public void testIAB_CONSENT_PARSED_VENDOR_CONSENTS(){
        consentLib.clearAllConsentData();
        assertEquals(sharedPrefs.contains(IAB_CONSENT_PARSED_VENDOR_CONSENTS) , false);
    }

    @Test
    public void testEU_CONSENT_KEY(){
        consentLib.clearAllConsentData();
        assertEquals(sharedPrefs.contains(EU_CONSENT_KEY) , false);
    }

    @Test
    public void testCONSENT_UUID_KEY(){
        consentLib.clearAllConsentData();
        assertEquals(sharedPrefs.contains(CONSENT_UUID_KEY) , false);
    }

}
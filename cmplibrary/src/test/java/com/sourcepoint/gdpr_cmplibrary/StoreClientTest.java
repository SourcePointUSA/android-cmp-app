package com.sourcepoint.gdpr_cmplibrary;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class StoreClientTest {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private StoreClient storeClient;
    private static final String CONSENT_UUID_KEY = "sp.gdpr.consentUUID";
    private static final String META_DATA_KEY = "sp.gdpr.metaData";
    private static final String AUTH_ID_KEY = "sp.gdpr.authId";
    private static final String EU_CONSENT__KEY = "sp.gdpr.euconsent";
    private static final String IAB_CONSENT_CONSENT_STRING = "IABConsent_ConsentString";

    @Before
    public void setUp() throws Exception {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application.getApplicationContext());
        storeClient = new StoreClient(sharedPreferences);
        editor = sharedPreferences.edit();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void setConsentUuid() {
        String consentUUID = "consentUUID";
        storeClient.setConsentUuid(consentUUID);
        assertTrue(sharedPreferences.contains(CONSENT_UUID_KEY));
        editor.clear().commit();
        assertFalse(sharedPreferences.contains(CONSENT_UUID_KEY));
    }

    @Test
    public void setMetaData() {
        String metaData = "metaData";
        storeClient.setMetaData(metaData);
        assertTrue(sharedPreferences.contains(META_DATA_KEY));
        editor.clear().commit();
        assertFalse(sharedPreferences.contains(META_DATA_KEY));
    }

    @Test
    public void setAuthId() {
        String authId = "authId";
        storeClient.setAuthId(authId);
        assertTrue(sharedPreferences.contains(AUTH_ID_KEY));
        editor.clear().commit();
        assertFalse(sharedPreferences.contains(AUTH_ID_KEY));
    }

    @Test
    public void getMetaData() {
        String metaData = "metaData";
        storeClient.setMetaData(metaData);
        assertEquals(metaData,storeClient.getMetaData());
        editor.clear().commit();
        assertFalse(sharedPreferences.contains(META_DATA_KEY));
    }

    @Test
    public void getConsentUUID() {
        String consentUUID = "consentUUID";
        storeClient.setConsentUuid(consentUUID);
        assertEquals(consentUUID,storeClient.getConsentUUID());
        editor.clear().commit();
        assertFalse(sharedPreferences.contains(CONSENT_UUID_KEY));
    }

    @Test
    public void getAuthId() {
        String authId = "authId";
        storeClient.setAuthId(authId);
        assertEquals(authId,storeClient.getAuthId());
        editor.clear().commit();
        assertFalse(sharedPreferences.contains(AUTH_ID_KEY));
    }

    @Test
    public void clearAllData() {
        storeClient.clearAllData();
        assertFalse(sharedPreferences.contains(CONSENT_UUID_KEY));
        assertFalse(sharedPreferences.contains(META_DATA_KEY));
        assertFalse(sharedPreferences.contains(EU_CONSENT__KEY));
        assertFalse(sharedPreferences.contains(AUTH_ID_KEY));
        assertFalse(sharedPreferences.contains(IAB_CONSENT_CONSENT_STRING));
    }

    @Test
    public void clearInternalData() {
        storeClient.clearInternalData();
        assertFalse(sharedPreferences.contains(CONSENT_UUID_KEY));
        assertFalse(sharedPreferences.contains(META_DATA_KEY));
        assertFalse(sharedPreferences.contains(EU_CONSENT__KEY));
        assertFalse(sharedPreferences.contains(AUTH_ID_KEY));
    }
}
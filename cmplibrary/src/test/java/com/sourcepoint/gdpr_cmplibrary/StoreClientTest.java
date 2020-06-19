package com.sourcepoint.gdpr_cmplibrary;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class StoreClientTest {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private StoreClient storeClient;
    private static final String IAB_CONSENT_CONSENT_STRING = "IABConsent_ConsentString";

    @Before
    public void setUp() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext());
        storeClient = new StoreClient(sharedPreferences);
        editor = sharedPreferences.edit();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void setConsentUuid() {
        String consentUUID = "consentUUID";
        storeClient.setConsentUuid(consentUUID);
        assertTrue(sharedPreferences.contains(StoreClient.CONSENT_UUID_KEY));
        editor.clear().commit();
        assertFalse(sharedPreferences.contains(StoreClient.CONSENT_UUID_KEY));
    }

    @Test
    public void setTCDataClearsIABTCFDataBeforeStoringNewOne() {
        editor.putString("IABTCF_bar", "bar");
        editor.clear().commit();

        HashMap<String, Object> tcData = new HashMap<>();
        tcData.put("IABTCF_foo", "foo");
        storeClient.setTCData(tcData);

        assertFalse("expected "+sharedPreferences.contains("IABTCF_bar")+" to be false", sharedPreferences.contains("IABTCF_bar"));
        assertEquals("foo", sharedPreferences.getString("IABTCF_foo", null));
    }

    @Test
    public void setMetaData() {
        String metaData = "metaData";
        storeClient.setMetaData(metaData);
        assertTrue(sharedPreferences.contains(StoreClient.META_DATA_KEY));
        editor.clear().commit();
        assertFalse(sharedPreferences.contains(StoreClient.META_DATA_KEY));
    }

    @Test
    public void setAuthId() {
        String authId = "authId";
        storeClient.setAuthId(authId);
        assertTrue(sharedPreferences.contains(StoreClient.AUTH_ID_KEY));
        editor.clear().commit();
        assertFalse(sharedPreferences.contains(StoreClient.AUTH_ID_KEY));
    }

    @Test
    public void getMetaData() {
        String metaData = "metaData";
        storeClient.setMetaData(metaData);
        assertEquals(metaData,storeClient.getMetaData());
        editor.clear().commit();
        assertFalse(sharedPreferences.contains(StoreClient.META_DATA_KEY));
    }

    @Test
    public void getConsentUUID() {
        String consentUUID = "consentUUID";
        storeClient.setConsentUuid(consentUUID);
        assertEquals(consentUUID,storeClient.getConsentUUID());
        editor.clear().commit();
        assertFalse(sharedPreferences.contains(StoreClient.CONSENT_UUID_KEY));
    }

    @Test
    public void getAuthId() {
        String authId = "authId";
        storeClient.setAuthId(authId);
        assertEquals(authId,storeClient.getAuthId());
        editor.clear().commit();
        assertFalse(sharedPreferences.contains(StoreClient.AUTH_ID_KEY));
    }

    @Test
    public void clearAllData() {
        storeClient.clearAllData();
        assertFalse(sharedPreferences.contains(StoreClient.CONSENT_UUID_KEY));
        assertFalse(sharedPreferences.contains(StoreClient.META_DATA_KEY));
        assertFalse(sharedPreferences.contains(StoreClient.EU_CONSENT_KEY));
        assertFalse(sharedPreferences.contains(StoreClient.AUTH_ID_KEY));
        assertFalse(sharedPreferences.contains(IAB_CONSENT_CONSENT_STRING));
    }

    @Test
    public void clearInternalData() {
        storeClient.clearInternalData();
        assertFalse(sharedPreferences.contains(StoreClient.CONSENT_UUID_KEY));
        assertFalse(sharedPreferences.contains(StoreClient.META_DATA_KEY));
        assertFalse(sharedPreferences.contains(StoreClient.EU_CONSENT_KEY));
        assertFalse(sharedPreferences.contains(StoreClient.AUTH_ID_KEY));
    }


    @Test
    public void setTCDataAndcheckPrimitives() {
        editor.clear().commit();

        HashMap<String, Object> tcData = new HashMap<>();
        tcData.put("IABTCF_String", "foo");
        tcData.put("IABTCF_Integer" , 100);
        storeClient.setTCData(tcData);

        Map map = sharedPreferences.getAll();

        assertEquals("foo", map.get("IABTCF_String"));
        assertEquals(100 , map.get("IABTCF_Integer"));
    }

    @Test
    public void getTCDataAndCheckPrimitives() {
        editor.clear().commit();

        HashMap<String, Object> tcData = new HashMap<>();
        tcData.put("IABTCF_String", "foo");
        tcData.put("IABTCF_Integer" , 100);
        storeClient.setTCData(tcData);

        HashMap clientTCData = storeClient.getTCData();

        assertEquals("foo", clientTCData.get("IABTCF_String"));
        assertEquals(100 , clientTCData.get("IABTCF_Integer"));

        assertTrue(clientTCData.get("IABTCF_String") instanceof String);
        assertTrue(clientTCData.get("IABTCF_Integer") instanceof Integer);
    }

}
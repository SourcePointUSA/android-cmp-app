package com.sourcepoint.gdpr_cmplibrary;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.test.core.app.ApplicationProvider;

import org.json.JSONException;
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
        editor.clear().commit();

        HashMap<String, Object> tcData = new HashMap<>();
        tcData.put("IABTCF_foo", "foo");
        tcData.put("IABTCF_number", 4);
        storeClient.setTCData(tcData);

        assertEquals("foo", sharedPreferences.getString("IABTCF_foo", null));
        assertEquals(4, sharedPreferences.getInt("IABTCF_number", 0));
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

        String keyForStringVlaue =  "IABTCF_String";
        String stringValue = "foo";
        String keyForIntValue = "IABTCF_Integer";
        int intValue = 100;

        HashMap<String, Object> tcData = new HashMap<>();
        tcData.put(keyForStringVlaue, stringValue);
        tcData.put(keyForIntValue , intValue);
        storeClient.setTCData(tcData);

        Map map = sharedPreferences.getAll();

        assertEquals(stringValue, map.get(keyForStringVlaue));
        assertEquals(intValue , map.get(keyForIntValue));

        assertEquals(map.get(keyForStringVlaue).getClass(), String.class);
        assertEquals(map.get(keyForIntValue).getClass(), Integer.class);
    }

    @Test
    public void getTCDataAndCheckPrimitives() {
        editor.clear().commit();

        String keyForStringVlaue =  "IABTCF_String";
        String stringValue = "foo";
        String keyForIntValue = "IABTCF_Integer";
        int intValue = 100;

        HashMap<String, Object> tcData = new HashMap<>();
        tcData.put(keyForStringVlaue, stringValue);
        tcData.put(keyForIntValue , intValue);
        storeClient.setTCData(tcData);

        HashMap clientTCData = storeClient.getTCData();

        assertEquals(stringValue, clientTCData.get(keyForStringVlaue));
        assertEquals(intValue , clientTCData.get(keyForIntValue));

        assertEquals(clientTCData.get(keyForStringVlaue).getClass(), String.class);
        assertEquals(clientTCData.get(keyForIntValue).getClass(), Integer.class);
    }

    @Test
    public void setUserConsentAndCheckPrimitives() throws JSONException,ConsentLibException {
        editor.clear().commit();
        GDPRUserConsent userConsent = new GDPRUserConsent();
        userConsent.consentString = "consentString";
        userConsent.uuid = "uuid";

        String keyForStringVlaue =  "IABTCF_String";
        String stringValue = "foo";
        String keyForIntValue = "IABTCF_Integer";
        int intValue = 100;

        HashMap<String, Object> tcData = new HashMap<>();
        tcData.put(keyForStringVlaue, stringValue);
        tcData.put(keyForIntValue , intValue);
        userConsent.TCData = tcData;

        storeClient.setUserConsents(userConsent);
        GDPRUserConsent clientUserConsent = storeClient.getUserConsent();

        assertNotNull(clientUserConsent);
        assertEquals(clientUserConsent.getClass(), GDPRUserConsent.class);

        assertEquals(userConsent.uuid , clientUserConsent.uuid);
        assertEquals(clientUserConsent.uuid.getClass() , String.class);

        assertEquals(userConsent.consentString , clientUserConsent.consentString);
        assertEquals(clientUserConsent.consentString.getClass() , String.class);

        HashMap clientTCData = clientUserConsent.TCData;

        assertEquals(stringValue, clientTCData.get(keyForStringVlaue));
        assertEquals(intValue , clientTCData.get(keyForIntValue));

        assertEquals(clientTCData.get(keyForStringVlaue).getClass(), String.class);
        assertEquals(clientTCData.get(keyForIntValue).getClass(), Integer.class);
    }
}
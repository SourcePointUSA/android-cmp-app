package com.sourcepoint.gdpr_cmplibrary;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.test.core.app.ApplicationProvider;

import org.json.JSONException;
import org.json.JSONObject;
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
        editor = sharedPreferences.edit().clear();
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
        assertFalse(sharedPreferences.contains(StoreClient.META_DATA_KEY));
        storeClient.setMetaData("metaData");
        assertEquals("metaData" ,sharedPreferences.getString(StoreClient.META_DATA_KEY, null));
    }

    @Test
    public void setAuthId() {
        assertFalse(sharedPreferences.contains(StoreClient.AUTH_ID_KEY));
        storeClient.setAuthId("authId");
        assertEquals("authId" ,sharedPreferences.getString(StoreClient.AUTH_ID_KEY, null));
    }

    @Test
    public void setConsentUuid() {
        assertFalse(sharedPreferences.contains(StoreClient.CONSENT_UUID_KEY));
        storeClient.setConsentUuid("foo_uuid");
        assertEquals("foo_uuid" ,sharedPreferences.getString(StoreClient.CONSENT_UUID_KEY, null));
    }

    @Test
    public void setCmpSdkID(){
        assertFalse(sharedPreferences.contains(StoreClient.CMP_SDK_ID_KEY));
        storeClient.setCmpSdkID();
        assertEquals(StoreClient.CMP_SDK_ID, sharedPreferences.getInt(StoreClient.CMP_SDK_ID_KEY, -1));
    }

    @Test
    public void setCmpSdkVersion(){
        assertFalse(sharedPreferences.contains(StoreClient.CMP_SDK_VERSION_KEY));
        storeClient.setCmpSdkVersion();
        assertEquals(StoreClient.CMP_SDK_VERSION, sharedPreferences.getInt(StoreClient.CMP_SDK_VERSION_KEY, -1));
    }

    @Test
    public void setConsentString() {
        assertFalse(sharedPreferences.contains(StoreClient.EU_CONSENT_KEY));
        storeClient.setConsentString("foo_str");
        assertEquals("foo_str" ,sharedPreferences.getString(StoreClient.EU_CONSENT_KEY, null));
    }

    @Test
    public void getMetaData() {
        assertFalse(sharedPreferences.contains(StoreClient.META_DATA_KEY));
        assertEquals(StoreClient.DEFAULT_META_DATA, storeClient.getMetaData());
        editor.putString(StoreClient.META_DATA_KEY, "metaData").commit();
        assertEquals("metaData",storeClient.getMetaData());
    }

    @Test
    public void getConsentUUID() {
        assertFalse(sharedPreferences.contains(StoreClient.CONSENT_UUID_KEY));
        assertEquals(StoreClient.DEFAULT_EMPTY_UUID, storeClient.getConsentUUID());
        editor.putString(StoreClient.CONSENT_UUID_KEY, "consentUUID").commit();
        assertEquals("consentUUID",storeClient.getConsentUUID());
    }

    @Test
    public void getAuthId() {
        assertFalse(sharedPreferences.contains(StoreClient.AUTH_ID_KEY));
        assertEquals(StoreClient.DEFAULT_AUTH_ID, storeClient.getAuthId());
        editor.putString(StoreClient.AUTH_ID_KEY, "authId").commit();
        assertEquals("authId",storeClient.getAuthId());
    }

    @Test
    public void getConsentString() {
        assertFalse(sharedPreferences.contains(StoreClient.EU_CONSENT_KEY));
        assertEquals(StoreClient.DEFAULT_EMPTY_CONSENT_STRING, storeClient.getConsentString());
        editor.putString(StoreClient.EU_CONSENT_KEY, "authId").commit();
        assertEquals("authId",storeClient.getConsentString());
    }

    @Test
    public void clearAllData() {
        editor
            .putString(StoreClient.CONSENT_UUID_KEY,"foo_uuid")
            .putString(StoreClient.META_DATA_KEY,"foo_metadata")
            .putString(StoreClient.EU_CONSENT_KEY,"foo_euconsent")
            .putString(StoreClient.AUTH_ID_KEY,"foo_authId")
            .putString(StoreClient.IABTCF_KEY_PREFIX+"foo_data","foo_IAB_data")
            .commit();
        storeClient.clearAllData();
        assertFalse(sharedPreferences.contains(StoreClient.CONSENT_UUID_KEY));
        assertFalse(sharedPreferences.contains(StoreClient.META_DATA_KEY));
        assertFalse(sharedPreferences.contains(StoreClient.EU_CONSENT_KEY));
        assertFalse(sharedPreferences.contains(StoreClient.AUTH_ID_KEY));
        assertFalse(sharedPreferences.contains(StoreClient.IABTCF_KEY_PREFIX+"foo_data"));
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
    public void setTCDataAndCheckPrimitives() {
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

    @Test
    public void shouldThrowConsentLibExceptionWhenGettingInvalidUserConsent(){
        editor.putString(StoreClient.USER_CONSENT_KEY, "{description:\"im not a valid UserConsent str ojb, sorry...\"}").commit();
        ConsentLibException err = assertThrows(ConsentLibException.class, () -> storeClient.getUserConsent());
        assertEquals("Error trying to recover UserConsents for sharedPrefs", err.consentLibErrorMessage);
    }

}
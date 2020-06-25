package com.sourcepoint.gdpr_cmplibrary;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.test.core.app.ApplicationProvider;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(RobolectricTestRunner.class)
public class GDPRUserConsentTest {

    private StoreClient storeClient;
    private SharedPreferences.Editor editor;
    private GDPRUserConsent userConsent;

    @Before
    public void setUp() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext());
        storeClient = new StoreClient(sharedPreferences);
        editor = sharedPreferences.edit();
        userConsent =  new GDPRUserConsent();
        userConsent.uuid = "consentUUID";
        userConsent.consentString = "consentString";
        userConsent.acceptedCategories = new ArrayList<>();
        userConsent.acceptedVendors = new ArrayList<>();
        userConsent.legIntCategories = new ArrayList<>();
        userConsent.specialFeatures = new ArrayList<>();
        userConsent.TCData = new HashMap();
    }

    private void clearSharedPrefs(){
        editor.clear().commit();
    }

    private void setUserConsent() throws JSONException, ConsentLibException {
        storeClient.setUserConsents(userConsent);
    }

    private GDPRUserConsent getUserConsent() throws ConsentLibException {
        return storeClient.getUserConsent();
    }

    @Test
    public void testEmptyUserConsent() throws ConsentLibException {
        clearSharedPrefs();
        GDPRUserConsent storeClientConsents = getUserConsent();

        assertNotNull(storeClientConsents);
        assertNotNull(storeClientConsents.uuid);
        assertNotNull(storeClientConsents.consentString);
        assertNotNull(storeClientConsents.acceptedCategories);
        assertNotNull(storeClientConsents.acceptedVendors);
        assertNotNull(storeClientConsents.legIntCategories);
        assertNotNull(storeClientConsents.specialFeatures);
        assertNotNull(storeClientConsents.TCData);
        assertNotNull(storeClientConsents.vendorGrants);
        clearSharedPrefs();
    }

    @Test
    public void testUserConsent() throws JSONException, ConsentLibException {
        setUserConsent();

        assertNotNull(getUserConsent());
        assertEquals(getUserConsent().getClass(), GDPRUserConsent.class);
        clearSharedPrefs();
    }

    @Test
    public void testUserConsentUUID() throws JSONException, ConsentLibException {
        setUserConsent();
        
        assertNotNull(getUserConsent().uuid);
        assertEquals(userConsent.uuid, getUserConsent().uuid);
        clearSharedPrefs();
    }

    @Test
    public void testUserConsentConsentString() throws JSONException, ConsentLibException {
        setUserConsent();

        assertNotNull(getUserConsent().consentString);
        assertEquals(userConsent.consentString, getUserConsent().consentString);
        clearSharedPrefs();
    }

    @Test
    public void testUserConsentAcceptedCategories() throws JSONException, ConsentLibException {
        setUserConsent();

        assertNotNull(getUserConsent().acceptedCategories);
        assertEquals(userConsent.acceptedCategories, getUserConsent().acceptedCategories);
        clearSharedPrefs();
    }

    @Test
    public void testUserConsentAcceptedVendors() throws JSONException, ConsentLibException {
        setUserConsent();

        assertNotNull(getUserConsent().acceptedVendors);
        assertEquals(userConsent.acceptedVendors, getUserConsent().acceptedVendors);
        clearSharedPrefs();
    }

    @Test
    public void testUserConsentLegInCategories() throws JSONException, ConsentLibException {
        setUserConsent();

        assertNotNull(getUserConsent().legIntCategories);
        assertEquals(userConsent.legIntCategories, getUserConsent().legIntCategories);
        clearSharedPrefs();
    }

    @Test
    public void testUserConsentSpecialFeatures() throws JSONException, ConsentLibException {
        setUserConsent();

        assertNotNull(getUserConsent().specialFeatures);
        assertEquals(userConsent.specialFeatures, getUserConsent().specialFeatures);
        clearSharedPrefs();
    }

    @Test
    public void testUserConsentTCData() throws JSONException, ConsentLibException {
        setUserConsent();

        assertNotNull(getUserConsent().TCData);
        assertEquals(userConsent.TCData, getUserConsent().TCData);
        clearSharedPrefs();
    }

    @Test
    public void testUserConsentVendorGrants() throws JSONException, ConsentLibException {
        setUserConsent();

        assertNotNull(getUserConsent().vendorGrants);
        assertEquals(userConsent.vendorGrants, getUserConsent().vendorGrants);
        clearSharedPrefs();
    }

    @Test
    public void toJsonObjectTest() throws JSONException, ConsentLibException {
        assertNotNull(userConsent.toJsonObject());
        assertEquals(userConsent.toJsonObject().getClass() , JSONObject.class);
    }
}

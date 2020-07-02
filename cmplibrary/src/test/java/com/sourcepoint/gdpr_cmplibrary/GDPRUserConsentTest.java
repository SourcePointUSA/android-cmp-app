package com.sourcepoint.gdpr_cmplibrary;

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


    private String jsonData;

    {
        jsonData = "{\r\n   \"uuid\":\"f9afbd99-cb24-442a-a2db-aaba58fdb929\",\r\n  " +
                " \"userConsent\":{\r\n \"euconsent\":\"CO16OmHO16OmHAGABCENAsCgAP_AAH_AAAYgAZQAgAHgAyAA8AAQBgQAEAGQQACADIMABABkIAAgAyHAAQAZFAAIAMgA.YAAAAAAAAAAA\",\r\n " +
                " \"acceptedVendors\":[\r\n \"5e4a5fbf26de4a77922b38a6\",\r\n  \"5e37fc3e56a5e60e003a7124\",\r\n \"5e7ced57b8e05c5a7d171cda\"\r\n ],\r\n " +
                " \"acceptedCategories\":[\r\n \"5e87321eb31ef52cd96cc552\",\r\n \"5e87321eb31ef52cd96cc554\",\r\n \"5e87321eb31ef52cd96cc555\",\r\n \"5e87321eb31ef52cd96cc556\",\r\n \"5e87321eb31ef52cd96cc558\",\r\n \"5e87321eb31ef52cd96cc55a\",\r\n \"5e87321eb31ef52cd96cc55b\",\r\n \"5e87321eb31ef52cd96cc55c\"\r\n ],\r\n " +
                " \"specialFeatures\":[\r\n\r\n ],\r\n \"legIntCategories\":[\r\n\r\n ],\r\n " +
                " \"grants\":{\r\n \"5e7ced57b8e05c5a7d171cda\":{\r\n \"vendorGrant\":false,\r\n " +
                " \"purposeGrants\":{\r\n \"5e87321eb31ef52cd96cc552\":false,\r\n \"5e87321eb31ef52cd96cc553\":false,\r\n \"5e87321eb31ef52cd96cc554\":false,\r\n \"5e87321eb31ef52cd96cc555\":false,\r\n \"5e87321eb31ef52cd96cc559\":false,\r\n \"5e87321eb31ef52cd96cc55c\":false\r\n }\r\n },\r\n \"5e37fc3e56a5e60e003a7124\":{\r\n " +
                " \"vendorGrant\":true,\r\n \"purposeGrants\":{\r\n \"5e87321eb31ef52cd96cc552\":true,\r\n \"5e87321eb31ef52cd96cc553\":true,\r\n \"5e87321eb31ef52cd96cc554\":true,\r\n \"5e87321eb31ef52cd96cc555\":true,\r\n \"5e87321eb31ef52cd96cc556\":true,\r\n \"5e87321eb31ef52cd96cc558\":true,\r\n \"5e87321eb31ef52cd96cc559\":true,\r\n \"5e87321eb31ef52cd96cc55a\":true,\r\n \"5e87321eb31ef52cd96cc55b\":true,\r\n \"5e87321eb31ef52cd96cc55c\":true\r\n            }\r\n },\r\n \"5e4a5fbf26de4a77922b38a6\":{\r\n \"vendorGrant\":true,\r\n \"purposeGrants\":{\r\n \"5e87321eb31ef52cd96cc556\":true\r\n }\r\n }\r\n },\r\n " +
                " \"TCData\":{\r\n \"IABTCF_CmpSdkID\":6,\r\n \"IABTCF_CmpSdkVersion\":2,\r\n \"IABTCF_PolicyVersion\":2,\r\n \"IABTCF_PublisherCC\":\"DE\",\r\n \"IABTCF_PurposeOneTreatment\":0,\r\n \"IABTCF_UseNonStandardStacks\":0,\r\n \"IABTCF_TCString\":\"CO16OmHO16OmHAGABCENAsCgAP_AAH_AAAYgAZQAgAHgAyAA8AAQBgQAEAGQQACADIMABABkIAAgAyHAAQAZFAAIAMgA.YAAAAAAAAAAA\",\r\n " +
                "\"IABTCF_VendorConsents\":\"0000000000000010000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\r\n         \"IABTCF_VendorLegitimateInterests\":\"0000000000000010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\r\n         \"IABTCF_PurposeConsents\":\"111111111100000000000000000000000000000000000000\"\r\n      }\r\n   }\r\n}[\"5e87321eb31ef52cd96cc552\",\"5e87321eb31ef52cd96cc554\",\"5e87321eb31ef52cd96cc555\",\"5e87321eb31ef52cd96cc556\",\"5e87321eb31ef52cd96cc558\",\"5e87321eb31ef52cd96cc55a\",\"5e87321eb31ef52cd96cc55b\",\"5e87321eb31ef52cd96cc55c\"],\"specialFeatures\":[],\"legIntCategories\":[],\"grants\":{\"5e7ced57b8e05c5a7d171cda\":{\"vendorGrant\":false,\"purposeGrants\":{\"5e87321eb31ef52cd96cc552\":false,\"5e87321eb31ef52cd96cc553\":false,\"5e87321eb31ef52cd96cc554\":false,\"5e87321eb31ef52cd96cc555\":false,\"5e87321eb31ef52cd96cc559\":false,\"5e87321eb31ef52cd96cc55c\":false}},\"5e37fc3e56a5e60e003a7124\":{\"vendorGrant\":true,\"purposeGrants\":{\"5e87321eb31ef52cd96cc552\":true,\"5e87321eb31ef52cd96cc553\":true,\"5e87321eb31ef52cd96cc554\":true,\"5e87321eb31ef52cd96cc555\":true,\"5e87321eb31ef52cd96cc556\":true,\"5e87321eb31ef52cd96cc558\":true,\"5e87321eb31ef52cd96cc559\":true,\"5e87321eb31ef52cd96cc55a\":true,\"5e87321eb31ef52cd96cc55b\":true,\"5e87321eb31ef52cd96cc55c\":true}},\"5e4a5fbf26de4a77922b38a6\":{\"vendorGrant\":true,\"purposeGrants\":{\"5e87321eb31ef52cd96cc556\":true}}},\"TCData\":{\"IABTCF_CmpSdkID\":6,\"IABTCF_CmpSdkVersion\":2,\"IABTCF_PolicyVersion\":2,\"IABTCF_PublisherCC\":\"DE\",\"IABTCF_PurposeOneTreatment\":0,\"IABTCF_UseNonStandardStacks\":0,\"IABTCF_TCString\":\"CO16OmHO16OmHAGABCENAsCgAP_AAH_AAAYgAZQAgAHgAyAA8AAQBgQAEAGQQACADIMABABkIAAgAyHAAQAZFAAIAMgA.YAAAAAAAAAAA\",\"IABTCF_VendorConsents\":\"0000000000000010000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\"IABTCF_VendorLegitimateInterests\":\"0000000000000010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\"," +
                "\"IABTCF_PurposeConsents\":\"111111111100000000000000000000000000000000000000";
    }


    private GDPRUserConsent userConsent;
    private JSONObject jsonConsents;
    private String consentUUID;

    @Before
    public void setUp() throws JSONException, ConsentLibException {
        JSONObject consentsResponse = new JSONObject(jsonData);
        consentUUID = consentsResponse.getString("uuid");
        jsonConsents = consentsResponse.getJSONObject("userConsent");
        userConsent = new GDPRUserConsent(jsonConsents ,consentUUID);
    }

    @Test
    public void testEmptyUserConsent() {
        GDPRUserConsent storeClientConsents = new GDPRUserConsent();

        assertNotNull(storeClientConsents);
        assertNotNull(storeClientConsents.uuid);
        assertNotNull(storeClientConsents.consentString);
        assertNotNull(storeClientConsents.acceptedCategories);
        assertNotNull(storeClientConsents.acceptedVendors);
        assertNotNull(storeClientConsents.legIntCategories);
        assertNotNull(storeClientConsents.specialFeatures);
        assertNotNull(storeClientConsents.TCData);
        assertNotNull(storeClientConsents.vendorGrants);
    }

    @Test
    public void testUserConsent() {

        assertNotNull(userConsent);
        assertEquals(userConsent.getClass(), GDPRUserConsent.class);
    }

    @Test
    public void testUserConsentUUID() {

        assertNotNull(userConsent.uuid);
        assertEquals(consentUUID, userConsent.uuid);
    }

    @Test
    public void testUserConsentConsentString() throws JSONException {

        assertNotNull(userConsent.consentString);
        assertEquals(userConsent.consentString, jsonConsents.getString("euconsent"));
    }

    @Test
    public void testUserConsentAcceptedCategories() throws JSONException {

        ArrayList<String> acceptedCategories = userConsent.json2StrArr(jsonConsents.getJSONArray("acceptedCategories"));

        assertNotNull(userConsent.acceptedCategories);
        assertEquals(acceptedCategories, userConsent.acceptedCategories);
    }

    @Test
    public void testUserConsentAcceptedVendors() throws JSONException {
        ArrayList<String> acceptedVendors = userConsent.json2StrArr(jsonConsents.getJSONArray("acceptedVendors"));

        assertNotNull(userConsent.acceptedVendors);
        assertEquals(acceptedVendors , userConsent.acceptedVendors);
    }

    @Test
    public void testUserConsentLegInCategories() throws JSONException {
        ArrayList<String> legIntCategories = userConsent.json2StrArr(jsonConsents.getJSONArray("legIntCategories"));

        assertNotNull(userConsent.legIntCategories);
        assertEquals(legIntCategories, userConsent.legIntCategories);
    }

    @Test
    public void testUserConsentSpecialFeatures() throws JSONException {

        ArrayList<String> specialFeatures = userConsent.json2StrArr(jsonConsents.getJSONArray("specialFeatures"));

        assertNotNull(userConsent.specialFeatures);
        assertEquals(specialFeatures, userConsent.specialFeatures);

    }

    @Test
    public void testUserConsentTCData() throws JSONException, ConsentLibException {
        HashMap tcData = CustomJsonParser.getHashMap(jsonConsents.getJSONObject("TCData"));

        assertNotNull(userConsent.TCData);
        assertEquals(tcData, userConsent.TCData);
    }

    @Test
    public void testUserConsentVendorGrants() throws JSONException, ConsentLibException {
        jsonConsents.put("uuid",consentUUID);

        GDPRUserConsent.VendorGrants vendorGrants = new GDPRUserConsent(jsonConsents).vendorGrants;

        assertNotNull( userConsent.vendorGrants);
        assertEquals(vendorGrants.toString(), userConsent.vendorGrants.toString());
    }

    @Test
    public void toJsonObjectTest() throws JSONException, ConsentLibException {

        assertNotNull(userConsent.toJsonObject());
        assertEquals(userConsent.toJsonObject().getClass() , JSONObject.class);
    }

    @Test
    public void testUserConsentConstructorWithoutUUID() throws ConsentLibException, JSONException {
        jsonConsents.put("uuid",consentUUID);

        GDPRUserConsent userConsentWithoutUUID = new GDPRUserConsent(jsonConsents);

        assertEquals(userConsent.uuid , userConsentWithoutUUID.uuid );
        assertEquals(userConsent.consentString, userConsentWithoutUUID.consentString);
        assertEquals(userConsent.acceptedCategories,userConsentWithoutUUID.acceptedCategories);
        assertEquals(userConsent.acceptedVendors, userConsentWithoutUUID.acceptedVendors);
        assertEquals(userConsent.legIntCategories, userConsentWithoutUUID.legIntCategories);
        assertEquals(userConsent.specialFeatures, userConsentWithoutUUID.specialFeatures);
        assertEquals(userConsent.TCData, userConsentWithoutUUID.TCData);
        assertEquals(userConsent.vendorGrants.toString() , userConsentWithoutUUID.vendorGrants.toString());

    }
}

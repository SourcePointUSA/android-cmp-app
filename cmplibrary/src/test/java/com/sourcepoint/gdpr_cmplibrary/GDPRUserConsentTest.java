package com.sourcepoint.gdpr_cmplibrary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static com.sourcepoint.gdpr_cmplibrary.StoreClient.DEFAULT_EMPTY_CONSENT_STRING;
import static com.sourcepoint.gdpr_cmplibrary.StoreClient.DEFAULT_EMPTY_UUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;


@RunWith(RobolectricTestRunner.class)
public class GDPRUserConsentTest {


    private String jsonData =
                "{\r\n   \"uuid\":\"f9afbd99-cb24-442a-a2db-aaba58fdb929\",\r\n  " +
                " \"userConsent\":{\r\n \"euconsent\":\"CO16OmHO16OmHAGABCENAsCgAP_AAH_AAAYgAZQAgAHgAyAA8AAQBgQAEAGQQACADIMABABkIAAgAyHAAQAZFAAIAMgA.YAAAAAAAAAAA\",\r\n " +
                " \"acceptedVendors\":[\r\n \"5e4a5fbf26de4a77922b38a6\",\r\n  \"5e37fc3e56a5e60e003a7124\",\r\n \"5e7ced57b8e05c5a7d171cda\"\r\n ],\r\n " +
                " \"acceptedCategories\":[\r\n \"5e87321eb31ef52cd96cc552\",\r\n \"5e87321eb31ef52cd96cc554\",\r\n \"5e87321eb31ef52cd96cc55c\"\r\n ],\r\n " +
                " \"specialFeatures\":[\r\n \"5e87321eb31ef52cd96cc552\"\r\n ],\r\n \"legIntCategories\":[\r\n \"5e87321eb31ef52cd96cc552\"\r\n ],\r\n " +
                " \"grants\":{\r\n \"5e7ced57b8e05c5a7d171cda\":{\r\n \"vendorGrant\":false,\r\n \"purposeGrants\":{\r\n \"5e87321eb31ef52cd96cc552\":false,\r\n \"5e87321eb31ef52cd96cc553\":false,\r\n \"5e87321eb31ef52cd96cc554\":false,\r\n \"5e87321eb31ef52cd96cc555\":false,\r\n \"5e87321eb31ef52cd96cc559\":false,\r\n \"5e87321eb31ef52cd96cc55c\":false\r\n }\r\n },\r\n " +
                        "\"5e37fc3e56a5e60e003a7124\":{\r\n \"vendorGrant\":true,\r\n \"purposeGrants\":{\r\n \"5e87321eb31ef52cd96cc552\":true,\r\n \"5e87321eb31ef52cd96cc553\":true,\r\n \"5e87321eb31ef52cd96cc554\":true,\r\n \"5e87321eb31ef52cd96cc555\":true,\r\n \"5e87321eb31ef52cd96cc556\":true,\r\n \"5e87321eb31ef52cd96cc558\":true,\r\n \"5e87321eb31ef52cd96cc559\":true,\r\n \"5e87321eb31ef52cd96cc55a\":true,\r\n \"5e87321eb31ef52cd96cc55b\":true,\r\n \"5e87321eb31ef52cd96cc55c\":true\r\n            }\r\n },\r\n \"5e4a5fbf26de4a77922b38a6\":{\r\n \"vendorGrant\":true,\r\n \"purposeGrants\":{\r\n \"5e87321eb31ef52cd96cc556\":true\r\n }\r\n }\r\n },\r\n " +
                " \"TCData\":{\r\n \"IABTCF_CmpSdkID\":6,\r\n \"IABTCF_CmpSdkVersion\":2,\r\n \"IABTCF_PolicyVersion\":2,\r\n \"IABTCF_PublisherCC\":\"DE\",\r\n \"IABTCF_PurposeOneTreatment\":0,\r\n \"IABTCF_UseNonStandardStacks\":0,\r\n \"IABTCF_TCString\":\"CO16OmHO16OmHAGABCENAsCgAP_AAH_AAAYgAZQAgAHgAyAA8AAQBgQAEAGQQACADIMABABkIAAgAyHAAQAZFAAIAMgA.YAAAAAAAAAAA\",\r\n " +
                "\"IABTCF_VendorConsents\":\"000000000000001000000000000000000000000000000000010\",\r\n         \"IABTCF_VendorLegitimateInterests\":\"0000000000000010\",\r\n         \"IABTCF_PurposeConsents\":\"111111111100000000000000000000000000000000000000\"\r\n      }\r\n   }\r\n}[\"5e87321eb31ef52cd96cc552\",\"5e87321eb31ef52cd96cc554\",\"5e87321eb31ef52cd96cc555\",\"5e87321eb31ef52cd96cc556\",\"5e87321eb31ef52cd96cc558\",\"5e87321eb31ef52cd96cc55a\",\"5e87321eb31ef52cd96cc55b\",\"5e87321eb31ef52cd96cc55c\"],\"specialFeatures\":[],\"legIntCategories\":[],\"grants\":{\"5e7ced57b8e05c5a7d171cda\":{\"vendorGrant\":false,\"purposeGrants\":{\"5e87321eb31ef52cd96cc552\":false,\"5e87321eb31ef52cd96cc553\":false,\"5e87321eb31ef52cd96cc554\":false,\"5e87321eb31ef52cd96cc555\":false,\"5e87321eb31ef52cd96cc559\":false,\"5e87321eb31ef52cd96cc55c\":false}},\"5e37fc3e56a5e60e003a7124\":{\"vendorGrant\":true,\"purposeGrants\":{\"5e87321eb31ef52cd96cc552\":true,\"5e87321eb31ef52cd96cc553\":true,\"5e87321eb31ef52cd96cc554\":true,\"5e87321eb31ef52cd96cc555\":true,\"5e87321eb31ef52cd96cc556\":true,\"5e87321eb31ef52cd96cc558\":true,\"5e87321eb31ef52cd96cc559\":true,\"5e87321eb31ef52cd96cc55a\":true,\"5e87321eb31ef52cd96cc55b\":true,\"5e87321eb31ef52cd96cc55c\":true}},\"5e4a5fbf26de4a77922b38a6\":{\"vendorGrant\":true,\"purposeGrants\":{\"5e87321eb31ef52cd96cc556\":true}}},\"TCData\":{\"IABTCF_CmpSdkID\":6,\"IABTCF_CmpSdkVersion\":2,\"IABTCF_PolicyVersion\":2,\"IABTCF_PublisherCC\":\"DE\",\"IABTCF_PurposeOneTreatment\":0,\"IABTCF_UseNonStandardStacks\":0,\"IABTCF_TCString\":\"CO16OmHO16OmHAGABCENAsCgAP_AAH_AAAYgAZQAgAHgAyAA8AAQBgQAEAGQQACADIMABABkIAAgAyHAAQAZFAAIAMgA.YAAAAAAAAAAA\",\"IABTCF_VendorConsents\":\"0000000000000010000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\"IABTCF_VendorLegitimateInterests\":\"0000000000000010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\"," +
                "\"IABTCF_PurposeConsents\":\"111111111100000000";

    private GDPRUserConsent userConsent;
    private JSONObject jsonConsentsMock;
    private String consentUUIDMock;

    @Before
    public void setUp() throws JSONException, ConsentLibException {
        JSONObject consentsResponse = new JSONObject(jsonData);
        consentUUIDMock = consentsResponse.getString("uuid");
        jsonConsentsMock = consentsResponse.getJSONObject("userConsent");
        userConsent = new GDPRUserConsent(jsonConsentsMock ,consentUUIDMock);
    }

    @Test
    public void emptyUserConsentConstructor() {
        GDPRUserConsent emptyUserConsent = new GDPRUserConsent();

        assertEquals(DEFAULT_EMPTY_UUID, emptyUserConsent.uuid);
        assertEquals(DEFAULT_EMPTY_CONSENT_STRING, emptyUserConsent.consentString);
        assertTrue(emptyUserConsent.acceptedCategories.isEmpty());
        assertTrue(emptyUserConsent.acceptedVendors.isEmpty());
        assertTrue(emptyUserConsent.legIntCategories.isEmpty());
        assertTrue(emptyUserConsent.specialFeatures.isEmpty());
        assertTrue(emptyUserConsent.TCData.isEmpty());
        assertTrue(emptyUserConsent.vendorGrants.isEmpty());
    }


    @Test
    public void userConsentUUID() {
        assertEquals(consentUUIDMock, userConsent.uuid);
    }

    @Test
    public void userConsentConsentString() throws JSONException {
        assertEquals(userConsent.consentString, jsonConsentsMock.getString("euconsent"));
    }

    @Test
    public void userConsentAcceptedCategories() throws JSONException {
        JSONArray  acceptedCategories = jsonConsentsMock.getJSONArray("acceptedCategories");
        assertEquals(acceptedCategories, new JSONArray(userConsent.acceptedCategories));
    }

    @Test
    public void userConsentAcceptedVendors() throws JSONException {
        JSONArray acceptedVendors = jsonConsentsMock.getJSONArray("acceptedVendors");
        assertEquals(acceptedVendors , new JSONArray(userConsent.acceptedVendors));
    }

    @Test
    public void json2StrArr() throws JSONException {
        JSONArray acceptedVendors = jsonConsentsMock.getJSONArray("acceptedVendors");
        assertEquals(userConsent.acceptedVendors , userConsent.json2StrArr(acceptedVendors));
    }

    @Test
    public void userConsentLegInCategories() throws JSONException {
        JSONArray legIntCategories = jsonConsentsMock.getJSONArray("legIntCategories");
        assertEquals(legIntCategories, new JSONArray(userConsent.legIntCategories));
    }

    @Test
    public void userConsentSpecialFeatures() throws JSONException {
        JSONArray specialFeatures = jsonConsentsMock.getJSONArray("specialFeatures");
        assertEquals(specialFeatures, new JSONArray(userConsent.specialFeatures));
    }

    @Test
    public void userConsentTCData() throws JSONException {
        JSONObject tcData = jsonConsentsMock.getJSONObject("TCData");
        JSONObject consentTCData = new JSONObject(userConsent.TCData);
        assertEquals(tcData.get("IABTCF_CmpSdkID"), consentTCData.get("IABTCF_CmpSdkID"));
        assertEquals(tcData.get("IABTCF_VendorConsents"), consentTCData.get("IABTCF_VendorConsents"));
    }

    @Test
    public void userConsentVendorGrants() throws JSONException, ConsentLibException {
        jsonConsentsMock.put("uuid",consentUUIDMock);
        GDPRUserConsent.VendorGrants vendorGrants = new GDPRUserConsent(jsonConsentsMock).vendorGrants;
        assertEquals(vendorGrants.toString(), userConsent.vendorGrants.toString());
    }

    @Test
    public void toJsonObjectTest() throws JSONException, ConsentLibException {
        JSONObject jsonObject = userConsent.toJsonObject();

        assertEquals(userConsent.uuid , jsonObject.getString("uuid"));
        assertEquals(userConsent.consentString, jsonObject.getString("euconsent"));

        JSONArray categories = jsonObject.getJSONArray("acceptedCategories");
        assertEquals(userConsent.acceptedCategories.size(), categories.length());
        assertEquals(userConsent.acceptedCategories.get(0), categories.get(0));
        assertEquals(userConsent.acceptedCategories.get(userConsent.acceptedCategories.size()-1), categories.get(categories.length()-1));

        JSONArray vendors = jsonObject.getJSONArray("acceptedVendors");
        assertEquals(userConsent.acceptedVendors.size(), vendors.length());
        assertEquals(userConsent.acceptedVendors.get(0), vendors.get(0));
        assertEquals(userConsent.acceptedVendors.get(userConsent.acceptedVendors.size()-1), vendors.get(vendors.length()-1));

        JSONArray specialFeatures = jsonObject.getJSONArray("specialFeatures");
        assertEquals(userConsent.specialFeatures.size(), specialFeatures.length());
        assertEquals(userConsent.specialFeatures.get(0), specialFeatures.get(0));
        assertEquals(userConsent.specialFeatures.get(userConsent.specialFeatures.size()-1), specialFeatures.get(specialFeatures.length()-1));

        JSONArray legIntCategories = jsonObject.getJSONArray("legIntCategories");
        assertEquals(userConsent.legIntCategories.size(), legIntCategories.length());
        assertEquals(userConsent.legIntCategories.get(0), legIntCategories.get(0));
        assertEquals(userConsent.legIntCategories.get(userConsent.legIntCategories.size()-1), legIntCategories.get(legIntCategories.length()-1));
    }

    @Test
    public void userConsentConstructorWithoutUUID() throws ConsentLibException, JSONException {
        jsonConsentsMock.put("uuid",consentUUIDMock);

        GDPRUserConsent userConsentWithoutUUID = new GDPRUserConsent(jsonConsentsMock);

        assertEquals(userConsent.uuid , userConsentWithoutUUID.uuid );
        assertEquals(userConsent.consentString, userConsentWithoutUUID.consentString);
        assertEquals(userConsent.acceptedCategories,userConsentWithoutUUID.acceptedCategories);
        assertEquals(userConsent.acceptedVendors, userConsentWithoutUUID.acceptedVendors);
        assertEquals(userConsent.legIntCategories, userConsentWithoutUUID.legIntCategories);
        assertEquals(userConsent.specialFeatures, userConsentWithoutUUID.specialFeatures);
        assertEquals(userConsent.TCData, userConsentWithoutUUID.TCData);
        assertEquals(userConsent.vendorGrants.toString() , userConsentWithoutUUID.vendorGrants.toString());

    }

    @Test
    public void constructorShouldThrowExceptionForNoUUIDOnJConsent() {
        ConsentLibException err = assertThrows(ConsentLibException.class, () -> new GDPRUserConsent(jsonConsentsMock));
        assertEquals("No uuid found on jConsent", err.consentLibErrorMessage);
    }

    @Test
    public void tcDataFromToJSONObject() throws JSONException, ConsentLibException {
        JSONObject jsonObject = userConsent.toJsonObject();
        assertEquals(userConsent.TCData.get("IABTCF_CmpSdkID") , jsonObject.getJSONObject("TCData").get("IABTCF_CmpSdkID"));
    }

    @Test
    public void vendorGrantsToJSONObject() throws JSONException, ConsentLibException {
        JSONObject jsonObject = userConsent.toJsonObject();
        JSONObject vendorGrantsJSON = jsonObject.getJSONObject("grants");

        assertEquals(userConsent.vendorGrants.toJsonObject().toString(), vendorGrantsJSON.toString());
        assertEquals(userConsent.vendorGrants.get("5e7ced57b8e05c5a7d171cda").vendorGrant, vendorGrantsJSON.getJSONObject("5e7ced57b8e05c5a7d171cda").get("vendorGrant"));
        assertEquals(userConsent.vendorGrants.get("5e7ced57b8e05c5a7d171cda").purposeGrants.get("5e87321eb31ef52cd96cc552"),
                vendorGrantsJSON.getJSONObject("5e7ced57b8e05c5a7d171cda").getJSONObject("purposeGrants").get("5e87321eb31ef52cd96cc552"));
    }

    @Test
    public void constructorShouldThrowExceptionForNullUUID() {
        ConsentLibException err = assertThrows(ConsentLibException.class, () ->  new GDPRUserConsent(jsonConsentsMock, null));
        assertTrue(err.getCause().getMessage().contains("uuid should not be null"));
        assertEquals("Error parsing JSONObject to ConsentUser obj", err.consentLibErrorMessage);
    }

    @Test
    public void constructorShouldThrowExceptionForEmptyJSONObject() {
        ConsentLibException err = assertThrows(ConsentLibException.class, () ->  new GDPRUserConsent(new JSONObject(), "foo_uuid"));
        assertEquals("Error parsing JSONObject to ConsentUser obj", err.consentLibErrorMessage);
    }
}
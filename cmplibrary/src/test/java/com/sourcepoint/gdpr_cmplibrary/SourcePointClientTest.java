//package com.sourcepoint.gdpr_cmplibrary;
//
//import android.content.Context;
//
//import com.loopj.android.http.AsyncHttpClient;
//import com.loopj.android.http.ResponseHandlerInterface;
//import com.loopj.android.http.SyncHttpClient;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.invocation.InvocationOnMock;
//import org.mockito.stubbing.Answer;
//import org.robolectric.RobolectricTestRunner;
//import org.robolectric.annotation.Config;
//
//import cz.msebera.android.httpclient.Header;
//import cz.msebera.android.httpclient.HttpEntity;
//import okhttp3.mockwebserver.MockResponse;
//import okhttp3.mockwebserver.MockWebServer;
//
//import static android.os.Build.VERSION_CODES.O;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.ArgumentMatchers.nullable;
//import static org.mockito.Mockito.doAnswer;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.reset;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//
//
//@RunWith(RobolectricTestRunner.class)
//@Config(manifest = Config.NONE, sdk = O)
//public class SourcePointClientTest {
//
//    private AsyncHttpClient http;
//    private SourcePointClient sourcePointClient , sourcePointClientMock;
//    private GDPRConsentLib.OnLoadComplete onLoadComplete;
//
//    private String consentUUID = "consentUUID";
//    private String euConsent = "euConsent";
//    private String meta = "meta";
//    private boolean isNative = false;
//
//    @Rule
//    public MockWebServer mockWebServer = new MockWebServer();
//
//
//
//    @Before
//    public void setUp() {
//        http = mock(AsyncHttpClient.class);
//        onLoadComplete = mock(GDPRConsentLib.OnLoadComplete.class);
//        int accountId = 123;
//        int propertyId = 1234;
//        String property = "example.com";
//        String targettingParams = "targetingParams";
//        String authId = "";
//        boolean staging = false;
//        boolean stagingCampaign = false;
//        sourcePointClient =  new SourcePointClient(accountId, property, propertyId, stagingCampaign, staging, targettingParams, authId);
//        sourcePointClientMock =  new SourcePointClient(accountId, property, propertyId, stagingCampaign, staging, targettingParams, authId);
//
//        sourcePointClient.setHttpDummy(http);
//    }
//
//    private JSONObject jsonObject = null;
//
//    private void doAPICallWithAnswer(final boolean onSuccess , String response) {
//        doAnswer(new Answer() {
//            @Override
//            public Object answer(InvocationOnMock invocation) {
//                SourcePointClient.ResponseHandler listener = (SourcePointClient.ResponseHandler) invocation.getArguments()[1];
//
//                if (onSuccess) {
//                    try {
//                        jsonObject = new JSONObject(response);
//                    } catch (JSONException err) {
//                        System.out.println("Error " + err.toString());
//                    }
//
//                    Header[] headers = new Header[]{};
//                    listener.onSuccess(200, headers, jsonObject);
//                } else {
//                    Header[] headers = new Header[]{};
//                    listener.onFailure(404, headers, "Error", new ConsentLibException("Error"));
//                }
//
//                return null;
//            }
//        }).when(http).get(anyString(), any(ResponseHandlerInterface.class));
//
//        doAnswer(new Answer() {
//            @Override
//            public Object answer(InvocationOnMock invocation) {
//                SourcePointClient.ResponseHandler listener = (SourcePointClient.ResponseHandler) invocation.getArguments()[4];
//                if (onSuccess) {
//                    try {
//                        jsonObject = new JSONObject(response);
//                    } catch (JSONException err) {
//                        System.out.println("Error " + err.toString());
//                    }
//
//                    Header[] headers = new Header[]{};
//                    listener.onSuccess(200, headers, jsonObject);
//                } else {
//                    Header[] headers = new Header[]{};
//                    listener.onFailure(404, headers, "Error", new ConsentLibException("Error"));
//                }
//
//                return null;
//            }
//        }).when(http).post(nullable(Context.class), anyString(), any(HttpEntity.class), anyString(), any(ResponseHandlerInterface.class));
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        reset(onLoadComplete);
//        reset(http);
//    }
//
//    @Test
//    public void getMessageSuccess() throws Exception {
//        String response = "{\"messageUrl\":\"http://google.com\"}";
//        JSONObject expectedResponse = new JSONObject(response);
//
//        doAPICallWithAnswer(true, response);
//
//        sourcePointClient.getMessage(isNative, consentUUID, euConsent, meta, onLoadComplete);
//
//        ArgumentCaptor<JSONObject> captor = ArgumentCaptor.forClass(JSONObject.class);
//        verify(onLoadComplete, times(1)).onSuccess(captor.capture());
//
//        JSONObject actualResponse = captor.getValue();
//        assertEquals(actualResponse.toString(), expectedResponse.toString());
//
//        verify(onLoadComplete, never()).onFailure(any(ConsentLibException.class));
//    }
//
//    @Test
//    public void getMessageFailure() throws Exception {
//        String response = "{\"messageUrl\":\"http://google.com\"}";
//        JSONObject expectedResponse = new JSONObject(response);
//
//        doAPICallWithAnswer(false, response);
//
//        sourcePointClient.getMessage(isNative, consentUUID, euConsent, meta, onLoadComplete);
//
//        verify(onLoadComplete, times(1)).onFailure(any(ConsentLibException.class));
//        verify(onLoadComplete , never()).onSuccess(response);
//
//    }
//
//    @Test
//    public void sendConsentSuccess() throws Exception {
//
//        String response = "{\"messageUrl\":\"http://google.com\",\"requestUUID\":\"requested UUID\"}";
//
//        JSONObject expectedResponse = new JSONObject(response);
//
//        doAPICallWithAnswer(true, response);
//
//        sourcePointClient.sendConsent(expectedResponse, onLoadComplete);
//
//        ArgumentCaptor<JSONObject> captor = ArgumentCaptor.forClass(JSONObject.class);
//        verify(onLoadComplete, times(1)).onSuccess(captor.capture());
//
//        JSONObject actualResponse = captor.getValue();
//        //assertEquals(actualResponse.toString(), expectedResponse.toString());
//
//        verify(onLoadComplete, never()).onFailure(any(ConsentLibException.class));
//    }
//
//    @Test
//    public void sendConsentFailure () throws Exception{
//        String response = "{\"messageUrl\":\"http://google.com\",\"requestUUID\":\"requested UUID\"}";
//
//        JSONObject expectedResponse = new JSONObject(response);
//
//        doAPICallWithAnswer(false, response);
//
//        sourcePointClient.sendConsent(expectedResponse, onLoadComplete);
//
//        verify(onLoadComplete, times(1)).onFailure(any(ConsentLibException.class));
//        verify(onLoadComplete, never()).onSuccess(response);
//    }
//
//    @Test
//    public void getMessage_onSuccess() throws Exception {
//
//        GDPRConsentLib.OnLoadComplete callback = mock(GDPRConsentLib.OnLoadComplete.class);
//
////        sourcePointClientMock.baseMsgUrl = mockWebServer.url("/").toString();
//
//        sourcePointClientMock.setHttpDummy( new SyncHttpClient());
//
//        String response = "{\"messageUrl\":\"http://google.com\"}";
//        JSONObject jsonObject = new JSONObject(response);
//
//        mockWebServer.enqueue(new MockResponse().setBody(response.toString()));
//
//        try {
//            sourcePointClientMock.getMessage(false, consentUUID , euConsent, meta,  callback); // calling the method under test
//        } catch (ConsentLibException e) {
//            assertNotNull(e);
//            verify(callback).onFailure(e);
//        }
//
//        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
//        //verify(callback ,times(1)).onSuccess(captor.capture()); // using verify of mockito
//    }
//
//    @Test
//    public void getMessage_onFailure() throws Exception {
//
//        GDPRConsentLib.OnLoadComplete callback = mock(GDPRConsentLib.OnLoadComplete.class);
//
//        //sourcePointClientMock.baseMsgUrl = mockWebServer.url("/").toString();
//
//        sourcePointClientMock.setHttpDummy( new SyncHttpClient());
//
//        mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("Server Error"));
//
//        try {
//            sourcePointClientMock.getMessage(false,consentUUID , euConsent, meta, callback); // calling the method under test
//        } catch (ConsentLibException e) {
//            assertNotNull(e);
//            verify(callback).onFailure(e);
//        }
//
//        ArgumentCaptor<ConsentLibException> captor = ArgumentCaptor.forClass(ConsentLibException.class);
//        verify(callback, times(1)).onFailure(captor.capture());
//    }
//
//    @Test
//    public void sendConsent_onFailure() throws Exception {
//
//        GDPRConsentLib.OnLoadComplete callback = mock(GDPRConsentLib.OnLoadComplete.class);
//
//        //sourcePointClientMock.baseMsgUrl = mockWebServer.url("/").toString();
//
//        String response = "{\"messageUrl\":\"http://google.com\"}";
//        JSONObject jsonObject = new JSONObject(response);
//
//        sourcePointClientMock.setHttpDummy( new SyncHttpClient());
//        mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("Server Error"));
//        sourcePointClientMock.sendConsent(jsonObject,callback); // calling the method under test
//
//        ArgumentCaptor<ConsentLibException> captor = ArgumentCaptor.forClass(ConsentLibException.class);
//        verify(callback, times(1)).onFailure(captor.capture());
//    }
//}
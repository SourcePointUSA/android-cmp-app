package com.sourcepoint.gdpr_cmplibrary;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;

import static android.os.Build.VERSION_CODES.O;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = O)
public class SourcePointClientTest {

    private AsyncHttpClient http;
    private SourcePointClient sourcePointClient;
    private GDPRConsentLib.OnLoadComplete onLoadComplete;

    private String consentUUID = "consentUUID";
    private String euConsent = "euConsent";
    private String meta = "meta";
    private boolean isNative = false;



    @Before
    public void setUp() throws Exception {
        http = mock(AsyncHttpClient.class);
        onLoadComplete = mock(GDPRConsentLib.OnLoadComplete.class);
        int accountId = 123;
        int propertyId = 1234;
        String property = "example.com";
        String targettingParams = "targetingParams";
        String authId = "";
        boolean staging = false;
        boolean stagingCampaign = false;
        sourcePointClient =  new SourcePointClient(accountId, property, propertyId, stagingCampaign, staging, targettingParams, authId);
        sourcePointClient.setHttpDummy(http);
    }

    private JSONObject jsonObject = null;

    private void doAPICallWithAnswer(final boolean onSuccess , String response) {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                SourcePointClient.ResponseHandler listener = (SourcePointClient.ResponseHandler) invocation.getArguments()[1];

                if (onSuccess) {
                    try {
                        jsonObject = new JSONObject(response);
                    } catch (JSONException err) {
                        System.out.println("Error " + err.toString());
                    }

                    Header[] headers = new Header[]{};
                    listener.onSuccess(200, headers, jsonObject);
                } else {
                    Header[] headers = new Header[]{};
                    listener.onFailure(404, headers, "Error", new ConsentLibException("Error"));
                }

                return null;
            }
        }).when(http).get(anyString(), any(ResponseHandlerInterface.class));

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                SourcePointClient.ResponseHandler listener = (SourcePointClient.ResponseHandler) invocation.getArguments()[4];
                if (onSuccess) {
                    try {
                        jsonObject = new JSONObject(response);
                    } catch (JSONException err) {
                        System.out.println("Error " + err.toString());
                    }

                    Header[] headers = new Header[]{};
                    listener.onSuccess(200, headers, jsonObject);
                } else {
                    Header[] headers = new Header[]{};
                    listener.onFailure(404, headers, "Error", new ConsentLibException("Error"));
                }

                return null;
            }
        }).when(http).post(nullable(Context.class), anyString(), any(HttpEntity.class), anyString(), any(ResponseHandlerInterface.class));
    }

    @After
    public void tearDown() throws Exception {
        reset(onLoadComplete);
        reset(http);
    }


    @Test
    public void getGDPRStatusSuccess() {
        String response = "{\"gdprApplies\":\"http://google.com\"}";
        doAPICallWithAnswer(true ,response);

        sourcePointClient.getGDPRStatus(onLoadComplete);

        verify(onLoadComplete, times(1)).onSuccess(eq("http://google.com"));
        verify(onLoadComplete, never()).onFailure(any(ConsentLibException.class));
    }

    @Test
    public void getGDPRStatusFailure(){
        String response = "{\"gdprApplies\":\"http://google.com\"}";
        doAPICallWithAnswer(false ,response);

        sourcePointClient.getGDPRStatus(onLoadComplete);

        verify(onLoadComplete, never()).onSuccess(any());
        verify(onLoadComplete, times(1)).onFailure(any(ConsentLibException.class));
    }

    @Test
    public void getMessageSuccess() throws Exception {
        String response = "{\"messageUrl\":\"http://google.com\"}";
        JSONObject expectedResponse = new JSONObject(response);

        doAPICallWithAnswer(true, response);

        sourcePointClient.getMessage(isNative, consentUUID, euConsent, onLoadComplete);

        ArgumentCaptor<JSONObject> captor = ArgumentCaptor.forClass(JSONObject.class);
        verify(onLoadComplete, times(1)).onSuccess(captor.capture());

        JSONObject actualResponse = captor.getValue();
        assertEquals(actualResponse.toString(), expectedResponse.toString());

        verify(onLoadComplete, never()).onFailure(any(ConsentLibException.class));
    }

    @Test
    public void getMessageFailure() throws Exception {
        String response = "{\"messageUrl\":\"http://google.com\"}";
        JSONObject expectedResponse = new JSONObject(response);

        doAPICallWithAnswer(false, response);

        sourcePointClient.getMessage(isNative, consentUUID, euConsent, onLoadComplete);

        verify(onLoadComplete, times(1)).onFailure(any(ConsentLibException.class));
        verify(onLoadComplete , never()).onSuccess(response);

    }

    @Test
    public void sendConsentSuccess() throws Exception {

        String response = "{\"messageUrl\":\"http://google.com\",\"requestUUID\":\"requested UUID\"}";

        JSONObject expectedResponse = new JSONObject(response);
        sourcePointClient.setReuestedUUID("requested UUID");

        doAPICallWithAnswer(true, response);

        sourcePointClient.sendConsent(expectedResponse, onLoadComplete);

        ArgumentCaptor<JSONObject> captor = ArgumentCaptor.forClass(JSONObject.class);
        verify(onLoadComplete, times(1)).onSuccess(captor.capture());

        JSONObject actualResponse = captor.getValue();
        assertEquals(actualResponse.toString(), expectedResponse.toString());

        verify(onLoadComplete, never()).onFailure(any(ConsentLibException.class));
    }

    @Test
    public void sendConsentFailure () throws Exception{
        String response = "{\"messageUrl\":\"http://google.com\",\"requestUUID\":\"requested UUID\"}";

        JSONObject expectedResponse = new JSONObject(response);
        sourcePointClient.setReuestedUUID("requested UUID");

        doAPICallWithAnswer(false, response);

        sourcePointClient.sendConsent(expectedResponse, onLoadComplete);

        verify(onLoadComplete, times(1)).onFailure(any(ConsentLibException.class));
        verify(onLoadComplete, never()).onSuccess(response);
    }
}
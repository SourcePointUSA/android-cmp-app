package com.sourcepoint.gdpr_cmplibrary;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = Build.VERSION_CODES.O_MR1)
public class SourcePointClientTest {

    private OkHttpClient http = new OkHttpClient();
    private SourcePointClient sourcePointClientMock;
    private GDPRConsentLib.OnLoadComplete onLoadComplete;

    private MockWebServer webServerMock;

    private String consentUUID = "consentUUID";
    private String euConsent = "euConsent";
    private String meta = "meta";
    private boolean isNative = false;
    private NetworkInfo networkInfo;
    private ConnectivityManager connectivityManager;


    @Before
    public void setUp() throws IOException {
        onLoadComplete = mock(GDPRConsentLib.OnLoadComplete.class);
        int accountId = 123;
        int propertyId = 1234;
        String property = "example.com";
        String targettingParams = "targetingParams";
        String authId = "";
        boolean staging = false;
        boolean stagingCampaign = false;
        PropertyConfig propertyConfig = new PropertyConfig(accountId,propertyId,property,"123221");
        SourcePointClientConfig config = new SourcePointClientConfig(propertyConfig,stagingCampaign,staging,targettingParams,authId);

        networkInfo = mock(NetworkInfo.class);
        when(networkInfo.isConnectedOrConnecting()).thenReturn(true);
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_MOBILE);
        when(networkInfo.isRoaming()).thenReturn(true);
        connectivityManager = mock(ConnectivityManager.class);
        when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        sourcePointClientMock =  new SourcePointClient(http,config, connectivityManager);
        webServerMock = new MockWebServer();
        webServerMock.start();
    }

    @After
    public void finish() throws IOException {
        webServerMock.shutdown();
        reset(onLoadComplete);
        reset(networkInfo);
        reset(connectivityManager);
    }

    @Test
    public void noInternetErrorWhileGetMessge() {
        when(networkInfo.isConnectedOrConnecting()).thenReturn(false);
        webServerMock.enqueue(new MockResponse().setBody("{}").setResponseCode(200));
        sourcePointClientMock.baseMsgUrl = webServerMock.url("/").toString();

        try {
            sourcePointClientMock.getMessage(isNative, consentUUID, meta, euConsent, onLoadComplete);
            ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
            verify(onLoadComplete, timeout(1000)).onSuccess(captor.capture());
            String response = captor.getValue().toString();
            assertEquals("{}",response);
        } catch (ConsentLibException e) {
            assertEquals("The device is not connected to the internet.", e.consentLibErrorMessage);
        }
    }

    @Test
    public void getMessageSuccess() {
        String responseString = "https://notice.sp-prod.net?message_id=162961";
        webServerMock.enqueue(new MockResponse().setBody(responseString).setResponseCode(200));
        sourcePointClientMock.baseMsgUrl = webServerMock.url("/").toString();

        try {
            sourcePointClientMock.getMessage(isNative, consentUUID, meta, euConsent, onLoadComplete);
            ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
            verify(onLoadComplete, timeout(1000)).onSuccess(captor.capture());
            String response = captor.getValue().toString();
            assertEquals(responseString,response);
        } catch (ConsentLibException e) {
            assertEquals("The device is not connected to the internet.", e.consentLibErrorMessage);
        }
    }

    @Test
    public void getMessageFailure() {
        webServerMock.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST));
        sourcePointClientMock.baseMsgUrl = webServerMock.url("/").toString();

        try {
            sourcePointClientMock.getMessage(isNative ,consentUUID, meta , euConsent, onLoadComplete);
            ArgumentCaptor<ConsentLibException> captor = ArgumentCaptor.forClass(ConsentLibException.class);
            verify(onLoadComplete, timeout(1000)).onFailure(captor.capture());
            assertTrue(captor.getValue().consentLibErrorMessage.contains("Fail to get message from: "));

        } catch (ConsentLibException e) {
            assertEquals("The device is not connected to the internet.",e.consentLibErrorMessage);
        }
    }

    @Test
    public void noInternetErrorWhileSendConsent() {
        when(networkInfo.isConnectedOrConnecting()).thenReturn(false);
        webServerMock.enqueue(new MockResponse().setBody("{}").setResponseCode(200));
        sourcePointClientMock.baseSendConsentUrl = webServerMock.url("/").toString();

        try {
            sourcePointClientMock.sendConsent(mock(JSONObject.class), onLoadComplete);
            ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
            verify(onLoadComplete, timeout(1000)).onSuccess(captor.capture());
            String response = captor.getValue().toString();
            assertEquals("{}",response);
        } catch (ConsentLibException e) {
            assertEquals("The device is not connected to the internet.", e.consentLibErrorMessage);
        }
    }

    @Test
    public void errorWhileSendConsent() throws JSONException {
        webServerMock.enqueue(new MockResponse().setBody("{}").setResponseCode(200));
        sourcePointClientMock.baseSendConsentUrl = webServerMock.url("/").toString();
        JSONObject jsonMock = mock(JSONObject.class);
        doThrow(JSONException.class).when(jsonMock).put(anyString(),anyString());
        try {
            sourcePointClientMock.sendCustomConsents(jsonMock, onLoadComplete);
            ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
            verify(onLoadComplete, timeout(1000)).onSuccess(captor.capture());
            String response = captor.getValue().toString();
            assertEquals("{}",response);
        } catch (ConsentLibException e) {
            assertEquals("Error adding param requestUUID.", e.consentLibErrorMessage);
        }
    }

    @Test
    public void sendConsentSuccess() {
        String responseString = "A dummy response string";
        webServerMock.enqueue(new MockResponse().setBody(responseString).setResponseCode(200));
        sourcePointClientMock.baseSendConsentUrl = webServerMock.url("/").toString();

        try {
            sourcePointClientMock.sendConsent(mock(JSONObject.class), onLoadComplete);
            ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
            verify(onLoadComplete, timeout(1000)).onSuccess(captor.capture());
            String response = captor.getValue().toString();
            assertEquals(responseString,response);
        } catch (ConsentLibException e) {
            assertEquals("The device is not connected to the internet.", e.consentLibErrorMessage);
        }
    }

    @Test
    public void sendConsentFailure() {
        webServerMock.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST));
        sourcePointClientMock.baseSendConsentUrl = webServerMock.url("/").toString();

        try {
            sourcePointClientMock.sendConsent(mock(JSONObject.class), onLoadComplete);
            ArgumentCaptor<ConsentLibException> captor = ArgumentCaptor.forClass(ConsentLibException.class);
            verify(onLoadComplete, timeout(1000)).onFailure(captor.capture());
            assertTrue(captor.getValue().consentLibErrorMessage.contains("Fail to send consent to: "));
        } catch (ConsentLibException e) {
            assertEquals("The device is not connected to the internet.",e.consentLibErrorMessage);
        }
    }

    @Test
    public void errorWhileSendCustomConsent() throws JSONException {
        webServerMock.enqueue(new MockResponse().setBody("{}").setResponseCode(200));
        sourcePointClientMock.baseSendCustomConsentsUrl = webServerMock.url("/").toString();
        JSONObject jsonMock = mock(JSONObject.class);
        doThrow(JSONException.class).when(jsonMock).put(anyString(),anyString());
        try {
            sourcePointClientMock.sendConsent(jsonMock, onLoadComplete);
            ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
            verify(onLoadComplete, timeout(1000)).onSuccess(captor.capture());
            String response = captor.getValue().toString();
            assertEquals("{}",response);
        } catch (ConsentLibException e) {
            assertEquals("Error adding param requestUUID.", e.consentLibErrorMessage);
        }
    }

    @Test
    public void sendCustomConsentsSuccess()  {
        String responseString = "A dummy response string";
        webServerMock.enqueue(new MockResponse().setBody(responseString).setResponseCode(200));
        sourcePointClientMock.baseSendCustomConsentsUrl = webServerMock.url("/").toString();

        try {
            sourcePointClientMock.sendCustomConsents(mock(JSONObject.class), onLoadComplete);
            ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
            verify(onLoadComplete, timeout(1000)).onSuccess(captor.capture());
            String response = captor.getValue().toString();
            assertEquals(responseString, response);
        } catch (ConsentLibException e) {
            assertEquals("The device is not connected to the internet.", e.consentLibErrorMessage);
        }
    }

    @Test
    public void sendCustomConsentsFailure() {
        webServerMock.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST));
        sourcePointClientMock.baseSendCustomConsentsUrl = webServerMock.url("/").toString();

        try {
            sourcePointClientMock.sendCustomConsents(mock(JSONObject.class), onLoadComplete);
            ArgumentCaptor<ConsentLibException> captor = ArgumentCaptor.forClass(ConsentLibException.class);
            verify(onLoadComplete, timeout(1000)).onFailure(captor.capture());
            assertTrue(captor.getValue().consentLibErrorMessage.contains("Fail to send consent to: "));

        } catch (ConsentLibException e) {
            assertEquals("The device is not connected to the internet.",e.consentLibErrorMessage);
        }
    }
}
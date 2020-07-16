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

    private SourcePointClient sourcePointClient;
    private GDPRConsentLib.OnLoadComplete onLoadComplete;

    private MockWebServer webServerMock;

    private static final String consentUUID = "consentUUID";
    private static final String euConsent = "euConsent";
    private static final String meta = "meta";
    private static final boolean isNative = false;
    private static NetworkInfo networkInfo;
    private static ConnectivityManager connectivityManager;
    private static long timeout = 1000;


    @Before
    public void setUp() throws IOException {
        OkHttpClient http = new OkHttpClient();

        onLoadComplete = mock(GDPRConsentLib.OnLoadComplete.class);

        networkInfo = getNetworkInfo();
        connectivityManager = getConnectivityManager();

        sourcePointClient =  new SourcePointClient(http, getSourcePointClientConfig(), connectivityManager);
        webServerMock = new MockWebServer();
        webServerMock.start();
    }

    private SourcePointClientConfig getSourcePointClientConfig(){

        PropertyConfig propertyConfig = new PropertyConfig(123, 1234, "example.com","123221");
        SourcePointClientConfig config = new SourcePointClientConfig(propertyConfig, false, false,"targetingParams","authId");
        return config;
    }

    private ConnectivityManager getConnectivityManager(){
        ConnectivityManager connectivityManager = mock(ConnectivityManager.class);
        when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        return connectivityManager;
    }

    private NetworkInfo getNetworkInfo(){
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(networkInfo.isConnectedOrConnecting()).thenReturn(true);
        when(networkInfo.getType()).thenReturn(ConnectivityManager.TYPE_MOBILE);
        when(networkInfo.isRoaming()).thenReturn(true);
        return networkInfo;
    }

    @After
    public void finish() throws IOException {
        webServerMock.shutdown();
        reset(onLoadComplete);
        reset(networkInfo);
        reset(connectivityManager);
    }

    @Test
    public void noInternetErrorWhileGetMessage() {
        when(networkInfo.isConnectedOrConnecting()).thenReturn(false);
        webServerMock.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK));
        sourcePointClient.baseMsgUrl = webServerMock.url("/").toString();

        try {
            sourcePointClient.getMessage(isNative, consentUUID, meta, euConsent, onLoadComplete);
        } catch (ConsentLibException e) {
            assertEquals(ConsentLibException.NoInternetConnectionException.description, e.consentLibErrorMessage);
        }
    }

    @Test
    public void getMessageSuccess() throws ConsentLibException {
        String responseString = "https://notice.sp-prod.net?message_id=162961";
        webServerMock.enqueue(new MockResponse().setBody(responseString).setResponseCode(HttpURLConnection.HTTP_OK));
        sourcePointClient.baseMsgUrl = webServerMock.url("/").toString();

        sourcePointClient.getMessage(isNative, consentUUID, meta, euConsent, onLoadComplete);
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(onLoadComplete, timeout(timeout)).onSuccess(captor.capture());
        String response = captor.getValue().toString();
        assertEquals(responseString, response);
    }

    @Test
    public void getMessageFailure() throws ConsentLibException {
        String errorMessage = "Fail to get message from: ";
        webServerMock.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST));
        sourcePointClient.baseMsgUrl = webServerMock.url("/").toString();

        sourcePointClient.getMessage(isNative ,consentUUID, meta , euConsent, onLoadComplete);
        ArgumentCaptor<ConsentLibException> captor = ArgumentCaptor.forClass(ConsentLibException.class);
        verify(onLoadComplete, timeout(timeout)).onFailure(captor.capture());
        assertTrue(captor.getValue().consentLibErrorMessage.contains(errorMessage));
}

    @Test
    public void noInternetErrorWhileSendConsent() {
        when(networkInfo.isConnectedOrConnecting()).thenReturn(false);
        webServerMock.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK));
        sourcePointClient.baseSendConsentUrl = webServerMock.url("/").toString();

        try {
            sourcePointClient.sendConsent(mock(JSONObject.class), onLoadComplete);
        } catch (ConsentLibException e) {
            assertEquals(ConsentLibException.NoInternetConnectionException.description, e.consentLibErrorMessage);
        }
    }

    @Test
    public void errorWhileSendConsent() throws JSONException {
        String errorMessage = "Error adding param requestUUID.";
        webServerMock.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK));
        sourcePointClient.baseSendConsentUrl = webServerMock.url("/").toString();
        JSONObject jsonMock = mock(JSONObject.class);
        doThrow(JSONException.class).when(jsonMock).put(anyString(),anyString());
        try {
            sourcePointClient.sendCustomConsents(jsonMock, onLoadComplete);
        } catch (ConsentLibException e) {
            assertEquals(errorMessage, e.consentLibErrorMessage);
        }
    }

    @Test
    public void sendConsentSuccess() throws ConsentLibException {
        String responseString = "A dummy response string";
        webServerMock.enqueue(new MockResponse().setBody(responseString).setResponseCode(HttpURLConnection.HTTP_OK));
        sourcePointClient.baseSendConsentUrl = webServerMock.url("/").toString();

        sourcePointClient.sendConsent(mock(JSONObject.class), onLoadComplete);
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(onLoadComplete, timeout(timeout)).onSuccess(captor.capture());
        String response = captor.getValue().toString();
        assertEquals(responseString,response);
    }

    @Test
    public void sendConsentFailure() throws ConsentLibException {
        String errorMessage = "Fail to send consent to: ";
        webServerMock.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST));
        sourcePointClient.baseSendConsentUrl = webServerMock.url("/").toString();

        sourcePointClient.sendConsent(mock(JSONObject.class), onLoadComplete);
        ArgumentCaptor<ConsentLibException> captor = ArgumentCaptor.forClass(ConsentLibException.class);
        verify(onLoadComplete, timeout(timeout)).onFailure(captor.capture());
        assertTrue(captor.getValue().consentLibErrorMessage.contains(errorMessage));
    }

    @Test
    public void noInternetErrorWhileSendCustomConsents() {
        when(networkInfo.isConnectedOrConnecting()).thenReturn(false);
        webServerMock.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK));
        sourcePointClient.baseSendCustomConsentsUrl = webServerMock.url("/").toString();

        try {
            sourcePointClient.sendCustomConsents(mock(JSONObject.class), onLoadComplete);
        } catch (ConsentLibException e) {
            assertEquals(ConsentLibException.NoInternetConnectionException.description, e.consentLibErrorMessage);
        }
    }


    @Test
    public void errorWhileSendCustomConsent() throws JSONException {
        String errorMessage = "Error adding param requestUUID.";
        webServerMock.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK));
        sourcePointClient.baseSendCustomConsentsUrl = webServerMock.url("/").toString();
        JSONObject jsonMock = mock(JSONObject.class);
        doThrow(JSONException.class).when(jsonMock).put(anyString(),anyString());
        try {
            sourcePointClient.sendConsent(jsonMock, onLoadComplete);
        } catch (ConsentLibException e) {
            assertEquals(errorMessage, e.consentLibErrorMessage);
        }
    }

    @Test
    public void sendCustomConsentsSuccess() throws ConsentLibException {
        String responseString = "A dummy response string";
        webServerMock.enqueue(new MockResponse().setBody(responseString).setResponseCode(HttpURLConnection.HTTP_OK));
        sourcePointClient.baseSendCustomConsentsUrl = webServerMock.url("/").toString();

        sourcePointClient.sendCustomConsents(mock(JSONObject.class), onLoadComplete);
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(onLoadComplete, timeout(timeout)).onSuccess(captor.capture());
        String response = captor.getValue().toString();
        assertEquals(responseString, response);
    }

    @Test
    public void sendCustomConsentsFailure() throws ConsentLibException {
        String errorMessage ="Fail to send consent to: " ;
        webServerMock.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST));
        sourcePointClient.baseSendCustomConsentsUrl = webServerMock.url("/").toString();

        sourcePointClient.sendCustomConsents(mock(JSONObject.class), onLoadComplete);
        ArgumentCaptor<ConsentLibException> captor = ArgumentCaptor.forClass(ConsentLibException.class);
        verify(onLoadComplete, timeout(timeout)).onFailure(captor.capture());
        assertTrue(captor.getValue().consentLibErrorMessage.contains(errorMessage));
    }
}
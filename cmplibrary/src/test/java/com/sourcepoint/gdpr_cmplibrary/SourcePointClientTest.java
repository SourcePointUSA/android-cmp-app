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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = Build.VERSION_CODES.O_MR1)
public class SourcePointClientTest {

    private SourcePointClient sourcePointClient;
    private GDPRConsentLib.OnLoadComplete onLoadComplete;

    private static final String consentUUID = "consentUUID";
    private static final String euConsent = "euConsent";
    private static final String meta = "meta";
    private static final String authId = "authId";
    private static final boolean isNative = false;
    private static NetworkInfo networkInfo;
    private static ConnectivityManager connectivityManager;

    OkHttpClient http;
    Call remoteCall;

    @Before
    public void setUp() {
        remoteCall = mock(Call.class);
        http = getOkHttpClientMock();
        onLoadComplete = mock(GDPRConsentLib.OnLoadComplete.class);
        networkInfo = getNetworkInfo();
        connectivityManager = getConnectivityManager();
        sourcePointClient =  new SourcePointClient(http, getSourcePointClientConfig(), connectivityManager, new MockLogger());
    }

    private OkHttpClient getOkHttpClientMock(){
        OkHttpClient okhttp = mock(OkHttpClient.class);
        when(okhttp.newCall(any())).thenReturn(remoteCall);
        return okhttp;
    }

    private SourcePointClientConfig getSourcePointClientConfig(){

        PropertyConfig propertyConfig = new PropertyConfig(123, 1234, "example.com","123221");
        SourcePointClientConfig config = new SourcePointClientConfig(propertyConfig, false, "targetingParams");
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
    public void finish() {
        reset(onLoadComplete);
        reset(networkInfo);
        reset(connectivityManager);
        reset(http);
        reset(remoteCall);
    }

    private void doAPICallWithAnswer(final boolean isSuccess , final int responseCode, final String responseString){
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback callback = invocation.getArgument(0);
                final Response response = new Response.Builder()
                        .request(new Request.Builder().url("http://url.com").build())
                        .protocol(Protocol.HTTP_1_1)
                        .code(responseCode).message("").body(
                                ResponseBody.create(
                                        MediaType.parse(""),
                                        responseString
                                ))
                        .build();
                if (isSuccess)
                    callback.onResponse(remoteCall , response);
                else callback.onFailure(remoteCall , new IOException(new ConsentLibException("A failure while API call ")));
                return null;
            }
        }).when(remoteCall).enqueue(any(Callback.class));
    }

    @Test
    public void noInternetErrorWhileGetMessage() {
        when(networkInfo.isConnectedOrConnecting()).thenReturn(false);
        try {
            sourcePointClient.getMessage(isNative, consentUUID, meta, euConsent, authId, onLoadComplete);
        } catch (ConsentLibException e) {
            assertEquals(ConsentLibException.NoInternetConnectionException.description, e.consentLibErrorMessage);
        }
    }

    @Test
    public void getMessageSuccess() throws ConsentLibException {
        String responseString = "foo_string";

        doAPICallWithAnswer(true , HttpURLConnection.HTTP_OK, responseString);

        sourcePointClient.getMessage(isNative, consentUUID, meta, euConsent, authId, onLoadComplete);
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(onLoadComplete).onSuccess(captor.capture());
        String response = captor.getValue().toString();
        assertEquals(responseString, response);
    }

    @Test
    public void getMessageRequest() throws ConsentLibException {
        String responseString = "foo_string";

        doAPICallWithAnswer(true , HttpURLConnection.HTTP_OK, responseString);

        sourcePointClient.getMessage(isNative, consentUUID, meta, euConsent, authId, onLoadComplete);
        ArgumentCaptor<Request> requestArgumentCaptor = ArgumentCaptor.forClass(Request.class);
        verify(http).newCall(requestArgumentCaptor.capture());
        Request request = requestArgumentCaptor.getValue();
        assertEquals(sourcePointClient.messageUrl(isNative),request.url().toString());
    }

    @Test
    public void getMessageFailure() throws ConsentLibException {
        String errorMessage = "Fail to get message from: ";

        doAPICallWithAnswer(true , HttpURLConnection.HTTP_BAD_REQUEST, errorMessage);

        sourcePointClient.getMessage(isNative ,consentUUID, meta , euConsent, authId, onLoadComplete);
        ArgumentCaptor<ConsentLibException> captor = ArgumentCaptor.forClass(ConsentLibException.class);
        verify(onLoadComplete).onFailure(captor.capture());
        assertTrue(captor.getValue().consentLibErrorMessage.contains(errorMessage));
}

    @Test
    public void getMessageOnFailureCallBack() throws ConsentLibException {
        String errorMessage = "Fail to get message from: ";

        doAPICallWithAnswer(false , HttpURLConnection.HTTP_BAD_REQUEST, errorMessage);

        sourcePointClient.getMessage(isNative ,consentUUID, meta , euConsent, authId, onLoadComplete);
        ArgumentCaptor<ConsentLibException> captor = ArgumentCaptor.forClass(ConsentLibException.class);
        verify(onLoadComplete).onFailure(captor.capture());
        assertTrue(captor.getValue().consentLibErrorMessage.contains(errorMessage));
    }

    @Test
    public void noInternetErrorWhileSendConsent() {
        when(networkInfo.isConnectedOrConnecting()).thenReturn(false);

        try {
            sourcePointClient.sendConsent(mock(JSONObject.class), onLoadComplete);
        } catch (ConsentLibException e) {
            assertEquals(ConsentLibException.NoInternetConnectionException.description, e.consentLibErrorMessage);
        }
    }

    @Test
    public void errorWhileSendConsent() throws JSONException {
        String errorMessage = "Error adding param requestUUID.";
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
        String responseString = "foo_string";
        doAPICallWithAnswer(true, HttpURLConnection.HTTP_OK, responseString);

        sourcePointClient.sendConsent(mock(JSONObject.class), onLoadComplete);
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(onLoadComplete).onSuccess(captor.capture());
        String response = captor.getValue().toString();
        assertEquals(responseString,response);
    }

    @Test
    public void sendConsentRequest() throws ConsentLibException {
        String responseString = "foo_string";
        doAPICallWithAnswer(true, HttpURLConnection.HTTP_OK, responseString);

        sourcePointClient.sendConsent(mock(JSONObject.class), onLoadComplete);
        ArgumentCaptor<Request> requestArgumentCaptor = ArgumentCaptor.forClass(Request.class);
        verify(http).newCall(requestArgumentCaptor.capture());
        assertEquals(sourcePointClient.consentUrl(),requestArgumentCaptor.getValue().url().toString());
    }

    @Test
    public void sendConsentFailure() throws ConsentLibException {
        String errorMessage = "Fail to send consent to: ";
        doAPICallWithAnswer(true, HttpURLConnection.HTTP_BAD_REQUEST, errorMessage);

        sourcePointClient.sendConsent(mock(JSONObject.class), onLoadComplete);
        ArgumentCaptor<ConsentLibException> captor = ArgumentCaptor.forClass(ConsentLibException.class);
        verify(onLoadComplete).onFailure(captor.capture());
        assertTrue(captor.getValue().consentLibErrorMessage.contains(errorMessage));
    }

    @Test
    public void sendConsentOnFailureCallback() throws ConsentLibException {
        String errorMessage = "Fail to send consent to: ";
        doAPICallWithAnswer(false, HttpURLConnection.HTTP_BAD_REQUEST, errorMessage);

        sourcePointClient.sendConsent(mock(JSONObject.class), onLoadComplete);
        ArgumentCaptor<ConsentLibException> captor = ArgumentCaptor.forClass(ConsentLibException.class);
        verify(onLoadComplete).onFailure(captor.capture());
        assertTrue(captor.getValue().consentLibErrorMessage.contains(errorMessage));
    }

    @Test
    public void noInternetErrorWhileSendCustomConsents() {
        when(networkInfo.isConnectedOrConnecting()).thenReturn(false);
        try {
            sourcePointClient.sendCustomConsents(mock(JSONObject.class), onLoadComplete);
        } catch (ConsentLibException e) {
            assertEquals(ConsentLibException.NoInternetConnectionException.description, e.consentLibErrorMessage);
        }
    }


    @Test
    public void errorWhileSendCustomConsent() throws JSONException {
        String errorMessage = "Error adding param requestUUID.";
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
        String responseString = "foo_string";
        doAPICallWithAnswer(true, HttpURLConnection.HTTP_OK, responseString);

        sourcePointClient.sendCustomConsents(mock(JSONObject.class), onLoadComplete);
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(onLoadComplete).onSuccess(captor.capture());
        String response = captor.getValue().toString();
        assertEquals(responseString, response);
    }

    @Test
    public void sendCustomConsentsRequest() throws ConsentLibException {
        String responseString = "foo_string";
        doAPICallWithAnswer(true, HttpURLConnection.HTTP_OK, responseString);

        sourcePointClient.sendCustomConsents(mock(JSONObject.class), onLoadComplete);
        ArgumentCaptor<Request> requestArgumentCaptor = ArgumentCaptor.forClass(Request.class);
        verify(http).newCall(requestArgumentCaptor.capture());
        assertEquals(sourcePointClient.customConsentsUrl(), requestArgumentCaptor.getValue().url().toString());
    }

    @Test
    public void sendCustomConsentsFailure() throws ConsentLibException {
        String errorMessage ="Fail to send consent to: ";
        doAPICallWithAnswer(true, HttpURLConnection.HTTP_BAD_REQUEST, errorMessage);

        sourcePointClient.sendCustomConsents(mock(JSONObject.class), onLoadComplete);
        ArgumentCaptor<ConsentLibException> captor = ArgumentCaptor.forClass(ConsentLibException.class);
        verify(onLoadComplete).onFailure(captor.capture());
        assertTrue(captor.getValue().consentLibErrorMessage.contains(errorMessage));
    }

    @Test
    public void sendCustomConsentsOnFailureCallback() throws ConsentLibException {
        String errorMessage ="Fail to send consent to: " ;
        doAPICallWithAnswer(false, HttpURLConnection.HTTP_BAD_REQUEST, errorMessage);

        sourcePointClient.sendCustomConsents(mock(JSONObject.class), onLoadComplete);
        ArgumentCaptor<ConsentLibException> captor = ArgumentCaptor.forClass(ConsentLibException.class);
        verify(onLoadComplete).onFailure(captor.capture());
        assertTrue(captor.getValue().consentLibErrorMessage.contains(errorMessage));
    }
}
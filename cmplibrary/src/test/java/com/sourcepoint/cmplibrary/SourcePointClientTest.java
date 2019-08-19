package com.sourcepoint.cmplibrary;

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

import java.util.HashSet;

import cz.msebera.android.httpclient.Header;

import static android.os.Build.VERSION_CODES.O;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
    private ConsentLib.OnLoadComplete onLoadComplete;

    @Before
    public void setSourcePointClient() throws ConsentLibException.BuildException {
        http = mock(AsyncHttpClient.class);
        onLoadComplete = mock(ConsentLib.OnLoadComplete.class);

        sourcePointClient = new SourcePointClientBuilder(123, "example.com", true).build();
        sourcePointClient.setHttpDummy(http);
    }

    JSONObject jsonObject = null;

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
    }

    @Test
    public void getSiteIDSuccess() {
        String response = "{\"site_id\":\"http://google.com\"}";
        doAPICallWithAnswer(true,response);

        sourcePointClient.getSiteID(onLoadComplete);

        verify(onLoadComplete, times(1)).onSuccess(eq("http://google.com"));
        verify(onLoadComplete, never()).onFailure(any(ConsentLibException.class));
    }

    @Test
    public void getSiteIDFailure(){
        String response = "{\"site_id\":\"http://google.com\"}";
        doAPICallWithAnswer(false,response);

        sourcePointClient.getSiteID(onLoadComplete);

        verify(onLoadComplete, never()).onSuccess(any());
        verify(onLoadComplete, times(1)).onFailure(any(ConsentLibException.class));
        //reset(onLoadComplete);
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
    public void getCustomConsentsSuccess()  {
        String response = "{\"consentedPurposes\":[{\"_id\":\"5d287f273e5ba6241423f58d\",\"name\":\"Personalisation\"},{\"_id\":\"5d287f273e5ba6241423f58e\",\"name\":\"Essential Cookies\"}],\"consentedVendors\":[{\"_id\":\"5b07836aecb3fe2955eba270\",\"name\":\"Google Ad Manager\",\"vendorType\":\"CUSTOM\"}]}";
        doAPICallWithAnswer(true , response);

        String[] anyString = {"consnetUUID", "euConsent", "siteId"};
        sourcePointClient.getCustomConsents("consentUUID", "euConsent", "siteId", anyString, onLoadComplete);

        ArgumentCaptor<HashSet<Consent>> captor = ArgumentCaptor.forClass(HashSet.class);

        verify(onLoadComplete, times(1)).onSuccess(captor.capture());
        verify(onLoadComplete, never()).onFailure(any(ConsentLibException.class));
    }

    @Test
    public void getgetCustomConsentsFailure(){
        String response = "{\"consentedPurposes\":[{\"_id\":\"5d287f273e5ba6241423f58d\",\"name\":\"Personalisation\"},{\"_id\":\"5d287f273e5ba6241423f58e\",\"name\":\"Essential Cookies\"}],\"consentedVendors\":[{\"_id\":\"5b07836aecb3fe2955eba270\",\"name\":\"Google Ad Manager\",\"vendorType\":\"CUSTOM\"}]}";
        doAPICallWithAnswer(false ,response);

        String[] anyString = {"consnetUUID", "euConsent", "siteId"};
        sourcePointClient.getCustomConsents("consentUUID", "euConsent", "siteId", anyString, onLoadComplete);

        verify(onLoadComplete, never()).onSuccess(any());
        verify(onLoadComplete, times(1)).onFailure(any(ConsentLibException.class));
    }

    @After
    public void resetOnLoadComplete(){
        reset(onLoadComplete);
    }
}
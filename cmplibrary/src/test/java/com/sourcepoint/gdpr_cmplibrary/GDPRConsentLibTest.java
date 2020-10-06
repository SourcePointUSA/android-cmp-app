package com.sourcepoint.gdpr_cmplibrary;

import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class GDPRConsentLibTest {

    private GDPRConsentLib lib;

    private ConsentAction consentActionMock = new ConsentAction(ActionTypes.ACCEPT_ALL.code, "foo", null, "null",false, new JSONObject());
    private ConsentAction consentActionMockPMDismiss = new ConsentAction(ActionTypes.PM_DISMISS.code, "foo", null, null, false, new JSONObject());
    private ConsentAction consentActionMockMsgCancel = new ConsentAction(ActionTypes.MSG_CANCEL.code, "foo", null, null,false, new JSONObject());
    private ConsentAction consentActionMockShowOptions = new ConsentAction(ActionTypes.SHOW_OPTIONS.code, "foo", "foo_pmId", "foo_pmTab",false, new JSONObject());

    @Mock
    Activity activityMock;

    @Mock
    StoreClient storeClientMock;

    @Mock
    SourcePointClient sourcePointClientMock;

    @Mock
    ConsentWebView webViewMock;

    @Mock
    CountDownTimer timerMock;

    @Mock
    UIThreadHandler uiThreadHandlerMock;

    Boolean requestFromPM = false;

    @Captor
    ArgumentCaptor<Runnable> lambdaCaptor;

    private ConsentLibBuilder builderMock(int accountId, String propertyName, int propertyId, String pmId, Activity activity){
        return new ConsentLibBuilder(accountId, propertyName, propertyId, pmId, activity){
            @Override
            public SourcePointClient getSourcePointClient(){
                return sourcePointClientMock;
            }
            @Override
            public StoreClient getStoreClient(){
                return storeClientMock;
            }
            @Override
            public CountDownTimer getTimer(Runnable r){
                return timerMock;
            }
            @Override
            public UIThreadHandler getUIThreadHandler(){
                return uiThreadHandlerMock;
            }
        };
    }

    private ConsentLibBuilder builderMock(){
        return builderMock(123, "example.com", 321, "abcd", activityMock);
    }

    private void setStoreClientMock() throws ConsentLibException {
        doReturn(null).when(storeClientMock).getAuthId();
        doReturn("").when(storeClientMock).getConsentString();
        doReturn("").when(storeClientMock).getConsentUUID();
        doReturn("").when(storeClientMock).getMetaData();
        doReturn(new GDPRUserConsent()).when(storeClientMock).getUserConsent();
        doNothing().when(storeClientMock).setAuthId(anyString());
        doNothing().when(storeClientMock).setConsentString(anyString());
        doNothing().when(storeClientMock).setConsentUuid(anyString());
        doNothing().when(storeClientMock).setMetaData(anyString());
        doNothing().when(storeClientMock).setTCData(any());
        doNothing().when(storeClientMock).clearAllData();
        doNothing().when(storeClientMock).clearConsentData();
        doNothing().when(storeClientMock).clearInternalData();
    }

    private void setTimerMock(){
        doReturn(timerMock).when(timerMock).start();
        doNothing().when(timerMock).cancel();
    }

    private void setSourcePointClientMock() throws ConsentLibException {
        doNothing().when(sourcePointClientMock).sendConsent(any(JSONObject.class), any(GDPRConsentLib.OnLoadComplete.class));
        doNothing().when(sourcePointClientMock).sendCustomConsents(any(JSONObject.class), any(GDPRConsentLib.OnLoadComplete.class));
        doNothing().when(sourcePointClientMock).getMessage(anyBoolean(), anyString(), anyString(), anyString(), any(GDPRConsentLib.OnLoadComplete.class));
    }


    @Before
    public void setUp() throws ConsentLibException {
        initMocks(this);
        setStoreClientMock();
        setTimerMock();
        setSourcePointClientMock();
        lambdaCaptor = ArgumentCaptor.forClass(Runnable.class);
        lib = spy(new GDPRConsentLib(builderMock()){
            @Override
            ConsentWebView buildWebView(Context context){
                return webViewMock;
            }
        });
        doNothing().when(webViewMock).loadConsentUIFromUrl(any());
        doReturn(webViewMock).when(lib).buildWebView(any());
    }

    @Test
    public void run_followed_by_show_view(){
        lib.run();
        verify(timerMock).start();
        lib.showView(lib.webView , false);
        verify(timerMock , atLeast(1)).cancel();
    }

    @Test
    public void run_followed_by_consentFinished() throws JSONException, ConsentLibException {
        lib.run();
        verify(timerMock).start();
        lib.consentFinished();
        verify(timerMock).cancel();
        verify(lib).storeData();
    }

    @Test
    public void run_followed_by_onErrorTask(){
        lib.run();
        verify(timerMock).start();
        lib.onErrorTask(new ConsentLibException("ooops, I have a bad feeling about this..."));
        verify(timerMock, atLeast(1)).cancel();
    }

    @Test
    public void clearAllData() {
        lib.clearAllData();
        verify(storeClientMock).clearAllData();
    }

    @Test
    public void closeAllViews() {
        lib.closeAllViews(requestFromPM);
        verify(lib).closeView(lib.webView, requestFromPM);
    }

    @Test
    public void onAction(){
        lib.onAction(consentActionMock);
        verify(lib).onDefaultAction(any());

        lib.onAction(consentActionMockPMDismiss);
        verify(lib).onPmDismiss(requestFromPM);

        lib.onAction(consentActionMockMsgCancel);
        verify(lib).onMsgCancel(requestFromPM);

        lib.onAction(consentActionMockShowOptions);
        verify(lib).onShowOptions(consentActionMockShowOptions);
    }

    @Test
    public void onShowOptions() throws ConsentLibException {
        lib.onShowOptions(consentActionMockShowOptions);
        verify(lib.uiThreadHandler).postIfEnabled(lambdaCaptor.capture());
        lambdaCaptor.getValue().run();
        verify(lib.webView).loadConsentUIFromUrl(lib.pmUrl("foo_pmId","foo_pmTab"));
    }

    @Test
    public void customConsentTo() throws ConsentLibException, JSONException {
        ArgumentCaptor<JSONObject> captor = ArgumentCaptor.forClass(JSONObject.class);
        lib.customConsentTo(
                new ArrayList(Arrays.asList("foo_vendor")),
                new ArrayList(Arrays.asList("foo_category")),
                new ArrayList(Arrays.asList("foo_legIntCategory"))
        );
        verify(sourcePointClientMock).sendCustomConsents(captor.capture(), any());
        JSONObject requestParams = captor.getValue();
        assertEquals("foo_vendor", requestParams.getJSONArray("vendors").get(0));
        assertEquals("foo_category", requestParams.getJSONArray("categories").get(0));
        assertEquals("foo_legIntCategory", requestParams.getJSONArray("legIntCategories").get(0));
    }
}
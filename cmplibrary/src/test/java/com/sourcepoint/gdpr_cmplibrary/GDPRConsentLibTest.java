package com.sourcepoint.gdpr_cmplibrary;

import android.app.Activity;
import android.os.CountDownTimer;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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

    private ConsentAction consentActionMock = new ConsentAction(ActionTypes.ACCEPT_ALL.code, "foo", false, new JSONObject());
    private ConsentAction consentActionMockPMDismiss = new ConsentAction(ActionTypes.PM_DISMISS.code, "foo", false, new JSONObject());
    private ConsentAction consentActionMockMsgCancel = new ConsentAction(ActionTypes.MSG_CANCEL.code, "foo", false, new JSONObject());
    private ConsentAction consentActionMockShowOptions = new ConsentAction(ActionTypes.SHOW_OPTIONS.code, "foo", false, new JSONObject());

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
        };
    }

    private ConsentLibBuilder builderMock(){
        return builderMock(123, "example.com", 321, "abcd", activityMock);
    }

    private void setStoreClientMock(){
        doReturn(null).when(storeClientMock).getAuthId();
        doReturn("").when(storeClientMock).getConsentString();
        doReturn("").when(storeClientMock).getConsentUUID();
        doReturn("").when(storeClientMock).getMetaData();
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

    @Before
    public void setUp() throws ConsentLibException {
        initMocks(this);
        setStoreClientMock();
        setTimerMock();
        lambdaCaptor = ArgumentCaptor.forClass(Runnable.class);
        lib = spy(builderMock().build());
        doNothing().when(webViewMock).loadConsentUIFromUrl(any());
        doReturn(webViewMock).when(lib).buildWebView();
    }

    @Test
    public void run_followed_by_show_view(){
        lib.run();
        verify(timerMock).start();
        lib.showView(lib.webView);
        verify(timerMock , atLeast(1)).cancel();
    }

    @Test
    public void run_followed_by_consentFinished(){
        lib.run();
        verify(timerMock).start();
        lib.consentFinished();
        verify(timerMock, atLeast(1)).cancel();
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
        lib.closeAllViews();
        verify(lib).closeView(lib.webView);
    }

    @Test
    public void onAction(){
        lib.onAction(consentActionMock);
        verify(lib).onDefaultAction(any());

        lib.onAction(consentActionMockPMDismiss);
        verify(lib).onPmDismiss();

        lib.onAction(consentActionMockMsgCancel);
        verify(lib).onMsgCancel();

        lib.onAction(consentActionMockShowOptions);
        verify(lib).onShowOptions();
    }

    @Test
    public void onShowOptions() throws ConsentLibException {
        lib.onShowOptions();
        verify(lib.activity).runOnUiThread(lambdaCaptor.capture());
        lambdaCaptor.getValue().run();
        verify(lib.webView).loadConsentUIFromUrl(lib.pmUrl());
    }

    @Test
    public void onDefaultAction() {
        lib.onDefaultAction(consentActionMock);
        verify(lib, atLeast(1)).closeView(any());
    }

    @Test
    public void storeData(){
        lib.storeData();
        verify(storeClientMock).setConsentUuid(lib.consentUUID);
        verify(storeClientMock).setMetaData(lib.metaData);
        verify(storeClientMock).setTCData(lib.userConsent.TCData);
        verify(storeClientMock).setConsentString(lib.euConsent);
    }

    @Test
    public void pmUrl() {
        GDPRConsentLib lib = builderMock(1, "propertyName", 1, "pmId", activityMock).build();
        lib.consentUUID = null;
        assertEquals(lib.pmUrl(), "https://notice.sp-prod.net/privacy-manager/index.html?site_id=1&message_id=pmId");
        lib.consentUUID = "ExampleUUID";
        assertEquals(lib.pmUrl(), "https://notice.sp-prod.net/privacy-manager/index.html?consentUUID=ExampleUUID&site_id=1&message_id=pmId");
    }
}

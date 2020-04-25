package com.sourcepoint.gdpr_cmplibrary;

import android.app.Activity;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class GDPRConsentLibTest {

    private GDPRConsentLib lib;

    private ConsentWebView view;

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

    private ConsentLibBuilder builderMock(){
        ConsentLibBuilder consentLibBuilder = new ConsentLibBuilder(123, "example.com", 321, "abcd", activityMock){
            @Override
            public void setSourcePointClient(){
                sourcePointClient = sourcePointClientMock;
            }
            public void setStoreClient(){
                storeClient = storeClientMock;
            }
        };
        return consentLibBuilder;
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


    @Before
    public void setUp() {
        initMocks(this);
        setStoreClientMock();
        lib = spy(builderMock().build());
        doNothing().when(webViewMock).loadConsentUIFromUrl(any());
        doReturn(webViewMock).when(lib).buildWebView();
    }

    @After
    public void tearDown() {
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
    public void onShowOptions(){
        lib.onShowOptions();
        verify(lib.webView).loadConsentUIFromUrl(lib.pmUrl());
    }

    @Test
    public void onDefaultAction() throws ConsentLibException {
        lib.onDefaultAction(consentActionMock);
        verify(lib).closeView(any());
        verify(sourcePointClientMock).sendConsent(any(), any());
    }

    @Test
    public void storeData(){
        lib.storeData();
        verify(storeClientMock).setConsentUuid(lib.consentUUID);
        verify(storeClientMock).setMetaData(lib.metaData);
        verify(storeClientMock).setTCData(lib.userConsent.TCData);
        verify(storeClientMock).setConsentString(lib.euConsent);
    }

}
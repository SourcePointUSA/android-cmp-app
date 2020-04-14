package com.sourcepoint.gdpr_cmplibrary;

import android.app.Activity;
import android.content.SharedPreferences;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class GDPRConsentLibTest {

    private GDPRConsentLib spyLib;

    @Mock
    Activity activityMock;

    @Mock
    StoreClient storeClientMock;

    @Mock
    SourcePointClient sourcePointClientMock;

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
        spyLib = spy(builderMock().build());

    }

    @After
    public void tearDown() {
    }

    @Test
    public void clearAllData() {
        spyLib.clearAllData();
        verify(storeClientMock).clearAllData();
    }

    @Test
    public void onAction_MSG_ACCEPT() {
        spyLib.onAction(ActionTypes.ACCEPT_ALL, 1);

        verify(spyLib, times(1)).onMsgAccepted(1);

    }


    @Test
    public void onAction_MSG_SHOW_OPTIONS() {
        spyLib.onAction(ActionTypes.SHOW_OPTIONS, 1);

        verify(spyLib, times(1)).onMsgShowOptions();
    }

    @Test
    public void onAction_MSG_CANCEL() {
        spyLib.onAction(ActionTypes.MSG_CANCEL , 1);

        verify(spyLib, times(1)).onMsgCancel(1);
    }

    @Test
    public void onAction_MSG_REJECT() {
        spyLib.onAction(ActionTypes.REJECT_ALL, 1);

        verify(spyLib, times(1)).onMsgRejected(1);
    }

    @Test
    public void onAction_PM_DISMISS() {
        spyLib.onAction(ActionTypes.PM_DISMISS , 1);

        verify(spyLib, times(1)).onPmDismiss();
    }

    @Test
    public void onMsgAccepted() {
        spyLib.onMsgAccepted(1);
        verify(spyLib,times(1)).closeAllViews();
        verify(spyLib,times(1)).sendConsent(ActionTypes.ACCEPT_ALL, 1);

    }

    @Test
    public void onMsgRejected() {
        spyLib.onMsgRejected(1);
        verify(spyLib ,times(1)).closeAllViews();
        verify(spyLib ,times(1)).sendConsent(ActionTypes.REJECT_ALL, 1);
    }


    @Test
    public void closeAllViews() {
        spyLib.closeAllViews();
        verify(spyLib ,times(1)).closeView(spyLib.webView);
    }

}
package com.sourcepoint.gdpr_cmplibrary;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class GDPRConsentLibTest {

    private GDPRConsentLib gdprConsentLib;
    private GDPRConsentLib spyLib;
    private SharedPreferences sharedPreferences;

    private static final String CONSENT_UUID_KEY = "sp.gdpr.consentUUID";
    private static final String META_DATA_KEY = "sp.gdpr.metaData";
    private static final String AUTH_ID_KEY = "sp.gdpr.authId";
    private static final String EU_CONSENT__KEY = "sp.gdpr.euconsent";
    private static final String IAB_CONSENT_CONSENT_STRING = "IABConsent_ConsentString";


    @Before
    public void setUp() throws Exception {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application.getApplicationContext());
        StoreClient storeClient = new StoreClient(sharedPreferences);

        ConsentLibBuilder consentLibBuilder = new ConsentLibBuilder(123, "example.com", 321, "abcd", mock(Activity.class));
        gdprConsentLib = consentLibBuilder.build();
        spyLib = spy(gdprConsentLib);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void clearAllData() {
        gdprConsentLib.clearAllData();
        assertFalse(sharedPreferences.contains(CONSENT_UUID_KEY));
        assertFalse(sharedPreferences.contains(META_DATA_KEY));
        assertFalse(sharedPreferences.contains(EU_CONSENT__KEY));
        assertFalse(sharedPreferences.contains(AUTH_ID_KEY));
        assertFalse(sharedPreferences.contains(IAB_CONSENT_CONSENT_STRING));
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
        spyLib = spy(gdprConsentLib);

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
        verify(spyLib ,times(1)).closeView(gdprConsentLib.webView);
    }

}
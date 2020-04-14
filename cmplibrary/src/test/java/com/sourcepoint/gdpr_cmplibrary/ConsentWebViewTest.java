package com.sourcepoint.gdpr_cmplibrary;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class ConsentWebViewTest {

    private ConsentWebView spyView;

    @Before
    public void setUp() {

        ConsentWebView consentWebView = new ConsentWebView(RuntimeEnvironment.systemContext) {
            @Override
            public void onConsentUIReady() {   }

            @Override
            public void onError(ConsentLibException error) {  }

            @Override
            public void onAction(int choiceType, Integer choiceId) {  }

            @Override
            public void onSavePM(GDPRUserConsent GDPRUserConsent) {   }
        };

        spyView = spy(consentWebView);

    }

    @Test
    public void onConsentUIReady(){
        spyView.onConsentUIReady();
        verify(spyView,times(1)).onConsentUIReady();
    }

    @Test
    public void onError(){
        ConsentLibException consentLibException = new ConsentLibException();
        spyView.onError(consentLibException);
        verify(spyView, times(1)).onError(consentLibException);
    }

    @Test
    public void onAction(){
        spyView.onAction(GDPRConsentLib.ActionTypes.MSG_ACCEPT,1);
        verify(spyView, times(1)).onAction(GDPRConsentLib.ActionTypes.MSG_ACCEPT,1);
    }

    @Test
    public void onSavePM(){
        GDPRUserConsent userConsent = mock(GDPRUserConsent.class);
        spyView.onSavePM(userConsent);
        verify(spyView, times(1)).onSavePM(userConsent);
    }
}



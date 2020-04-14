package com.sourcepoint.gdpr_cmplibrary;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class ConsentWebViewTest {

    private ConsentWebView consentWebView;


    @Before
    public void setUp() throws Exception{

        consentWebView = mock(ConsentWebView.class, CALLS_REAL_METHODS);

    }

    @Test
    public void onConsentUIReady(){
        consentWebView.onConsentUIReady();
        verify(consentWebView,times(1)).onConsentUIReady();
        verify(consentWebView, times(0)).onError(new ConsentLibException());
        verify(consentWebView, times(0)).onAction(ActionTypes.ACCEPT_ALL,1);
    }

    @Test
    public void onError(){
        ConsentLibException consentLibException = new ConsentLibException();
        consentWebView.onError(consentLibException);
        verify(consentWebView, times(1)).onError(consentLibException);
        verify(consentWebView,times(0)).onConsentUIReady();
        verify(consentWebView, times(0)).onAction(ActionTypes.ACCEPT_ALL,1);
    }

    @Test
    public void onAction(){
        consentWebView.onAction(ActionTypes.ACCEPT_ALL,1);
        verify(consentWebView, times(1)).onAction(ActionTypes.ACCEPT_ALL,1);
        verify(consentWebView,times(0)).onConsentUIReady();
        verify(consentWebView, times(0)).onError(new ConsentLibException());
    }
}



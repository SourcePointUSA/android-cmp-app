package com.sourcepoint.gdpr_cmplibrary;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.TestCase.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class ConsentLibExceptionTest {

    String errorMsgMock = "Something bad just happened";
    Exception errorMock = new Exception();

    String noInternetConnectionMsgExpected = ConsentLibException.NoInternetConnectionException.description ;
    String ApiExceptionMsgExpected = ConsentLibException.ApiException.description + ": " + errorMsgMock;

    @Test
    public void ConsentLibExceptionWithMessage(){
        ConsentLibException e = new ConsentLibException(errorMsgMock);
        assertEquals(e.consentLibErrorMessage, errorMsgMock);
    }

    @Test
    public void ConsentLibExceptionWithErrorPlusMessage(){
        ConsentLibException e = new ConsentLibException(errorMock , errorMsgMock);
        assertEquals(e.consentLibErrorMessage, errorMsgMock);
        assertEquals(e.getCause(), errorMock);
    }

    @Test
    public void NoInternetConnectionExceptionWithMessage(){
        ConsentLibException e = new ConsentLibException.NoInternetConnectionException();
        assertEquals(e.consentLibErrorMessage, noInternetConnectionMsgExpected);
    }

    @Test
    public void NoInternetConnectionExceptionWithErrorPlusMessage(){
        ConsentLibException e = new ConsentLibException.NoInternetConnectionException(errorMock);
        assertEquals(e.consentLibErrorMessage, noInternetConnectionMsgExpected);
        assertEquals(e.getCause(), errorMock);
    }

    @Test
    public void ApiExceptionWithMessage(){
        ConsentLibException e = new ConsentLibException.ApiException(errorMsgMock);
        assertEquals(e.consentLibErrorMessage, ApiExceptionMsgExpected);
    }

    @Test
    public void ApiExceptionWithErrorPlusMessage(){
        ConsentLibException e = new ConsentLibException.ApiException(errorMock , errorMsgMock);
        assertEquals(e.consentLibErrorMessage, ApiExceptionMsgExpected);
        assertEquals(e.getCause(), errorMock);
    }
}

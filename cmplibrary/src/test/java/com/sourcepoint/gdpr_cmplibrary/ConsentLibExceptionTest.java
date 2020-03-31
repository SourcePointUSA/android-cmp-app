package com.sourcepoint.gdpr_cmplibrary;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class ConsentLibExceptionTest {

    private ConsentLibException consentLibException ;

    @Test
    public void buildExceptionTest(){
        String exception = "Build exception";
        String excepected = "Error during ConsentLib build: "+ exception;
        consentLibException = new ConsentLibException.BuildException(exception);
        assertEquals(excepected , consentLibException.consentLibErrorMessage);
    }

    @Test
    public void buildExceptionAndMessageTet(){
        String exception = "Build exception";
        Exception e = new Exception(exception);
        String excepected = "Error during ConsentLib build: "+ exception;
        consentLibException = new ConsentLibException.BuildException(e ,exception);
        assertEquals(excepected , consentLibException.consentLibErrorMessage);
    }

    @Test
    public void noInternetConnectionTest(){
        consentLibException = new ConsentLibException.NoInternetConnectionException();
        assertNull(consentLibException.consentLibErrorMessage);
    }

    @Test
    public void apiExceptionTest(){
        String exception = "API exception";
        String excepected = "Error due to android API: "+ exception;
        consentLibException = new ConsentLibException.ApiException(exception);
        assertEquals(excepected , consentLibException.consentLibErrorMessage);
    }

    @Test
    public void apiExceptionAndMessageTet(){
        String exception = "API exception";
        Exception e = new Exception(exception);
        String excepected = "Error due to android API: "+ exception;
        consentLibException = new ConsentLibException.ApiException(e ,exception);
        assertEquals(excepected , consentLibException.consentLibErrorMessage);
    }
}

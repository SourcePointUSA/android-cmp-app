package com.sourcepoint.gdpr_cmplibrary;

public class ConsentLibException extends Exception {

    public String consentLibErrorMessage;

    ConsentLibException() { super(); }
    ConsentLibException(Exception e, String  message){ super(e); consentLibErrorMessage = message;}
    ConsentLibException(String  message){ super(); consentLibErrorMessage = message;}

    public static class BuildException extends ConsentLibException {
        BuildException(Exception e, String message) { super(e, "Error during ConsentLib build: "+message); }
        BuildException(String message) { super("Error during ConsentLib build: "+message); }
    }

    public static class NoInternetConnectionException extends ConsentLibException {}

    public static class ApiException extends ConsentLibException {
        ApiException(Exception e, String message) { super(e, "Error due to android API: " + message); }
        ApiException(String message) { super("Error due to android API: " + message); }
    }
}

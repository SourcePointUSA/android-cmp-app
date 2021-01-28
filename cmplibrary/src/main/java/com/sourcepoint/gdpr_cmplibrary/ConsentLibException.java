package com.sourcepoint.gdpr_cmplibrary;

public class ConsentLibException extends Exception {

    public String consentLibErrorMessage;

    public ConsentLibException(Throwable e, String  message){ super(e); consentLibErrorMessage = message;}
    public ConsentLibException(String  message){ super(); consentLibErrorMessage = message;}

    public static class NoInternetConnectionException extends ConsentLibException {
        public static final String description = "The device is not connected to the internet.";

        public NoInternetConnectionException(Throwable e) { super(e, description); }
        public NoInternetConnectionException() { super(description); }

    }

    public static class ApiException extends ConsentLibException {
        public static final String description = "Error due to android API";
        public ApiException(Throwable e, String message) { super(e, description + ": " + message); }
        public ApiException(String message) { super(description + ": " + message); }
    }
}

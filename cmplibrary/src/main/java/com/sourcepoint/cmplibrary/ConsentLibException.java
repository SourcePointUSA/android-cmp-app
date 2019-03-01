package com.sourcepoint.cmplibrary;

public class ConsentLibException extends Exception {
    ConsentLibException() { super(); }
    ConsentLibException(String message) { super(message); }

    public static class BuildException extends ConsentLibException {
        BuildException(String message) { super("Error during ConsentLib build: "+message); }
    }

    public static class NoInternetConnectionException extends ConsentLibException {}
    
    public static class ApiException extends ConsentLibException {
        ApiException(String message) { super(message); }
    }
}

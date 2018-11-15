package com.example.cmplibrary;

public class ConsentLibException extends Exception {
    public ConsentLibException(String message) { super(message); }

    public class BuildException extends ConsentLibException {
        public BuildException(String message) { super("Error during ConsentLib build: "+message); }
    }
}

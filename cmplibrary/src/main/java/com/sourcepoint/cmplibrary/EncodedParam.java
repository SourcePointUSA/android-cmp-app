package com.sourcepoint.cmplibrary;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

class EncodedParam {
    private String value;

    EncodedParam(String name, String value) throws ConsentLibException.BuildException {
        this.value = encode(name, value);
    }

    private String encode(String attrName, String attrValue) throws ConsentLibException.BuildException {
        try {
            return URLEncoder.encode(attrValue, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new ConsentLibException.BuildException("Unable to encode "+attrName+", with the value: "+attrValue+" when instantiating SourcePointClient");
        }
    }

    @Override
    public String toString() { return value; }
}

package com.sourcepointmeta.app.models;

//object of this class will be returned while checking data already exist or not
public class TargetingParameterList {
    public String keyList;
    public String valueList;

    public String getKeyList() {
        return keyList;
    }

    public void setKeyList(String keyList) {
        this.keyList = keyList;
    }

    public String getValueList() {
        return valueList;
    }

    public void setValueList(String valueList) {
        this.valueList = valueList;
    }
}

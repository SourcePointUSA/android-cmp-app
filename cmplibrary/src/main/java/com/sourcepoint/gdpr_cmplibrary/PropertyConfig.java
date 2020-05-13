package com.sourcepoint.gdpr_cmplibrary;

public class PropertyConfig {
    public int accountId;
    public int propertyId;
    public String propertyName;
    public String pmId;

    public PropertyConfig(
            int accountId,
            int propertyId,
            String propertyName,
            String pmId
    ){
        this.accountId = accountId;
        this.pmId = pmId;
        this.propertyId = propertyId;
        this.propertyName = propertyName;
    }
}

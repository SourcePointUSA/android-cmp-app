package com.sourcepoint.gdpr_cmplibrary;

class SourcePointClientConfig {
    PropertyConfig prop;
    Boolean isStagingCampaign;
    String targetingParams;

    SourcePointClientConfig(
            PropertyConfig prop,
            Boolean isStagingCampaign,
            String targetingParams
    ){
        this.prop = prop;
        this.isStagingCampaign = isStagingCampaign;
        this.targetingParams = targetingParams;
    }
}
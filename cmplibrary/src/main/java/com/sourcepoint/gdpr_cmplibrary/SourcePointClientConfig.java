package com.sourcepoint.gdpr_cmplibrary;

class SourcePointClientConfig {
    PropertyConfig prop;
    Boolean isStagingCampaign;
    String targetingParams, authId;

    SourcePointClientConfig(
            PropertyConfig prop,
            Boolean isStagingCampaign,
            String targetingParams,
            String authId
    ){
        this.prop = prop;
        this.isStagingCampaign = isStagingCampaign;
        this.targetingParams = targetingParams;
        this.authId = authId;
    }
}
package com.sourcepoint.gdpr_cmplibrary;

class SourcePointClientConfig {
    PropertyConfig prop;
    Boolean isStagingCampaign, isStaging;
    String targetingParams, authId;

    SourcePointClientConfig(
            PropertyConfig prop,
            Boolean isStagingCampaign,
            Boolean isStaging,
            String targetingParams,
            String authId
    ){
        this.prop = prop;
        this.isStagingCampaign = isStagingCampaign;
        this.isStaging = isStaging;
        this.targetingParams = targetingParams;
        this.authId = authId;
    }
}
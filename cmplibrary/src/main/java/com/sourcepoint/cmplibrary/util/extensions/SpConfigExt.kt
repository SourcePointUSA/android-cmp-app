package com.sourcepoint.cmplibrary.util.extensions

import com.sourcepoint.cmplibrary.creation.ConfigOption
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.SpConfig

internal fun SpConfig.isIncluded(campaign: CampaignType) =
    campaigns.find { it.campaignType == campaign } != null

internal fun SpConfig.hasTransitionCCPAAuth() =
    campaigns.find { it.campaignType == CampaignType.USNAT }
        ?.configParams
        ?.find { it == ConfigOption.TRANSITION_CCPA_AUTH } != null

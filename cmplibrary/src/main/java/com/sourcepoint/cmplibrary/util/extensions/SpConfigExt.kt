package com.sourcepoint.cmplibrary.util.extensions

import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.SpConfig

internal fun SpConfig.isIncluded(campaign: CampaignType) =
    campaigns.find { it.campaignType == campaign } != null

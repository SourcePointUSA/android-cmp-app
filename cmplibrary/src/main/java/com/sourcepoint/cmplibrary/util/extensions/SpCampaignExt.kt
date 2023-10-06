package com.sourcepoint.cmplibrary.util.extensions

import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.SpCampaign

fun List<SpCampaign>.containsCcpa(): Boolean =
    firstOrNull { it.campaignType == CampaignType.CCPA } != null

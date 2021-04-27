package com.sourcepoint.cmplibrary.model.exposed

import com.sourcepoint.cmplibrary.exception.CampaignType

data class SpConfig(
    @JvmField val accountId: Int,
    @JvmField val propertyName: String,
    @JvmField val campaigns: List<SpCampaign>
)

data class SpCampaign(
    @JvmField val campaignType: CampaignType,
    @JvmField internal val targetingParams: List<TargetingParam>
)

data class TargetingParam(val key: String, val value: String)

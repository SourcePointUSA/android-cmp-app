package com.sourcepoint.cmplibrary.model.exposed

import com.sourcepoint.cmplibrary.exception.Legislation

data class SpConfig(
    @JvmField val accountId: Int,
    @JvmField val propertyName: String,
    @JvmField val campaigns: List<SpCampaign>
)

data class SpCampaign(
    @JvmField val legislation: Legislation,
    @JvmField internal val targetingParams: List<TargetingParam>
)

data class TargetingParam(val key: String, val value: String)

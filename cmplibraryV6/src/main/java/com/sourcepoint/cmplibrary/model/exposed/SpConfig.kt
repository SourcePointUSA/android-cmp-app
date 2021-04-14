package com.sourcepoint.cmplibrary.model.exposed

import com.sourcepoint.cmplibrary.data.network.util.CampaignEnv
import com.sourcepoint.cmplibrary.exception.Legislation

data class SpConfig(
    @JvmField val accountId: Int,
    @JvmField val propertyName: String,
    @JvmField val campaigns: Array<SpCampaign>
)

data class SpCampaign(
    @JvmField val legislation: Legislation,
    @JvmField val environment: CampaignEnv,
    @JvmField val targetingParams: Array<TargetingParam>
)

data class TargetingParam(val key: String, val value: String)

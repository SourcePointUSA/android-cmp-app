package com.sourcepoint.cmplibrary.model.exposed

import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.model.MessageLanguage

data class SpConfig(
    @JvmField val accountId: Int,
    @JvmField val propertyName: String,
    @JvmField val campaigns: List<SpCampaign>,
    @JvmField val messageLanguage: MessageLanguage,
    @JvmField val messageTimeout: Long,
    @JvmField val logger: Logger? = null
)

data class SpCampaign(
    @JvmField val campaignType: CampaignType,
    @JvmField internal val targetingParams: List<TargetingParam>
)

data class TargetingParam(val key: String, val value: String)

fun Pair<String, String>.toTParam() = TargetingParam(this.first, this.second)

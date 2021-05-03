package com.sourcepoint.cmplibrary.model.exposed

import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.PMTab

data class SpConfig(
    @JvmField val accountId: Int,
    @JvmField val propertyName: String,
    @JvmField val campaigns: List<SpCampaign>,
    @JvmField val pmTab: PMTab,
    @JvmField val messageLanguage: MessageLanguage,

)

data class SpCampaign(
    @JvmField val campaignType: CampaignType,
    @JvmField internal val targetingParams: List<TargetingParam>
)

data class TargetingParam(val key: String, val value: String)

fun Pair<String, String>.toTParam() = TargetingParam(this.first, this.second)

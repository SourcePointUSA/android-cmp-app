package com.sourcepoint.cmplibrary.model.exposed

import com.sourcepoint.cmplibrary.data.network.DEFAULT_TIMEOUT
import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.exposed.gpp.GppConfig
import com.sourcepoint.cmplibrary.model.MessageLanguage

data class SpConfig(
    @JvmField val accountId: Int,
    @JvmField val propertyName: String,
    @JvmField val campaigns: List<SpCampaign>,
    @JvmField val messageLanguage: MessageLanguage,
    @JvmField val messageTimeout: Long = DEFAULT_TIMEOUT,
    @JvmField val propertyId: Int,
    @JvmField val campaignsEnv: CampaignsEnv = CampaignsEnv.PUBLIC,
    @JvmField val logger: Logger? = null,
    @JvmField val gppConfig: GppConfig? = null,
)

data class SpCampaign(
    @JvmField val campaignType: CampaignType,
    @JvmField internal var targetingParams: List<TargetingParam> = emptyList(),
    @JvmField var groupPmId: String? = null
) {
    constructor(
        campaignType: CampaignType,
        targetingParams: List<TargetingParam>
    ) : this(campaignType, targetingParams, null)

    constructor(
        campaignType: CampaignType,
        groupPmId: String
    ) : this(campaignType, emptyList(), groupPmId)
}

data class TargetingParam(val key: String, val value: String)

fun Pair<String, String>.toTParam() = TargetingParam(this.first, this.second)

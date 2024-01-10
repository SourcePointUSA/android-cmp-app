package com.sourcepoint.cmplibrary.util.extensions

import com.sourcepoint.cmplibrary.creation.ConfigOption
import com.sourcepoint.cmplibrary.data.network.model.optimized.includeData.IncludeDataGppParam
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.CampaignType.CCPA
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepoint.cmplibrary.model.exposed.SpGppConfig
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement

internal fun SpConfig.isIncluded(campaign: CampaignType) =
    campaigns.find { it.campaignType == campaign } != null

internal fun SpConfig.hasTransitionCCPAAuth() =
    campaigns.find { it.campaignType == CampaignType.USNAT }
        ?.configParams
        ?.find { it == ConfigOption.TRANSITION_CCPA_AUTH } != null

internal fun SpGppConfig?.toIncludeDataGppParam(): IncludeDataGppParam = IncludeDataGppParam(
    coveredTransaction = this?.coveredTransaction?.type,
    optOutOptionMode = this?.optOutOptionMode?.type,
    serviceProviderMode = this?.serviceProviderMode?.type,
)

internal fun SpConfig.getGppCustomOption(): JsonElement? = when(isIncluded(CCPA)){
    false -> null
    true -> spGppConfig
        ?.toIncludeDataGppParam()
        ?.let { Json.encodeToJsonElement(it) }
}

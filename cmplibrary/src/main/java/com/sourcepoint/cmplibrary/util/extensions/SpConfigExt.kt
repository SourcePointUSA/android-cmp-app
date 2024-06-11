package com.sourcepoint.cmplibrary.util.extensions

import com.sourcepoint.cmplibrary.creation.ConfigOption
import com.sourcepoint.cmplibrary.data.network.model.optimized.includeData.IncludeDataGppParam
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.CampaignType.CCPA
import com.sourcepoint.cmplibrary.exception.CampaignType.USNAT
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

internal fun SpConfig.hasSupportForLegacyUSPString() = campaigns
    .find { it.campaignType == USNAT }
    ?.configParams
    ?.find { it == ConfigOption.SUPPORT_LEGACY_USPSTRING } != null

internal fun SpGppConfig?.toIncludeDataGppParam(supportLegacyUSPString: Boolean? = null): IncludeDataGppParam = IncludeDataGppParam(
    coveredTransaction = this?.coveredTransaction?.type,
    optOutOptionMode = this?.optOutOptionMode?.type,
    serviceProviderMode = this?.serviceProviderMode?.type,
    uspString = supportLegacyUSPString
)

internal fun SpConfig.getGppCustomOption(): JsonElement? = when {
    isIncluded(CCPA) ->
        spGppConfig
            ?.toIncludeDataGppParam()
            ?.let { Json.encodeToJsonElement(it) }
    isIncluded(USNAT) ->
        hasSupportForLegacyUSPString().let { Json.encodeToJsonElement(it) }
    else -> null
}

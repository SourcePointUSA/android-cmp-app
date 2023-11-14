package com.sourcepoint.cmplibrary.util.extensions

import com.sourcepoint.cmplibrary.data.network.model.optimized.includeData.IncludeDataGppParam
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exposed.gpp.SpGppConfig
import com.sourcepoint.cmplibrary.gpp.utils.toIncludeDataGppParam
import com.sourcepoint.cmplibrary.model.exposed.SpConfig

/**
 * This method extracts required GPP data for an IncludeData object.
 *
 * In order for GPP to be present in the IncludeData object, the SpConfig should contain CCPA
 * campaign in it. Then if the user set up custom SpGppConfig, it will be used in the IncludeData.
 * If not - the default SpGppConfig will be used, which is and empty SpGppConfig object (all the
 * params are nulls).
 */
internal fun SpConfig.extractIncludeGppDataParamIfEligible(): IncludeDataGppParam? =
    (this.spGppConfig ?: SpGppConfig())
        .takeIf { this.campaigns.containsCcpa() }
        ?.toIncludeDataGppParam()

internal fun SpConfig.isIncluded(campaign: CampaignType) =
    campaigns.find { it.campaignType == campaign } != null

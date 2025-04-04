package com.sourcepoint.cmplibrary.data.network.util

import com.sourcepoint.cmplibrary.data.network.converter.CampaignTypeSerializer
import com.sourcepoint.mobile_core.models.SPCampaignType
import kotlinx.serialization.Serializable

/**
 * Type of configurable campaigns
 */
@Serializable(with = CampaignTypeSerializer::class)
enum class CampaignType {
    GDPR,
    CCPA,
    USNAT,
    UNKNOWN;

    companion object {
        fun fromCore(type: SPCampaignType) = when (type) {
            SPCampaignType.Gdpr -> GDPR
            SPCampaignType.Ccpa -> CCPA
            SPCampaignType.UsNat -> USNAT
            SPCampaignType.Unknown, SPCampaignType.IOS14 -> UNKNOWN
        }
    }

    fun toCore(): SPCampaignType = when (this) {
        GDPR -> SPCampaignType.Gdpr
        CCPA -> SPCampaignType.Ccpa
        USNAT -> SPCampaignType.UsNat
        UNKNOWN -> SPCampaignType.Unknown
    }
}

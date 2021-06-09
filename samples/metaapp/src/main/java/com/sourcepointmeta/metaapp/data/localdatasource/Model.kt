package com.sourcepointmeta.metaapp.data.localdatasource

import com.sourcepoint.cmplibrary.exception.CampaignType
import java.util.*

data class Property(
    val propertyName: String,
    val accountId: Long,
    val gdprPmId: Long?,
    val ccpaPmId: Long?,
    val messageType: String,
    val is_staging: Boolean = false,
    val targetingParameters: List<MetaTargetingParam> = emptyList(),
    val propertyId: Long? = null,
    val authId: String? = null,
    val messageLanguage: String? = null,
    val pmTab: String? = null,
    val statusCampaignSet: Set<StatusCampaign>,
    val timestamp: Long = Date().time
)

data class MetaTargetingParam(
    val propertyName: String,
    val campaign: CampaignType,
    val key: String,
    val value: String
)

data class StatusCampaign(
    val propertyName: String,
    val campaignType: CampaignType,
    val enabled: Boolean = false
) {
    override fun hashCode(): Int {
        var result = propertyName.hashCode()
        result = 31 * result + campaignType.name.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        return (other as? StatusCampaign)?.let {
            it.propertyName == this.propertyName &&
                it.campaignType == this.campaignType
        } ?: return false
    }
}

package com.sourcepointmeta.metaapp.data.localdatasource

import com.sourcepoint.cmplibrary.exception.CampaignType
import comsourcepointmetametaappdb.* // ktlint-disable
import java.util.* // ktlint-disable

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

data class MetaLog(
    val id: Long?,
    val propertyName: String,
    val timestamp: Long,
    val type: String,
    val tag: String,
    val message: String,
    val logSession: String? = null,
    val jsonBody: String? = null,
    val statusReq: String? = null
)

fun Targeting_param.toTargetingParam() = MetaTargetingParam(
    propertyName = property_name,
    value = value,
    key = key,
    campaign = CampaignType.values().find { it.name == campaign } ?: CampaignType.GDPR
)

fun Property_.toProperty(tp: List<MetaTargetingParam>, statusCampaign: Set<StatusCampaign>) = Property(
    propertyId = property_id,
    propertyName = property_name,
    is_staging = is_staging != 0L,
    accountId = account_id,
    pmTab = pm_tab,
    messageLanguage = message_language,
    authId = auth_Id,
    targetingParameters = tp,
    statusCampaignSet = statusCampaign,
    messageType = message_type,
    timestamp = timestamp,
    gdprPmId = gdpr_pm_id,
    ccpaPmId = ccpa_pm_id
)

fun CampaignQueries.getTargetingParams(propName: String) =
    this.selectTargetingParametersByPropertyName(propName)

fun Status_campaign.toStatusCampaign() = StatusCampaign(
    propertyName = property_name,
    campaignType = CampaignType.valueOf(campaign_type),
    enabled = enabled != 0L
)

fun Boolean.toValueDB() = when (this) {
    true -> 1L
    false -> 0L
}

fun Meta_log.toMetaLog() = MetaLog(
    id = id,
    propertyName = property_name,
    timestamp = timestamp,
    type = type,
    tag = tag,
    message = message,
    logSession = log_session,
    jsonBody = json_body,
    statusReq = status_req
)

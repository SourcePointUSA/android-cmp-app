package com.sourcepointmeta.metaapp.data.localdatasource

import com.sourcepoint.cmplibrary.data.network.DEFAULT_TIMEOUT
import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exposed.gpp.SpGppOptionBinary
import com.sourcepoint.cmplibrary.exposed.gpp.SpGppOptionTernary
import com.sourcepoint.cmplibrary.model.exposed.MessageType
import comsourcepointmetametaappdb.* // ktlint-disable
import java.util.* // ktlint-disable

data class Property(
    val propertyName: String,
    val accountId: Long,
    val gdprPmId: Long?,
    val usnatPmId: Long?,
    val is_staging: Boolean = false,
    val targetingParameters: List<MetaTargetingParam> = emptyList(),
    val timeout: Long = DEFAULT_TIMEOUT,
    val authId: String? = null,
    val messageLanguage: String? = null,
    val pmTab: String? = null,
    val statusCampaignSet: Set<StatusCampaign>,
    val campaignsEnv: CampaignsEnv,
    val timestamp: Long = Date().time,
    val gdprGroupPmId: String? = null,
    val useGdprGroupPmIfAvailable: Boolean = false,
    val ccpaGroupPmId: String? = null,
    val usnatGroupPmId: String? = null,
    val propertyId: Int,
    val useCcpaGroupPmIfAvailable: Boolean = false,
    val messageType: MessageType = MessageType.MOBILE,
    val ccpaPmId: Long? = null,
    val gpp: GPP? = null,
)

data class MetaTargetingParam(
    val propertyName: String,
    val campaign: CampaignType,
    val key: String,
    val value: String
)

data class GPP(
    val propertyName: String,
    val serviceProviderMode: SpGppOptionTernary?,
    val coveredTransaction: SpGppOptionBinary?,
    val optOutOptionMode: SpGppOptionTernary?,
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

fun Property_.toProperty(tp: List<MetaTargetingParam>, statusCampaign: Set<StatusCampaign>, gpp: GPP?) = Property(
    propertyName = property_name,
    accountId = account_id,
    gdprPmId = gdpr_pm_id,
    ccpaPmId = ccpa_pm_id,
    is_staging = is_staging != 0L,
    targetingParameters = tp,
    timeout = timeout ?: DEFAULT_TIMEOUT,
    authId = if (auth_Id.isNullOrEmpty()) null else auth_Id,
    messageLanguage = message_language,
    pmTab = pm_tab,
    statusCampaignSet = statusCampaign,
    campaignsEnv = CampaignsEnv.values().find { it.env == campaign_env } ?: CampaignsEnv.PUBLIC,
    timestamp = timestamp,
    gdprGroupPmId = group_pm_id,
    useGdprGroupPmIfAvailable = use_gdpr_groupid_if_available != 0L,
    propertyId = property_id.toString().toInt(),
    messageType = MessageType.values().find { it.name == message_type } ?: MessageType.MOBILE,
    usnatPmId = usnat_pm_id,
    gpp = gpp
)

fun CampaignQueries.getTargetingParams(propName: String) =
    this.selectTargetingParametersByPropertyName(propName)

fun Status_campaign.toStatusCampaign() = StatusCampaign(
    propertyName = property_name,
    campaignType = CampaignType.valueOf(campaign_type),
    enabled = enabled != 0L
)

fun Gpp.toGpp() = GPP(
    propertyName = property_name,
    serviceProviderMode = SpGppOptionTernary.values().find { it.type == service_provider_mode },
    coveredTransaction = SpGppOptionBinary.values().find { it.type == covered_transaction },
    optOutOptionMode = SpGppOptionTernary.values().find { it.type == opt_out_option_mode },
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

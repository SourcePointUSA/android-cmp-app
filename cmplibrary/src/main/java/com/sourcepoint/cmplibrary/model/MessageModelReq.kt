package com.sourcepoint.cmplibrary.model

import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.TargetingParam
import org.json.JSONObject

internal data class Campaigns(val list: List<CampaignReq> = emptyList())

internal interface CampaignReq {
    val targetingParams: List<TargetingParam>
    val campaignsEnv: CampaignsEnv
    val campaignType: CampaignType
    val groupPmId: String?
}

internal data class CampaignReqImpl(
    override val targetingParams: List<TargetingParam>,
    override val campaignsEnv: CampaignsEnv,
    override val campaignType: CampaignType,
    override val groupPmId: String? = null
) : CampaignReq

internal data class UnifiedMessageRequest(
    val accountId: Int,
    val propertyHref: String,
    val campaigns: Campaigns,
    val includeData: IncludeData = IncludeData(),
    val consentLanguage: MessageLanguage = MessageLanguage.ENGLISH,
    val hasCSP: Boolean = true,
    val campaignsEnv: CampaignsEnv = CampaignsEnv.PUBLIC,
    val localState: String? = null,
    val authId: String? = null,
    val requestUUID: String? = null,
    val pubData: JSONObject? = null
)

data class DataType(val type: String)
data class IncludeData(
    val localState: DataType = DataType("RecordString"),
    val tCData: DataType = DataType("RecordString"),
    val campaigns: DataType = DataType("RecordString"),
    val customVendorsResponse: DataType = DataType("RecordString"),
    val messageMetaData: DataType = DataType("RecordString")
)

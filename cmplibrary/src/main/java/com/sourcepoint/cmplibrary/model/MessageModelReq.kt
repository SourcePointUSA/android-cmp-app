package com.sourcepoint.cmplibrary.model

import com.sourcepoint.cmplibrary.data.network.util.CampaignEnv
import com.sourcepoint.cmplibrary.exception.CampaignType

internal data class Campaigns(val list: List<CampaignReq> = emptyList())

internal interface CampaignReq {
    val targetingParams: String?
    val campaignEnv: CampaignEnv
    val campaignType: CampaignType
}

internal data class CampaignReqImpl(
    override val targetingParams: String?,
    override val campaignEnv: CampaignEnv,
    override val campaignType: CampaignType
) : CampaignReq

internal data class UnifiedMessageRequest(
    val accountId: Int,
    val propertyHref: String,
    val campaigns: Campaigns,
    val includeData: IncludeData = IncludeData(),
    val consentLanguage: MessageLanguage = MessageLanguage.ENGLISH,
    val hasCSP: Boolean = true,
    val campaignEnv: String = "prod",
    val localState: String? = null,
    val authId: String? = null,
    val requestUUID: String? = null
)

data class DataType(val type: String)
data class IncludeData(
    val localState: DataType = DataType("RecordString"),
    val tCData: DataType = DataType("RecordString"),
    val customVendorsResponse: DataType = DataType("RecordString"),
    val messageMetaData: DataType = DataType("RecordString")
)

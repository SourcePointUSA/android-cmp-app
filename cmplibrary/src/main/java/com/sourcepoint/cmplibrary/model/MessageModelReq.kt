package com.sourcepoint.cmplibrary.model

import com.sourcepoint.cmplibrary.exception.CampaignType

internal data class Campaigns(val list: List<CampaignReq> = emptyList())

internal interface CampaignReq {
    val targetingParamsList: List<String>
    val campaignType: CampaignType
}

internal data class CampaignReqImpl(
    override val campaignType: CampaignType,
    override val targetingParamsList: List<String> = emptyList()
) : CampaignReq

internal data class UnifiedMessageRequest(
    val accountId: Int,
    val propertyHref: String,
    val campaigns: Campaigns,
    val includeData: IncludeData = IncludeData(
        tCData = TCData("RecordString"),
        messageMetaData = MessageMetaData("RecordString"),
        localState = LocalState("string")
    ),
    val consentLanguage: MessageLanguage = MessageLanguage.ENGLISH,
    val hasCSP: Boolean = true,
    val campaignEnv: String = "prod",
    val localState: String? = null,
    val authId: String? = null,
    val requestUUID: String? = null
)

data class LocalState(val type: String)
data class TCData(val type: String)
data class MessageMetaData(val type: String)
data class IncludeData(
    val localState: LocalState,
    val tCData: TCData? = null,
    val messageMetaData: MessageMetaData? = null
)

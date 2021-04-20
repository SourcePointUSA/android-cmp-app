package com.sourcepoint.cmplibrary.model

import com.sourcepoint.cmplibrary.data.network.util.CampaignEnv
import com.sourcepoint.cmplibrary.exception.Legislation

internal data class Campaigns(val list: List<CampaignReq> = emptyList()) {
    val gdpr: CampaignReq? = list.find { it.legislation == Legislation.GDPR }
    val ccpa: CampaignReq? = list.find { it.legislation == Legislation.CCPA }
}

internal interface CampaignReq {
    val targetingParams: String
    val campaignEnv: CampaignEnv
    val legislation: Legislation
}

internal data class CampaignReqImpl(
    override val targetingParams: String,
    override val campaignEnv: CampaignEnv,
    override val legislation: Legislation
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
    val requestUUID: String? = null
)

data class LocalState(val type: String)
data class TCData(val type: String)
data class MessageMetaData(val type: String)
data class IncludeData(
    val tCData: TCData = TCData("RecordString"),
    val messageMetaData: MessageMetaData = MessageMetaData("RecordString"),
    val localState: LocalState = LocalState("string")
)

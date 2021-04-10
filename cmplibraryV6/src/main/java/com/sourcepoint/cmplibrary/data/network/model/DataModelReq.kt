package com.sourcepoint.cmplibrary.data.network.model

import com.sourcepoint.cmplibrary.data.network.util.CampaignEnv
import com.sourcepoint.cmplibrary.model.MessageLanguage

internal data class MessageReq(
    val requestUUID: String,
    val campaigns: Campaigns
)

internal data class Campaigns(
    val gdpr: CampaignReq? = null,
    val ccpa: CampaignReq? = null
)

internal interface CampaignReq {
    val targetingParams: String
    val campaignEnv: CampaignEnv
}

internal data class GdprReq(
    override val targetingParams: String,
    override val campaignEnv: CampaignEnv
) : CampaignReq

internal data class CcpaReq(
    override val targetingParams: String,
    override val campaignEnv: CampaignEnv
) : CampaignReq

// internal class TargetingParams(
//    val legislation: String,
//    val location: String
// )

internal data class UnifiedMessageRequest(
    val accountId: Int,
    val propertyHref: String,
    val consentLanguage: MessageLanguage,
    val campaigns: Campaigns,
    val includeData: IncludeData = IncludeData(),
    val campaignEnv: String = "prod",
    val idfaStatus: String? = null,
    val requestUUID: String? = null
)

data class Actions(val type: String)
data class Cookies(val type: String)
data class CustomVendorsResponse(val type: String)
data class LocalState(val type: String)
data class IncludeData(
    val actions: Actions = Actions("RecordString"),
    val cookies: Cookies = Cookies("RecordString"),
    val customVendorsResponse: CustomVendorsResponse = CustomVendorsResponse("RecordString"),
    val localState: LocalState = LocalState("string")
)

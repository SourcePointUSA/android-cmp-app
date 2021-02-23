package com.sourcepoint.cmplibrary.data.network.model

internal data class MessageReq(
    val requestUUID: String,
    val campaigns: Campaigns
)

internal data class Campaigns(
    val gdpr: CampaignReq? = null,
    val ccpa: CampaignReq? = null
)

internal interface CampaignReq {
    val accountId: Int
    val propertyHref: String
    val propertyId: Int
    val targetingParams: String
}

internal data class GdprReq(
    override val accountId: Int,
    override val propertyHref: String,
    override val propertyId: Int,
    override val targetingParams: String
) : CampaignReq

internal data class CcpaReq(
    override val accountId: Int,
    override val propertyHref: String,
    override val propertyId: Int,
    override val targetingParams: String
) : CampaignReq

internal class TargetingParams(
    val legislation: String,
    val location: String
)

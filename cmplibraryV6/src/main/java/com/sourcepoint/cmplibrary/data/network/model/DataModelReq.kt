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
    val uuid: String?
    val meta: String?
}

internal data class GdprReq(
    override val accountId: Int,
    override val propertyHref: String,
    override val propertyId: Int,
    override val targetingParams: String,
    override val uuid: String? = null,
    override val meta: String? = null
) : CampaignReq

internal data class CcpaReq(
    override val accountId: Int,
    override val propertyHref: String,
    override val propertyId: Int,
    override val targetingParams: String,
    override val uuid: String? = null,
    override val meta: String? = null
) : CampaignReq

internal class TargetingParams(
    val legislation: String,
    val location: String
)

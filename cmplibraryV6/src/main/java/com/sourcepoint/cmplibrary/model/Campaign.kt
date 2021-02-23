package com.sourcepoint.cmplibrary.model

import com.sourcepoint.cmplibrary.data.network.model.CcpaReq
import com.sourcepoint.cmplibrary.data.network.model.GdprReq
import com.sourcepoint.cmplibrary.data.network.model.TargetingParams
import com.sourcepoint.cmplibrary.data.network.model.toJsonObjStringify
import com.sourcepoint.cmplibrary.exception.Legislation

data class Campaign(
    @JvmField val accountId: Int,
    @JvmField val propertyId: Int,
    @JvmField val propertyName: String,
    @JvmField val pmId: String
)

open class CampaignTemplate(
    open val accountId: Int,
    open val propertyId: Int,
    open val propertyName: String,
    open val pmId: String
)

class GDPRCampaign(
    @JvmField override val accountId: Int,
    @JvmField override val propertyId: Int,
    @JvmField override val propertyName: String,
    @JvmField override val pmId: String
) : CampaignTemplate(accountId, propertyId, propertyName, pmId)

class CCPACampaign(
    @JvmField override val accountId: Int,
    @JvmField override val propertyId: Int,
    @JvmField override val propertyName: String,
    @JvmField override val pmId: String
) : CampaignTemplate(accountId, propertyId, propertyName, pmId)

internal fun CampaignTemplate.toGdprReq(location: String): GdprReq {
    return GdprReq(
        accountId = accountId,
        propertyId = propertyId,
        propertyHref = propertyName,
        targetingParams = TargetingParams(
            legislation = Legislation.GDPR.name,
            location = location
        ).toJsonObjStringify(),
    )
}

internal fun CampaignTemplate.toCcpaReq(location: String): CcpaReq {
    return CcpaReq(
        accountId = accountId,
        propertyId = propertyId,
        propertyHref = propertyName,
        targetingParams = TargetingParams(
            legislation = Legislation.CCPA.name,
            location = location
        ).toJsonObjStringify(),
    )
}

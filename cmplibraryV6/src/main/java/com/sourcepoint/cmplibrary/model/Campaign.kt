package com.sourcepoint.cmplibrary.model

import com.sourcepoint.cmplibrary.data.network.model.CcpaReq
import com.sourcepoint.cmplibrary.data.network.model.GdprReq
import com.sourcepoint.cmplibrary.data.network.model.TargetingParams
import com.sourcepoint.cmplibrary.data.network.model.toJsonObjStringify
import com.sourcepoint.cmplibrary.exception.Legislation

data class SpProperty(
    @JvmField val accountId: Int,
    @JvmField val propertyName: String,
    @JvmField val gdprPmId: String?,
    @JvmField val ccpaPmId: String?
)

data class Campaign(
    @JvmField val accountId: Int,
    @JvmField val propertyId: Int,
    @JvmField val propertyName: String,
    @JvmField val pmId: String
)

internal open class CampaignTemplate(
    open val accountId: Int,
//    open val propertyId: Int,
    open val propertyName: String,
    open val pmId: String
)

internal class GDPRCampaign(
    @JvmField override val accountId: Int,
//    @JvmField override val propertyId: Int,
    @JvmField override val propertyName: String,
    @JvmField override val pmId: String
) : CampaignTemplate(accountId, propertyName, pmId)

internal class CCPACampaign(
    @JvmField override val accountId: Int,
//    @JvmField override val propertyId: Int,
    @JvmField override val propertyName: String,
    @JvmField override val pmId: String
) : CampaignTemplate(accountId, propertyName, pmId)

internal fun CampaignTemplate.toGdprReq(
    location: String,
    uuid: String? = null,
    meta: String? = null
): GdprReq {
    return GdprReq(
        accountId = accountId,
//        propertyId = propertyId,
        propertyHref = propertyName,
        targetingParams = TargetingParams(
            legislation = Legislation.GDPR.name,
            location = location
        ).toJsonObjStringify(),
        uuid = uuid,
        meta = meta
    )
}

internal fun CampaignTemplate.toCcpaReq(
    location: String,
    uuid: String? = null,
    meta: String? = null
): CcpaReq {
    return CcpaReq(
        accountId = accountId,
//        propertyId = propertyId,
        propertyHref = propertyName,
        targetingParams = TargetingParams(
            legislation = Legislation.CCPA.name,
            location = location
        ).toJsonObjStringify(),
        uuid = uuid,
        meta = meta
    )
}

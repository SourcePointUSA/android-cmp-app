package com.sourcepoint.cmplibrary.model

import com.sourcepoint.cmplibrary.data.network.model.CcpaReq
import com.sourcepoint.cmplibrary.data.network.model.GdprReq
import com.sourcepoint.cmplibrary.data.network.model.TargetingParams
import com.sourcepoint.cmplibrary.data.network.model.toJsonObjStringify
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.Legislation

data class SpConfig(
    @JvmField val accountId: Int,
    @JvmField val propertyName: String,
    @JvmField val campaigns: Array<SpCampaign>
)

data class SpCampaign(
    @JvmField val legislation: Legislation,
    @JvmField val environment: Env,
    @JvmField val targetingParams: Array<TargetingParam>
)

data class TargetingParam(val key: String, val value: String)

data class Campaign(
    @JvmField val accountId: Int,
    @JvmField val propertyName: String,
    @JvmField val pmId: String
)

internal open class CampaignTemplate(
    open val accountId: Int,
    open val propertyName: String,
    open val env: Env
)

internal class GDPRCampaign(
    @JvmField override val accountId: Int,
    @JvmField override val propertyName: String,
    @JvmField override val env: Env
) : CampaignTemplate(accountId, propertyName, env)

internal class CCPACampaign(
    @JvmField override val accountId: Int,
    @JvmField override val propertyName: String,
    @JvmField override val env: Env
) : CampaignTemplate(accountId, propertyName, env)

internal fun CampaignTemplate.toGdprReq(
    location: String,
    uuid: String? = null,
    meta: String? = null
): GdprReq {
    return GdprReq(
        accountId = accountId,
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
        propertyHref = propertyName,
        targetingParams = TargetingParams(
            legislation = Legislation.CCPA.name,
            location = location
        ).toJsonObjStringify(),
        uuid = uuid,
        meta = meta
    )
}

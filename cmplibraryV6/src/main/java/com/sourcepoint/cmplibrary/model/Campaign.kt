package com.sourcepoint.cmplibrary.model

import com.sourcepoint.cmplibrary.data.network.util.CampaignEnv
import com.sourcepoint.cmplibrary.model.exposed.TargetingParam
import com.sourcepoint.cmplibrary.model.ext.toJsonObjStringify

internal data class Campaign(
    @JvmField val accountId: Int,
    @JvmField val propertyName: String,
    @JvmField val pmId: String
)

internal open class CampaignTemplate(
    open val campaignEnv: CampaignEnv,
    open val targetingParams: Array<TargetingParam>
)

internal class GDPRCampaign(
    @JvmField override val campaignEnv: CampaignEnv,
    @JvmField override val targetingParams: Array<TargetingParam>
) : CampaignTemplate(campaignEnv, targetingParams)

internal class CCPACampaign(
    @JvmField override val campaignEnv: CampaignEnv,
    @JvmField override val targetingParams: Array<TargetingParam>
) : CampaignTemplate(campaignEnv, targetingParams)

internal fun CampaignTemplate.toGdprReq(
    targetingParams: Array<TargetingParam>,
    campaignEnv: CampaignEnv
): GdprReq {
    return GdprReq(
        targetingParams = targetingParams.toJsonObjStringify(),
        campaignEnv = campaignEnv
    )
}

internal fun CampaignTemplate.toCcpaReq(
    targetingParams: Array<TargetingParam>,
    campaignEnv: CampaignEnv
): CcpaReq {
    return CcpaReq(
        targetingParams = targetingParams.toJsonObjStringify(),
        campaignEnv = campaignEnv
    )
}

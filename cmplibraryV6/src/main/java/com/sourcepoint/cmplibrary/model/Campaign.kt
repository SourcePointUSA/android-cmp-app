package com.sourcepoint.cmplibrary.model

import com.sourcepoint.cmplibrary.data.network.util.CampaignEnv
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.model.exposed.TargetingParam
import com.sourcepoint.cmplibrary.model.ext.toJsonObjStringify

internal data class Campaign(
    @JvmField val accountId: Int,
    @JvmField val propertyName: String,
    @JvmField val pmId: String
)

internal open class CampaignTemplate(
    open val campaignEnv: CampaignEnv,
    open val targetingParams: List<TargetingParam>,
    open val legislation: Legislation
)

internal fun CampaignTemplate.toCampaignReqImpl(
    targetingParams: List<TargetingParam>,
    campaignEnv: CampaignEnv
): CampaignReqImpl {
    return CampaignReqImpl(
        targetingParams = targetingParams.toJsonObjStringify(),
        campaignEnv = campaignEnv,
        legislation = legislation
    )
}

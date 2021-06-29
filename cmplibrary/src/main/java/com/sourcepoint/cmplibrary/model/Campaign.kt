package com.sourcepoint.cmplibrary.model

import com.sourcepoint.cmplibrary.data.network.model.toJsonObjStringify
import com.sourcepoint.cmplibrary.data.network.util.CampaignEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.TargetingParam

internal data class Campaign(
    @JvmField val accountId: Int,
    @JvmField val propertyName: String,
    @JvmField val pmId: String
)

internal open class CampaignTemplate(
    open val campaignEnv: CampaignEnv,
    open val targetingParams: List<TargetingParam>,
    open val campaignType: CampaignType
)

internal fun CampaignTemplate.toCampaignReqImpl(
    targetingParams: List<TargetingParam>,
    campaignEnv: CampaignEnv
): CampaignReqImpl {
    return CampaignReqImpl(
        targetingParams = if (targetingParams.isEmpty()) null else targetingParams.toJsonObjStringify(),
        campaignEnv = campaignEnv,
        campaignType = campaignType
    )
}

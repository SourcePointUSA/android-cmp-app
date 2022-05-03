package com.sourcepoint.cmplibrary.model

import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.TargetingParam

internal data class Campaign(
    @JvmField val accountId: Int,
    @JvmField val propertyName: String,
    @JvmField val pmId: String
)

internal open class CampaignTemplate(
    open val campaignsEnv: CampaignsEnv,
    open val targetingParams: List<TargetingParam>,
    open val campaignType: CampaignType,
    open val groupPmId: String?
)

internal fun CampaignTemplate.toCampaignReqImpl(
    targetingParams: List<TargetingParam>,
    campaignsEnv: CampaignsEnv,
    groupPmId: String? = null
): CampaignReqImpl {
    return CampaignReqImpl(
        targetingParams = targetingParams.ifEmpty { emptyList() },
        campaignsEnv = campaignsEnv,
        campaignType = campaignType,
        groupPmId = groupPmId
    )
}

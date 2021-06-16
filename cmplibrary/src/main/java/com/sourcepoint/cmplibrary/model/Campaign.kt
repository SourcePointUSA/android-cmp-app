package com.sourcepoint.cmplibrary.model

import com.sourcepoint.cmplibrary.data.network.util.CampaignEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.TargetingParam

internal data class Campaign(
    @JvmField val accountId: Int,
    @JvmField val propertyName: String,
    @JvmField val pmId: String
)

internal open class CampaignTemplate(
    open val targetingParams: List<TargetingParam>,
    open val campaignType: CampaignType
)

internal fun CampaignTemplate.toCampaignReqImpl(
    targetingParams: List<TargetingParam>
): CampaignReqImpl {
    val mTP = targetingParams.toMutableList()
    mTP.find { it.key == "campaignEnv" }
        ?: run { mTP.add(TargetingParam("campaignEnv", CampaignEnv.PUBLIC.value)) }
    return CampaignReqImpl(
        campaignType = campaignType,
        targetingParamsList = mTP.map { "${it.key}:${it.value}" }
    )
}

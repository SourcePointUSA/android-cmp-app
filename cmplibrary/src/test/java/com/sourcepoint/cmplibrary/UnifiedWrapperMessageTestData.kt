package com.sourcepoint.cmplibrary

import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.TargetingParam

internal val campaigns = Campaigns(
    list = listOf(
        CampaignReqImpl(
            targetingParams = listOf(TargetingParam("location", "EU")),
            campaignsEnv = CampaignsEnv.STAGE,
            campaignType = CampaignType.GDPR,
            groupPmId = null
        ),
        CampaignReqImpl(
            targetingParams = listOf(TargetingParam("location", "US")),
            campaignsEnv = CampaignsEnv.STAGE,
            campaignType = CampaignType.CCPA,
            groupPmId = null
        )
    )
)
internal val campaignsGroupPmId = Campaigns(
    list = listOf(
        CampaignReqImpl(
            targetingParams = listOf(TargetingParam("location", "EU")),
            campaignsEnv = CampaignsEnv.STAGE,
            campaignType = CampaignType.GDPR,
            groupPmId = "613056"
        ),
        CampaignReqImpl(
            targetingParams = listOf(TargetingParam("location", "US")),
            campaignsEnv = CampaignsEnv.STAGE,
            campaignType = CampaignType.CCPA,
            groupPmId = "613056"
        )
    )
)

package com.sourcepoint.cmplibrary

import com.sourcepoint.cmplibrary.data.network.model.toJsonObjStringify
import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.TargetingParam

internal val campaigns = Campaigns(
    list = listOf(
        CampaignReqImpl(
            targetingParams = listOf(TargetingParam("location", "EU")).toJsonObjStringify(),
            campaignsEnv = CampaignsEnv.STAGE,
            campaignType = CampaignType.GDPR
        ),
        CampaignReqImpl(
            targetingParams = listOf(TargetingParam("location", "US")).toJsonObjStringify(),
            campaignsEnv = CampaignsEnv.STAGE,
            campaignType = CampaignType.CCPA
        )
    )
)

internal val uwMessDataTest = UnifiedMessageRequest(
    requestUUID = "test",
    consentLanguage = MessageLanguage.ENGLISH,
    campaigns = campaigns,
    propertyHref = "com.test",
    campaignsEnv = CampaignsEnv.STAGE,
    accountId = 1,
    hasCSP = true,
    includeData = IncludeData()
)

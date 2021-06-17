package com.sourcepoint.cmplibrary

import com.sourcepoint.cmplibrary.data.network.util.CampaignEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.TargetingParam
import com.sourcepoint.cmplibrary.model.ext.toJsonObjStringify

internal val campaigns = Campaigns(
    list = listOf(
        CampaignReqImpl(
            targetingParams = listOf(TargetingParam("location", "EU")).toJsonObjStringify(),
            campaignEnv = CampaignEnv.STAGE,
            campaignType = CampaignType.GDPR
        ),
        CampaignReqImpl(
            targetingParams = listOf(TargetingParam("location", "US")).toJsonObjStringify(),
            campaignEnv = CampaignEnv.STAGE,
            campaignType = CampaignType.CCPA
        )
    )
)

internal val uwMessDataTest = UnifiedMessageRequest(
    requestUUID = "test",
    consentLanguage = MessageLanguage.ENGLISH,
    campaigns = campaigns,
    propertyHref = "com.test",
    campaignEnv = CampaignEnv.STAGE.value,
    accountId = 1,
    hasCSP = true,
    includeData = IncludeData(localState = LocalState("string"))
)

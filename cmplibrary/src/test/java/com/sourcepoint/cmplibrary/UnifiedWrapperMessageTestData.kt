package com.sourcepoint.cmplibrary

import com.sourcepoint.cmplibrary.data.network.util.CampaignEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.* // ktlint-disable

internal val campaigns = Campaigns(
    list = listOf(
        CampaignReqImpl(
            campaignType = CampaignType.GDPR,
            targetingParamsList = listOf("location:EU")
        ),
        CampaignReqImpl(
            campaignType = CampaignType.CCPA,
            targetingParamsList = listOf("location:EU")
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

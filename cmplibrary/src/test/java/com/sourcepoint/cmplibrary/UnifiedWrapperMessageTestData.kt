package com.sourcepoint.cmplibrary

import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.TargetingParam
import org.json.JSONObject

internal val campaigns = Campaigns(
    list = listOf(
        CampaignReqImpl(
            targetingParams = listOf(TargetingParam("location", "EU")),
            campaignsEnv = CampaignsEnv.STAGE,
            campaignType = CampaignType.GDPR
        ),
        CampaignReqImpl(
            targetingParams = listOf(TargetingParam("location", "US")),
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

internal val uwMessDataTestPubData = UnifiedMessageRequest(
    requestUUID = "test",
    consentLanguage = MessageLanguage.ENGLISH,
    campaigns = campaigns,
    propertyHref = "com.test",
    campaignsEnv = CampaignsEnv.STAGE,
    accountId = 1,
    hasCSP = true,
    includeData = IncludeData(),
    pubData = JSONObject().apply {
        put("key_1", true)
        put("key_2", "test_pb")
        put("key_3", 1)
    }
)

package com.sourcepoint.cmplibrary

import com.sourcepoint.cmplibrary.data.network.util.CampaignEnv
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.TargetingParam
import com.sourcepoint.cmplibrary.model.ext.toJsonObjStringify

internal val campaigns = Campaigns(
    list = listOf(
        CampaignReqImpl(
            targetingParams = Array(1) {
                TargetingParam("location", "EU")
            }.toJsonObjStringify(),
            campaignEnv = CampaignEnv.STAGE,
            legislation = Legislation.GDPR
        ),
        CampaignReqImpl(
            targetingParams = Array(1) {
                TargetingParam("location", "US")
            }.toJsonObjStringify(),
            campaignEnv = CampaignEnv.STAGE,
            legislation = Legislation.CCPA
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
    includeData = IncludeData()
)

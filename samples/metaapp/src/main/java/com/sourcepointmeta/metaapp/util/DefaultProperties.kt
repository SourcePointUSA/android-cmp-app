package com.sourcepointmeta.metaapp.util

import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.data.localdatasource.StatusCampaign

val defaultProperty = Property(
    accountId = 22,
    propertyName = "ott.test.suite",
    timeout = 3000,
    authId = null,
    messageLanguage = "ENGLISH",
    pmTab = "DEFAULT",
    is_staging = false,
    targetingParameters = emptyList(),
    statusCampaignSet = setOf(StatusCampaign("ott.test.suite", CampaignType.GDPR, true)),
    gdprPmId = 579231L,
    ccpaPmId = 1L,
    campaignsEnv = CampaignsEnv.PUBLIC,
    propertyId = 22231
)

val defaultProperty1 = Property(
    accountId = 22,
    propertyName = "mobile.multicampaign.demo",
    propertyId = 16893,
    timeout = 3000,
    authId = null,
    messageLanguage = "ENGLISH",
    pmTab = "DEFAULT",
    is_staging = false,
    targetingParameters = emptyList(),
    statusCampaignSet = setOf(
        StatusCampaign("mobile.multicampaign.demo", CampaignType.GDPR, true),
        StatusCampaign("mobile.multicampaign.demo", CampaignType.CCPA, true),
    ),
    gdprPmId = 488393L,
    ccpaPmId = 509688L,
    campaignsEnv = CampaignsEnv.PUBLIC
)

val defaultProperty2 = Property(
    accountId = 22,
    propertyName = "mobile.multicampaign.native.demo",
    propertyId = 18958,
    timeout = 3000,
    authId = null,
    messageLanguage = "ENGLISH",
    pmTab = "DEFAULT",
    is_staging = false,
    targetingParameters = emptyList(),
    statusCampaignSet = setOf(
        StatusCampaign("mobile.multicampaign.native.demo", CampaignType.GDPR, true),
        StatusCampaign("mobile.multicampaign.native.demo", CampaignType.CCPA, true),
    ),
    gdprPmId = 545258L,
    ccpaPmId = 547869L,
    campaignsEnv = CampaignsEnv.PUBLIC
)

val defaultProperty3 = Property(
    accountId = 22,
    propertyName = "mobile.multicampaign.native.demo2",
    propertyId = 19210,
    timeout = 3000,
    authId = null,
    messageLanguage = "ENGLISH",
    pmTab = "DEFAULT",
    is_staging = false,
    targetingParameters = emptyList(),
    statusCampaignSet = setOf(
        StatusCampaign("mobile.multicampaign.native.demo2", CampaignType.GDPR, true),
        StatusCampaign("mobile.multicampaign.native.demo2", CampaignType.CCPA, true),
    ),
    gdprPmId = 548285L,
    ccpaPmId = 548276L,
    campaignsEnv = CampaignsEnv.PUBLIC
)

val defaultProperty4 = Property(
    accountId = 22,
    propertyName = "mobile.multicampaign.fully.native",
    propertyId = 22758,
    timeout = 3000,
    authId = null,
    messageLanguage = "ENGLISH",
    pmTab = "DEFAULT",
    is_staging = false,
    targetingParameters = emptyList(),
    statusCampaignSet = setOf(
        StatusCampaign("mobile.multicampaign.fully.native", CampaignType.GDPR, true),
        StatusCampaign("mobile.multicampaign.fully.native", CampaignType.CCPA, true),
    ),
    gdprPmId = 594218L,
    ccpaPmId = 594219L,
    campaignsEnv = CampaignsEnv.PUBLIC
)

val defaultProperty5 = Property(
    accountId = 22,
    propertyName = "mobile.multicampaign.fully.native2",
    propertyId = 22932,
    timeout = 3000,
    authId = null,
    messageLanguage = "ENGLISH",
    pmTab = "DEFAULT",
    is_staging = false,
    targetingParameters = emptyList(),
    statusCampaignSet = setOf(
        StatusCampaign("mobile.multicampaign.fully.native2", CampaignType.GDPR, true),
        StatusCampaign("mobile.multicampaign.fully.native2", CampaignType.CCPA, true),
    ),
    gdprPmId = 598486L,
    ccpaPmId = 598492L,
    campaignsEnv = CampaignsEnv.PUBLIC
)

val propList = listOf(
    defaultProperty1,
    defaultProperty2,
    defaultProperty3,
    defaultProperty4,
    defaultProperty5
)

package com.sourcepoint.cmplibrary.creation

import android.app.Activity
import android.content.Context
import com.example.cmplibrary.BuildConfig
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.SpConsentLib
//import com.sourcepoint.cmplibrary.SpConsentLibImpl
import com.sourcepoint.cmplibrary.SpConsentLibMobileCore
//import com.sourcepoint.cmplibrary.campaign.CampaignManager
//import com.sourcepoint.cmplibrary.campaign.create
//import com.sourcepoint.cmplibrary.consent.ClientEventManager
//import com.sourcepoint.cmplibrary.consent.ConsentManager
//import com.sourcepoint.cmplibrary.consent.ConsentManagerUtils
//import com.sourcepoint.cmplibrary.consent.create
//import com.sourcepoint.cmplibrary.core.ExecutorManager
//import com.sourcepoint.cmplibrary.core.create
//import com.sourcepoint.cmplibrary.data.Service
//import com.sourcepoint.cmplibrary.data.create
//import com.sourcepoint.cmplibrary.data.local.DataStorage
//import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa
//import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr
//import com.sourcepoint.cmplibrary.data.local.DataStorageUSNat
//import com.sourcepoint.cmplibrary.data.local.create
//import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
//import com.sourcepoint.cmplibrary.data.network.converter.create
import com.sourcepoint.cmplibrary.data.network.util.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManager
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManagerSingleton
import com.sourcepoint.cmplibrary.data.network.util.ResponseManager
import com.sourcepoint.cmplibrary.data.network.util.create
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.SpCampaign
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepoint.cmplibrary.model.exposed.SpGppConfig
import com.sourcepoint.cmplibrary.model.exposed.TargetingParam
//import com.sourcepoint.cmplibrary.util.ViewsManager
//import com.sourcepoint.cmplibrary.util.create
import com.sourcepoint.cmplibrary.util.extensions.hasSupportForLegacyUSPString
import com.sourcepoint.mobile_core.Coordinator
import com.sourcepoint.mobile_core.models.SPCampaign
import com.sourcepoint.mobile_core.models.SPCampaignEnv
import com.sourcepoint.mobile_core.models.SPCampaigns
import com.sourcepoint.mobile_core.models.SPPropertyName
import com.sourcepoint.mobile_core.models.SPTargetingParams
import com.sourcepoint.mobile_core.models.consents.State
import com.sourcepoint.mobile_core.network.SPClient
import com.sourcepoint.mobile_core.network.SourcepointClient
import com.sourcepoint.mobile_core.storage.Repository
import okhttp3.OkHttpClient
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

fun makeConsentLib(
    spConfig: SpConfig,
    activity: Activity,
    spClient: SpClient
): SpConsentLib {
//    val campaignManager: CampaignManager = CampaignManager.create(dataStorage, spConfig)
//    val errorManager = errorMessageManager(campaignManager, client)
//    val connManager = getConnectionManager(appCtx)
//    val viewManager = ViewsManager.create(WeakReference<Activity>(activity), connManager, spConfig.messageTimeout)
//    val execManager = ExecutorManager.create(appCtx)
//    val urlManager: HttpUrlManager = HttpUrlManagerSingleton
//    val consentManagerUtils: ConsentManagerUtils = ConsentManagerUtils.create(campaignManager, dataStorage)
//    val clientEventManager: ClientEventManager = ClientEventManager.create(
//        logger = logger,
//        executor = execManager,
//        spClient = spClient,
//        consentManagerUtils = consentManagerUtils,
//        connectionManager = connManager
//    )
//    val consentManager: ConsentManager =
//        ConsentManager.create(service, consentManagerUtils, env, logger, dataStorage, execManager, clientEventManager)

    return SpConsentLibMobileCore(
        coordinator = Coordinator(
            accountId = spConfig.accountId,
            propertyName = SPPropertyName.create(spConfig.propertyName),
            propertyId = spConfig.propertyId,
            campaigns = spConfig.campaigns.toCore(spConfig),
//            repository = Repository(),
//            state = State(),
//            spClient = SourcepointClient(
//                accountId = spConfig.accountId,
//                propertyName = spConfig.propertyName,
//                propertyId = spConfig.propertyId,
//                requestTimeoutInSeconds = spConfig.messageTimeout.toInt() / 1000 // TODO: pass timeout directly to Coordinator
//            )
        ),
        propertyId = spConfig.propertyId,
        language = spConfig.messageLanguage,
        spClient = spClient,
        context = activity.applicationContext,
        activity = WeakReference(activity)
    )
}

fun List<SpCampaign>.toCore(spConfig: SpConfig): SPCampaigns = SPCampaigns(
    gdpr = firstOrNull { it.campaignType == CampaignType.GDPR }?.toCore(),
    ccpa = firstOrNull { it.campaignType == CampaignType.CCPA }?.toCore(),
    usnat = firstOrNull { it.campaignType == CampaignType.USNAT }?.toCore(),
    environment = spConfig.campaignsEnv.toCore()
)

fun SpCampaign.toCore(): SPCampaign = SPCampaign(
    targetingParams = targetingParams.associate { it.key to it.value },
    groupPmId = groupPmId,
    supportLegacyUSPString = configParams.contains(ConfigOption.SUPPORT_LEGACY_USPSTRING),
    transitionCCPAAuth = configParams.contains(ConfigOption.TRANSITION_CCPA_AUTH),
    gppConfig = null // TODO implement
)

fun CampaignsEnv.toCore() = when(this) {
    CampaignsEnv.PUBLIC -> SPCampaignEnv.PUBLIC
    CampaignsEnv.STAGE -> SPCampaignEnv.STAGE
}

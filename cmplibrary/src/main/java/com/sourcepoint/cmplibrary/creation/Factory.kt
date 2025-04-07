package com.sourcepoint.cmplibrary.creation

import android.app.Activity
import android.preference.PreferenceManager
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.SpConsentLib
import com.sourcepoint.cmplibrary.SpConsentLibMobileCore
import com.sourcepoint.cmplibrary.creation.ConfigOption.*
import com.sourcepoint.cmplibrary.data.network.connection.ConnectionManager
import com.sourcepoint.cmplibrary.data.network.connection.ConnectionManagerImpl
import com.sourcepoint.cmplibrary.data.network.util.CampaignType.*
import com.sourcepoint.cmplibrary.legacy.migrateLegacyToNewState
import com.sourcepoint.cmplibrary.model.CampaignsEnv
import com.sourcepoint.cmplibrary.model.exposed.SpCampaign
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepoint.cmplibrary.model.exposed.SpGppConfig
import com.sourcepoint.mobile_core.Coordinator
import com.sourcepoint.mobile_core.models.SPCampaign
import com.sourcepoint.mobile_core.models.SPCampaignEnv
import com.sourcepoint.mobile_core.models.SPCampaigns
import com.sourcepoint.mobile_core.models.SPPropertyName
import java.lang.ref.WeakReference

fun makeConsentLib(spConfig: SpConfig, activity: Activity, spClient: SpClient) = makeConsentLib(
    spConfig = spConfig,
    activity = activity,
    spClient = spClient,
    connectionManager = ConnectionManagerImpl(activity.applicationContext)
)

fun makeConsentLib(
    spConfig: SpConfig,
    activity: Activity,
    spClient: SpClient,
    connectionManager: ConnectionManager = ConnectionManagerImpl(activity.applicationContext),
): SpConsentLib = SpConsentLibMobileCore(
    coordinator = Coordinator(
        accountId = spConfig.accountId,
        propertyName = SPPropertyName.create(spConfig.propertyName),
        propertyId = spConfig.propertyId,
        campaigns = spConfig.campaigns.toCore(spConfig),
        state = migrateLegacyToNewState(
            preferences = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext),
            accountId = spConfig.accountId,
            propertyId = spConfig.propertyId
        ),
        timeoutInSeconds = spConfig.messageTimeout.toInt() / 1000 // convert to seconds
    ),
    propertyId = spConfig.propertyId,
    language = spConfig.messageLanguage,
    spClient = spClient,
    context = activity.applicationContext,
    activity = WeakReference(activity),
    connectionManager = connectionManager
)

fun List<SpCampaign>.toCore(spConfig: SpConfig) = SPCampaigns(
    gdpr = firstOrNull { it.campaignType == GDPR }?.toCore(),
    ccpa = firstOrNull { it.campaignType == CCPA }?.toCore(),
    usnat = firstOrNull { it.campaignType == USNAT }?.toCore(spConfig.spGppConfig),
    environment = spConfig.campaignsEnv.toCore()
)

fun SpCampaign.toCore(gppConfig: SpGppConfig? = null) = SPCampaign(
    targetingParams = targetingParams.associate { it.key to it.value },
    groupPmId = groupPmId,
    supportLegacyUSPString = configParams.contains(SUPPORT_LEGACY_USPSTRING),
    transitionCCPAAuth = configParams.contains(TRANSITION_CCPA_AUTH),
    gppConfig = gppConfig?.toCore()
)

fun CampaignsEnv.toCore() = when (this) {
    CampaignsEnv.PUBLIC -> SPCampaignEnv.PUBLIC
    CampaignsEnv.STAGE -> SPCampaignEnv.STAGE
}

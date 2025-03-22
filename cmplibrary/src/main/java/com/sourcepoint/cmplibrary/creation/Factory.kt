package com.sourcepoint.cmplibrary.creation

import android.app.Activity
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.SpConsentLib
import com.sourcepoint.cmplibrary.SpConsentLibMobileCore
import com.sourcepoint.cmplibrary.data.network.connection.ConnectionManager
import com.sourcepoint.cmplibrary.data.network.connection.ConnectionManagerImpl
import com.sourcepoint.cmplibrary.data.network.util.CampaignType
import com.sourcepoint.cmplibrary.model.CampaignsEnv
import com.sourcepoint.cmplibrary.model.exposed.SpCampaign
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepoint.mobile_core.Coordinator
import com.sourcepoint.mobile_core.models.SPCampaign
import com.sourcepoint.mobile_core.models.SPCampaignEnv
import com.sourcepoint.mobile_core.models.SPCampaigns
import com.sourcepoint.mobile_core.models.SPPropertyName
import java.lang.ref.WeakReference

fun makeConsentLib(
    spConfig: SpConfig,
    activity: Activity,
    spClient: SpClient
) = makeConsentLib(
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
        // TODO: pass timeout directly to Coordinator
    ),
    propertyId = spConfig.propertyId,
    language = spConfig.messageLanguage,
    spClient = spClient,
    context = activity.applicationContext,
    activity = WeakReference(activity),
    connectionManager = connectionManager
)

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

fun CampaignsEnv.toCore() = when (this) {
    CampaignsEnv.PUBLIC -> SPCampaignEnv.PUBLIC
    CampaignsEnv.STAGE -> SPCampaignEnv.STAGE
}

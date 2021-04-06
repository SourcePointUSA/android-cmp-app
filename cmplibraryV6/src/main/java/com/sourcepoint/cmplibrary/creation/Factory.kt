package com.sourcepoint.cmplibrary.creation

import android.app.Activity
import android.content.Context
import com.sourcepoint.cmplibrary.SpConsentLib
import com.sourcepoint.cmplibrary.SpConsentLibImpl
import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.campaign.create
import com.sourcepoint.cmplibrary.consent.ConsentManager
import com.sourcepoint.cmplibrary.consent.create
import com.sourcepoint.cmplibrary.data.Service
import com.sourcepoint.cmplibrary.data.create
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr
import com.sourcepoint.cmplibrary.data.local.create
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.create
import com.sourcepoint.cmplibrary.data.network.util.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManager
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManagerSingleton
import com.sourcepoint.cmplibrary.data.network.util.ResponseManager
import com.sourcepoint.cmplibrary.data.network.util.create
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.model.CCPACampaign
import com.sourcepoint.cmplibrary.model.GDPRCampaign
import com.sourcepoint.cmplibrary.model.PrivacyManagerTabK
import com.sourcepoint.cmplibrary.util.ConnectionManager
import com.sourcepoint.cmplibrary.util.ExecutorManager
import com.sourcepoint.cmplibrary.util.ViewsManager
import com.sourcepoint.cmplibrary.util.create
import okhttp3.OkHttpClient
import java.lang.ref.WeakReference

fun makeConsentLib(
    gdpr: GDPRCampaign? = null,
    ccpa: CCPACampaign? = null,
    context: Activity,
    privacyManagerTab: PrivacyManagerTabK,
): SpConsentLib {
    return makeConsentLibEnv(
        gdpr = gdpr,
        ccpa = ccpa,
        context = context,
        privacyManagerTab = privacyManagerTab,
        env = Env.PROD
    )
}

/**
 * Use only for test!!!
 */
fun makeConsentLibEnv(
    gdpr: GDPRCampaign? = null,
    ccpa: CCPACampaign? = null,
    context: Activity,
    privacyManagerTab: PrivacyManagerTabK,
    env: Env
): SpConsentLib {

    val appCtx: Context = context.applicationContext
    val client = createClientInfo()
    val dataStorageGdpr = DataStorageGdpr.create(appCtx)
    val dataStorageCcpa = DataStorageCcpa.create(appCtx)
    val dataStorage = DataStorage.create(appCtx, dataStorageGdpr, dataStorageCcpa)
    val campaignManager: CampaignManager = CampaignManager.create(dataStorage).apply {
        gdpr?.let { addCampaign(Legislation.GDPR, it) }
        ccpa?.let { addCampaign(Legislation.CCPA, it) }
    }
    val errorManager = errorMessageManager(campaignManager, client)
    val logger = createLogger(errorManager)
    val jsonConverter = JsonConverter.create()
    val connManager = ConnectionManager.create(appCtx)
    val responseManager = ResponseManager.create(jsonConverter)
    val networkClient = networkClient(OkHttpClient(), responseManager, logger)
    val viewManager = ViewsManager.create(WeakReference<Activity>(context), connManager)
    val execManager = ExecutorManager.create(appCtx)
    val urlManager: HttpUrlManager = HttpUrlManagerSingleton
    val consentManager: ConsentManager = ConsentManager.create(campaignManager, dataStorage)
    val service: Service = Service.create(networkClient, campaignManager, consentManager, urlManager)

    return SpConsentLibImpl(
        urlManager = urlManager,
        pPrivacyManagerTab = privacyManagerTab,
        context = appCtx,
        pLogger = logger,
        pJsonConverter = jsonConverter,
        pConnectionManager = connManager,
        service = service,
        viewManager = viewManager,
        executor = execManager,
        campaignManager = campaignManager,
        consentManager = consentManager,
        env = env
    )
}

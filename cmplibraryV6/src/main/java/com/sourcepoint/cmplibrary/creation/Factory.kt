package com.sourcepoint.cmplibrary.creation

import android.app.Activity
import android.content.Context
import com.sourcepoint.cmplibrary.SpCacheObjet.fetchOrStore
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
import com.sourcepoint.cmplibrary.data.network.NetworkClient
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.create
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManager
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManagerSingleton
import com.sourcepoint.cmplibrary.data.network.util.ResponseManager
import com.sourcepoint.cmplibrary.data.network.util.create
import com.sourcepoint.cmplibrary.exception.ClientInfo
import com.sourcepoint.cmplibrary.exception.ErrorMessageManager
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.exception.Logger
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
    privacyManagerTab: PrivacyManagerTabK
): SpConsentLib {

    val appCtx: Context = context.applicationContext
    val client = fetchOrStore(ClientInfo::class.java) { createClientInfo() }
    val dataStorageGdpr = fetchOrStore(DataStorageGdpr::class.java) { DataStorageGdpr.create(appCtx) }
    val dataStorageCcpa = fetchOrStore(DataStorageCcpa::class.java) { DataStorageCcpa.create(appCtx) }
    val dataStorage = fetchOrStore(DataStorage::class.java) { DataStorage.create(appCtx, dataStorageGdpr, dataStorageCcpa) }
    val campaignManager: CampaignManager = fetchOrStore(CampaignManager::class.java) {
        CampaignManager.create(dataStorage).apply {
            gdpr?.let { addCampaign(Legislation.GDPR, it) }
            ccpa?.let { addCampaign(Legislation.CCPA, it) }
        }
    }
    val errorManager = fetchOrStore(ErrorMessageManager::class.java) { errorMessageManager(campaignManager, client) }
    val logger = fetchOrStore(Logger::class.java) { createLogger(errorManager) }
    val jsonConverter = fetchOrStore(JsonConverter::class.java) { JsonConverter.create() }
    val connManager = fetchOrStore(ConnectionManager::class.java) { ConnectionManager.create(appCtx) }
    val responseManager = fetchOrStore(ResponseManager::class.java) { ResponseManager.create(jsonConverter) }
    val networkClient = fetchOrStore(NetworkClient::class.java) { networkClient(OkHttpClient(), responseManager) }
    val service: Service = fetchOrStore(Service::class.java) { Service.create(networkClient, dataStorage, campaignManager) }
    val viewManager = fetchOrStore(ViewsManager::class.java) { ViewsManager.create(WeakReference<Activity>(context), connManager) }
    val execManager = fetchOrStore(ExecutorManager::class.java) { ExecutorManager.create(appCtx) }
    val urlManager: HttpUrlManager = HttpUrlManagerSingleton
    val consentManager: ConsentManager = fetchOrStore(ConsentManager::class.java) { ConsentManager.create(campaignManager, dataStorage) }

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
        consentManager = consentManager
    )
}

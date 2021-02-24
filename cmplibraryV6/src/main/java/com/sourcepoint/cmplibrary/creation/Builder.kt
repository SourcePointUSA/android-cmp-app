package com.sourcepoint.cmplibrary.creation

import android.app.Activity
import android.content.Context
import com.sourcepoint.cmplibrary.SpCacheObjet
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

class Builder {

    private var ccpa: CCPACampaign? = null
    private var gdpr: GDPRCampaign? = null
    private var authId: String? = null
    private var weakReference: WeakReference<Activity>? = null
    private var ott: Boolean = false
    private var privacyManagerTab: PrivacyManagerTabK? = null

    fun setGdprCampaign(gdpr: GDPRCampaign) = apply {
        this.gdpr = gdpr
    }

    fun isOtt(ott: Boolean) = apply {
        this.ott = ott
    }

    fun setCCPACampaign(ccpa: CCPACampaign) = apply {
        this.ccpa = ccpa
    }

    fun setAuthId(authId: String) = apply {
        this.authId = authId
    }

    fun setContext(context: Activity) = apply {
        this.weakReference = WeakReference(context)
    }

    fun setPrivacyManagerTab(privacyManagerTab: PrivacyManagerTabK) = apply {
        this.privacyManagerTab = privacyManagerTab
    }

    //    @Suppress("UNCHECKED_CAST")
//    fun <T : ConsentLib> build(clazz: Class<out T>): T {
    fun build(): SpConsentLib {

        val activityWeakRef: WeakReference<Activity> = weakReference ?: failParam("context")
        val appCtx: Context = activityWeakRef.get()?.applicationContext ?: failParam("context")
        val client = SpCacheObjet.fetchOrStore(ClientInfo::class.java) { createClientInfo() }
        val dataStorageGdpr = SpCacheObjet.fetchOrStore(DataStorageGdpr::class.java) { DataStorageGdpr.create(appCtx) }
        val dataStorageCcpa = SpCacheObjet.fetchOrStore(DataStorageCcpa::class.java) { DataStorageCcpa.create(appCtx) }
        val dataStorage = SpCacheObjet.fetchOrStore(DataStorage::class.java) { DataStorage.create(appCtx, dataStorageGdpr, dataStorageCcpa) }
        val campaignManager: CampaignManager = SpCacheObjet.fetchOrStore(CampaignManager::class.java) {
            CampaignManager.create(dataStorage).apply {
                gdpr?.let { addCampaign(Legislation.GDPR, it) }
                ccpa?.let { addCampaign(Legislation.CCPA, it) }
            }
        }
        val errorManager = SpCacheObjet.fetchOrStore(ErrorMessageManager::class.java) { errorMessageManager(campaignManager, client) }
        val logger = SpCacheObjet.fetchOrStore(Logger::class.java) { createLogger(errorManager) }
        val jsonConverter = SpCacheObjet.fetchOrStore(JsonConverter::class.java) { JsonConverter.create() }
        val connManager = SpCacheObjet.fetchOrStore(ConnectionManager::class.java) { ConnectionManager.create(appCtx) }
        val responseManager = SpCacheObjet.fetchOrStore(ResponseManager::class.java) { ResponseManager.create(jsonConverter) }
        val networkClient = SpCacheObjet.fetchOrStore(NetworkClient::class.java) { networkClient(OkHttpClient(), responseManager) }
        val service: Service = SpCacheObjet.fetchOrStore(Service::class.java) { Service.create(networkClient, dataStorage, campaignManager) }
        val viewManager = SpCacheObjet.fetchOrStore(ViewsManager::class.java) { ViewsManager.create(activityWeakRef, connManager) }
        val execManager = SpCacheObjet.fetchOrStore(ExecutorManager::class.java) { ExecutorManager.create(appCtx) }
        val urlManager: HttpUrlManager = HttpUrlManagerSingleton
        val consentManager: ConsentManager = SpCacheObjet.fetchOrStore(ConsentManager::class.java) { ConsentManager.create(campaignManager, dataStorage) }
        val pmTab = privacyManagerTab ?: PrivacyManagerTabK.FEATURES

        return SpConsentLibImpl(
            urlManager = urlManager,
            pPrivacyManagerTab = pmTab,
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

    private fun fail(m: String): Nothing = throw RuntimeException("Invalid class exception. $m is not an available option.")
    private fun failParam(p: String): Nothing = throw RuntimeException("$p cannot be null!!!")
}

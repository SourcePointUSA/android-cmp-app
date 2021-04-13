package com.sourcepoint.cmplibrary.creation

import android.app.Activity
import android.content.Context
import com.sourcepoint.cmplibrary.SpConsentLib
import com.sourcepoint.cmplibrary.SpConsentLibImpl
import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.campaign.create
import com.sourcepoint.cmplibrary.consent.ConsentManager
import com.sourcepoint.cmplibrary.consent.ConsentManagerUtils
import com.sourcepoint.cmplibrary.consent.create
import com.sourcepoint.cmplibrary.core.ExecutorManager
import com.sourcepoint.cmplibrary.core.create
import com.sourcepoint.cmplibrary.data.Service
import com.sourcepoint.cmplibrary.data.create
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr
import com.sourcepoint.cmplibrary.data.local.create
import com.sourcepoint.cmplibrary.data.network.connection.ConnectionManager
import com.sourcepoint.cmplibrary.data.network.connection.create
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.create
import com.sourcepoint.cmplibrary.data.network.util.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManager
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManagerSingleton
import com.sourcepoint.cmplibrary.data.network.util.ResponseManager
import com.sourcepoint.cmplibrary.data.network.util.create
import com.sourcepoint.cmplibrary.exception.InvalidArgumentException
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.model.CCPACampaign
import com.sourcepoint.cmplibrary.model.GDPRCampaign
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.SpConfig
import com.sourcepoint.cmplibrary.util.ViewsManager
import com.sourcepoint.cmplibrary.util.create
import okhttp3.OkHttpClient
import java.lang.ref.WeakReference

class Builder {

    private var spConfig: SpConfig? = null
    private var authId: String? = null
    private var weakReference: WeakReference<Activity>? = null
    private var ott: Boolean = false
    private var privacyManagerTab: PMTab? = null

    fun isOtt(ott: Boolean) = apply {
        this.ott = ott
    }

    fun setAuthId(authId: String) = apply {
        this.authId = authId
    }

    fun setContext(context: Activity) = apply {
        this.weakReference = WeakReference(context)
    }

    fun setPrivacyManagerTab(privacyManagerTab: PMTab) = apply {
        this.privacyManagerTab = privacyManagerTab
    }

    //    @Suppress("UNCHECKED_CAST")
//    fun <T : ConsentLib> build(clazz: Class<out T>): T {
    fun build(): SpConsentLib {

        val activityWeakRef: WeakReference<Activity> = weakReference ?: failParam("context")
        val appCtx: Context = activityWeakRef.get()?.applicationContext ?: failParam("context")
        val client = createClientInfo()
        val dataStorageGdpr = DataStorageGdpr.create(appCtx)
        val dataStorageCcpa = DataStorageCcpa.create(appCtx)
        val dataStorage = DataStorage.create(appCtx, dataStorageGdpr, dataStorageCcpa)
        val campaignManager: CampaignManager = CampaignManager.create(dataStorage).apply {
            spConfig.also { spp ->
                spp?.let {
                    if (!it.propertyName.contains(validPattern)) {
                        throw InvalidArgumentException(
                            description = """
                PropertyName can only include letters, numbers, '.', ':', '-' and '/'. (string) passed is invalid
                            """.trimIndent()
                        )
                    }
                }
                spp?.campaigns?.forEach {
                    when (it.legislation) {
                        Legislation.GDPR -> addCampaign(
                            it.legislation,
                            GDPRCampaign(it.environment, it.targetingParams)
                        )
                        Legislation.CCPA -> addCampaign(
                            it.legislation,
                            CCPACampaign(it.environment, it.targetingParams)
                        )
                    }
                }
            }
        }
        val errorManager = errorMessageManager(campaignManager, client)
        val logger = createLogger(errorManager)
        val pmTab = privacyManagerTab ?: PMTab.FEATURES
        val jsonConverter = JsonConverter.create()
        val connManager = ConnectionManager.create(appCtx)
        val responseManager = ResponseManager.create(jsonConverter)
        val networkClient = networkClient(OkHttpClient(), responseManager, logger)
        val viewManager = ViewsManager.create(activityWeakRef, connManager)
        val execManager = ExecutorManager.create(appCtx)
        val urlManager: HttpUrlManager = HttpUrlManagerSingleton
        val consentManagerUtils: ConsentManagerUtils = ConsentManagerUtils.create(campaignManager, dataStorage, logger)
        val service: Service = Service.create(networkClient, campaignManager, consentManagerUtils, dataStorage, logger)
        val consentManager: ConsentManager = ConsentManager.create(service, consentManagerUtils, Env.PROD, logger, dataStorage, execManager)

        return SpConsentLibImpl(
            urlManager = urlManager,
            context = appCtx,
            pLogger = logger,
            pJsonConverter = jsonConverter,
            pConnectionManager = connManager,
            service = service,
            viewManager = viewManager,
            executor = execManager,
            campaignManager = campaignManager,
            consentManagerUtils = consentManagerUtils,
            consentManager = consentManager,
            env = Env.STAGE
        )
    }

    private fun fail(m: String): Nothing = throw RuntimeException("Invalid class exception. $m is not an available option.")
    private fun failParam(p: String): Nothing = throw RuntimeException("$p cannot be null!!!")
}

package com.sourcepoint.cmplibrary.creation

import android.app.Activity
import android.content.Context
import com.sourcepoint.cmplibrary.SpConsentLib
import com.sourcepoint.cmplibrary.SpConsentLibImpl
import com.sourcepoint.cmplibrary.data.Service
import com.sourcepoint.cmplibrary.data.create
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr
import com.sourcepoint.cmplibrary.data.local.create
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.create
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManager
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManagerSingleton
import com.sourcepoint.cmplibrary.data.network.util.ResponseManager
import com.sourcepoint.cmplibrary.data.network.util.create
import com.sourcepoint.cmplibrary.model.Campaign
import com.sourcepoint.cmplibrary.model.PrivacyManagerTabK
import com.sourcepoint.cmplibrary.util.ConnectionManager
import com.sourcepoint.cmplibrary.util.ExecutorManager
import com.sourcepoint.cmplibrary.util.ViewsManager
import com.sourcepoint.cmplibrary.util.create
import okhttp3.OkHttpClient
import java.lang.ref.WeakReference

class Builder {

    private var accountId: Int? = null
    private var propertyName: String? = null
    private var authId: String? = null
    private var propertyId: Int? = null
    private var pmId: String? = null
    private var weakReference: WeakReference<Activity>? = null
    private var ott: Boolean = false
    private var privacyManagerTab: PrivacyManagerTabK? = null

    fun setAccountId(accountId: Int) = apply {
        this.accountId = accountId
    }

    fun isOtt(ott: Boolean) = apply {
        this.ott = ott
    }

    fun setPropertyName(property: String) = apply {
        this.propertyName = property
    }

    fun setAuthId(authId: String) = apply {
        this.authId = authId
    }

    fun setPmId(pmId: String) = apply {
        this.pmId = pmId
    }

    fun setPropertyId(propertyId: Int) = apply {
        this.propertyId = propertyId
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
        val account = createAccount()
        val client = createClientInfo()
        val errorManager = errorMessageManager(account, client)
        val logger = createLogger(errorManager)
        val pmTab = privacyManagerTab ?: PrivacyManagerTabK.FEATURES
        val jsonConverter = JsonConverter.create()
        val connManager = ConnectionManager.create(appCtx)
        val responseManager = ResponseManager.create(jsonConverter)
        val networkClient = networkClient(OkHttpClient(), responseManager)
        val dataStorageGdpr = DataStorageGdpr.create(appCtx)
        val dataStorageCcpa = DataStorageCcpa.create(appCtx)
        val dataStorage = DataStorage.create(appCtx, dataStorageGdpr, dataStorageCcpa)
        val service: Service = Service.create(networkClient, dataStorage)
        val viewManager = ViewsManager.create(activityWeakRef, connManager)
        val execManager = ExecutorManager.create(appCtx)
        val urlManager: HttpUrlManager = HttpUrlManagerSingleton

        return SpConsentLibImpl(
            urlManager,
            account,
            pmTab,
            appCtx,
            logger,
            jsonConverter,
            connManager,
            service,
            viewManager,
            execManager
        )

//        return when (clazz) {
//            GDPRConsentLib::class.java -> {
//                GDPRConsentLibImpl(
//                    urlManager,
//                    account,
//                    pmTab,
//                    appCtx,
//                    logger,
//                    jsonConverter,
//                    connManager,
//                    networkClient,
//                    dataStorage,
//                    viewManager,
//                    execManager
//                ) as T
//            }
//            CCPAConsentLib::class.java -> {
//                CCPAConsentLibImpl() as T
//            }
//            else -> fail(clazz.name)
//        }
    }

    private fun createAccount(): Campaign {
        return Campaign(
            propertyId = propertyId ?: failParam("propertyId"),
            propertyName = propertyName ?: failParam("property"),
            accountId = accountId ?: failParam("accountId"),
            pmId = pmId ?: failParam("pmId"),
        )
    }

    private fun fail(m: String): Nothing = throw RuntimeException("Invalid class exception. $m is not an available option.")
    private fun failParam(p: String): Nothing = throw RuntimeException("$p cannot be null!!!")
}

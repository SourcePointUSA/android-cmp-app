package com.sourcepoint.cmplibrary.creation

import android.app.Activity
import android.content.Context
import com.sourcepoint.cmplibrary.Campaign
import com.sourcepoint.cmplibrary.ConsentLib
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.local.create
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.create
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManager
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManagerSingleton
import com.sourcepoint.cmplibrary.data.network.util.ResponseManager
import com.sourcepoint.cmplibrary.data.network.util.create
import com.sourcepoint.cmplibrary.legislation.ccpa.CCPAConsentLib
import com.sourcepoint.cmplibrary.legislation.ccpa.CCPAConsentLibImpl
import com.sourcepoint.cmplibrary.legislation.gdpr.GDPRConsentLib
import com.sourcepoint.cmplibrary.legislation.gdpr.GDPRConsentLibImpl
import com.sourcepoint.cmplibrary.util.ConnectionManager
import com.sourcepoint.cmplibrary.util.ExecutorManager
import com.sourcepoint.cmplibrary.util.ViewsManager
import com.sourcepoint.cmplibrary.util.create
import com.sourcepoint.gdpr_cmplibrary.PrivacyManagerTab
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
    private var privacyManagerTab: PrivacyManagerTab? = null

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

    fun setPrivacyManagerTab(privacyManagerTab: PrivacyManagerTab) = apply {
        this.privacyManagerTab = privacyManagerTab
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : ConsentLib> build(clazz: Class<out T>): T {

        val activityWeakRef: WeakReference<Activity> = weakReference ?: failParam("context")
        val appCtx: Context = activityWeakRef.get()?.applicationContext ?: failParam("context")
        val account = createAccount()
        val client = createClientInfo()
        val errorManager = errorMessageManager(account, client)
        val logger = createLogger(errorManager)
        val pmTab = privacyManagerTab ?: PrivacyManagerTab.FEATURES
        val jsonConverter = JsonConverter.create()
        val connManager = ConnectionManager.create(appCtx)
        val responseManager = ResponseManager.create(jsonConverter)
        val networkClient = networkClient(OkHttpClient(), responseManager)
        val dataStorage = DataStorage.create(appCtx)
        val viewManager = ViewsManager.create(activityWeakRef)
        val execManager = ExecutorManager.create(appCtx)
        val urlManager: HttpUrlManager = HttpUrlManagerSingleton

        return when (clazz) {
            GDPRConsentLib::class.java -> {
                GDPRConsentLibImpl(
                    urlManager,
                    account,
                    pmTab,
                    appCtx,
                    logger,
                    jsonConverter,
                    connManager,
                    networkClient,
                    dataStorage,
                    viewManager,
                    execManager
                ) as T
            }
            CCPAConsentLib::class.java -> {
                CCPAConsentLibImpl() as T
            }
            else -> fail(clazz.name)
        }
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

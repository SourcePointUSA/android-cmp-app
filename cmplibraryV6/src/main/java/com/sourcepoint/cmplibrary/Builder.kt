package com.sourcepoint.cmplibrary

import android.app.Activity
import com.example.gdpr_cmplibrary.BuildConfig
import com.sourcepoint.cmplibrary.data.network.NetworkClient
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.create
import com.sourcepoint.cmplibrary.data.network.createNetworkClient
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManagerSingleton
import com.sourcepoint.cmplibrary.data.network.util.ResponseManager
import com.sourcepoint.cmplibrary.data.network.util.create
import com.sourcepoint.cmplibrary.legislation.ccpa.CCPAConsentLib
import com.sourcepoint.cmplibrary.legislation.ccpa.CCPAConsentLibImpl
import com.sourcepoint.cmplibrary.legislation.gdpr.GDPRConsentLib
import com.sourcepoint.cmplibrary.legislation.gdpr.GDPRConsentLibImpl
import com.sourcepoint.cmplibrary.util.ConnectionManager
import com.sourcepoint.cmplibrary.util.create
import com.sourcepoint.gdpr_cmplibrary.PrivacyManagerTab
import com.sourcepoint.gdpr_cmplibrary.exception.* //ktlint-disable
import okhttp3.OkHttpClient

class Builder {

    private var accountId: Int? = null
    private var propertyName: String? = null
    private var authId: String? = null
    private var propertyId: Int? = null
    private var pmId: String? = null
    private var context: Activity? = null
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
        this.context = context
    }

    fun setPrivacyManagerTab(privacyManagerTab: PrivacyManagerTab) = apply {
        this.privacyManagerTab = privacyManagerTab
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : ConsentLib> build(clazz: Class<out T>): T {

        val ctx = context ?: failParam("context")
        val account = createAccount()
        val client = createClientInfo()
        val errorManager = errorMessageManager(account, client)
        val logger = createLogger(errorManager)
        val pmTab = privacyManagerTab ?: PrivacyManagerTab.FEATURES
        val jsonConverter = JsonConverter.create()
        val connManager = ConnectionManager.create(ctx)
        val responseManager = ResponseManager.create(jsonConverter)
        val networkClient = networkClient(OkHttpClient(), responseManager)

        return when (clazz) {
            GDPRConsentLib::class.java -> {
                GDPRConsentLibImpl(account, pmTab, ctx, logger, jsonConverter, connManager, networkClient) as T
            }
            CCPAConsentLib::class.java -> {
                CCPAConsentLibImpl() as T
            }
            else -> fail(clazz.name)
        }
    }

    private fun createAccount(): Account {
        return Account(
            propertyId = propertyId ?: failParam("propertyId"),
            propertyName = propertyName ?: failParam("property"),
            accountId = accountId ?: failParam("accountId"),
            pmId = pmId ?: failParam("pmId"),
        )
    }

    private fun createClientInfo(): ClientInfo {
        return ClientInfo(
            clientVersion = "5.X.X",
            deviceFamily = "android",
            osVersion = "30"
        )
    }

    private fun errorMessageManager(a: Account, client: ClientInfo): ErrorMessageManager {
        return createErrorManager(
            accountId = a.accountId,
            propertyId = a.propertyId,
            propertyHref = "http://dev.local",
            clientInfo = client,
            legislation = Legislation.GDPR
        )
    }

    private fun createLogger(errorMessageManager: ErrorMessageManager): Logger {
        return createLogger(
            networkClient = OkHttpClient(),
            errorMessageManager = errorMessageManager,
            url = BuildConfig.LOGGER_URL
        )
    }

    private fun networkClient(netClient : OkHttpClient, responseManage : ResponseManager) : NetworkClient {
        return createNetworkClient(
            httpClient = netClient,
            responseManager = responseManage,
            url = HttpUrlManagerSingleton.inAppUrlMessage
        )
    }

    private fun fail(m: String): Nothing = throw RuntimeException("Invalid class exception. $m is not an available option.")
    private fun failParam(p: String): Nothing = throw RuntimeException("$p cannot be null!!!")
}

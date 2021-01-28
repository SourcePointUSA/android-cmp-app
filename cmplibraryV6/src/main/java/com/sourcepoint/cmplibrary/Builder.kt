package com.sourcepoint.cmplibrary

import android.content.Context
import com.example.cmplibrary.BuildConfig
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.create
import com.sourcepoint.cmplibrary.legislation.ccpa.CCPAConsentLibClient
import com.sourcepoint.cmplibrary.legislation.ccpa.CCPAConsentLibImpl
import com.sourcepoint.cmplibrary.legislation.gdpr.GDPRConsentLibClient
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
    private var context: Context? = null
    private var privacyManagerTab: PrivacyManagerTab? = null

    fun setAccountId(accountId: Int) = apply {
        this.accountId = accountId
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

    fun setContext(context: Context) = apply {
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

        return when (clazz) {

            GDPRConsentLibClient::class.java -> {
                GDPRConsentLibImpl(account, pmTab, ctx, logger, jsonConverter, connManager) as T
            }
            CCPAConsentLibClient::class.java -> {
                CCPAConsentLibImpl() as T
            }

            else -> fail(clazz.name)
        }
    }

    internal fun createAccount(): Account {
        return Account(
            propertyId = propertyId ?: failParam("propertyId"),
            propertyName = propertyName ?: failParam("property"),
            accountId = accountId ?: failParam("accountId"),
            pmId = pmId ?: failParam("pmId"),
        )
    }

    internal fun createClientInfo(): ClientInfo {
        return ClientInfo(
            clientVersion = "5.X.X",
            deviceFamily = "android",
            osVersion = "30"
        )
    }

    internal fun errorMessageManager(a: Account, client: ClientInfo): ErrorMessageManager {
        return createErrorManager(
            accountId = a.accountId,
            propertyId = a.propertyId,
            propertyHref = "http://dev.local",
            clientInfo = client,
            legislation = Legislation.GDPR
        )
    }

    internal fun createLogger(errorMessageManager: ErrorMessageManager): Logger {
        return createLogger(
            networkClient = OkHttpClient(),
            errorMessageManager = errorMessageManager,
            url = BuildConfig.LOGGER_URL
        )
    }

    internal fun fail(m: String): Nothing = throw RuntimeException("Invalid class exception. $m is not an available option.")
    internal fun failParam(p: String): Nothing = throw RuntimeException("$p cannot be null!!!")
}

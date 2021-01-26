package com.sourcepoint.cmplibrary

import android.content.Context
import com.sourcepoint.cmplibrary.ccpa.CCPAConsentLibClient
import com.sourcepoint.cmplibrary.ccpa.CCPAConsentLibImpl
import com.sourcepoint.cmplibrary.gdpr.GDPRConsentLibClient
import com.sourcepoint.cmplibrary.gdpr.GDPRConsentLibImpl
import com.sourcepoint.gdpr_cmplibrary.PrivacyManagerTab

class BuilderV6 {

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
        checkParameter()
        return when (clazz) {

            GDPRConsentLibClient::class.java -> {
                (
                    GDPRConsentLibImpl(
                        accountId ?: fail("accountId"),
                        propertyName ?: fail("propertyName"),
                        propertyId ?: fail("propertyId"),
                        pmId ?: fail("pmId"),
                        authId,
                        privacyManagerTab,
                        context ?: fail("context"),
                    ) as? T) ?: fail(this::class.java.name)
            }
            CCPAConsentLibClient::class.java -> {
                (CCPAConsentLibImpl() as? T) ?: fail(this::class.java.name)
            }

            else -> fail(clazz.name)
        }

    }

    private fun checkParameter() {
        accountId ?: failParam("accountId")
        pmId ?: failParam("pmId")
        context ?: failParam("context")
        propertyId ?: failParam("propertyId")
        propertyName ?: failParam("property")
    }

    private fun fail(m: String): Nothing = throw RuntimeException("Invalid class exception. $m is not an available option.")
    private fun failParam(p: String): Nothing = throw RuntimeException("$p cannot be null!!!")
}
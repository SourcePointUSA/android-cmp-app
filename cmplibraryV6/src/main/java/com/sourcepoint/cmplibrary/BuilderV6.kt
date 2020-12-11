package com.sourcepoint.cmplibrary

import android.content.Context
import com.sourcepoint.cmplibrary.gdpr.ClientInteraction
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
    private var clientInteraction: ClientInteraction? = null

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

    fun setClientInteraction(clientInteraction: ClientInteraction) = apply {
        this.clientInteraction = clientInteraction
    }



    @Suppress("UNCHECKED_CAST")
    fun <T : ConsentLib> build(clazz: Class<out T>): T {
        checkParameter()
        return when (clazz) {

//            IGDPRConsentLib::class.java -> (createGDPR(accountId!!, property!!, propertyId!!, pmId!!, context!!) as? T)
//                ?: fail("this")

            GDPRConsentLibClient::class.java -> (
                GDPRConsentLibImpl(
                    accountId ?: fail("accountId"),
                    propertyName ?: fail("propertyName"),
                    propertyId ?: fail("propertyId"),
                    pmId ?: fail("pmId"),
                    authId,
                    privacyManagerTab,
                    context ?: fail("context"),
                    clientInteraction ?: fail("clientInteraction")) as? T
                ) ?: fail(this::class.java.name)

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
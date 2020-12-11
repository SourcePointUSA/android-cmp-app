package com.sourcepoint.gdpr_cmplibrary

import android.content.Context


open class ConsentLibBuilderTest(
    accountId: Int,
    property: String,
    propertyId: Int,
    pmId: String,
    context: Context) : ConsentLibBuilder(accountId, property, propertyId, pmId, context) {
    fun getAuthId() = authId

    override fun build(): GDPRConsentLibTest {
        return GDPRConsentLibTest(this)
    }
}

open class GDPRConsentLibTest(b: ConsentLibBuilder) : GDPRConsentLib(b) {
    companion object {
        @JvmStatic
        fun newBuilder_(accountId: Int, property: String, propertyId: Int, pmId: String, context: Context): ConsentLibBuilderTest {
            return ConsentLibBuilderTest(accountId, property, propertyId, pmId, context)
        }
    }

    fun setAuthId(authId : String?){
        if (didConsentUserChange(authId, storeClient.authId)) storeClient.clearAllData()
        storeClient.authId = authId
    }

}
package com.sourcepoint.cmplibrary.data.local

import android.content.SharedPreferences

internal interface DataStorage : DataStorageGdpr, DataStorageCcpa {
    companion object {
        const val LOCAL_DATA_VERSION_KEY = "sp.key.localDataVersion"
        const val LOCAL_DATA_VERSION_HARDCODED_VALUE = 1

        const val LOCAL_STATE = "sp.key.local.state"
        const val LOCAL_STATE_OLD = "key_local_state"
        const val SAVED_CONSENT = "sp.key.saved.consent"
        const val MESSAGES_OPTIMIZED = "sp.key.messages"
        const val CONSENT_STATUS = "sp.key.consent.status"
        const val META_DATA_RESP = "sp.key.meta.data"
        const val PV_DATA_RESP = "sp.key.pv.data"
        const val CHOICE_RESP = "sp.key.choice"
        const val DATA_RECORDED_CONSENT = "sp.key.data.recorded.consent"

        const val KEY_PROPERTY_ID = "sp.key.config.propertyId"

        const val CONSENT_STATUS_RESPONSE = "sp.key.consent.status.response"
        const val GDPR_CONSENT_STATUS = "sp.gdpr.key.consent.status"
        const val CCPA_CONSENT_STATUS = "sp.ccpa.key.consent.status"
        const val MESSAGES_OPTIMIZED_LOCAL_STATE = "sp.key.messages.v7.local.state"
        const val NON_KEYED_LOCAL_STATE = "sp.key.messages.v7.nonKeyedLocalState"
    }

    override val preference: SharedPreferences

    var localDataVersion: Int
    var savedConsent: Boolean
    var messagesOptimized: String?
    var consentStatus: String?
    var metaDataResp: String?
    var choiceResp: String?
    var dataRecordedConsent: String?

    var consentStatusResponse: String?
    var gdprConsentStatus: String?
    val gdprApplies: Boolean
    val ccpaApplies: Boolean
    var ccpaConsentStatus: String?
    var messagesOptimizedLocalState: String?
    var nonKeyedLocalState: String?
    var propertyId: Int

    fun saveLocalState(value: String)
    fun getLocalState(): String?
    fun updateLocalDataVersion()
}

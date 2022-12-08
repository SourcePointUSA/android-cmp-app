package com.sourcepoint.cmplibrary.data.local

import android.content.SharedPreferences

internal interface DataStorage : DataStorageGdpr, DataStorageCcpa {

    companion object {
        const val LOCAL_STATE = "sp.key.local.state"
        const val PROPERTY_PRIORITY_DATA = "sp.key.property.priority.data"
        const val PROPERTY_ID = "sp.key.property.id"
        const val SAVED_CONSENT = "sp.key.saved.consent"
        const val MESSAGES_V7 = "sp.key.messages"
        const val CONSENT_STATUS = "sp.key.consent.status"
        const val META_DATA_RESP = "sp.key.meta.data"
        const val PV_DATA_RESP = "sp.key.pv.data"
        const val CHOICE_RESP = "sp.key.choice"
        const val DATA_RECORDED_CONSENT = "sp.key.data.recorded.consent"

        const val CONSENT_STATUS_RESPONSE = "sp.key.consent.status.response"
        const val GDPR_CONSENT_STATUS = "sp.gdpr.key.consent.status"
        const val CCPA_CONSENT_STATUS = "sp.ccpa.key.consent.status"
        const val MESSAGES_V7_LOCAL_STATE = "sp.key.messages.v7.local.state"
    }

    override val preference: SharedPreferences

    var savedConsent: Boolean
    var messagesV7: String?
    var consentStatus: String?
    var metaDataResp: String?
    var pvDataResp: String?
    var choiceResp: String?
    var dataRecordedConsent: String?

    var consentStatusResponse: String?
    var gdprConsentStatus: String?
    var ccpaConsentStatus: String?
    var messagesV7LocalState: String?

    fun savePropertyPriorityData(value: String)
    fun saveLocalState(value: String)
    fun getLocalState(): String?
    fun getPropertyPriorityData(): String?
}

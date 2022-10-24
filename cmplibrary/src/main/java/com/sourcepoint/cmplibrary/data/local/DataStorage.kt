package com.sourcepoint.cmplibrary.data.local

import android.content.SharedPreferences

internal interface DataStorage : DataStorageGdpr, DataStorageCcpa {

    companion object {
        const val LOCAL_STATE = "sp.key.local.state"
        const val PROPERTY_PRIORITY_DATA = "sp.key.property.priority.data"
        const val PROPERTY_ID = "sp.key.property.id"
        const val SAVED_CONSENT = "sp.key.saved.consent"
        const val TRIGGER_BY_SAMPLE = "sp.key.trigger.by.sample"
        const val MESSAGES_V7 = "sp.key.messages"
        const val CONSENT_STATUS_RESPONSE = "sp.key.consent.status.response"
        const val GDPR_CONSENT_STATUS = "sp.key.gdpr.consent.status"
        const val META_DATA_RESP = "sp.key.meta.data"
        const val PV_DATA_RESP = "sp.key.pv.data"
        const val CHOICE_RESP = "sp.key.choice"
        const val DATA_RECORDED_CONSENT = "sp.key.data.recorded.consent"
    }

    override val preference: SharedPreferences

    var savedConsent: Boolean
    var shouldTriggerBySample: Boolean
    var messagesV7: String?
    var consentStatusResponse: String?
    var gdprConsentStatus: String?
    var metaDataResp: String?
    var pvDataResp: String?
    var choiceResp: String?
    var dataRecordedConsent: String?

    fun savePropertyId(value: Int)
    fun savePropertyPriorityData(value: String)
    fun saveLocalState(value: String)
    fun getLocalState(): String?
    fun getPropertyId(): Int
    fun getPropertyPriorityData(): String?
}

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
        const val CONSENT_STATUS = "sp.key.consent.status"
    }

    override val preference: SharedPreferences

    var savedConsent: Boolean
    var shouldTriggerBySample: Boolean
    var messagesV7: String?
    var consentStatus: String?

    fun savePropertyId(value: Int)
    fun savePropertyPriorityData(value: String)
    fun saveLocalState(value: String)
    fun getLocalState(): String?
    fun getPropertyId(): Int
    fun getPropertyPriorityData(): String?
}

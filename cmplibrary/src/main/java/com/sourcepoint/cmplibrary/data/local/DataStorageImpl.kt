package com.sourcepoint.cmplibrary.data.local

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.CCPA_CONSENT_STATUS
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.CHOICE_RESP
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.CONSENT_STATUS
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.CONSENT_STATUS_RESPONSE
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.DATA_RECORDED_CONSENT
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.GDPR_CONSENT_STATUS
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.LOCAL_STATE
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.MESSAGES_V7
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.MESSAGES_V7_LOCAL_STATE
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.META_DATA_RESP
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.PROPERTY_ID
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.PROPERTY_PRIORITY_DATA
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.PV_DATA_RESP
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.SAVED_CONSENT

/**
 * Factory method to create an instance of a [DataStorage] using its implementation
 * @param context is the client application context
 * @return an instance of the [DataStorageImpl] implementation
 */
internal fun DataStorage.Companion.create(
    context: Context,
    dsGdpr: DataStorageGdpr,
    dsCcpa: DataStorageCcpa
): DataStorage = DataStorageImpl(context, dsGdpr, dsCcpa)

private class DataStorageImpl(
    context: Context,
    val dsGdpr: DataStorageGdpr,
    val dsCcpa: DataStorageCcpa
) : DataStorage,
    DataStorageGdpr by dsGdpr,
    DataStorageCcpa by dsCcpa {

    override val preference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    override var savedConsent: Boolean
        get() = preference.getBoolean(SAVED_CONSENT, false)
        set(value) {
            preference
                .edit()
                .putBoolean(SAVED_CONSENT, value)
                .apply()
        }

    override var messagesV7: String?
        get() = preference.getString(MESSAGES_V7, null)
        set(value) {
            preference
                .edit()
                .putString(MESSAGES_V7, value)
                .apply()
        }

    override var consentStatusResponse: String?
        get() = preference.getString(CONSENT_STATUS_RESPONSE, null)
        set(value) {
            preference
                .edit()
                .putString(CONSENT_STATUS_RESPONSE, value)
                .apply()
        }

    override var gdprConsentStatus: String?
        get() = preference.getString(GDPR_CONSENT_STATUS, null)
        set(value) {
            preference
                .edit()
                .putString(GDPR_CONSENT_STATUS, value)
                .apply()
        }

    override var ccpaConsentStatus: String?
        get() = preference.getString(CCPA_CONSENT_STATUS, null)
        set(value) {
            preference
                .edit()
                .putString(CCPA_CONSENT_STATUS, value)
                .apply()
        }

    override var messagesV7LocalState: String?
        get() = preference.getString(MESSAGES_V7_LOCAL_STATE, null)
        set(value) {
            preference
                .edit()
                .putString(MESSAGES_V7_LOCAL_STATE, value)
                .apply()
        }

    override var consentStatus: String?
        get() = preference.getString(CONSENT_STATUS, null)
        set(value) {
            preference
                .edit()
                .putString(CONSENT_STATUS, value)
                .apply()
        }

    override var metaDataResp: String?
        get() = preference.getString(META_DATA_RESP, null)
        set(value) {
            preference
                .edit()
                .putString(META_DATA_RESP, value)
                .apply()
        }

    override var pvDataResp: String?
        get() = preference.getString(PV_DATA_RESP, null)
        set(value) {
            preference
                .edit()
                .putString(PV_DATA_RESP, value)
                .apply()
        }

    override var choiceResp: String?
        get() = preference.getString(CHOICE_RESP, null)
        set(value) {
            preference
                .edit()
                .putString(CHOICE_RESP, value)
                .apply()
        }

    override var dataRecordedConsent: String?
        get() = preference.getString(DATA_RECORDED_CONSENT, null)
        set(value) {
            preference
                .edit()
                .putString(DATA_RECORDED_CONSENT, value)
                .apply()
        }

    override fun saveLocalState(value: String) {
        preference
            .edit()
            .putString(LOCAL_STATE, value)
            .apply()
    }

    override fun getLocalState(): String? {
        return preference.getString(LOCAL_STATE, null)
    }

    override fun savePropertyId(value: Int) {
        preference
            .edit()
            .putInt(PROPERTY_ID, value)
            .apply()
    }

    override fun savePropertyPriorityData(value: String) {
        preference
            .edit()
            .putString(PROPERTY_PRIORITY_DATA, value)
            .apply()
    }

    override fun getPropertyId(): Int {
        return preference.getInt(PROPERTY_ID, -1)
    }

    override fun getPropertyPriorityData(): String? {
        return preference.getString(PROPERTY_PRIORITY_DATA, null)
    }

    override fun clearAll() {
        dsCcpa.clearAll()
        dsGdpr.clearAll()
        preference
            .edit()
            .remove(LOCAL_STATE)
            .remove(PROPERTY_PRIORITY_DATA)
            .remove(PROPERTY_ID)
            .remove(SAVED_CONSENT)
            .remove(MESSAGES_V7)
            .remove(META_DATA_RESP)
            .remove(PV_DATA_RESP)
            .remove(CHOICE_RESP)
            .remove(DATA_RECORDED_CONSENT)
            .remove(CONSENT_STATUS_RESPONSE)
            .remove(GDPR_CONSENT_STATUS)
            .remove(CONSENT_STATUS)
            .remove(CCPA_CONSENT_STATUS)
            .remove(MESSAGES_V7_LOCAL_STATE)
            .apply()
    }

    companion object
}

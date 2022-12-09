package com.sourcepoint.cmplibrary.data.local

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa.Companion.CCPA_CONSENT_RESP
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa.Companion.CCPA_DATE_CREATED
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa.Companion.CCPA_JSON_MESSAGE
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa.Companion.CCPA_MESSAGE_METADATA
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa.Companion.CCPA_POST_CHOICE_RESP
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa.Companion.CCPA_SAMPLING_VALUE
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa.Companion.CCPA_STATUS
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa.Companion.CONSENT_CCPA_UUID_KEY
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa.Companion.KEY_CCPA
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa.Companion.KEY_CCPA_APPLIES
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa.Companion.KEY_CCPA_CHILD_PM_ID
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa.Companion.KEY_CCPA_MESSAGE_SUBCATEGORY
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa.Companion.KEY_CCPA_OLD
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa.Companion.KEY_IAB_US_PRIVACY_STRING
import com.sourcepoint.cmplibrary.data.network.converter.fail
import com.sourcepoint.cmplibrary.data.network.model.toCCPAUserConsent
import com.sourcepoint.cmplibrary.model.exposed.CCPAConsentInternal
import com.sourcepoint.cmplibrary.model.toTreeMap
import com.sourcepoint.cmplibrary.util.check
import org.json.JSONObject

internal interface DataStorageCcpa {

    val preference: SharedPreferences

    var ccpaApplies: Boolean
    var ccpaChildPmId: String?

    var ccpaPostChoiceResp: String?
    var ccpaStatus: String?
    var ccpaMessageMetaData: String?
    var ccpaConsentUuid: String?

    var ccpaDateCreated: String?

    var ccpaSamplingValue: Double

    fun saveCcpa(value: String)
    fun saveCcpaConsentResp(value: String)
    var usPrivacyString: String?
    fun saveCcpaMessage(value: String)

    fun getCcpa(): String?
    fun getCcpaConsentResp(): String?
    fun getCcpaMessage(): String
    fun clearCcpaConsent()
    fun clearAll()

    companion object {
        const val KEY_CCPA = "sp.ccpa.key"
        const val KEY_CCPA_OLD = "sp.key.ccpa"
        const val KEY_CCPA_APPLIES = "sp.ccpa.key.applies"
        const val KEY_CCPA_CHILD_PM_ID = "sp.ccpa.key.childPmId"
        const val CCPA_CONSENT_RESP = "sp.ccpa.consent.resp"
        const val CCPA_JSON_MESSAGE = "sp.ccpa.json.message"
        const val CONSENT_CCPA_UUID_KEY = "sp.ccpa.consentUUID"
        const val KEY_IAB_US_PRIVACY_STRING = "IABUSPrivacy_String"
        const val KEY_CCPA_MESSAGE_SUBCATEGORY = "sp.ccpa.key.message.subcategory"
        const val CCPA_POST_CHOICE_RESP = "sp.ccpa.key.post.choice"
        const val CCPA_STATUS = "sp.ccpa.key.v7.status"
        const val CCPA_MESSAGE_METADATA = "sp.ccpa.key.message.metadata"
        const val CCPA_DATE_CREATED = "sp.ccpa.key.date.created"
        const val CCPA_SAMPLING_VALUE = "sp.ccpa.key.sampling"
    }
}

internal fun DataStorageCcpa.Companion.create(
    context: Context
): DataStorageCcpa = DataStorageCcpaImpl(context)

private class DataStorageCcpaImpl(context: Context) : DataStorageCcpa {

    override val preference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    override fun saveCcpa(value: String) {
        preference
            .edit()
            .putString(KEY_CCPA, value)
            .apply()
    }

    override fun getCcpa(): String? {
        return preference.getString(KEY_CCPA, null)
    }

    override var ccpaApplies: Boolean
        get() = preference.getBoolean(KEY_CCPA_APPLIES, false)
        set(value) {
            preference
                .edit()
                .putBoolean(KEY_CCPA_APPLIES, value)
                .apply()
        }

    override var ccpaChildPmId: String?
        get() = preference.getString(KEY_CCPA_CHILD_PM_ID, null)
        set(value) {
            preference
                .edit()
                .putString(KEY_CCPA_CHILD_PM_ID, value)
                .apply()
        }

    override fun saveCcpaConsentResp(value: String) {

        preference
            .edit()
            .putString(CCPA_CONSENT_RESP, value)
            .apply()
    }

    override var usPrivacyString: String?
        get() = preference.getString(KEY_IAB_US_PRIVACY_STRING, null)
        set(value) {
            preference
                .edit()
                .putString(KEY_IAB_US_PRIVACY_STRING, value)
                .apply()
        }

    override var ccpaConsentUuid: String?
        get() = preference.getString(CONSENT_CCPA_UUID_KEY, null)
        set(value) {
            value?.let {
                preference
                    .edit()
                    .putString(CONSENT_CCPA_UUID_KEY, it)
                    .apply()
            }
        }

    override var ccpaDateCreated: String?
        get() = preference.getString(CCPA_DATE_CREATED, null)
        set(value) {
            value?.let {
                preference
                    .edit()
                    .putString(CCPA_DATE_CREATED, it)
                    .apply()
            }
        }

    override var ccpaSamplingValue: Double
        get() = preference.getFloat(CCPA_SAMPLING_VALUE, 1.0F).toDouble()
        set(value) {
            preference
                .edit()
                .putFloat(CCPA_SAMPLING_VALUE, value.toFloat())
                .apply()
        }

    override fun saveCcpaMessage(value: String) {
        preference
            .edit()
            .putString(CCPA_JSON_MESSAGE, value)
            .apply()
    }

    override fun getCcpaConsentResp(): String? {
        return preference.getString(CCPA_CONSENT_RESP, null)
    }

    override fun getCcpaMessage(): String {
        return preference.getString(CCPA_JSON_MESSAGE, "")!!
    }

    override fun clearCcpaConsent() {
        preference
            .edit()
            .remove(CCPA_CONSENT_RESP)
            .apply()

        preference
            .edit()
            .remove(KEY_IAB_US_PRIVACY_STRING)
            .apply()
    }

    override var ccpaPostChoiceResp: String?
        get() = preference.getString(CCPA_POST_CHOICE_RESP, null)
        set(value) {
            preference
                .edit()
                .putString(CCPA_POST_CHOICE_RESP, value)
                .apply()
        }

    override var ccpaStatus: String?
        get() = preference.getString(CCPA_STATUS, null)
        set(value) {
            preference
                .edit()
                .putString(CCPA_STATUS, value)
                .apply()
        }

    override var ccpaMessageMetaData: String?
        get() = preference.getString(CCPA_MESSAGE_METADATA, null)
        set(value) {
            preference
                .edit()
                .putString(CCPA_MESSAGE_METADATA, value)
                .apply()
        }

    override fun clearAll() {
        preference
            .edit()
            .remove(KEY_CCPA)
            .remove(KEY_CCPA_OLD)
            .remove(KEY_CCPA_APPLIES)
            .remove(CCPA_CONSENT_RESP)
            .remove(CCPA_JSON_MESSAGE)
            .remove(CONSENT_CCPA_UUID_KEY)
            .remove(KEY_CCPA_CHILD_PM_ID)
            .remove(KEY_IAB_US_PRIVACY_STRING)
            .remove(KEY_CCPA_MESSAGE_SUBCATEGORY)
            .remove(CCPA_POST_CHOICE_RESP)
            .remove(CCPA_STATUS)
            .remove(CCPA_MESSAGE_METADATA)
            .remove(CCPA_DATE_CREATED)
            .remove(CCPA_SAMPLING_VALUE)
            .apply()
    }
}

internal fun DataStorageCcpa.getCCPAConsent(): Either<CCPAConsentInternal> = check {
    getCcpaConsentResp()
        .also { if (it == null || it.isBlank()) fail("CCPAConsent is not saved in the the storage!!") }
        .let { JSONObject(it) }
        .toTreeMap()
        .toCCPAUserConsent(uuid = this.ccpaConsentUuid, applies = this.ccpaApplies)
}

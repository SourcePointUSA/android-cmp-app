package com.sourcepoint.cmplibrary.data.local

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa.Companion.CCPA_CONSENT_RESP
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa.Companion.CCPA_JSON_MESSAGE
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa.Companion.CONSENT_CCPA_UUID_KEY
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa.Companion.KEY_CCPA
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa.Companion.KEY_CCPA_1203
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa.Companion.KEY_CCPA_APPLIES

internal interface DataStorageCcpa {

    val preference: SharedPreferences
    var ccpaApplies: Boolean

    fun saveCcpa(value: String)
    fun saveCcpa1203(value: String)
    fun saveCcpaConsentResp(value: String)
    fun saveCcpaConsentUuid(value: String)
    fun saveCcpaMessage(value: String)

    fun getCcpa(): String?
    fun getCcpa1203(): String?
    fun getCcpaConsentResp(): String
    fun getCcpaMessage(): String
    fun getCcpaConsentUuid(): String?
    fun clearCcpaConsent()
    fun clearAll()

    companion object {
        const val KEY_CCPA = "key_ccpa"
        const val KEY_CCPA_1203 = "key_ccpa_1203"
        const val KEY_CCPA_APPLIES = "key_ccpa_applies"
        const val CCPA_CONSENT_RESP = "ccpa_consent_resp"
        const val CCPA_JSON_MESSAGE = "ccpa_json_message"
        const val CONSENT_CCPA_UUID_KEY = "sp.ccpa.consentUUID"
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

    override fun saveCcpa1203(value: String) {
        preference
            .edit()
            .putString(KEY_CCPA_1203, value)
            .apply()
    }

    override fun getCcpa(): String? {
        return preference.getString(KEY_CCPA, null)
    }

    override fun getCcpa1203(): String? {
        return preference.getString(KEY_CCPA_1203, null)
    }

    override var ccpaApplies: Boolean
        get() = preference.getBoolean(KEY_CCPA_APPLIES, false)
        set(value) {
            preference
                .edit()
                .putBoolean(KEY_CCPA_APPLIES, value)
                .apply()
        }

    override fun saveCcpaConsentResp(value: String) {
        preference
            .edit()
            .putString(CCPA_CONSENT_RESP, value)
            .apply()
    }

    override fun saveCcpaConsentUuid(value: String) {
        preference
            .edit()
            .putString(CONSENT_CCPA_UUID_KEY, value)
            .apply()
    }

    override fun saveCcpaMessage(value: String) {
        preference
            .edit()
            .putString(CCPA_JSON_MESSAGE, value)
            .apply()
    }

    override fun getCcpaConsentResp(): String {
        return preference.getString(CCPA_CONSENT_RESP, "")!!
    }

    override fun getCcpaMessage(): String {
        return preference.getString(CCPA_JSON_MESSAGE, "")!!
    }

    override fun getCcpaConsentUuid(): String? {
        return preference.getString(CONSENT_CCPA_UUID_KEY, null)
    }

    override fun clearCcpaConsent() {
        preference
            .edit()
            .putString(CCPA_CONSENT_RESP, "")
            .apply()
    }

    override fun clearAll() {
        preference.edit().clear().apply()
    }

    private fun fail(param: String): Nothing = throw RuntimeException("$param not fund in local storage.")
}

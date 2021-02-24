package com.sourcepoint.cmplibrary.data.local

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa.Companion.CCPA_CONSENT_RESP
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa.Companion.CCPA_JSON_MESSAGE
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa.Companion.KEY_CCPA
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa.Companion.KEY_CCPA_APPLIES
import com.sourcepoint.cmplibrary.data.network.converter.toCCPA
import com.sourcepoint.cmplibrary.data.network.model.Ccpa
import com.sourcepoint.cmplibrary.util.Either
import com.sourcepoint.cmplibrary.util.check

internal interface DataStorageCcpa {

    val preference: SharedPreferences
    var ccpaApplies: Boolean

    fun saveCcpa(ccpa: Ccpa)
    fun saveCcpaConsentResp(value: String)
    fun saveCcpaMessage(value: String)

    fun getCcpa(): Either<Ccpa>
    fun getCcpaConsentResp(): String
    fun getCcpaMessage(): String?
    fun clearCcpaConsent()

    companion object {
        const val KEY_CCPA = "key_ccpa"
        const val KEY_CCPA_APPLIES = "key_ccpa_applies"
        const val CCPA_CONSENT_RESP = "ccpa_consent_resp"
        const val CCPA_JSON_MESSAGE = "ccpa_json_message"
    }
}

internal fun DataStorageCcpa.Companion.create(
    context: Context
): DataStorageCcpa = DataStorageCcpaImpl(context)

private class DataStorageCcpaImpl(context: Context) : DataStorageCcpa {

    override val preference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    override fun saveCcpa(ccpa: Ccpa) {

        val json = ccpa.thisContent.toString()

        preference
            .edit()
            .putString(KEY_CCPA, json)
            .apply()
    }

    override fun getCcpa(): Either<Ccpa> = check {
        preference.getString(KEY_CCPA, null)
            ?.toCCPA()
            ?: fail("Ccpa")
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

    override fun saveCcpaMessage(value: String) {
        preference
            .edit()
            .putString(CCPA_JSON_MESSAGE, value)
            .apply()
    }

    override fun getCcpaConsentResp(): String {
        return preference.getString(CCPA_CONSENT_RESP, "")!!
    }

    override fun getCcpaMessage(): String? {
        return preference.getString(CCPA_JSON_MESSAGE, null)
    }

    override fun clearCcpaConsent() {
        preference
            .edit()
            .putString(CCPA_CONSENT_RESP, "")
            .apply()
    }

    private fun fail(param: String): Nothing = throw RuntimeException("$param not fund in local storage.")
}

package com.sourcepoint.cmplibrary.data.local

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.AUTH_ID_KEY
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.CMP_SDK_ID_KEY
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.CMP_SDK_VERSION_KEY
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.CONSENT_UUID_KEY
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.DEFAULT_AUTH_ID
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.DEFAULT_EMPTY_CONSENT_STRING
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.DEFAULT_EMPTY_UUID
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.DEFAULT_META_DATA
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.EU_CONSENT_KEY
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.GDPR_APPLIED_LEGISLATION
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.GDPR_CONSENT_RESP
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.GDPR_JSON_MESSAGE
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.GDPR_TCData
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.IABTCF_KEY_PREFIX
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.KEY_GDPR_APPLIES
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.META_DATA_KEY
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.USER_CONSENT_KEY
import com.sourcepoint.cmplibrary.model.getMap
import com.sourcepoint.cmplibrary.model.toTreeMap
import com.sourcepoint.cmplibrary.util.check
import org.json.JSONObject
import java.util.*  //ktlint-disable

internal interface DataStorageGdpr {

    val preference: SharedPreferences

    var gdprApplies: Boolean

    fun saveGdpr(value: String)
    fun getGdpr(): String?

    /** store data */
    fun saveTcData(deferredMap: Map<String, Any?>)
    fun saveAuthId(value: String)
    fun saveEuConsent(value: String)
    fun saveMetaData(value: String)
    fun saveGdprConsentUuid(value: String?)
    fun saveAppliedLegislation(value: String)
    fun saveGdprConsentResp(value: String)
    fun saveGdprMessage(value: String)

    /** fetch data */
    fun getTcData(): Map<String, Any?>
    fun getAuthId(): String
    fun getEuConsent(): String
    fun getMetaData(): String
    fun getGdprConsentUuid(): String?
    fun getAppliedLegislation(): String
    fun getGdprConsentResp(): String
    fun getGdprMessage(): String

    fun clearGdprConsent()
    fun clearInternalData()
    fun clearAll()

    companion object {
        const val CONSENT_UUID_KEY = "sp.gdpr.consentUUID"
        const val META_DATA_KEY = "sp.gdpr.metaData"
        const val EU_CONSENT_KEY = "sp.gdpr.euconsent"
        const val USER_CONSENT_KEY = "sp.gdpr.userConsent"
        const val AUTH_ID_KEY = "sp.gdpr.authId"
        const val DEFAULT_EMPTY_UUID = ""
        const val CMP_SDK_ID_KEY = "IABTCF_CmpSdkID"
        const val CMP_SDK_ID = 6
        const val CMP_SDK_VERSION_KEY = "IABTCF_CmpSdkVersion"
        const val CMP_SDK_VERSION = 2
        const val DEFAULT_EMPTY_CONSENT_STRING = ""
        const val DEFAULT_META_DATA = "{}"
        val DEFAULT_AUTH_ID: String? = null
        const val IABTCF_KEY_PREFIX = "IABTCF_"
        const val KEY_GDPR_APPLIES = "sp.key.gdpr.applies"
        const val GDPR_CONSENT_RESP = "sp.gdpr.consent.resp"
        const val GDPR_JSON_MESSAGE = "sp.gdpr.json.message"
        const val GDPR_APPLIED_LEGISLATION = "sp.gdpr.applied.legislation"
        const val GDPR_TCData = "TCData"
    }
}

internal fun DataStorageGdpr.Companion.create(
    context: Context
): DataStorageGdpr = DataStorageGdprImpl(context)

private class DataStorageGdprImpl(context: Context) : DataStorageGdpr {

    companion object {
        const val KEY_GDPR = "sp.key.gdpr"
    }

    override val preference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    override var gdprApplies: Boolean
        get() = preference.getBoolean(KEY_GDPR_APPLIES, false)
        set(value) {
            preference
                .edit()
                .putBoolean(KEY_GDPR_APPLIES, value)
                .apply()
        }

    override fun saveGdpr(value: String) {
        preference
            .edit()
            .putString(KEY_GDPR, value)
            .apply()
    }

    override fun getGdpr(): String? {
        return preference.getString(KEY_GDPR, null)
    }

    override fun saveTcData(deferredMap: Map<String, Any?>) {
        val spEditor = preference.edit()
        deferredMap.forEach { entry ->
            when (val value = entry.value) {
                is Int -> {
                    spEditor.putInt(entry.key, value)
                }
                is String -> {
                    spEditor.putString(entry.key, value)
                }
            }
        }
        spEditor.apply()
    }

    override fun saveAuthId(value: String) {
        preference
            .edit()
            .putString(DataStorageGdpr.AUTH_ID_KEY, value)
            .apply()
    }

    override fun saveEuConsent(value: String) {
        preference
            .edit()
            .putString(EU_CONSENT_KEY, value)
            .apply()
    }

    override fun saveMetaData(value: String) {
        preference
            .edit()
            .putString(META_DATA_KEY, value)
            .apply()
    }

    override fun saveGdprConsentUuid(value: String?) {
        value?.let {
            preference
                .edit()
                .putString(CONSENT_UUID_KEY, it)
                .apply()
        }
    }

    override fun saveGdprConsentResp(value: String) {

        check {
            JSONObject(value)
                .toTreeMap()
                .getMap(GDPR_TCData)
                ?.let { tc -> saveTcData(tc) }
        }

        preference
            .edit()
            .putString(GDPR_CONSENT_RESP, value)
            .apply()
    }

    override fun saveGdprMessage(value: String) {
        preference
            .edit()
            .putString(GDPR_JSON_MESSAGE, value)
            .apply()
    }

    override fun saveAppliedLegislation(value: String) {
        preference
            .edit()
            .putString(GDPR_APPLIED_LEGISLATION, value)
            .apply()
    }

    override fun getTcData(): Map<String, Any?> {
        val res = TreeMap<String, Any?>()
        val map: Map<String, *> = preference.all
        map
            .filter { it.key.startsWith(IABTCF_KEY_PREFIX) }
            .forEach { res[it.key] = it.value }
        return res
    }

    override fun getAuthId(): String {
        return preference.getString(AUTH_ID_KEY, "")!!
    }

    override fun getEuConsent(): String {
        return preference.getString(EU_CONSENT_KEY, "")!!
    }

    override fun getMetaData(): String {
        return preference.getString(META_DATA_KEY, "")!!
    }

    override fun getGdprConsentUuid(): String? {
        return preference.getString(CONSENT_UUID_KEY, null)
    }

    override fun getAppliedLegislation(): String {
        return preference.getString(GDPR_APPLIED_LEGISLATION, "")!!
    }

    override fun getGdprConsentResp(): String {
        return preference.getString(GDPR_CONSENT_RESP, "")!!
    }

    override fun getGdprMessage(): String {
        return preference.getString(GDPR_JSON_MESSAGE, "")!!
    }

    override fun clearInternalData() {
        preference
            .edit()
            .remove(CONSENT_UUID_KEY)
            .remove(META_DATA_KEY)
            .remove(EU_CONSENT_KEY)
            .remove(AUTH_ID_KEY)
            .apply()
    }

    override fun clearAll() {
        val listIABTCF = preference.all.filter { prefix -> prefix.key.startsWith(IABTCF_KEY_PREFIX) }.keys
        preference.edit()
            .apply {
                remove(CONSENT_UUID_KEY)
                remove(META_DATA_KEY)
                remove(EU_CONSENT_KEY)
                remove(USER_CONSENT_KEY)
                remove(AUTH_ID_KEY)
                remove(DEFAULT_EMPTY_UUID)
                remove(CMP_SDK_ID_KEY)
                remove(CMP_SDK_VERSION_KEY)
                remove(DEFAULT_EMPTY_CONSENT_STRING)
                remove(DEFAULT_META_DATA)
                remove(DEFAULT_AUTH_ID)
                remove(KEY_GDPR_APPLIES)
                remove(GDPR_CONSENT_RESP)
                remove(GDPR_JSON_MESSAGE)
                remove(GDPR_APPLIED_LEGISLATION)
                remove(GDPR_TCData)
                remove(KEY_GDPR)
                listIABTCF.forEach { remove(it) }
            }.apply()
    }

    override fun clearGdprConsent() {

        val spEditor = preference.edit()
        preference
            .all
            .filter { it.key.startsWith(IABTCF_KEY_PREFIX) }
            .forEach { entry -> spEditor.remove(entry.key) }
        spEditor.apply()

        preference
            .edit()
            .remove(GDPR_CONSENT_RESP)
            .apply()
    }

    private fun fail(param: String): Nothing = throw RuntimeException("$param not fund in local storage.")
}

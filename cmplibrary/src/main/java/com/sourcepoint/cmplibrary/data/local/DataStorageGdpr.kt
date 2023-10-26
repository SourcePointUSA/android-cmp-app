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
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.GDPR_CONSENT_RESP
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.GDPR_DATE_CREATED
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.GDPR_JSON_MESSAGE
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.GDPR_MESSAGE_METADATA
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.GDPR_POST_CHOICE_RESP
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.GDPR_SAMPLING_RESULT
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.GDPR_SAMPLING_VALUE
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.GDPR_TCData
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.IABTCF_KEY_PREFIX
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.KEY_GDPR_APPLIES
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.KEY_GDPR_APPLIES_OLD
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.KEY_GDPR_CHILD_PM_ID
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.KEY_GDPR_MESSAGE_SUBCATEGORY
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.KEY_GDPR_MESSAGE_SUBCATEGORY_OLD
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.META_DATA_KEY
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.USER_CONSENT_KEY
import com.sourcepoint.cmplibrary.model.getMap
import com.sourcepoint.cmplibrary.model.toTreeMap
import com.sourcepoint.cmplibrary.util.check
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull
import org.json.JSONObject
import java.util.TreeMap

internal interface DataStorageGdpr {

    val preference: SharedPreferences

    var gdprChildPmId: String?
    var gdprPostChoiceResp: String?
    var gdprConsentUuid: String?
    var gdprMessageMetaData: String?

    var tcData: Map<String, Any?>

    var gdprDateCreated: String?

    var gdprSamplingValue: Double
    var gdprSamplingResult: Boolean?

    fun saveGdpr(value: String)
    fun getGdpr(): String?

    /** store data */
    fun saveAuthId(value: String?)
    fun saveGdprConsentResp(value: String)

    /** fetch data */
    fun getAuthId(): String?
    fun getGdprConsentResp(): String?
    fun getGdprMessage(): String

    fun clearGdprConsent()
    fun clearTCData()
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
        const val KEY_GDPR_APPLIES = "sp.gdpr.key.applies"
        const val KEY_GDPR_APPLIES_OLD = "sp.key.gdpr.applies"
        const val KEY_GDPR_CHILD_PM_ID = "sp.gdpr.key.childPmId"
        const val KEY_GDPR_MESSAGE_SUBCATEGORY = "sp.gdpr.key.message.subcategory"
        const val KEY_GDPR_MESSAGE_SUBCATEGORY_OLD = "sp.key.gdpr.message.subcategory"
        const val GDPR_CONSENT_RESP = "sp.gdpr.consent.resp"
        const val GDPR_JSON_MESSAGE = "sp.gdpr.json.message"
        const val GDPR_TCData = "TCData"
        const val GDPR_POST_CHOICE_RESP = "sp.gdpr.key.post.choice"
        const val GDPR_MESSAGE_METADATA = "sp.gdpr.key.message.metadata"
        const val GDPR_DATE_CREATED = "sp.gdpr.key.date.created"
        const val GDPR_SAMPLING_VALUE = "sp.gdpr.key.sampling"
        const val GDPR_SAMPLING_RESULT = "sp.gdpr.key.sampling.result"
    }
}

internal fun DataStorageGdpr.Companion.create(
    context: Context
): DataStorageGdpr = DataStorageGdprImpl(context)

private class DataStorageGdprImpl(context: Context) : DataStorageGdpr {

    companion object {
        const val KEY_GDPR = "sp.gdpr.key"
        const val KEY_GDPR_OLD = "sp.key.gdpr"
    }

    override val preference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    override var gdprChildPmId: String?
        get() = preference.getString(KEY_GDPR_CHILD_PM_ID, null)
        set(value) {
            preference
                .edit()
                .putString(KEY_GDPR_CHILD_PM_ID, value)
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

    override var tcData: Map<String, Any?>
        get() {
            val res = TreeMap<String, Any?>()
            val map: Map<String, *> = preference.all
            map
                .filter { it.key.startsWith(IABTCF_KEY_PREFIX) }
                .forEach { res[it.key] = it.value }
            return res
        }
        set(value) {
            val spEditor = preference.edit()
            value.forEach { entry ->
                val primitive = (entry.value as? JsonPrimitive)
                val isThisAString = primitive?.isString ?: false
                if (isThisAString) {
                    primitive?.content
                        ?.let { spEditor.putString(entry.key, it) }
                } else {
                    primitive?.intOrNull
                        ?.let { spEditor.putInt(entry.key, it) }
                }
            }
            spEditor.apply()
        }

    override fun saveAuthId(value: String?) {
        preference
            .edit()
            .putString(AUTH_ID_KEY, value)
            .apply()
    }

    override fun saveGdprConsentResp(value: String) {

        check {
            JSONObject(value)
                .toTreeMap()
                .getMap(GDPR_TCData)
                ?.let { tc -> tcData = tc }
        }

        preference
            .edit()
            .putString(GDPR_CONSENT_RESP, value)
            .apply()
    }

    override fun getAuthId(): String? {
        return preference.getString(AUTH_ID_KEY, null)
    }

    override fun getGdprConsentResp(): String? {
        return preference.getString(GDPR_CONSENT_RESP, null)
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

    override var gdprPostChoiceResp: String?
        get() = preference.getString(GDPR_POST_CHOICE_RESP, null)
        set(value) {
            preference
                .edit()
                .putString(GDPR_POST_CHOICE_RESP, value)
                .apply()
        }

    override var gdprDateCreated: String?
        get() = preference.getString(GDPR_DATE_CREATED, null)
        set(value) {
            preference
                .edit()
                .putString(GDPR_DATE_CREATED, value)
                .apply()
        }

    override var gdprSamplingValue: Double
        get() = preference.getFloat(GDPR_SAMPLING_VALUE, 1.0F).toDouble()
        set(value) {
            preference
                .edit()
                .putFloat(GDPR_SAMPLING_VALUE, value.toFloat())
                .apply()
        }

    override var gdprSamplingResult: Boolean?
        get() {
            return if (preference.contains(GDPR_SAMPLING_RESULT))
                preference.getBoolean(GDPR_SAMPLING_RESULT, false)
            else null
        }
        set(value) {
            value?.let {
                preference
                    .edit()
                    .putBoolean(GDPR_SAMPLING_RESULT, it)
                    .apply()
            }
        }

    override var gdprConsentUuid: String?
        get() = preference.getString(CONSENT_UUID_KEY, null)
        set(value) {
            value?.let {
                preference
                    .edit()
                    .putString(CONSENT_UUID_KEY, it)
                    .apply()
            }
        }

    override var gdprMessageMetaData: String?
        get() = preference.getString(GDPR_MESSAGE_METADATA, null)
        set(value) {
            preference
                .edit()
                .putString(GDPR_MESSAGE_METADATA, value)
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
                remove(KEY_GDPR_APPLIES_OLD)
                remove(GDPR_CONSENT_RESP)
                remove(GDPR_JSON_MESSAGE)
                remove(KEY_GDPR_MESSAGE_SUBCATEGORY)
                remove(KEY_GDPR_MESSAGE_SUBCATEGORY_OLD)
                remove(GDPR_TCData)
                remove(KEY_GDPR)
                remove(KEY_GDPR_OLD)
                remove(KEY_GDPR_CHILD_PM_ID)
                remove(GDPR_POST_CHOICE_RESP)
                remove(GDPR_DATE_CREATED)
                remove(GDPR_MESSAGE_METADATA)
                remove(GDPR_SAMPLING_VALUE)
                remove(GDPR_SAMPLING_RESULT)
                listIABTCF.forEach { remove(it) }
            }.apply()
    }

    override fun clearGdprConsent() {

        clearTCData()

        preference
            .edit()
            .remove(GDPR_CONSENT_RESP)
            .apply()
    }

    override fun clearTCData() {
        val spEditor = preference.edit()
        preference
            .all
            .filter { it.key.startsWith(IABTCF_KEY_PREFIX) }
            .forEach { entry -> spEditor.remove(entry.key) }
        spEditor.apply()
    }

    private fun fail(param: String): Nothing = throw RuntimeException("$param not fund in local storage.")
}

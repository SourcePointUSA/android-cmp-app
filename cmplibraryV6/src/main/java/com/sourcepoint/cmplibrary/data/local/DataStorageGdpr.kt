package com.sourcepoint.cmplibrary.data.local

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.fasterxml.jackson.jr.ob.JSON
import com.fasterxml.jackson.jr.ob.impl.DeferredMap
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.AUTH_ID_KEY
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.CONSENT_UUID_KEY
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.EU_CONSENT_KEY
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.IABTCF_KEY_PREFIX
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.META_DATA_KEY
import com.sourcepoint.cmplibrary.data.network.converter.toGDPR
import com.sourcepoint.cmplibrary.data.network.model.Gdpr
import com.sourcepoint.cmplibrary.util.Either
import com.sourcepoint.cmplibrary.util.check

internal interface DataStorageGdpr {
    val preference: SharedPreferences
    fun saveGdpr(gdpr: Gdpr)
    fun getGdpr(): Either<Gdpr>

    /** store data */
    fun saveTcData(deferredMap: DeferredMap)
    fun saveAuthId(value: String)
    fun saveEuConsent(value: String)
    fun saveMetaData(value: String)
    fun saveConsentUuid(value: String)
    fun saveAppliedLegislation(value: String)

    /** fetch data */
    fun getTcData(): DeferredMap
    fun getAuthId(): String
    fun getEuConsent(): String
    fun getMetaData(): String
    fun getConsentUuid(): String
    fun getAppliedLegislation(): String

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
    }
}

internal fun DataStorageGdpr.Companion.create(
    context: Context
): DataStorageGdpr = DataStorageGdprImpl(context)

private class DataStorageGdprImpl(context: Context) : DataStorageGdpr {

    companion object {
        const val KEY_GDPR = "key_gdpr"
    }

    override val preference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    override fun saveGdpr(gdpr: Gdpr) {

        val json = JSON.std.asString(gdpr)

        preference
            .edit()
            .putString(KEY_GDPR, json)
            .apply()
    }

    override fun getGdpr(): Either<Gdpr> = check {
        preference.getString(KEY_GDPR, null)
            ?.toGDPR()
            ?: fail("Gdpr")
    }

    override fun saveTcData(deferredMap: DeferredMap) {
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

    override fun saveConsentUuid(value: String) {
        preference
            .edit()
            .putString(CONSENT_UUID_KEY, value)
            .apply()
    }

    override fun saveAppliedLegislation(value: String) {
        preference
            .edit()
            .putString("applied_legislation", value)
            .apply()
    }

    override fun getTcData(): DeferredMap {
        val res = DeferredMap(false)
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

    override fun getConsentUuid(): String {
        return preference.getString(CONSENT_UUID_KEY, "")!!
    }

    override fun getAppliedLegislation(): String {
        return preference.getString("applied_legislation", "")!!
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
        preference.edit().clear().apply()
    }

    private fun fail(param: String): Nothing = throw RuntimeException("$param not fund in local storage.")
}

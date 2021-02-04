package com.sourcepoint.cmplibrary.data.local

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.fasterxml.jackson.jr.ob.impl.DeferredMap
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.AUTH_ID_KEY
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.CONSENT_UUID_KEY
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.EU_CONSENT_KEY
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.IABTCF_KEY_PREFIX
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.META_DATA_KEY

/**
 * Factory method to create an instance of a [DataStorage] using its implementation
 * @param context is the client application context
 * @return an instance of the [DataStorageImpl] implementation
 */
internal fun DataStorage.Companion.create(context: Context): DataStorage = DataStorageImpl(context)

private class DataStorageImpl(context: Context) : DataStorage {

    override val preference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
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
            .putString(AUTH_ID_KEY, value)
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
}

package com.sourcepoint.cmplibrary.data.local

import com.fasterxml.jackson.jr.ob.impl.DeferredMap
import com.sourcepoint.cmplibrary.data.local.DSKeys.AUTH_ID_KEY
import com.sourcepoint.cmplibrary.data.local.DSKeys.CONSENT_UUID_KEY
import com.sourcepoint.cmplibrary.data.local.DSKeys.EU_CONSENT_KEY
import com.sourcepoint.cmplibrary.data.local.DSKeys.IABTCF_KEY_PREFIX
import com.sourcepoint.cmplibrary.data.local.DSKeys.META_DATA_KEY

object DSKeys {
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

internal fun DataStorage.saveTcData(deferredMap: DeferredMap) {
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

internal fun DataStorage.getTcData(): DeferredMap {
    val res = DeferredMap(false)
    val map: Map<String, *> = preference.all
    map
        .filter { it.key.startsWith(IABTCF_KEY_PREFIX) }
        .forEach { res[it.key] = it.value }
    return res
}

internal fun DataStorage.clearInternalData() {
    preference
        .edit()
        .remove(CONSENT_UUID_KEY)
        .remove(META_DATA_KEY)
        .remove(EU_CONSENT_KEY)
        .remove(AUTH_ID_KEY)
        .apply()
}

internal fun DataStorage.saveAuthId(value: String) {
    preference
        .edit()
        .putString(AUTH_ID_KEY, value)
        .apply()
}

internal fun DataStorage.saveEuConsent(value: String) {
    preference
        .edit()
        .putString(EU_CONSENT_KEY, value)
        .apply()
}

internal fun DataStorage.saveMetaData(value: String) {
    preference
        .edit()
        .putString(META_DATA_KEY, value)
        .apply()
}

internal fun DataStorage.saveConsentUuid(value: String) {
    preference
        .edit()
        .putString(CONSENT_UUID_KEY, value)
        .apply()
}

internal fun DataStorage.getAuthId(): String {
    return preference.getString(AUTH_ID_KEY, "")!!
}

internal fun DataStorage.getEuConsent(): String {
    return preference.getString(EU_CONSENT_KEY, "")!!
}

internal fun DataStorage.getMetaData(): String {
    return preference.getString(META_DATA_KEY, "")!!
}

internal fun DataStorage.getConsentUuid(): String {
    return preference.getString(CONSENT_UUID_KEY, "")!!
}

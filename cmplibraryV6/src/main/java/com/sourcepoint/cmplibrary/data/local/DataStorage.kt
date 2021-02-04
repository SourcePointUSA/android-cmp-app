package com.sourcepoint.cmplibrary.data.local

import android.content.SharedPreferences
import com.fasterxml.jackson.jr.ob.impl.DeferredMap

internal interface DataStorage {
    val preference: SharedPreferences

    fun clearInternalData()
    fun clearAll()

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

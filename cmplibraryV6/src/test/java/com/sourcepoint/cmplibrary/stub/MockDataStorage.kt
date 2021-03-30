package com.sourcepoint.cmplibrary.stub

import android.content.SharedPreferences
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa.Companion.CCPA_CONSENT_RESP
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa.Companion.CCPA_JSON_MESSAGE
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr
import io.mockk.mockk

internal class MockDataStorage : DataStorage {

    var gdprVal: String? = null
    var ccpaVal: String? = null
    var tcDataMap: Map<String, Any?> = emptyMap()
    var storage: MutableMap<String, Any> = mutableMapOf()

    override val preference: SharedPreferences = mockk()
    override var gdprApplies: Boolean = false

    override fun saveGdpr(value: String) {
        gdprVal = value
    }

    override fun getGdpr(): String? {
        return gdprVal
    }

    override fun saveTcData(deferredMap: Map<String, Any?>) {
        this.tcDataMap = deferredMap
    }

    override fun saveAuthId(value: String) {
        storage[DataStorageGdpr.AUTH_ID_KEY] = value
    }

    override fun saveEuConsent(value: String) {
        storage[DataStorageGdpr.EU_CONSENT_KEY] = value
    }

    override fun saveMetaData(value: String) {
        storage[DataStorageGdpr.META_DATA_KEY] = value
    }

    override fun saveConsentUuid(value: String) {
        storage[DataStorageGdpr.CONSENT_UUID_KEY] = value
    }

    override fun saveGdprConsentResp(value: String) {
        storage[DataStorageGdpr.GDPR_CONSENT_RESP] = value
    }

    override fun saveGdprMessage(value: String) {
        storage[DataStorageGdpr.GDPR_JSON_MESSAGE] = value
    }

    override fun saveAppliedLegislation(value: String) {
        storage["applied_legislation"] = value
    }

    override fun getTcData(): Map<String, Any?> {
        return tcDataMap
    }

    override fun getAuthId(): String {
        return storage[DataStorageGdpr.AUTH_ID_KEY] as? String ?: ""
    }

    override fun getEuConsent(): String {
        return storage[DataStorageGdpr.EU_CONSENT_KEY] as? String ?: ""
    }

    override fun getMetaData(): String {
        return storage[DataStorageGdpr.META_DATA_KEY] as? String ?: ""
    }

    override fun getConsentUuid(): String {
        return storage[DataStorageGdpr.CONSENT_UUID_KEY] as? String ?: ""
    }

    override fun getAppliedLegislation(): String {
        return storage["applied_legislation"]!! as String
    }

    override fun getGdprConsentResp(): String {
        return storage[DataStorageGdpr.GDPR_CONSENT_RESP] as? String ?: ""
    }

    override fun getGdprMessage(): String {
        return (storage[DataStorageGdpr.GDPR_JSON_MESSAGE] as? String) ?: ""
    }

    override fun clearInternalData() {
        gdprVal = null
        ccpaVal = null
        tcDataMap = emptyMap()
        storage = mutableMapOf()
    }

    override fun clearAll() {
        preference.edit().clear().apply()
    }

    override fun clearGdprConsent() {
        storage[DataStorageGdpr.GDPR_CONSENT_RESP] = ""
    }

    override fun saveCcpa(value: String) {
        ccpaVal = value
    }

    override fun getCcpa(): String? = ccpaVal

    override var ccpaApplies: Boolean = false

    override fun saveCcpaConsentResp(value: String) {
        storage[CCPA_CONSENT_RESP] = value
    }

    override fun saveCcpaMessage(value: String) {
        storage[CCPA_JSON_MESSAGE] = value
    }

    override fun getCcpaConsentResp(): String {
        return storage[CCPA_CONSENT_RESP] as? String ?: ""
    }

    override fun getCcpaMessage(): String {
        return (storage[CCPA_JSON_MESSAGE] as? String?) ?: ""
    }

    override fun clearCcpaConsent() {
        storage[CCPA_CONSENT_RESP] = ""
    }

    override fun saveLocalState(value: String) {
        TODO("Not yet implemented")
    }

    override fun getLocalState(): String? {
        TODO("Not yet implemented")
    }

    override fun saveGdpr1203(value: String) {
        TODO("Not yet implemented")
    }

    override fun getGdpr1203(): String? {
        TODO("Not yet implemented")
    }

    override fun saveCcpa1203(value: String) {
        TODO("Not yet implemented")
    }

    override fun getCcpa1203(): String? {
        TODO("Not yet implemented")
    }

    override fun savePropertyId(value: Int) {
        TODO("Not yet implemented")
    }

    override fun savePropertyPriorityData(value: String) {
        TODO("Not yet implemented")
    }

    override fun getPropertyId(): Int {
        TODO("Not yet implemented")
    }

    override fun getPropertyPriorityData(): String? {
        TODO("Not yet implemented")
    }
}

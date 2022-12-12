package com.sourcepoint.cmplibrary.stub

import android.content.SharedPreferences
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa.Companion.CCPA_CONSENT_RESP
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa.Companion.CCPA_JSON_MESSAGE
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr
import com.sourcepoint.cmplibrary.model.exposed.MessageSubCategory
import io.mockk.mockk

internal class MockDataStorage : DataStorage {

    var gdprVal: String? = null
    var ccpaVal: String? = null
    var tcDataMap: Map<String, Any?> = emptyMap()
    var storage: MutableMap<String, Any> = mutableMapOf()
    var mockLocalState: String? = null
    var savedConsentVar: Boolean = false
    var localStateV7: String? = null

    override var tcDataV7: Map<String, String>?
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun clearTCData() {
        TODO("Not yet implemented")
    }

    override var ccpaMessageSubCategory: MessageSubCategory = MessageSubCategory.TCFv2
    override val isCcpaOtt: Boolean
        get() = ccpaMessageSubCategory == MessageSubCategory.OTT

    override var ccpaChildPmId: String? = null
    override var gdprChildPmId: String? = null

    override val preference: SharedPreferences = mockk()
    override var gdprApplies: Boolean = false

    override var savedConsent: Boolean
        get() = savedConsentVar
        set(value) { savedConsentVar = value }

    override var gdprMessageSubCategory: MessageSubCategory = MessageSubCategory.TCFv2

    override val isGdprOtt: Boolean
        get() = gdprMessageSubCategory == MessageSubCategory.OTT

    override var ccpaMessageMetaData: String?
        get() = TODO("Not yet implemented")
        set(value) {}

    override var usPrivacyString: String?
        get() = storage[DataStorageCcpa.KEY_IAB_US_PRIVACY_STRING] as? String
        set(value) {
            storage[DataStorageCcpa.KEY_IAB_US_PRIVACY_STRING] = value.toString()
        }
    override var gdprMessageMetaData: String?
        get() = TODO("Not yet implemented")
        set(value) {}

    override var tcData: Map<String, Any?>
        get() = tcDataMap
        set(value) {
            this.tcDataMap = value
        }

    override fun saveAuthId(value: String?) {
        storage[DataStorageGdpr.AUTH_ID_KEY] = value ?: ""
    }

    override fun saveEuConsent(value: String) {
        storage[DataStorageGdpr.EU_CONSENT_KEY] = value
    }

    override fun saveMetaData(value: String) {
        storage[DataStorageGdpr.META_DATA_KEY] = value
    }

    override fun saveGdprConsentResp(value: String) {
        storage[DataStorageGdpr.GDPR_CONSENT_RESP] = value
    }

    override fun saveGdprMessage(value: String) {
        storage[DataStorageGdpr.GDPR_JSON_MESSAGE] = value
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

    override fun getGdprConsentResp(): String? {
        return storage[DataStorageGdpr.GDPR_CONSENT_RESP] as? String
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

    override var ccpaConsentUuid: String?
        get() {
            return storage[DataStorageCcpa.CONSENT_CCPA_UUID_KEY] as? String
        }
        set(value) {
            value?.let { storage[DataStorageCcpa.CONSENT_CCPA_UUID_KEY] = it }
        }
    override var ccpaApplies: Boolean = false

    override fun saveCcpaConsentResp(value: String) {
        storage[CCPA_CONSENT_RESP] = value
    }

    override fun saveCcpaMessage(value: String) {
        storage[CCPA_JSON_MESSAGE] = value
    }

    override fun getCcpaConsentResp(): String? {
        return storage[CCPA_CONSENT_RESP] as? String
    }

    override fun getCcpaMessage(): String {
        return (storage[CCPA_JSON_MESSAGE] as? String?) ?: ""
    }

    override fun clearCcpaConsent() {
        storage[CCPA_CONSENT_RESP] = ""
    }

    override fun saveLocalState(value: String) {
        mockLocalState = value
    }

    override fun getLocalState(): String? {
        return mockLocalState
    }

    override var messagesV7LocalState: String?
        get() = localStateV7
        set(value) { localStateV7 = value }

    override fun saveGdpr(value: String) {
        TODO("Not yet implemented")
    }

    override fun getGdpr(): String? {
        TODO("Not yet implemented")
    }

    override fun saveCcpa(value: String) {
        TODO("Not yet implemented")
    }

    override fun getCcpa(): String? {
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

    override var messagesV7: String?
        get() = TODO("Not yet implemented")
        set(value) {}
    override var consentStatusResponse: String?
        get() = TODO("Not yet implemented")
        set(value) {}
    override var metaDataResp: String?
        get() = TODO("Not yet implemented")
        set(value) {}
    override var dataRecordedConsent: String?
        get() = TODO("Not yet implemented")
        set(value) {}
    override var consentStatus: String?
        get() = TODO("Not yet implemented")
        set(value) {}
    override var pvDataResp: String?
        get() = TODO("Not yet implemented")
        set(value) {}
    override var choiceResp: String?
        get() = TODO("Not yet implemented")
        set(value) {}
    override var ccpaPostChoiceResp: String?
        get() = TODO("Not yet implemented")
        set(value) {}
    override var gdprPostChoiceResp: String?
        get() = TODO("Not yet implemented")
        set(value) {}
    override var gdprConsentStatus: String?
        get() = TODO("Not yet implemented")
        set(value) {}
    override var ccpaConsentStatus: String?
        get() = TODO("Not yet implemented")
        set(value) {}
    override var ccpaStatus: String?
        get() = TODO("Not yet implemented")
        set(value) {}
    override var gdprConsentUuid: String?
        get() = storage[DataStorageGdpr.CONSENT_UUID_KEY] as? String
        set(value) {
            value?.let { storage[DataStorageGdpr.CONSENT_UUID_KEY] = it }
        }
    override var ccpaDateCreated: String?
        get() = TODO("Not yet implemented")
        set(value) {}
    override var gdprDateCreated: String?
        get() = TODO("Not yet implemented")
        set(value) {}
    override var ccpaSamplingValue: Double
        get() = TODO("Not yet implemented")
        set(value) {}
    override var gdprSamplingValue: Double
        get() = TODO("Not yet implemented")
        set(value) {}
}

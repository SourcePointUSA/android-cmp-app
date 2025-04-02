package com.sourcepoint.cmplibrary.legacy

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class USNatLegacyConsent(
    val applies: Boolean,
    val consentStatus: ConsentStatus,
    val consentStrings: List<ConsentString>,
    val dateCreated: String,
    val uuid: String,
    val webConsentPayload: JsonObject,
    val GPPData: JsonObject,
    val expirationDate: String,
    val userConsents: UserConsents
) {
    companion object {
        val sharedPrefsKey = "sp.usnat.key.consent.status"
    }

    @Serializable
    data class ConsentStatus(
        val rejectedAny: Boolean,
        val consentedToAll: Boolean,
        val consentedToAny: Boolean,
        val granularStatus: GranularStatus,
        val hasConsentData: Boolean
    )

    @Serializable
    data class GranularStatus(
        val sellStatus: Boolean,
        val shareStatus: Boolean,
        val sensitiveDataStatus: Boolean,
        val gpcStatus: Boolean
    )

    @Serializable
    data class ConsentString(
        val sectionId: Int,
        val sectionName: String,
        val consentString: String
    )

    @Serializable
    data class UserConsents(val vendors: List<Consentable>, val categories: List<Consentable>)

    @Serializable
    data class Consentable(val _id: String, val consented: Boolean)
}

@Serializable
data class CCPALegacyConsent(
    val applies: Boolean,
    val consentedAll: Boolean,
    val dateCreated: String,
    val rejectedAll: Boolean,
    val rejectedCategories: List<String>,
    val rejectedVendors: List<String>,
    val signedLspa: Boolean,
    val uspstring: String,
    val status: String,
    val GPPData: JsonObject,
    val uuid: String,
    val webConsentPayload: JsonObject,
    val expirationDate: String
) {
    companion object {
        val sharedPrefsKey = "sp.ccpa.key.consent.status"
    }
}

@Serializable
data class GDPRLegacyConsent(
    val applies: Boolean,
    val categories: List<String>,
    val consentAllRef: String,
    val consentedToAll: Boolean,
    val legIntCategories: List<String>,
    val legIntVendors: List<String>,
    val rejectedAny: Boolean,
    val specialFeatures: List<String>,
    val vendors: List<String>,
    val addtlConsent: String,
    val consentStatus: ConsentStatus,
    val dateCreated: String,
    val euconsent: String,
    val grants: Map<String, Grant>,
    val TCData: JsonObject,
    val uuid: String,
    val vendorListId: String,
    val webConsentPayload: JsonObject,
    val expirationDate: String,
    val gcmStatus: Map<String, String>
) {
    companion object {
        val sharedPrefsKey = "sp.gdpr.key.consent.status"
    }

    @Serializable
    data class GranularStatus(
        val defaultConsent: Boolean,
        val previousOptInAll: Boolean,
        val purposeConsent: String,
        val purposeLegInt: String,
        val vendorConsent: String,
        val vendorLegInt: String
    )

    @Serializable
    data class ConsentStatus(
        val consentedAll: Boolean,
        val consentedToAny: Boolean,
        val granularStatus: GranularStatus,
        val hasConsentData: Boolean,
        val rejectedAny: Boolean,
        val rejectedLI: Boolean
    )

    @Serializable
    data class Grant(
        val vendorGrant: Boolean,
        val purposeGrants: Map<String, Boolean>
    )
}

@Serializable
data class LegacyMetaData(
    val gdpr: GDPR? = null,
    val usnat: USNAT? = null,
    val ccpa: CCPA? = null
) {
    companion object {
        val sharedPrefsKey = "sp.key.meta.data"
    }
    @Serializable
    data class GDPR(
        val applies: Boolean,
        val sampleRate: Double?,
        val additionsChangeDate: String?,
        val legalBasisChangeDate: String?,
        val _id: String?
    )

    @Serializable
    data class USNAT(
        val applies: Boolean,
        val sampleRate: Double?,
        val additionsChangeDate: String?,
        val applicableSections: List<Int>?,
        val _id: String?
    )

    @Serializable
    data class CCPA(
        val applies: Boolean,
        val sampleRate: Double?
    )
}

@Serializable
data class LegacyGDPRSampled(val value: Boolean?) {

}

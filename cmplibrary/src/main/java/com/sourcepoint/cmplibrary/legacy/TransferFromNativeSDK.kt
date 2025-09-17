package com.sourcepoint.cmplibrary.legacy

import android.content.SharedPreferences
import com.sourcepoint.cmplibrary.model.exposed.CcpaStatus
import com.sourcepoint.mobile_core.models.consents.CCPAConsent
import com.sourcepoint.mobile_core.models.consents.GDPRConsent
import com.sourcepoint.mobile_core.models.consents.State
import com.sourcepoint.mobile_core.models.consents.USNatConsent
import com.sourcepoint.mobile_core.models.consents.UserConsents
import com.sourcepoint.mobile_core.models.consents.UserConsents.Consentable
import com.sourcepoint.mobile_core.network.json
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

typealias CoreConsentStatus = com.sourcepoint.mobile_core.models.consents.ConsentStatus
typealias CoreGranularStatus = com.sourcepoint.mobile_core.models.consents.ConsentStatus.ConsentStatusGranularStatus

fun migrateLegacyToNewState(
    preferences: SharedPreferences,
    accountId: Int,
    propertyId: Int
): State? {
    if (preferences.contains(LegacyLocalState.PREFS_KEY)) {
        val legacyState = LegacyState(preferences)
        val newState = legacyState.toState(accountId, propertyId)
        removeLegacyData(preferences)
        return newState
    }
    return null
}

fun removeLegacyData(preferences: SharedPreferences) {
    preferences.edit().apply {
        remove(LegacyState.AUTH_ID_PREFS_KEY)
        remove(GDPRLegacyConsent.PREFS_KEY)
        remove(CCPALegacyConsent.PREFS_KEY)
        remove(USNatLegacyConsent.PREFS_KEY)
        remove(LegacyMetaData.PREFS_KEY)
        remove(LegacyGDPRSampled.PREFS_KEY)
        remove(LegacyUSNATSampled.PREFS_KEY)
        remove(LegacyCCPASampled.PREFS_KEY)
        remove(LegacyLocalState.PREFS_KEY)
        remove(LegacyNonKeyedLocalState.PREFS_KEY)
        remove(LegacyGDPRChildPmId.PREFS_KEY)
        remove(LegacyCCPAChildPmId.PREFS_KEY)
        remove(LegacyUSNATChildPmId.PREFS_KEY)
        unusedSPKeys.forEach { remove(it) }
        commit()
    }
}

@Serializable
data class LegacyState(
    val authId: String? = null,
    val gdpr: GDPRLegacyConsent? = null,
    val usnat: USNatLegacyConsent? = null,
    val ccpa: CCPALegacyConsent? = null,
    val metaData: LegacyMetaData? = null,
    val gdprSampled: Boolean? = null,
    val usnatSampled: Boolean? = null,
    val ccpaSampled: Boolean? = null,
    val gdprChildPmId: String? = null,
    val ccpaChildPmId: String? = null,
    val usnatChildPmId: String? = null,
    val localState: JsonObject? = null,
    val nonKeyedLocalState: JsonObject? = null
) {
    companion object {
        const val AUTH_ID_PREFS_KEY = "sp.gdpr.authId"

        private inline fun <reified T> decodeWithLogging(jsonString: String, name: String): T? = runCatching<T?> {
            json.decodeFromString(jsonString)
        }.onFailure { e ->
            println("Failed to decode $name: ${e.message}")
        }.getOrNull()
    }

    constructor(sharedPrefs: SharedPreferences) : this(
        authId = sharedPrefs.getString(AUTH_ID_PREFS_KEY, null),
        gdpr = sharedPrefs.getString(GDPRLegacyConsent.PREFS_KEY, null)?.let { decodeWithLogging(it, "GDPRLegacyConsent") },
        ccpa = sharedPrefs.getString(CCPALegacyConsent.PREFS_KEY, null)?.let { decodeWithLogging(it, "CCPALegacyConsent") },
        usnat = sharedPrefs.getString(USNatLegacyConsent.PREFS_KEY, null)?.let { decodeWithLogging(it, "USNatLegacyConsent") },
        metaData = sharedPrefs.getString(LegacyMetaData.PREFS_KEY, null)?.let { decodeWithLogging(it, "LegacyMetaData") },
        gdprSampled = sharedPrefs.getBoolean(LegacyGDPRSampled.PREFS_KEY, false),
        usnatSampled = sharedPrefs.getBoolean(LegacyUSNATSampled.PREFS_KEY, false),
        ccpaSampled = sharedPrefs.getBoolean(LegacyCCPASampled.PREFS_KEY, false),
        gdprChildPmId = sharedPrefs.getString(LegacyGDPRChildPmId.PREFS_KEY, null),
        ccpaChildPmId = sharedPrefs.getString(LegacyCCPAChildPmId.PREFS_KEY, null),
        usnatChildPmId = sharedPrefs.getString(LegacyUSNATChildPmId.PREFS_KEY, null),
        localState = sharedPrefs.getString(LegacyLocalState.PREFS_KEY, null)?.let { decodeWithLogging(it, "LegacyLocalState") },
        nonKeyedLocalState = sharedPrefs.getString(LegacyNonKeyedLocalState.PREFS_KEY, null)?.let { decodeWithLogging(it, "LegacyNonKeyedLocalState") }
    )

    fun toState(accountId: Int, propertyId: Int) = State(
        accountId = accountId,
        propertyId = propertyId,
        authId = authId,
        gdpr = State.GDPRState(
            consents = gdpr?.toCore() ?: GDPRConsent(),
            childPmId = gdprChildPmId,
            metaData = metaData?.gdpr?.toCore(gdprSampled) ?: State.GDPRState.GDPRMetaData()
        ),
        usNat = State.USNatState(
            consents = usnat?.toCore() ?: USNatConsent(),
            childPmId = usnatChildPmId,
            metaData = metaData?.usnat?.toCore(usnatSampled) ?: State.USNatState.UsNatMetaData()
        ),
        ccpa = State.CCPAState(
            consents = ccpa?.toCore() ?: CCPAConsent(),
            childPmId = ccpaChildPmId,
            metaData = metaData?.ccpa?.toCore(ccpaSampled) ?: State.CCPAState.CCPAMetaData()
        ),
        localState = localState?.let { json.encodeToString(it) } ?: "",
        nonKeyedLocalState = nonKeyedLocalState?.let { json.encodeToString(it) } ?: ""
    )
}

@Serializable
data class USNatLegacyConsent(
    val applies: Boolean,
    val consentStatus: ConsentStatus,
    val consentStrings: List<ConsentString>,
    val dateCreated: String,
    val uuid: String,
    val webConsentPayload: JsonObject?,
    val GPPData: Map<String, JsonPrimitive>,
    val expirationDate: String,
    val userConsents: UserConsents
) {
    companion object {
        const val PREFS_KEY = "sp.usnat.key.consent.status"
    }

    fun toCore() = USNatConsent(
        applies = applies,
        consentStatus = consentStatus.toCore(),
        consentStrings = consentStrings.map { it.toCore() },
        dateCreated = Instant.parse(dateCreated),
        uuid = uuid,
        webConsentPayload = webConsentPayload?.let { json.encodeToString(it) },
        gppData = GPPData,
        expirationDate = Instant.parse(expirationDate),
        userConsents = userConsents.toCore()
    )

    @Serializable
    data class ConsentStatus(
        val rejectedAny: Boolean,
        val consentedToAll: Boolean,
        val consentedToAny: Boolean,
        val granularStatus: GranularStatus,
        val hasConsentData: Boolean
    ) {
        fun toCore() = CoreConsentStatus(
            consentedAll = consentedToAll,
            consentedToAny = consentedToAny,
            granularStatus = granularStatus.toCore(),
            hasConsentData = hasConsentData,
            rejectedAny = rejectedAny
        )
    }

    @Serializable
    data class GranularStatus(
        val sellStatus: Boolean,
        val shareStatus: Boolean,
        val sensitiveDataStatus: Boolean,
        val gpcStatus: Boolean
    ) {
        fun toCore() = CoreGranularStatus(
            sellStatus = sellStatus,
            shareStatus = shareStatus,
            sensitiveDataStatus = sensitiveDataStatus,
            gpcStatus = gpcStatus
        )
    }

    @Serializable
    data class ConsentString(
        val sectionId: Int,
        val sectionName: String,
        val consentString: String
    ) {
        fun toCore() = USNatConsent.USNatConsentSection(
            sectionId = sectionId,
            sectionName = sectionName,
            consentString = consentString
        )
    }

    @Serializable
    data class UserConsents(val vendors: List<Consentable>, val categories: List<Consentable>) {
        fun toCore() = UserConsents(
            vendors = vendors.map { it.toCore() },
            categories = categories.map { it.toCore() }
        )
    }

    @Serializable
    data class Consentable(val _id: String, val consented: Boolean) {
        fun toCore() = Consentable(
            id = _id,
            consented = consented
        )
    }
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
    val status: CcpaStatus,
    val GPPData: Map<String, JsonPrimitive>,
    val uuid: String,
    val webConsentPayload: JsonObject?,
    val expirationDate: String
) {
    companion object {
        const val PREFS_KEY = "sp.ccpa.key.consent.status"
    }

    fun toCore() = CCPAConsent(
        applies = applies,
        consentedAll = consentedAll,
        dateCreated = Instant.parse(dateCreated),
        rejectedAll = rejectedAll,
        rejectedCategories = rejectedCategories,
        rejectedVendors = rejectedVendors,
        signedLspa = signedLspa,
        status = status.toCore(),
        gppData = GPPData,
        uuid = uuid,
        webConsentPayload = webConsentPayload?.let { json.encodeToString(it) },
        expirationDate = Instant.parse(expirationDate)
    )
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
    val grants: Map<String, GDPRConsent.VendorGrantsValue>,
    val TCData: Map<String, JsonPrimitive>,
    val uuid: String,
    val vendorListId: String,
    val webConsentPayload: JsonObject?,
    val expirationDate: String,
    val gcmStatus: GDPRConsent.GCMStatus?
) {
    companion object {
        const val PREFS_KEY = "sp.gdpr.key.consent.status"
    }

    fun toCore() = GDPRConsent(
        applies = applies,
        categories = categories,
        legIntCategories = legIntCategories,
        legIntVendors = legIntVendors,
        specialFeatures = specialFeatures,
        vendors = vendors,
        consentStatus = consentStatus.toCore(),
        dateCreated = Instant.parse(dateCreated),
        euconsent = euconsent,
        grants = grants,
        tcData = TCData,
        uuid = uuid,
        webConsentPayload = webConsentPayload?.let { json.encodeToString(it) },
        expirationDate = Instant.parse(expirationDate),
        gcmStatus = gcmStatus
    )

    @Serializable
    data class GranularStatus(
        val defaultConsent: Boolean,
        val previousOptInAll: Boolean,
        val purposeConsent: String,
        val purposeLegInt: String,
        val vendorConsent: String,
        val vendorLegInt: String
    ) {
        fun toCore() = CoreGranularStatus(
            defaultConsent = defaultConsent,
            previousOptInAll = previousOptInAll,
            purposeConsent = purposeConsent,
            purposeLegInt = purposeLegInt,
            vendorConsent = vendorConsent,
            vendorLegInt = vendorLegInt
        )
    }

    @Serializable
    data class ConsentStatus(
        val consentedAll: Boolean,
        val consentedToAny: Boolean,
        val granularStatus: GranularStatus,
        val hasConsentData: Boolean,
        val rejectedAny: Boolean,
        val rejectedLI: Boolean
    ) {
        fun toCore() = CoreConsentStatus(
            consentedAll = consentedAll,
            consentedToAny = consentedToAny,
            granularStatus = granularStatus.toCore(),
            hasConsentData = hasConsentData,
            rejectedAny = rejectedAny,
            rejectedLI = rejectedLI
        )
    }
}

@Serializable
data class LegacyMetaData(
    val gdpr: GDPR? = null,
    val usnat: USNAT? = null,
    val ccpa: CCPA? = null
) {
    companion object {
        const val PREFS_KEY = "sp.key.meta.data"
    }

    @Serializable
    data class GDPR(
        val applies: Boolean,
        val sampleRate: Double?,
        val additionsChangeDate: String?,
        val legalBasisChangeDate: String?,
        val _id: String?
    ) {
        fun toCore(sampled: Boolean?) = State.GDPRState.GDPRMetaData(
            sampleRate = sampleRate?.toFloat() ?: 1.0f,
            additionsChangeDate = Instant.parse(additionsChangeDate ?: Instant.DISTANT_PAST.toString()),
            legalBasisChangeDate = Instant.parse(legalBasisChangeDate ?: Instant.DISTANT_PAST.toString()),
            vendorListId = _id,
            wasSampled = sampled,
            wasSampledAt = sampleRate?.toFloat() ?: 1.0f
        )
    }

    @Serializable
    data class USNAT(
        val applies: Boolean,
        val sampleRate: Double?,
        val additionsChangeDate: String?,
        val applicableSections: List<Int>?,
        val _id: String?
    ) {
        fun toCore(sampled: Boolean?) = State.USNatState.UsNatMetaData(
            sampleRate = sampleRate?.toFloat() ?: 1.0f,
            additionsChangeDate = Instant.parse(additionsChangeDate ?: Instant.DISTANT_PAST.toString()),
            vendorListId = _id,
            applicableSections = applicableSections ?: emptyList(),
            wasSampled = sampled,
            wasSampledAt = sampleRate?.toFloat() ?: 1.0f
        )
    }

    @Serializable
    data class CCPA(val applies: Boolean, val sampleRate: Double?) {
        fun toCore(sampled: Boolean?) = State.CCPAState.CCPAMetaData(
            sampleRate = sampleRate?.toFloat() ?: 1.0f,
            wasSampled = sampled,
            wasSampledAt = sampleRate?.toFloat() ?: 1.0f
        )
    }
}

class LegacyGDPRSampled {
    companion object {
        const val PREFS_KEY = "sp.gdpr.key.sampling.result"
    }
}

class LegacyUSNATSampled {
    companion object {
        const val PREFS_KEY = "sp.usnat.key.sampling.result"
    }
}

class LegacyCCPASampled {
    companion object {
        const val PREFS_KEY = "sp.ccpa.key.sampling.result"
    }
}

class LegacyLocalState {
    companion object {
        const val PREFS_KEY = "sp.key.messages.v7.local.state"
    }
}

class LegacyNonKeyedLocalState {
    companion object {
        const val PREFS_KEY = "sp.key.messages.v7.nonKeyedLocalState"
    }
}

class LegacyGDPRChildPmId {
    companion object {
        const val PREFS_KEY = "sp.gdpr.key.childPmId"
    }
}

class LegacyCCPAChildPmId {
    companion object {
        const val PREFS_KEY = "sp.ccpa.key.childPmId"
    }
}

class LegacyUSNATChildPmId {
    companion object {
        const val PREFS_KEY = "sp.usnat.key.childPmId"
    }
}

private val unusedSPKeys = listOf(
    "sp.gdpr.key.expiration.date",
    "sp.ccpa.key.expiration.date",
    "sp.usnat.key.expiration.date",
    "sp.gdpr.consentUUID",
    "sp.ccpa.consentUUID",
    "sp.usnat.consentUUID",
    "sp.key.config.propertyId"
)

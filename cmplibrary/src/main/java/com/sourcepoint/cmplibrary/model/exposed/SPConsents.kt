package com.sourcepoint.cmplibrary.model.exposed

import com.sourcepoint.cmplibrary.data.network.model.optimized.ConsentStatus
import com.sourcepoint.cmplibrary.util.generateCcpaUspString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.putJsonObject
import org.json.JSONObject

@Serializable
data class SPConsents(
    val gdpr: SPGDPRConsent? = null,
    val ccpa: SPCCPAConsent? = null
)

data class SPCustomConsents(
    val gdpr: JSONObject
)

@Serializable
data class SPGDPRConsent(
    val consent: GDPRConsent
)

@Serializable
data class SPCCPAConsent(
    val consent: CCPAConsent
)

@Serializable
data class GDPRPurposeGrants(
    @SerialName("vendorGrant") val granted: Boolean = false,
    val purposeGrants: Map<String, Boolean> = emptyMap()
)

@Serializable
sealed interface GDPRConsent {
    val uuid: String?
    var euconsent: String
    var tcData: JsonObject
    var grants: Map<String, GDPRPurposeGrants>
    val acceptedCategories: List<String>?
    val applies: Boolean?
    val webConsentPayload: JsonObject?
    val consentStatus: ConsentStatus
}

@Serializable
internal data class GDPRConsentInternal(
    override var euconsent: String = "",
    override val uuid: String? = null,
    override var tcData: JsonObject = JsonObject(emptyMap()),
    override var grants: Map<String, GDPRPurposeGrants> = emptyMap(),
    override val acceptedCategories: List<String>? = null,
    override val applies: Boolean? = null,
    override val consentStatus: ConsentStatus = ConsentStatus(),
    val childPmId: String? = null,
    val thisContent: JsonObject = JsonObject(emptyMap()),
    override val webConsentPayload: JsonObject? = null,
) : GDPRConsent
@Serializable
sealed interface CCPAConsent {
    val uuid: String?
    val rejectedCategories: List<String>
    val rejectedVendors: List<String>
    val status: CcpaStatus?
    val uspstring: String
    val childPmId: String?
    val applies: Boolean
    val signedLspa: Boolean?
    val webConsentPayload: JsonObject?
}

@Serializable
internal data class CCPAConsentInternal(
    override val uuid: String? = null,
    override val rejectedCategories: List<String> = listOf(),
    override val rejectedVendors: List<String> = listOf(),
    override val status: CcpaStatus? = null,
    override val childPmId: String? = null,
    override val applies: Boolean = false,
    val thisContent: JsonObject = JsonObject(emptyMap()),
    override val signedLspa: Boolean? = null,
    override val webConsentPayload: JsonObject? = null,
) : CCPAConsent {

    override val uspstring: String
        get() = generateCcpaUspString(
            applies = applies,
            ccpaStatus = status,
            signedLspa = signedLspa,
        )
}

enum class CcpaStatus {
    rejectedAll,
    rejectedSome,
    rejectedNone,
    consentedAll,
    linkedNoAction,
    unknown
}

internal fun SPConsents.toWebViewConsentsJsonObject(): JsonObject = buildJsonObject {
    ccpa?.consent?.let { ccpaConsent ->
        if (ccpaConsent.isWebConsentEligible()) {
            putJsonObject("ccpa") {
                put("uuid", JsonPrimitive(ccpaConsent.uuid))
                put("webConsentPayload", JsonPrimitive(ccpaConsent.webConsentPayload.toString()))
            }
        }
    }
    gdpr?.consent?.let { gdprConsent ->
        if (gdprConsent.isWebConsentEligible()) {
            putJsonObject("gdpr") {
                put("uuid", JsonPrimitive(gdprConsent.uuid))
                put("webConsentPayload", JsonPrimitive(gdprConsent.webConsentPayload.toString()))
            }
        }
    }
}

internal fun GDPRConsent.isWebConsentEligible(): Boolean =
    uuid.isNullOrEmpty().not() && webConsentPayload.isNullOrEmpty().not()

internal fun CCPAConsent.isWebConsentEligible(): Boolean =
    uuid.isNullOrEmpty().not() && webConsentPayload.isNullOrEmpty().not()

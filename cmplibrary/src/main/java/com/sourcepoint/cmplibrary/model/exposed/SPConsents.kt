package com.sourcepoint.cmplibrary.model.exposed

import com.sourcepoint.cmplibrary.data.network.model.optimized.ConsentStatus
import com.sourcepoint.cmplibrary.data.network.model.optimized.USNatConsentStatus
import com.sourcepoint.cmplibrary.model.toJSONObjGrant
import com.sourcepoint.cmplibrary.model.toTcfJSONObj
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.putJsonObject
import org.json.JSONArray
import org.json.JSONObject

data class SPConsents(
    val gdpr: SPGDPRConsent? = null,
    val ccpa: SPCCPAConsent? = null,
    val usNat: SpUsNatConsent? = null,
)

data class SPCustomConsents(
    val gdpr: JSONObject
)

data class SPGDPRConsent(
    val consent: GDPRConsent
)

data class SPCCPAConsent(
    val consent: CCPAConsent
)

data class SpUsNatConsent(
    val consent: UsNatConsent,
)

@Serializable
data class GDPRPurposeGrants(
    @SerialName("vendorGrant") val granted: Boolean = false,
    val purposeGrants: Map<String, Boolean> = emptyMap()
)

interface GDPRConsent {
    val uuid: String?
    var euconsent: String
    var tcData: Map<String, Any?>
    var grants: Map<String, GDPRPurposeGrants>
    val acceptedCategories: List<String>?
    val applies: Boolean
    val webConsentPayload: JsonObject?
    val consentStatus: ConsentStatus?
}

internal data class GDPRConsentInternal(
    override var euconsent: String = "",
    override val uuid: String? = null,
    override var tcData: Map<String, Any?> = emptyMap(),
    override var grants: Map<String, GDPRPurposeGrants> = emptyMap(),
    override val acceptedCategories: List<String>? = null,
    override val applies: Boolean = false,
    override val consentStatus: ConsentStatus? = null,
    val childPmId: String? = null,
    val thisContent: JSONObject = JSONObject(),
    override val webConsentPayload: JsonObject? = null,
) : GDPRConsent

interface CCPAConsent {
    companion object {
        const val DEFAULT_USPSTRING = "1YNN"
    }
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

internal data class CCPAConsentInternal(
    override val uuid: String? = null,
    override val rejectedCategories: List<String> = listOf(),
    override val rejectedVendors: List<String> = listOf(),
    override val status: CcpaStatus? = null,
    override val uspstring: String = "1YNN",
    override val childPmId: String? = null,
    override val applies: Boolean = false,
    val thisContent: JSONObject = JSONObject(),
    override val signedLspa: Boolean? = null,
    override val webConsentPayload: JsonObject? = null,
) : CCPAConsent

enum class CcpaStatus {
    rejectedAll,
    rejectedSome,
    rejectedNone,
    consentedAll,
    linkedNoAction,
    unknown
}

interface UsNatConsent {
    val applies: Boolean
    val consentStatus: USNatConsentStatus?
    val consentString: String?
    val dateCreated: String?
    val uuid: String?
    val webConsentPayload: JsonObject?
    val url: String?
}

internal data class UsNatConsentInternal(
    override val applies: Boolean = false,
    override val consentStatus: USNatConsentStatus? = null,
    override val consentString: String? = null,
    override val dateCreated: String? = null,
    override val uuid: String? = null,
    override val webConsentPayload: JsonObject? = null,
    override val url: String? = null,
) : UsNatConsent

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
    usNat?.consent?.let { usNatConsent ->
        if (usNatConsent.isWebConsentEligible()) {
            putJsonObject("usnat") {
                put("uuid", JsonPrimitive(usNatConsent.uuid))
                put("webConsentPayload", JsonPrimitive(usNatConsent.webConsentPayload.toString()))
            }
        }
    }
}

internal fun GDPRConsent.isWebConsentEligible(): Boolean =
    uuid.isNullOrEmpty().not() && webConsentPayload.isNullOrEmpty().not()

internal fun CCPAConsent.isWebConsentEligible(): Boolean =
    uuid.isNullOrEmpty().not() && webConsentPayload.isNullOrEmpty().not()

internal fun UsNatConsent.isWebConsentEligible(): Boolean =
    uuid.isNullOrEmpty().not() && webConsentPayload.isNullOrEmpty().not()

internal fun GDPRConsentInternal.toJsonObject(): JSONObject {
    return JSONObject().apply {
        put("uuid", uuid)
        put("tcData", tcData.toTcfJSONObj())
        put("grants", grants.toJSONObjGrant())
        put("euconsent", euconsent)
        put("apply", applies)
        put("acceptedCategories", JSONArray(acceptedCategories))
        put("consentStatus", consentStatus?.toJSONObj())
    }
}

internal fun ConsentStatus.toJSONObj(): Any {
    return JSONObject().apply {
        put("consentedAll", consentedAll)
        put("consentedToAny", consentedToAny)
        put("hasConsentData", hasConsentData)
        put("rejectedAny", rejectedAny)
        put("rejectedLI", rejectedLI)
        put("legalBasisChanges", legalBasisChanges)
        put("vendorListAdditions", vendorListAdditions)
        put("granularStatus", granularStatus?.toJSONObj())
    }
}

internal fun ConsentStatus.GranularStatus.toJSONObj(): Any {
    return JSONObject().apply {
        put("defaultConsent", defaultConsent)
        put("previousOptInAll", previousOptInAll)
        put("purposeConsent", purposeConsent?.name)
        put("purposeLegInt", purposeLegInt?.name)
        put("vendorConsent", vendorConsent?.name)
        put("vendorLegInt", vendorLegInt?.name)
    }
}

internal fun CCPAConsentInternal.toJsonObject(): JSONObject {
    return JSONObject().apply {
        put("uuid", uuid)
        put("status", status)
        put("uspstring", uspstring)
        put("rejectedCategories", JSONArray(rejectedCategories))
        put("apply", applies)
        put("rejectedVendors", JSONArray(rejectedVendors))
    }
}

internal fun UsNatConsentInternal.toJsonObject(): JSONObject {
    return JSONObject().apply {
        put("applies", applies)
        put("consentStatus", consentStatus?.toJsonObject())
        put("consentString", consentString)
        put("dateCreated", dateCreated)
        put("uuid", uuid)
        put("url", url)
    }
}

internal fun SPConsents.toJsonObject(): JSONObject {
    return JSONObject().apply {
        put("gdpr", (gdpr?.consent as? GDPRConsentInternal)?.toJsonObject())
        put("ccpa", (ccpa?.consent as? CCPAConsentInternal)?.toJsonObject())
        put("usnat", (usNat?.consent as? UsNatConsentInternal)?.toJsonObject())
    }
}

internal fun USNatConsentStatus.toJsonObject(): JSONObject {
    return JSONObject().apply {
        put("rejectedAny", rejectedAny)
        put("consentedToAll", consentedToAll)
        put("consentedToAny", consentedToAny)
        put("granularStatus", granularStatus?.toJsonObject())
        put("hasConsentData", hasConsentData)
    }
}

internal fun USNatConsentStatus.USNatGranularStatus.toJsonObject(): JSONObject {
    return JSONObject().apply {
        put("sellStatus", sellStatus)
        put("shareStatus", shareStatus)
        put("sensitiveDataStatus", sensitiveDataStatus)
        put("gpcStatus", gpcStatus)
        put("defaultConsent", defaultConsent)
        put("previousOptInAll", previousOptInAll)
        put("purposeConsent", purposeConsent)
    }
}

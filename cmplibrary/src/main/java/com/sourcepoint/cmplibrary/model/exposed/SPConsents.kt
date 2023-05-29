package com.sourcepoint.cmplibrary.model.exposed

import com.sourcepoint.cmplibrary.model.toJSONObjGrant
import com.sourcepoint.cmplibrary.model.toTcfJSONObj
import com.sourcepoint.cmplibrary.util.generateCcpaUspString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.json.JSONArray
import org.json.JSONObject

data class SPConsents(
    val gdpr: SPGDPRConsent? = null,
    val ccpa: SPCCPAConsent? = null
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
    val applies: Boolean?
}

internal data class GDPRConsentInternal(
    override var euconsent: String = "",
    override val uuid: String? = null,
    override var tcData: Map<String, Any?> = emptyMap(),
    override var grants: Map<String, GDPRPurposeGrants> = emptyMap(),
    override val acceptedCategories: List<String>? = null,
    override val applies: Boolean? = null,
    val childPmId: String? = null,
    val thisContent: JSONObject = JSONObject()
) : GDPRConsent

interface CCPAConsent {
    val uuid: String?
    val rejectedCategories: List<String>
    val rejectedVendors: List<String>
    val status: CcpaStatus?
    val uspstring: String
    val childPmId: String?
    val applies: Boolean
    val signedLspa: Boolean?
}

internal data class CCPAConsentInternal(
    override val uuid: String? = null,
    override val rejectedCategories: List<String> = listOf(),
    override val rejectedVendors: List<String> = listOf(),
    override val status: CcpaStatus? = null,
    override val childPmId: String? = null,
    override val applies: Boolean = false,
    val thisContent: JSONObject = JSONObject(),
    override val signedLspa: Boolean? = null,
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

internal fun GDPRConsentInternal.toJsonObject(): JSONObject {
    return JSONObject().apply {
        put("tcData", tcData.toTcfJSONObj())
        put("grants", grants.toJSONObjGrant())
        put("euconsent", euconsent)
        put("apply", applies)
        put("acceptedCategories", JSONArray(acceptedCategories))
    }
}

internal fun CCPAConsentInternal.toJsonObject(): JSONObject {
    return JSONObject().apply {
        put("status", status)
        put("uspstring", uspstring)
        put("rejectedCategories", JSONArray(rejectedCategories))
        put("apply", applies)
        put("rejectedVendors", JSONArray(rejectedVendors))
    }
}

internal fun SPConsents.toJsonObject(): JSONObject {
    return JSONObject().apply {
        put("gdpr", (gdpr?.consent as? GDPRConsentInternal)?.toJsonObject())
        put("ccpa", (ccpa?.consent as? CCPAConsentInternal)?.toJsonObject())
    }
}

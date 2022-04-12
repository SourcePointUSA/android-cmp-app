package com.sourcepoint.cmplibrary.model.exposed

import com.sourcepoint.cmplibrary.model.toJSONObj
import com.sourcepoint.cmplibrary.model.toJSONObjGrant
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

data class GDPRPurposeGrants(
    val granted: Boolean = false,
    val purposeGrants: Map<String, Boolean> = emptyMap()
)

interface GDPRConsent {
    val uuid: String?
    var euconsent: String
    var tcData: Map<String, Any?>
    var grants: Map<String, GDPRPurposeGrants>
    val acceptedCategories: List<String>
    val childPmId: String?
    val applies: Boolean
}

internal data class GDPRConsentInternal(
    override var euconsent: String = "",
    override val uuid: String? = null,
    override var tcData: Map<String, Any?> = emptyMap(),
    override var grants: Map<String, GDPRPurposeGrants> = emptyMap(),
    override val acceptedCategories: List<String> = emptyList(),
    override val childPmId: String? = null,
    override val applies: Boolean = false,
    val thisContent: JSONObject = JSONObject()
) : GDPRConsent

interface CCPAConsent {
    val uuid: String?
    val rejectedCategories: List<Any>
    val rejectedVendors: List<Any>
    val status: String?
    val uspstring: String
    val childPmId: String?
    val applies: Boolean
}

internal data class CCPAConsentInternal(
    override val uuid: String? = null,
    override val rejectedCategories: List<Any> = listOf(),
    override val rejectedVendors: List<Any> = listOf(),
    override val status: String? = null,
    override val uspstring: String = "",
    override val childPmId: String? = null,
    override val applies: Boolean = false,
    val thisContent: JSONObject = JSONObject()
) : CCPAConsent

internal fun GDPRConsentInternal.toJsonObject(): JSONObject {
    return JSONObject().apply {
        put("uuid", uuid)
        put("tcData", tcData.toJSONObj())
        put("grants", grants.toJSONObjGrant())
        put("euconsent", euconsent)
        put("apply", applies)
        put("acceptedCategories", JSONArray(acceptedCategories))
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

internal fun SPConsents.toJsonObject(): JSONObject {
    return JSONObject().apply {
        put("gdpr", (gdpr?.consent as? GDPRConsentInternal)?.toJsonObject())
        put("ccpa", (ccpa?.consent as? CCPAConsentInternal)?.toJsonObject())
    }
}

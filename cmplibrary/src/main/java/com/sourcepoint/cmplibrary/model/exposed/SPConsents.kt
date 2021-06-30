package com.sourcepoint.cmplibrary.model.exposed

import com.sourcepoint.cmplibrary.model.toJSONObj
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

interface GDPRConsent {
    var euconsent: String
    var tcData: Map<String, Any?>
    var grants: Map<String, Map<String, Boolean>>
    val acceptedCategories: List<String>
    val acceptedVendors: List<String>
}

internal data class GDPRConsentInternal(
    override var euconsent: String = "",
    override var tcData: Map<String, Any?> = emptyMap(),
    override var grants: Map<String, Map<String, Boolean>> = emptyMap(),
    override val acceptedCategories: List<String> = emptyList(),
    override val acceptedVendors: List<String> = emptyList(),
    val thisContent: JSONObject = JSONObject()
) : GDPRConsent

data class CCPAConsent(
    val rejectedCategories: List<Any> = listOf(),
    val rejectedVendors: List<Any> = listOf(),
    val status: String? = null,
    val signedLspa: Boolean = false,
    val uspstring: String = "",
    val thisContent: JSONObject = JSONObject()
)

internal fun GDPRConsentInternal.toJsonObject(): JSONObject {
    return JSONObject().apply {
        put("tcData", tcData.toJSONObj())
        put("grants", grants.toJSONObj())
        put("euconsent", euconsent)
        put("acceptedCategories", JSONArray(acceptedCategories))
        put("acceptedVendors", JSONArray(acceptedVendors))
    }
}

internal fun CCPAConsent.toJsonObject(): JSONObject {
    return JSONObject().apply {
        put("status", status)
        put("signedLspa", signedLspa)
        put("uspstring", uspstring)
        put("rejectedCategories", JSONArray(rejectedCategories))
        put("rejectedVendors", JSONArray(rejectedVendors))
    }
}

internal fun SPConsents.toJsonObject(): JSONObject {
    return JSONObject().apply {
        put("gdpr", (gdpr?.consent as? GDPRConsentInternal)?.toJsonObject())
        put("ccpa", ccpa?.consent?.toJsonObject())
    }
}

package com.sourcepoint.cmplibrary.model.exposed

import org.json.JSONObject

data class SPConsents(
    val gdpr: SPGDPRConsent? = null,
    val ccpa: SPCCPAConsent? = null
)

data class SPGDPRConsent(
    val consent: GDPRConsent
)
data class SPCCPAConsent(
    val consent: CCPAConsent
)

data class GDPRConsent(
    var euconsent: String = "",
    var tcData: Map<String, Any?> = emptyMap(),
    var vendorsGrants: Map<String, Map<String, Boolean>> = emptyMap(),
    val thisContent: JSONObject = JSONObject()
)

data class CCPAConsent(
    val rejectedCategories: List<Any> = listOf(),
    val rejectedVendors: List<Any> = listOf(),
    val status: String? = null,
    val signedLspa: Boolean = false,
    val uspstring: String = "",
    val thisContent: JSONObject = JSONObject()
)

internal fun SPConsents.toJsonObject(): JSONObject {
    return JSONObject().apply {
        put("gdpr", gdpr?.consent?.thisContent)
        put("ccpa", ccpa?.consent?.thisContent)
    }
}

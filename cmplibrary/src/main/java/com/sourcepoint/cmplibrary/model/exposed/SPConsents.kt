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
    val uuid: String?
    var euconsent: String
    var tcData: Map<String, Any?>
    var grants: Map<String, Map<String, Boolean>>
//    val acceptedCategories: List<String>
//    val acceptedVendors: List<String>
}

internal data class GDPRConsentInternal(
    override var euconsent: String = "",
    override val uuid: String? = null,
    override var tcData: Map<String, Any?> = emptyMap(),
    override var grants: Map<String, Map<String, Boolean>> = emptyMap(),
//    override val acceptedCategories: List<String> = emptyList(),
//    override val acceptedVendors: List<String> = emptyList(),
    val thisContent: JSONObject = JSONObject()
) : GDPRConsent

interface CCPAConsent {
    val uuid: String?
    val rejectedCategories: List<Any>
    val rejectedVendors: List<Any>
    val status: String?
    val uspstring: String
}

internal data class CCPAConsentInternal(
    override val uuid: String? = null,
    override val rejectedCategories: List<Any> = listOf(),
    override val rejectedVendors: List<Any> = listOf(),
    override val status: String? = null,
    override val uspstring: String = "",
    val thisContent: JSONObject = JSONObject()
) : CCPAConsent

internal fun GDPRConsentInternal.toJsonObject(): JSONObject {
    return JSONObject().apply {
        put("uuid", uuid)
        put("tcData", tcData.toJSONObj())
        put("grants", grants.toJSONObj())
        put("euconsent", euconsent)
//        put("acceptedCategories", JSONArray(acceptedCategories))
//        put("acceptedVendors", JSONArray(acceptedVendors))
    }
}

internal fun CCPAConsentInternal.toJsonObject(): JSONObject {
    return JSONObject().apply {
        put("uuid", uuid)
        put("status", status)
        put("uspstring", uspstring)
        put("rejectedCategories", JSONArray(rejectedCategories))
        put("rejectedVendors", JSONArray(rejectedVendors))
    }
}

internal fun SPConsents.toJsonObject(): JSONObject {
    return JSONObject().apply {
        put("gdpr", (gdpr?.consent as? GDPRConsentInternal)?.toJsonObject())
        put("ccpa", (ccpa?.consent as? CCPAConsentInternal)?.toJsonObject())
    }
}

package com.sourcepoint.cmplibrary.model.exposed

import com.sourcepoint.cmplibrary.data.network.model.optimized.ConsentStatus
import com.sourcepoint.cmplibrary.data.network.model.optimized.GoogleConsentMode
import com.sourcepoint.cmplibrary.data.network.model.optimized.сonsentStatus.USNatCS
import com.sourcepoint.cmplibrary.data.network.model.optimized.USNatConsentStatus
import com.sourcepoint.cmplibrary.model.toConsentJSONObj
import com.sourcepoint.cmplibrary.model.toJSONObjGrant
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
    var googleConsentMode: GoogleConsentMode?
}

internal data class GDPRConsentInternal(
    override var euconsent: String = "",
    override val uuid: String? = null,
    override var tcData: Map<String, Any?> = emptyMap(),
    override var grants: Map<String, GDPRPurposeGrants> = emptyMap(),
    override val acceptedCategories: List<String>? = null,
    override val applies: Boolean = false,
    override val consentStatus: ConsentStatus? = null,
    override var googleConsentMode: GoogleConsentMode? = null,
    val childPmId: String? = null,
    val thisContent: JSONObject = JSONObject(),
    override val webConsentPayload: JsonObject? = null,
) : GDPRConsent

interface CCPAConsent {
    companion object {
        const val DEFAULT_USPSTRING = "1YNN"
    }
    val uuid: String?
    var gppData: Map<String, Any?>
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
    override var gppData: Map<String, Any?> = emptyMap(),
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

interface Consentable {
    val id: String
    val consented: Boolean
}
@Serializable
data class ConsentableImpl(
    @SerialName("_id") override val id: String,
    override val consented: Boolean
) : Consentable {
    fun toJsonObject(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("consented", consented)
        }
    }
}

data class UsNatStatuses(
    val hasConsentData: Boolean?,
    val rejectedAny: Boolean?,
    val consentedToAll: Boolean?,
    val consentedToAny: Boolean?,
    val sellStatus: Boolean?,
    val shareStatus: Boolean?,
    val sensitiveDataStatus: Boolean?,
    val gpcStatus: Boolean?,
) {
    fun toJsonObject(): Any {
        return JSONObject().apply {
            putOpt("hasConsentData", hasConsentData)
            putOpt("rejectedAny", rejectedAny)
            putOpt("consentedToAll", consentedToAll)
            putOpt("consentedToAny", consentedToAny)
            putOpt("sellStatus", sellStatus)
            putOpt("shareStatus", shareStatus)
            putOpt("sensitiveDataStatus", sensitiveDataStatus)
            putOpt("gpcStatus", gpcStatus)
        }
    }
}

interface UsNatConsent {
    var gppData: Map<String, Any?>
    val applies: Boolean

    @Deprecated("`consentStatus` is deprecated and will be renamed to `statuses` in the next release.", ReplaceWith("statuses"))
    val consentStatus: USNatConsentStatus?
    val statuses: UsNatStatuses
    val consentStrings: List<USNatCS.ConsentString>?
    val dateCreated: String?
    val vendors: List<Consentable>?
    val categories: List<Consentable>?
    val uuid: String?
    val webConsentPayload: JsonObject?
}

internal data class UsNatConsentInternal(
    override var gppData: Map<String, Any?> = emptyMap(),
    override val applies: Boolean = false,
    override val consentStrings: List<USNatCS.ConsentString> = emptyList(),
    override val dateCreated: String? = null,
    override val vendors: List<ConsentableImpl> = listOf(),
    override val categories: List<ConsentableImpl> = listOf(),
    override val uuid: String? = null,
    override val webConsentPayload: JsonObject? = null,
    @Deprecated("`consentStatus` is deprecated and will be renamed to `statuses` in the next release.", ReplaceWith("statuses"))
    override val consentStatus: USNatConsentStatus? = null,
    val url: String? = null,
) : UsNatConsent {
    override val statuses: UsNatStatuses
        get() = UsNatStatuses(
            hasConsentData = consentStatus?.hasConsentData,
            rejectedAny = consentStatus?.rejectedAny,
            consentedToAll = consentStatus?.consentedToAll,
            consentedToAny = consentStatus?.consentedToAny,
            sellStatus = consentStatus?.granularStatus?.sellStatus,
            shareStatus = consentStatus?.granularStatus?.shareStatus,
            sensitiveDataStatus = consentStatus?.granularStatus?.sensitiveDataStatus,
            gpcStatus = consentStatus?.granularStatus?.gpcStatus,
        )
}
fun SPConsents.toWebViewConsentsJsonObject(): JsonObject = buildJsonObject {
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
        put("tcData", tcData.toConsentJSONObj())
        put("grants", grants.toJSONObjGrant())
        put("euconsent", euconsent)
        put("apply", applies)
        put("acceptedCategories", JSONArray(acceptedCategories))
        put("consentStatus", consentStatus?.toJSONObj())
        put("googleConsentMode", googleConsentMode?.toJSONObj())
    }
}

internal fun GoogleConsentMode.toJSONObj(): Any {
    return JSONObject().apply {
        put("ad_user_data", adUserData?.status)
        put("ad_personalization", adPersonalization?.status)
        put("analytics_storage", analyticsStorage?.status)
        put("ad_storage", adStorage?.status)
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
        put("gppData", gppData.toConsentJSONObj())
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
        put("gppData", gppData.toConsentJSONObj())
        put("statuses", statuses.toJsonObject())
        put("consentStrings", consentStrings.toJsonObjectList())
        put("dateCreated", dateCreated)
        put("vendors", vendors.map { it.toJsonObject() })
        put("categories", categories.map { it.toJsonObject() })
        put("uuid", uuid)
    }
}

internal fun SPConsents.toJsonObject(): JSONObject {
    return JSONObject().apply {
        put("gdpr", (gdpr?.consent as? GDPRConsentInternal)?.toJsonObject())
        put("ccpa", (ccpa?.consent as? CCPAConsentInternal)?.toJsonObject())
        put("usnat", (usNat?.consent as? UsNatConsentInternal)?.toJsonObject())
    }
}

internal fun List<USNatCS.ConsentString>.toJsonObjectList(): List<JSONObject> {
    return this.map { consentString ->
        JSONObject().apply {
            put("sectionId", consentString.sectionId)
            put("sectionName", consentString.sectionName)
            put("consentString", consentString.consentString)
        }
    }
}

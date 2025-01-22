package com.sourcepoint.cmplibrary.data.network.model.optimized.сonsentStatus

import com.sourcepoint.cmplibrary.data.network.converter.CcpaStatusSerializer
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverterImpl
import com.sourcepoint.cmplibrary.data.network.converter.JsonMapSerializer
import com.sourcepoint.cmplibrary.model.exposed.CCPAConsent.Companion.DEFAULT_USPSTRING
import com.sourcepoint.cmplibrary.model.exposed.CCPAConsentInternal
import com.sourcepoint.cmplibrary.model.exposed.CcpaStatus
import com.sourcepoint.cmplibrary.model.exposed.toCoreCCPAConsentStatus
import com.sourcepoint.cmplibrary.util.extensions.toMapOfAny
import com.sourcepoint.mobile_core.models.consents.CCPAConsent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.json.JSONObject

@Serializable
data class CcpaCS(
    val applies: Boolean? = null,
    @SerialName("consentedAll") val consentedAll: Boolean? = null,
    @SerialName("dateCreated") val dateCreated: String? = null,
    @SerialName("gpcEnabled") val gpcEnabled: Boolean? = null,
    @SerialName("newUser") val newUser: Boolean? = null,
    @SerialName("rejectedAll") val rejectedAll: Boolean? = null,
    @SerialName("rejectedCategories") val rejectedCategories: List<String>? = emptyList(),
    @SerialName("rejectedVendors") val rejectedVendors: List<String>? = emptyList(),
    @SerialName("signedLspa") val signedLspa: Boolean? = null,
    @SerialName("uspstring") val uspstring: String? = null,
    @Serializable(with = CcpaStatusSerializer::class) val status: CcpaStatus? = null,
    @SerialName("GPPData") @Serializable(with = JsonMapSerializer::class) val gppData: Map<String, JsonElement>? = emptyMap(),
    @SerialName("uuid") var uuid: String? = null,
    @SerialName("webConsentPayload") val webConsentPayload: JsonObject? = null,
    @SerialName("expirationDate") var expirationDate: String? = null,
) {
    fun toCoreCCPAConsent(): CCPAConsent {
        return CCPAConsent(
            applies = applies?: false,
            uuid = uuid,
            dateCreated = dateCreated,
            expirationDate = expirationDate,
            signedLspa = signedLspa,
            uspstring = uspstring,
            rejectedVendors = rejectedVendors?: emptyList(),
            rejectedCategories = rejectedCategories?: emptyList(),
            status = status?.toCoreCCPAConsentStatus(),
            webConsentPayload = webConsentPayload.toString(),
            gppData = gppData?.mapValues { it.value.jsonPrimitive } ?: emptyMap()
        )
    }
    fun copyingFrom(core: CCPAConsent?, applies: Boolean?): CcpaCS {
        if (core == null) { return this }

        return copy(
            applies = applies,
            rejectedVendors = core.rejectedVendors,
            rejectedCategories = core.rejectedCategories,
            signedLspa = core.signedLspa,
            status = CcpaStatus.entries.firstOrNull { it.name.lowercase() == core.status?.name?.lowercase() },
            dateCreated = core.dateCreated,
            expirationDate = core.expirationDate,
            uuid = core.uuid,
            webConsentPayload = core.webConsentPayload?.let { JsonConverterImpl().toJsonObject(it) },
            gppData = core.gppData,
            uspstring = core.uspstring
        )
    }

    internal fun toCCPAConsentInternal(): CCPAConsentInternal {
        return CCPAConsentInternal(
            uuid = uuid,
            applies = applies ?: false,
            gppData = gppData?.toMapOfAny() ?: emptyMap(),
            status = status,
            childPmId = null,
            rejectedVendors = rejectedVendors ?: emptyList(),
            rejectedCategories = rejectedCategories ?: emptyList(),
            thisContent = JSONObject(),
            signedLspa = signedLspa,
            webConsentPayload = webConsentPayload,
            uspstring = uspstring ?: DEFAULT_USPSTRING
        )
    }
}

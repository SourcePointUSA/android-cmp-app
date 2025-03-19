package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.data.network.converter.GranularStateSerializer
import com.sourcepoint.cmplibrary.data.network.converter.JsonMapSerializer
import com.sourcepoint.cmplibrary.model.exposed.ConsentableImpl
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable(with = GranularStateSerializer::class)
enum class GranularState {
    ALL,
    SOME,
    NONE,
    EMPTY_VL;

    companion object {
        fun fromString(value: String?): GranularState? =
            entries.find { it.name.equals(value, ignoreCase = true) }
    }
}

enum class GCMStatus(val status: String) {
    GRANTED("granted"),
    DENIED("denied");

    companion object {
        fun firstWithStatusOrNull(status: String?) = entries.firstOrNull { it.status == status }
    }
}

@Serializable
data class USNatConsentData(
    val applies: Boolean? = null,
    val consentStatus: USNatConsentStatus? = null,
    val consentStrings: List<ConsentString>? = null,
    var dateCreated: String? = null,
    var uuid: String? = null,
    val webConsentPayload: JsonObject? = null,
    val userConsents: UserConsents? = null,
    @SerialName("GPPData") @Serializable(with = JsonMapSerializer::class) val gppData: Map<String, JsonElement>? = emptyMap(),
) {
    @Serializable
    data class ConsentString(
        val sectionId: Int?,
        val sectionName: String?,
        val consentString: String?
    )

    @Serializable
    data class UserConsents(
        val vendors: List<ConsentableImpl>? = emptyList(),
        val categories: List<ConsentableImpl>? = emptyList()
    )

    val vendors: List<ConsentableImpl> get() = userConsents?.vendors ?: emptyList()
    val categories: List<ConsentableImpl> get() = userConsents?.categories ?: emptyList()
}

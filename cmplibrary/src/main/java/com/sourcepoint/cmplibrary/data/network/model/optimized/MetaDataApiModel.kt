package com.sourcepoint.cmplibrary.data.network.model.optimized

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class MetaDataArg(
    @SerialName("ccpa") val ccpa: CcpaArg? = null,
    @SerialName("gdpr") val gdpr: GdprArg? = null,
    @SerialName("usnat") val usNat: UsNatArg? = null,
) {
    @Serializable
    data class CcpaArg(
        @SerialName("applies") val applies: Boolean?,
        @SerialName("hasLocalData") val hasLocalData: Boolean? = null,
        @SerialName("groupPmId") val groupPmId: String? = null,
        @SerialName("targetingParams") val targetingParams: JsonElement? = null,
        @SerialName("uuid") val uuid: String? = null,
    )

    @Serializable
    data class GdprArg(
        @SerialName("applies") val applies: Boolean?,
        @SerialName("hasLocalData") val hasLocalData: Boolean? = null,
        @SerialName("groupPmId") val groupPmId: String? = null,
        @SerialName("targetingParams") val targetingParams: JsonElement? = null,
        @SerialName("uuid") val uuid: String? = null,
    )

    @Serializable
    data class UsNatArg(
        @SerialName("applies") val applies: Boolean?,
        @SerialName("hasLocalData") val hasLocalData: Boolean? = null,
        @SerialName("groupPmId") val groupPmId: String? = null,
        @SerialName("targetingParams") val targetingParams: JsonElement? = null,
        @SerialName("uuid") val uuid: String? = null,
        @SerialName("dateCreated") val dateCreated: String? = null,
        @SerialName("transitionCCPAAuth") val transitionCCPAAuth: Boolean? = null,
        @SerialName("optedOut") val optedOut: Boolean? = null,
    )
}

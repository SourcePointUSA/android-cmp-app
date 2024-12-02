package com.sourcepoint.cmplibrary.data.network.model.optimized.сonsentStatus

import com.sourcepoint.cmplibrary.data.network.util.Env
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
internal data class ConsentStatusParamRequest(
    @SerialName("env") val env: Env,
    @SerialName("metadata") val metadata: String,
    @SerialName("propertyId") val propertyId: Long,
    @SerialName("accountId") val accountId: Long,
    @SerialName("authId") val authId: String?,
    @SerialName("localState") val localState: JsonElement?,
    @SerialName("includeData") val includeData: JsonObject,
)

enum class GranularState {
    ALL,
    SOME,
    NONE,
    EMPTY_VL;

    companion object {
        fun firstWithStateOrNONE(state: String?) = (entries.firstOrNull { it.name == state })?: NONE
    }
}

enum class GCMStatus(val status: String) {
    GRANTED("granted"),
    DENIED("denied");

    companion object {
        fun firstWithStatusOrNull(status: String?) = entries.firstOrNull { it.status == status }
    }
}


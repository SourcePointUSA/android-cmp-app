package com.sourcepoint.cmplibrary.data.network.model.optimized.messages

import com.sourcepoint.cmplibrary.data.network.model.optimized.includeData.IncludeData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
internal data class MessagesBodyReq(
    @SerialName("accountId")
    val accountId: Int,
    @SerialName("propertyHref")
    val propertyHref: String,
    @SerialName("campaigns")
    val campaigns: JsonObject,
    @SerialName("campaignEnv")
    val campaignEnv: String?,
    @SerialName("consentLanguage")
    val consentLanguage: String?,
    @SerialName("hasCSP")
    val hasCSP: Boolean,
    @SerialName("includeData")
    val includeData: IncludeData,
    @SerialName("localState")
    val localState: JsonObject?,
    @SerialName("os")
    val operatingSystem: OperatingSystemInfoParam,
)

package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.model.optimized.includeData.IncludeData
import kotlinx.serialization.json.* // ktlint-disable

internal fun postChoiceGdprBody(
    sampleRate: Double,
    propertyId: Long,
    messageId: Long?,
    consentAllRef: String?,
    vendorListId: String?,
    granularStatus: ConsentStatus.GranularStatus?,
    sendPvData: Boolean?,
    pubData: JsonObject? = null,
    saveAndExitVariables: JsonObject? = null,
    authid: String? = null,
    uuid: String? = null
): JsonObject {
    return buildJsonObject {
        pubData?.let { put("pubData", it) }
        put("sendPVData", sendPvData)
        put("sampleRate", sampleRate)
        put("propertyId", propertyId)
        put("messageId", messageId)
        put("authId", authid)
        put("uuid", uuid)
        put("consentAllRef", consentAllRef)
        saveAndExitVariables?.let { put("pmSaveAndExitVariables", it) }
        put("granularStatus", granularStatus?.let { JsonConverter.converter.encodeToJsonElement(it) } ?: JsonNull)
        put("vendorListId", vendorListId)
        putJsonObject("includeData") {
            putJsonObject("TCData") {
                put("type", "RecordString")
            }
            putJsonObject("localState") {
                put("type", "RecordString")
            }
            putJsonObject("webConsentPayload") {
                put("type", "RecordString")
            }
        }
    }
}

internal fun postChoiceCcpaBody(
    sampleRate: Double,
    propertyId: Long,
    messageId: Long?,
    sendPvData: Boolean?,
    pubData: JsonObject? = null,
    saveAndExitVariables: JsonObject? = null,
    authid: String? = null,
    uuid: String? = null,
    includeData: IncludeData
): JsonObject {
    return buildJsonObject {
        put("sendPVData", sendPvData)
        put("sampleRate", sampleRate)
        put("propertyId", propertyId)
        put("uuid", uuid)
        put("includeData", JsonConverter.converter.encodeToJsonElement(includeData))
        messageId?.let { put("messageId", messageId) }
        authid?.let { put("authId", authid) }
        saveAndExitVariables?.let { put("pmSaveAndExitVariables", it) }
        pubData?.let { put("pubData", pubData) }
    }
}

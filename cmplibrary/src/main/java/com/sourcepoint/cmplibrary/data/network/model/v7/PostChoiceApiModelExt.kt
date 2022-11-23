package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import kotlinx.serialization.json.* // ktlint-disable

internal fun postChoiceGdprBody(
    sampleRate: Int,
    propertyId: Long,
    messageId: Long,
    consentAllRef: String?,
    vendorListId: String?,
    granularStatus: ConsentStatus.GranularStatus?,
    pubData: JsonObject? = null,
    saveAndExitVariables: JsonObject? = null,
    authid: String? = null,
    uuid: String? = null
): JsonObject {
    return buildJsonObject {
        pubData?.let { put("pubData", it) }
        put("sendPVData", true)
        put("sampleRate", sampleRate)
        put("propertyId", propertyId)
        put("messageId", messageId)
//        put("authid", authid)
//        put("uuid", uuid)
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
        }
    }
}

internal fun postChoiceCcpaBody(
    sampleRate: Int,
    propertyId: Long,
    messageId: Long,
    pubData: JsonObject? = null,
    saveAndExitVariables: JsonObject? = null,
    authid: String? = null,
    uuid: String? = null
): JsonObject {
    return buildJsonObject {
        pubData?.let { put("pubData", pubData) }
        put("sendPVData", true)
        put("sampleRate", sampleRate)
        put("propertyId", propertyId)
        put("messageId", messageId)
        put("authid", authid)
        put("uuid", uuid)
        saveAndExitVariables?.let { put("pmSaveAndExitVariables", it) }
        putJsonObject("includeData") {
            putJsonObject("localState") {
                put("type", "RecordString")
            }
        }
    }
}

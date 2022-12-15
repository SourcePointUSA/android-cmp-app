package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import kotlinx.serialization.json.* //ktlint-disable

internal fun toGdprChoiceBody(
    gdprCs: ConsentStatus?,
    accountId: Int?,
    propertyId: Int?,
    gdprApplies: Boolean?,
    gdprMessageMetaData: MessageMetaData?,
    sampleRate: Double,
    fromTest: Boolean = true,
): JsonObject {
    return buildJsonObject {
        put("accountId", accountId)
        put("gdprApplies", gdprApplies)
        put("siteId", propertyId)
        put("consentStatus", gdprCs?.let { JsonConverter.converter.encodeToJsonElement(it) } ?: JsonNull)
        put("msgId", gdprMessageMetaData?.messageId)
        put("categoryId", gdprMessageMetaData?.categoryId?.code)
        put("subCategoryId", gdprMessageMetaData?.subCategoryId?.code)
        put("prtnUUID", gdprMessageMetaData?.prtnUUID)
        put("fromTest", fromTest)
        put("sampleRate", sampleRate)
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

internal fun toCcpaChoiceBody(
    gdprCs: ConsentStatus?,
    accountId: Int?,
    propertyId: Int?,
    gdprApplies: Boolean?,
    gdprMessageMetaData: MessageMetaData?,
    sampleRate: Double,
    fromTest: Boolean = true,
): JsonObject {
    return buildJsonObject {
        put("accountId", accountId)
        put("gdprApplies", gdprApplies)
        put("siteId", propertyId)
        put("consentStatus", gdprCs?.let { JsonConverter.converter.encodeToJsonElement(it) } ?: JsonNull)
        put("msgId", gdprMessageMetaData?.messageId)
        put("categoryId", gdprMessageMetaData?.categoryId?.code)
        put("subCategoryId", gdprMessageMetaData?.subCategoryId?.code)
        put("prtnUUID", gdprMessageMetaData?.prtnUUID)
        put("fromTest", fromTest)
        put("sampleRate", sampleRate)
    }
}

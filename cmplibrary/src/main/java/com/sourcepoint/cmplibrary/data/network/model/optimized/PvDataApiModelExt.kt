package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put

internal fun toPvDataBody(
    gdprCs: GdprCS?,
    accountId: Long,
    propertyId: Long,
    gdprApplies: Boolean,
    ccpaApplies: Boolean,
    gdprMessageMetaData: MessageMetaData?,
    ccpaMessageMetaData: MessageMetaData?,
    ccpaCS: CcpaCS?,
    sampleRate: Double = 1.0,
    pubData: JsonObject = JsonObject(mapOf())
): JsonObject {

    return buildJsonObject {
        gdprCs?.let { gdpr ->
            put(
                "gdpr",
                buildJsonObject {
                    put("uuid", gdpr.uuid)
                    put("euconsent", gdpr.euconsent)
                    put("accountId", accountId)
                    put("pubData", pubData)
                    put("applies", gdprApplies)
                    put("siteId", propertyId)
                    put("consentStatus", JsonConverter.converter.encodeToJsonElement(gdpr.consentStatus))
                    put("msgId", gdprMessageMetaData?.messageId)
                    put("categoryId", gdprMessageMetaData?.categoryId?.code)
                    put("subCategoryId", gdprMessageMetaData?.subCategoryId?.code)
                    put("prtnUUID", gdprMessageMetaData?.prtnUUID)
                    put("sampleRate", sampleRate)
                }
            )
        }
        ccpaCS?.let { ccpa ->
            put(
                "ccpa",
                buildJsonObject {
                    put("uuid", ccpa.uuid)
                    put("accountId", accountId)
                    put("applies", ccpaApplies)
                    put("siteId", propertyId)
                    put("consentStatus", JsonConverter.converter.encodeToJsonElement(ccpa))
                    put("messageId", ccpaMessageMetaData?.messageId)
                    put("sampleRate", sampleRate)
                    put("pubData", pubData)
                }
            )
        }
    }
}

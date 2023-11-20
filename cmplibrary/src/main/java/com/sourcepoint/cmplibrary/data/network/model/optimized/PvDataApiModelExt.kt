package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.consent.ConsentManagerUtils.Companion.DEFAULT_SAMPLE_RATE
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put

internal fun toPvDataBody(
    gdprCs: GdprCS?,
    accountId: Long?,
    propertyId: Long?,
    gdprMessageMetaData: MessageMetaData?,
    ccpaMessageMetaData: MessageMetaData?,
    ccpaCS: CcpaCS?,
    usNatCS: USNatConsentData?,
    metaDataResp: MetaDataResp?,
    pubData: JsonObject = JsonObject(mapOf())
): JsonObject {

    return buildJsonObject {
        gdprCs?.let { cs ->
            put(
                "gdpr",
                buildJsonObject {
                    put("uuid", cs.uuid)
                    put("euconsent", cs.uuid)
                    put("accountId", accountId)
                    put("applies", metaDataResp?.gdpr?.applies)
                    put("siteId", propertyId)
                    put("consentStatus", JsonConverter.converter.encodeToJsonElement(cs.consentStatus))
                    put("msgId", gdprMessageMetaData?.messageId)
                    put("categoryId", gdprMessageMetaData?.categoryId?.code)
                    put("subCategoryId", gdprMessageMetaData?.subCategoryId?.code)
                    put("prtnUUID", gdprMessageMetaData?.prtnUUID)
                    put("sampleRate", metaDataResp?.gdpr?.sampleRate ?: DEFAULT_SAMPLE_RATE)
                }
            )
        }
        ccpaCS?.let { cs ->
            put(
                "ccpa",
                buildJsonObject {
                    put("uuid", cs.uuid)
                    put("accountId", accountId)
                    put("applies", metaDataResp?.ccpa?.applies)
                    put("siteId", propertyId)
                    put("consentStatus", JsonConverter.converter.encodeToJsonElement(cs))
                    put("messageId", ccpaMessageMetaData?.messageId)
                    put("uuid", cs.uuid)
                    put("sampleRate", metaDataResp?.ccpa?.sampleRate ?: DEFAULT_SAMPLE_RATE)
                    put("pubData", pubData)
                }
            )
        }
        usNatCS?.let { cs ->
            put(
                "usnat",
                buildJsonObject {
                    put("uuid", cs.uuid)
                    put("accountId", accountId)
                    put("applies", metaDataResp?.usNat?.applies)
                    put("siteId", propertyId)
                    cs.consentStatus?.let { put("consentStatus", JsonConverter.converter.encodeToJsonElement(it)) }
                    put("messageId", usNatCS?.messageMetaData?.messageId)
                    put("uuid", cs.uuid)
                    put("sampleRate", metaDataResp?.usNat?.sampleRate ?: DEFAULT_SAMPLE_RATE)
                    put("pubData", pubData)
                }
            )
        }
    }
}

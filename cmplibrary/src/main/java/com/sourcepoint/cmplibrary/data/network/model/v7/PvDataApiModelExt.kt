package com.sourcepoint.cmplibrary.data.network.model.v7

import com.example.cmplibrary.BuildConfig
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.model.v7.ConsentStatusResp.ConsentStatusData.CcpaCS
import kotlinx.serialization.json.* //ktlint-disable
import org.json.JSONObject

internal fun toPvDataBody2(
    gdprCs: ConsentStatus?,
    accountId: Long?,
    propertyId: Long?,
    gdprApplies: Boolean?,
    ccpaApplies: Boolean?,
    gdprMessageMetaData: MessageMetaData?,
    ccpaMessageMetaData: MessageMetaData?,
    ccpaCS: CcpaCS?,
    fromTest: Boolean = true,
    pubData: JsonObject = JsonNull.jsonObject
): JsonObject {

    return buildJsonObject {
        put(
            "gdpr",
            buildJsonObject {
                put("accountId", accountId)
                put("applies", gdprApplies)
                put("siteId", propertyId)
                put("consentStatus", gdprCs?.let { JsonConverter.converter.encodeToJsonElement(it) } ?: JsonNull)
                put("msgId", gdprMessageMetaData?.messageId)
                put("categoryId", gdprMessageMetaData?.categoryId?.code)
                put("subCategoryId", gdprMessageMetaData?.subCategoryId?.code)
                put("prtnUUID", gdprMessageMetaData?.prtnUUID)
                put("fromTest", fromTest)
                put("sampleRate", BuildConfig.SAMPLE_RATE)
            }
        )
        put(
            "ccpa",
            buildJsonObject {
                put("accountId", accountId)
                put("applies", ccpaApplies)
                put("siteId", propertyId)
                put("consentStatus", ccpaCS?.let { JsonConverter.converter.encodeToJsonElement(it) } ?: JsonNull)
                put("messageId", ccpaMessageMetaData?.messageId)
                put("uuid", ccpaCS?.uuid)
                put("sampleRate", BuildConfig.SAMPLE_RATE)
                put("pubData", pubData)
            }
        )
    }
}

internal fun toPvDataBody(
    messages: MessagesResp?,
    accountId: Long?,
    siteId: Long?,
    gdprApplies: Boolean?,
    ccpaUuid: String?,
    gdprUuid: String?
): JSONObject {

    return if (messages != null) {
        val gdpr = messages.campaigns
            ?.gdpr
            ?.let {
                JSONObject().apply {
                    put("applies", gdprApplies)
                    put("uuid", gdprUuid)
                    put("accountId", accountId)
                    put("siteId", siteId)
                    put("euconsent", it.euconsent)
                    put("pubData", "string")
                    put("msgId", it.messageMetaData.messageId)
                    put("categoryId", it.messageMetaData.categoryId.code)
                    put("subCategoryId", it.messageMetaData.subCategoryId?.code)
                    put("prtnUUID", it.messageMetaData.prtnUUID)
                    put("sampleRate", BuildConfig.SAMPLE_RATE)
                    put("consentStatus", "string")
                }
            }

        val ccpa = messages.campaigns
            ?.ccpa
            ?.let {
                JSONObject().apply {
                    put("applies", it.applies)
                    put("uuid", ccpaUuid)
                    put("accountId", accountId)
                    put("siteId", siteId)
                    put("messageId", it.messageMetaData.messageId)
                    put("pubData", "string")
                    put("sampleRate", BuildConfig.SAMPLE_RATE)
                    put("consentStatus", "string")
                }
            }

        JSONObject().apply {
            put("gdpr", gdpr)
            put("ccpa", ccpa)
        }
    } else JSONObject()
}

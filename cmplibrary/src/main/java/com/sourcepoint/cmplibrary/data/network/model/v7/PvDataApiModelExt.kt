package com.sourcepoint.cmplibrary.data.network.model.v7

import com.example.cmplibrary.BuildConfig
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import kotlinx.serialization.json.* //ktlint-disable
import org.json.JSONObject

internal fun toPvDataBody2(
    gdprCs: ConsentStatus?,
    accountId: Long?,
    propertyId: Long?,
    gdprApplies: Boolean?,
    messageMetaData: MessageMetaData,
    fromTest: Boolean
): JsonObject {
    return buildJsonObject {
        put("accountId", accountId)
        put("applies", gdprApplies)
        put("siteId", propertyId)
        put("consentStatus", gdprCs?.let { JsonConverter.converter.encodeToJsonElement(gdprCs) } ?: JsonNull)
        put("msgId", messageMetaData.messageId)
        put("categoryId", messageMetaData.categoryId.code)
        put("subCategoryId", messageMetaData.subCategoryId.code)
        put("prtnUUID", messageMetaData.prtnUUID)
        put("fromTest", fromTest)
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

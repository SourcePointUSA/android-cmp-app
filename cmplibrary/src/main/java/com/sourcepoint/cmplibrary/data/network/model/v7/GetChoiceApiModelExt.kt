package com.sourcepoint.cmplibrary.data.network.model.v7

import com.example.cmplibrary.BuildConfig
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import kotlinx.serialization.json.* //ktlint-disable

internal fun toChoiceBody(
    gdprCs: ConsentStatus?,
    accountId: Long?,
    propertyId: Long?,
    gdprApplies: Boolean?,
    gdprMessageMetaData: MessageMetaData?,
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
        put("sampleRate", BuildConfig.SAMPLE_RATE)
    }
}

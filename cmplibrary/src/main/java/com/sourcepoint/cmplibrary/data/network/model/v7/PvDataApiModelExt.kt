package com.sourcepoint.cmplibrary.data.network.model.v7

import com.example.cmplibrary.BuildConfig
import com.sourcepoint.cmplibrary.data.network.converter.failParam
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.getFieldValue
import com.sourcepoint.cmplibrary.model.getMap
import org.json.JSONObject
import java.util.TreeMap

internal fun Map<String, Any?>.toPvDataResp(): PvDataResp {

    val gdpr = getMap("gdpr")?.toGdprPv()

    return PvDataResp(
        thisContent = JSONObject(this),
        gdprPv = gdpr
    )
}

internal fun Map<String, Any?>.toGdprPv(): GdprPv {
    val uuid = getFieldValue<String>("uuid") ?: failParam("PvDataResp - uuid")

    val cookies = getFieldValue<Iterable<Any?>>("cookies")
        ?.filterIsInstance(TreeMap::class.java)
        ?.map {
            JSONObject().apply {
                it.keys.forEach { key -> (key as? String)?.let { k -> put(key, it[k]) } }
            }
        }
        ?: failParam("cookies")

    return GdprPv(
        thisContent = JSONObject(this),
        cookies = cookies,
        uuid = uuid
    )
}

internal fun toPvDataBody(
    messages: MessagesResp,
    accountId: Long,
    siteId: Long,
    gdprApplies: Boolean,
    ccpaUuid: String,
    gdprUuid: String
): JSONObject {

    val gdpr = messages.campaigns
        .find { it.type == CampaignType.GDPR.name }
        ?.let { it as? GdprMessage }
        ?.let {
            JSONObject().apply {
                put("applies", gdprApplies)
                put("uuid", gdprUuid)
                put("accountId", accountId)
                put("siteId", siteId)
                put("euconsent", it.euconsent)
                put("pubData", "string")
                put("msgId", it.messageMetaData?.messageId)
                put("categoryId", it.messageMetaData?.categoryId)
                put("subCategoryId", it.messageMetaData?.subCategoryId?.code)
                put("prtnUUID", it.messageMetaData?.prtnUUID)
                put("sampleRate", BuildConfig.SAMPLE_RATE)
                put("consentStatus", "string")
            }
        }

    val ccpa = messages.campaigns
        .find { it.type == CampaignType.CCPA.name }
        ?.let { it as? CcpaMessage }
        ?.let {
            JSONObject().apply {
                put("applies", it.applies)
                put("uuid", ccpaUuid)
                put("accountId", accountId)
                put("siteId", siteId)
                put("messageId", it.messageMetaData?.messageId)
                put("pubData", "string")
                put("sampleRate", BuildConfig.SAMPLE_RATE)
                put("consentStatus", "string")
            }
        }

    return JSONObject().apply {
        put("gdpr", gdpr)
        put("ccpa", ccpa)
    }
}

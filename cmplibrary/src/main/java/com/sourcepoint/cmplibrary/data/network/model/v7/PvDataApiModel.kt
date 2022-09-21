package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.CampaignType
import org.json.JSONObject

internal data class PvDataResp(
    val thisContent: JSONObject,
    val gdprPv: GdprPv?
)

internal data class GdprPv(
    val thisContent: JSONObject,
    val uuid: String,
    val cookies: List<JSONObject>
)

internal data class PvDataParamReq(
    val env: Env,
    val body: JSONObject
)

internal fun toPvDataBody(
    messages : MessagesResp,
    csd: ConsentStatusData,
    accountId: Long,
    siteId: Long,
    pubData: String,
): JSONObject {

    val gdpr = messages.campaigns
        .find { it.type == CampaignType.GDPR.name }
        ?.let { it as? GdprMessage }
        ?.let {
        JSONObject().apply {
            put("applies", csd.gdprCS?.gdprApplies)
            put("uuid", csd.gdprCS?.uuid)
            put("accountId", accountId)
            put("siteId", siteId)
            put("euconsent", it.euconsent)
            put("pubData", pubData)
            put("msgId", it.messageMetaData?.messageId)
            put("categoryId", it.messageMetaData?.categoryId)
            put("subCategoryId", it.messageMetaData?.subCategoryId)
            put("prtnUUID", it.messageMetaData?.prtnUUID)
            put("sampleRate", 1)
            put("consentStatus", it.consentStatusCS?.toJsonObject())
        }
    }

    val ccpa = messages.campaigns
        .find { it.type == CampaignType.GDPR.name }
        ?.let { it as? GdprMessage }
        ?.let {
            JSONObject().apply {
                put("applies", csd.ccpaCS?.ccpaApplies)
                put("uuid", csd.ccpaCS?.uuid)
                put("accountId", accountId)
                put("siteId", siteId)
                put("messageId",  it.messageMetaData?.messageId)
                put("pubData", pubData)
                put("sampleRate", 1)
                put("consentStatus", csd.ccpaCS?.thisContent)
            }
        }

    return JSONObject().apply {
        put("gdpr", gdpr)
        put("ccpa", ccpa)
    }
}

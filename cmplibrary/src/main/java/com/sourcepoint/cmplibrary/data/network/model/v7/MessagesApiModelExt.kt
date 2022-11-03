package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.CampaignReq
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.* // ktlint-disable

internal fun getMessageBody(
    propertyHref: String,
    accountId: Long,
    localState: JsonObject,
    campaigns: List<CampaignReq>,
    cs: ConsentStatus?,
    ccpaStatus: String?
): JsonObject {
    return buildJsonObject {
        put("accountId", accountId)
        putJsonObject("includeData") {
            putJsonObject("TCData") {
                put("type", "RecordString")
            }
            putJsonObject("campaigns") {
                put("type", "RecordString")
            }
        }
        put("propertyHref", "https://$propertyHref")
        put("hasCSP", true)
        put("campaigns", campaigns.toMetadataBody(cs, ccpaStatus))
        put("localState", localState)
    }
}

internal fun List<CampaignReq>.toMetadataBody(
    cs: ConsentStatus? = null,
    ccpaStatus: String? = null
): JsonObject {
    return buildJsonObject {
        this@toMetadataBody.forEach { c ->
            putJsonObject(c.campaignType.name.lowercase()) {
                if (c.campaignType == CampaignType.GDPR) {
                    put(
                        "consentStatus",
                        cs?.let { JsonConverter.converter.encodeToJsonElement(it) } ?: JsonObject(mapOf())
                    )
                }
                if (c.campaignType == CampaignType.CCPA) {
                    put("status", ccpaStatus ?: "")
                }
                putJsonObject("targetingParams") {
                    c.targetingParams.forEach { t -> put(t.key, t.value) }
                }
                put("groupPmId", c.groupPmId)
            }
        }
    }
}

internal fun List<CampaignReq>.toMetadataArgs(): MetaDataArg {
    val json = buildJsonObject {
        this@toMetadataArgs.forEach { c ->
            putJsonObject(c.campaignType.name.lowercase()) {
                putJsonObject("targetingParams") {
                    c.targetingParams.forEach { t -> put(t.key, t.value) }
                }
                put("groupPmId", c.groupPmId)
            }
        }
    }

    return JsonConverter.converter.decodeFromJsonElement<MetaDataArg>(json)
}

internal fun MessagesParamReq.toMetaDataParamReq(): MetaDataParamReq {
    return MetaDataParamReq(
        env = env,
        accountId = accountId,
        propertyId = propertyId,
        metadata = metadataArg?.let { JsonConverter.converter.encodeToString(it) } ?: "{}",
    )
}

internal fun MessagesParamReq.toConsentStatusParamReq(
    gdprUuid: String?,
    ccpaUuid: String?
): ConsentStatusParamReq {

    val mdArg = metadataArg?.copy(
        gdpr = metadataArg.gdpr?.copy(uuid = gdprUuid),
        ccpa = metadataArg.ccpa?.copy(uuid = ccpaUuid)
    )

    return ConsentStatusParamReq(
        env = env,
        accountId = accountId,
        propertyId = propertyId,
        metadata = mdArg?.let { JsonConverter.converter.encodeToString(it) } ?: "{}",
        authId = authId
    )
}

package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.CampaignReq
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

internal fun List<CampaignReq>.toMetadataBody(
    gdprConsentStatus: ConsentStatus? = null,
    ccpaConsentStatus: String? = null
): JsonObject {
    return buildJsonObject {
        this@toMetadataBody.forEach { c ->
            putJsonObject(c.campaignType.name.lowercase()) {
                if (c.campaignType == CampaignType.GDPR) {
                    put(
                        "consentStatus",
                        gdprConsentStatus?.let { JsonConverter.converter.encodeToJsonElement(it) } ?: JsonObject(mapOf())
                    )
                    put("hasLocalData", gdprConsentStatus != null)
                }
                if (c.campaignType == CampaignType.CCPA) {
                    put("status", ccpaConsentStatus ?: "")
                    put("hasLocalData", ccpaConsentStatus != null)
                }
                putJsonObject("targetingParams") {
                    c.targetingParams.forEach { t -> put(t.key, t.value) }
                }
            }
        }
    }
}

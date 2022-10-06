package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.CampaignReq
import kotlinx.serialization.json.* // ktlint-disable

internal fun getMessageBody(
    cs: ConsentStatusResp?,
    propertyHref: String,
    accountId: Long
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
        putJsonObject("campaigns") {
            putJsonObject("ccpa") {
                put("hasLocalData", false)
            }
            putJsonObject("gdpr") {
                put("hasLocalData", false)
                putJsonObject("consentStatus") {
                    put("hasConsentData", cs?.consentStatusData?.gdpr?.consentStatus?.hasConsentData ?: false)
                    put("consentedToAll", cs?.consentStatusData?.gdpr?.consentStatus?.consentedAll ?: false)
                    put("consentedToAny", cs?.consentStatusData?.gdpr?.consentStatus?.consentedToAny ?: false)
                    put("rejectedAny", cs?.consentStatusData?.gdpr?.consentStatus?.rejectedAny ?: false)
                    put("legalBasisChanges", cs?.consentStatusData?.gdpr?.consentStatus?.rejectedAny ?: false)
                    put("vendorListAdditions", cs?.consentStatusData?.gdpr?.consentStatus?.rejectedAny ?: false)
                }
            }
        }
    }
}

internal fun List<CampaignReq>.toMetadataBody(): JsonObject {
    return buildJsonObject {
        putJsonObject("gdpr") {
            find { it.campaignType == CampaignType.GDPR }?.let { c ->
                putJsonObject("targetingParams") {
                    c.targetingParams.forEach { t -> put(t.key, t.value) }
                }
                put("groupPmId", c.groupPmId)
            }
        }
        putJsonObject("ccpa") {
            find { it.campaignType == CampaignType.CCPA }?.let { c ->
                putJsonObject("targetingParams") {
                    c.targetingParams.forEach { t -> put(t.key, t.value) }
                }
                put("groupPmId", c.groupPmId)
            }
        }
    }
}

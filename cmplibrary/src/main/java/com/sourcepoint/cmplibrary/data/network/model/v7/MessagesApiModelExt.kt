package com.sourcepoint.cmplibrary.data.network.model.v7

import kotlinx.serialization.json.* // ktlint-disable

internal fun getMessageBody(
    cs: ConsentStatusResp,
    propertyHref: String,
    accountId: Int
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
                /**
                 *  ==============================> INTENTIONAL ERROR <===============================================
                 *  ==============================> the consentStatus is not present in the ccpa obj <===============================================
                 */
                put("consentStatus", cs.consentStatusData?.gdpr?.consentStatus?.toJsonObjectV7() ?: JsonNull)
            }
            putJsonObject("gdpr") {
                put("hasLocalData", false)
                put("consentStatus", cs.consentStatusData?.gdpr?.consentStatus?.toJsonObjectV7() ?: JsonNull)
            }
        }
    }
}

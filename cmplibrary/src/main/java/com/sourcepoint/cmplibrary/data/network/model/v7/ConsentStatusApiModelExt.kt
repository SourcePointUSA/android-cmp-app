package com.sourcepoint.cmplibrary.data.network.model.v7

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal fun ConsentStatusResp.ConsentStatusData.GdprCS.ConsentStatus.toJsonObjectV7(): JsonElement {
    return buildJsonObject {
        put("hasConsentData", hasConsentData)
        put("consentedToAll", consentedAll)
        put("consentedToAny", consentedToAny)
        put("rejectedAny", rejectedAny)
    }
}

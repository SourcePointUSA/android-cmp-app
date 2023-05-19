package com.sourcepoint.cmplibrary.model

import com.sourcepoint.cmplibrary.core.layout.model.NativeMessageDto
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.InvalidResponseWebMessageException
import org.json.JSONObject
import java.util.* // ktlint-disable

internal fun String.getAppliedLegislation(): CampaignType {
    return when (this.lowercase(Locale.getDefault())) {
        "gdpr" -> CampaignType.GDPR
        "ccpa" -> CampaignType.CCPA
        else -> throw InvalidResponseWebMessageException(description = "Invalid Legislation type")
    }
}

/**
 * ===================================== Native Message ====================================
 */

data class NativeMessageResp(
    val msgJSON: JSONObject
)

data class NativeMessageRespK(
    val msg: NativeMessageDto
)

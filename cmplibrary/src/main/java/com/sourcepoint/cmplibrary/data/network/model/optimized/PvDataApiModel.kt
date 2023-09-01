package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.util.check
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject

@Serializable
data class PvDataParamReq(
    val env: Env,
    val body: Body,
    val campaignType: CampaignType
) {
    @Serializable
    data class Body(val gdpr: GDPR? = null, val ccpa: CCPA? = null) {
        @Serializable
        data class GDPR(
            val uuid: String?,
            val euconsent: String?,
            val accountId: Int,
            val pubData: JsonObject?,
            val applies: Boolean,
            val siteId: Int,
            val consentStatus: ConsentStatus,
            val msgId: Int?,
            val categoryId: Int?,
            val subCategoryId: Int?,
            val prtnUUID: String?,
            val sampleRate: Double?
        )
        @Serializable
        data class CCPA(
            val uuid: String?,
            val accountId: Int,
            val pubData: JsonObject?,
            val applies: Boolean,
            val siteId: Int,
            val consentStatus: ConsentStatus,
            val messageId: Int?,
            val sampleRate: Double?
        ) {
            @Serializable
            data class ConsentStatus(
                val hasConsentData: Boolean,
                val rejectedVendors: List<String>,
                val rejectedCategories: List<String>
            )
        }
    }
}

@Serializable
data class PvDataResp(
    @SerialName("ccpa") val ccpa: Campaign?,
    @SerialName("gdpr") val gdpr: Campaign?
) {
    @Serializable
    data class Campaign(
        @SerialName("uuid") val uuid: String?
    )

    override fun toString(): String {
        return check { JsonConverter.converter.encodeToString(this) }.getOrNull()
            ?: super.toString()
    }
}

package com.sourcepoint.cmplibrary.mobile_core

import android.net.Uri
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.exposed.MessageType
import com.sourcepoint.cmplibrary.model.exposed.MessageType.LEGACY_OTT
import com.sourcepoint.cmplibrary.model.exposed.MessageType.MOBILE
import com.sourcepoint.cmplibrary.model.exposed.MessageType.OTT
import com.sourcepoint.mobile_core.models.consents.SPUserData

val basePmPaths = mapOf(
    CampaignType.GDPR to mapOf(
        LEGACY_OTT to "privacy-manager-ott/index.html",
        OTT to "native-ott/index.html",
        MOBILE to "privacy-manager/index.html"
    ),
    CampaignType.CCPA to mapOf(
        LEGACY_OTT to "ccpa_ott/index.html",
        OTT to "native-ott/index.html",
        MOBILE to "ccpa_pm/index.html"
    ),
    CampaignType.USNAT to mapOf(
        LEGACY_OTT to "ccpa_ott/index.html",
        OTT to "native-ott/index.html",
        MOBILE to "us_pm/index.html"
    ),
)

// TODO: guard against not finding the correct path for the campaign/pm type?
fun basePmUrlFor(campaignType: CampaignType, pmType: MessageType) =
    "https://cdn.privacy-mgmt.com/" + (basePmPaths[campaignType]?.get(pmType) ?: "")

fun buildPMUrl(
    campaignType: CampaignType,
    pmId: String,
    propertyId: Int,
    pmType: MessageType = MOBILE,
    baseUrl: String? = basePmUrlFor(campaignType, pmType),
    userData: SPUserData,
    language: String?,
    pmTab: PMTab?,
    useChildPmIfAvailable: Boolean
): String {
    val uuidAndChildPmId: Triple<String, String?, String?> = when(campaignType) {
        CampaignType.CCPA -> Triple("ccpaUUID", userData.ccpa?.consents?.uuid, userData?.ccpa?.childPmId)
        CampaignType.GDPR -> Triple("consentUUID", userData.gdpr?.consents?.uuid, userData?.gdpr?.childPmId)
        CampaignType.USNAT -> Triple("consentUUID", userData.usnat?.consents?.uuid, userData?.usnat?.childPmId)
        else -> Triple("consentUUID", null, null)
    }
    val messageId = if (useChildPmIfAvailable && uuidAndChildPmId.third?.isNotEmpty() == true) {
        uuidAndChildPmId.third!!
    } else {
        pmId
    }
    return baseUrl.let {
        Uri.parse(it).buildUpon()
            .appendQueryParameterIfPresent("consentLanguage", language)
            .appendQueryParameterIfPresent(uuidAndChildPmId.first, uuidAndChildPmId.second)
            .appendQueryParameterIfPresent("pmTab", pmTab?.key)
            .appendQueryParameter("message_id", messageId)
            .appendQueryParameter("site_id", propertyId.toString())
            .appendQueryParameter("preload_consent", "true")
            .build()
            .toString()
    }
}

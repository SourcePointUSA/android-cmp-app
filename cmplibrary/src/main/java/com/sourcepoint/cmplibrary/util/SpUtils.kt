@file:JvmName("SpUtils")
package com.sourcepoint.cmplibrary.util

import android.content.Context
import com.sourcepoint.cmplibrary.data.network.util.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.mobile_core.models.consents.SPUserData
import com.sourcepoint.mobile_core.storage.Repository

@Deprecated("Use userConsents() instead.", ReplaceWith("userConsents()"))
fun userConsents(context: Context) = userConsents()

fun userConsents(): SPConsents {
    val state = Repository().state
    return SPConsents(
        core = SPUserData(
            gdpr = SPUserData.SPConsent(consents = state?.gdpr?.consents),
            usnat = SPUserData.SPConsent(consents = state?.usNat?.consents),
            ccpa = SPUserData.SPConsent(consents = state?.ccpa?.consents)
        )
    )
}

@Deprecated("Use clearAllData() instead.", ReplaceWith("clearAllData()"))
fun clearAllData(context: Context) = clearAllData()

fun clearAllData() = Repository().clear()

@Deprecated("This function is deprecated and will be removed in the future")
fun campaignApplies(context: Context, campaign: CampaignType) = campaignApplies(campaign)

internal fun campaignApplies(campaignType: CampaignType): Boolean {
    val consents = userConsents()
    return when (campaignType) {
        CampaignType.GDPR -> consents.gdpr?.consent?.applies == true
        CampaignType.CCPA -> consents.ccpa?.consent?.applies == true
        CampaignType.USNAT -> consents.usNat?.consent?.applies == true
        CampaignType.UNKNOWN -> false
    }
}

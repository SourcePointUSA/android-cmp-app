@file:JvmName("SpUtils")
package com.sourcepoint.cmplibrary.util

import android.content.Context
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.mobile_core.models.consents.SPUserData
import com.sourcepoint.mobile_core.storage.Repository

@Deprecated("Use userConsents() instead.", ReplaceWith("userConsents()"))
fun userConsents(context: Context) = userConsents()

fun userConsents() = SPConsents(core = SPUserData(
    gdpr = SPUserData.SPConsent(consents = Repository.main.state?.gdpr?.consents),
    usnat = SPUserData.SPConsent(consents = Repository.main.state?.usNat?.consents),
    ccpa = SPUserData.SPConsent(consents = Repository.main.state?.ccpa?.consents)
))

@Deprecated("Use clearAllData() instead.", ReplaceWith("clearAllData()"))
fun clearAllData(context: Context) = clearAllData()

fun clearAllData() = Repository().clear()

@Deprecated("This function is deprecated and will be removed in the future")
fun campaignApplies(context: Context, campaign: CampaignType) = campaignApplies(campaign)

internal fun campaignApplies(campaignType: CampaignType) = when (campaignType) {
    CampaignType.GDPR -> userConsents().gdpr?.consent?.applies == true
    CampaignType.CCPA -> userConsents().ccpa?.consent?.applies == true
    CampaignType.USNAT -> userConsents().usNat?.consent?.applies == true
    CampaignType.UNKNOWN -> false
}

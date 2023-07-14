package com.sourcepoint.app.v6.pm_test

import android.content.Context
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.util.campaignApplies
import java.util.*

internal fun getCmpConsentType(context: Context): ConsentType = when {
    campaignApplies(context, CampaignType.GDPR) -> ConsentType.GDPR
    campaignApplies(context, CampaignType.CCPA) -> ConsentType.CCPA
    else -> ConsentType.OTHER
}

internal fun Locale?.asMessageLanguage(): MessageLanguage = this?.language.let { code ->
    MessageLanguage.values().find { it.value.equals(other = code, ignoreCase = true) }
} ?: MessageLanguage.ENGLISH
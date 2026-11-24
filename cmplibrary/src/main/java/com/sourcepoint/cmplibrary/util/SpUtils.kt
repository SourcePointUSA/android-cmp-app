@file:JvmName("SpUtils")
package com.sourcepoint.cmplibrary.util

import android.content.Context
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
            ccpa = SPUserData.SPConsent(consents = state?.ccpa?.consents),
            globalcmp = SPUserData.SPConsent(consents = state?.globalcmp?.consents),
            preferences = SPUserData.SPConsent(consents = state?.preferences?.consents)
        )
    )
}

@Deprecated("Use clearAllData() instead.", ReplaceWith("clearAllData()"))
fun clearAllData(context: Context) = clearAllData()

fun clearAllData() = Repository().clear()

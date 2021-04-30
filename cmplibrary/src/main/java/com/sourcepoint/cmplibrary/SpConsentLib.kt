package com.sourcepoint.cmplibrary

import android.view.View
import com.sourcepoint.cmplibrary.core.layout.nat.NativeMessage
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.exposed.SPConsents

interface SpConsentLib {

    fun loadMessage()
    fun loadMessage(authId: String)
    fun loadMessage(nativeMessage: NativeMessage)

    fun customConsentGDPR(
        vendors: List<String>,
        categories: List<String>,
        legIntCategories: List<String>,
        success: (SPConsents?) -> Unit,
    )

    fun loadPrivacyManager(pmId: String, pmTab: PMTab, campaignType: CampaignType)

    fun showView(view: View)
    fun removeView(view: View)

    fun dispose()
}

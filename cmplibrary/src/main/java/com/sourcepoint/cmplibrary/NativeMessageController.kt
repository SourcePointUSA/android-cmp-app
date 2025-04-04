package com.sourcepoint.cmplibrary

import android.view.View
import com.sourcepoint.cmplibrary.data.network.util.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.NativeMessageActionType

interface NativeMessageController {
    fun sendConsent(action: NativeMessageActionType, campaignType: CampaignType)
    fun showOptionNativeMessage(campaignType: CampaignType, pmId: String)
    fun removeNativeView(view: View)
    fun showNativeView(view: View)
    companion object
}

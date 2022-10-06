package com.sourcepoint.cmplibrary.core.web

import android.view.View
import com.sourcepoint.cmplibrary.data.network.model.v7.CampaignMessage

internal interface JSClientLib {
    fun log(view: View, tag: String?, msg: String?)
    fun log(view: View, msg: String?)
    fun onError(view: View, errorMessage: String)
    fun onConsentUIReady(view: View, isFromPM: Boolean)
    fun onAction(view: View, actionData: String)
    fun onAction(view: IConsentWebView, actionData: String, nextCampaign: CampaignModel)
    fun onAction(view: IConsentWebView, actionData: String, nextCampaign: CampaignMessage)
    fun onNoIntentActivitiesFoundFor(view: View, url: String)
    fun onError(view: View, error: Throwable)
    companion object
}

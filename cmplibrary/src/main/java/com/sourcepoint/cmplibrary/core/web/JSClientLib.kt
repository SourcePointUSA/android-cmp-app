package com.sourcepoint.cmplibrary.core.web

import android.view.View

internal interface JSClientLib {
    fun log(view: View, tag: String?, msg: String?)
    fun log(view: View, msg: String?)
    fun onError(view: View, errorMessage: String)
    fun onConsentUIReady(view: View, isFromPM: Boolean)
    fun onAction(view: View, actionData: String)
    fun onAction(view: IConsentWebView, actionData: String, nextCampaign: CampaignModel)
    fun onNoIntentActivitiesFoundFor(view: View, url: String)
    fun onError(view: View, error: Throwable)
    companion object
}
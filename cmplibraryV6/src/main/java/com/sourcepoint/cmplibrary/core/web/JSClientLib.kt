package com.sourcepoint.cmplibrary.core.web

import android.view.View
import android.webkit.WebView

internal interface JSClientLib : JSReceiver {
    fun onConsentUIReady(isFromPM: Boolean, wv: WebView)
    fun onAction(actionData: String, view: View)
    companion object
}

package com.sourcepoint.cmplibrary.core.web

import android.webkit.JavascriptInterface

internal interface JSClientWebView : JSReceiver {
    @JavascriptInterface
    fun onAction(actionData: String) // called when a choice is selected on the message
    // called when message or pm is about to be shown
    @JavascriptInterface
    fun onConsentUIReady(isFromPM: Boolean)
    companion object
}

package com.sourcepoint.cmplibrary.core.web

import android.webkit.JavascriptInterface

internal interface JSReceiver {

    @JavascriptInterface
    fun log(tag: String?, msg: String?)

    @JavascriptInterface
    fun log(msg: String?)

    @JavascriptInterface
    fun onError(errorMessage: String)

    // called when a choice is selected on the message
    @JavascriptInterface
    fun onAction(actionData: String)

    // called when message or pm is about to be shown
    @JavascriptInterface
    fun onConsentUIReady(isFromPM: Boolean)

    @JavascriptInterface
    fun readyForPreloadConsent()

    companion object
}

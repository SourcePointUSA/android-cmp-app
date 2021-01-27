package com.sourcepoint.cmplibrary.core.web

import android.webkit.JavascriptInterface

interface JSReceiver {
    @JavascriptInterface
    fun log(tag: String?, msg: String?)
    @JavascriptInterface
    fun log(msg: String?)
    // called when message or pm is about to be shown
    @JavascriptInterface
    fun onConsentUIReady(isFromPM: Boolean)
    // called when a choice is selected on the message
    @JavascriptInterface
    fun onAction(actionData: String?)
    @JavascriptInterface
    fun onError(errorMessage: String?)

    companion object
}

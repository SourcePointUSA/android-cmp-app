package com.sourcepoint.cmplibrary.core.web

internal interface JSClientWebView : JSReceiver {
    // called when message or pm is about to be shown
    fun onConsentUIReady(isFromPM: Boolean)
    companion object
}

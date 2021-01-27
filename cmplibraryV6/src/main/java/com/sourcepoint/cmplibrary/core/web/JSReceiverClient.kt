package com.sourcepoint.cmplibrary.core.web

interface JSReceiverClient {
    fun onConsentUIReady(isFromPM: Boolean)
    fun onAction(actionData: String)
    fun onError(errorMessage: String)
}

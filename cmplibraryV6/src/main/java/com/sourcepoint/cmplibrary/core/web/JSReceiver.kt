package com.sourcepoint.cmplibrary.core.web

import android.webkit.JavascriptInterface

internal interface JSReceiver {

    @JavascriptInterface
    fun log(tag: String?, msg: String?)

    @JavascriptInterface
    fun log(msg: String?)

    @JavascriptInterface
    fun onAction(actionData: String) // called when a choice is selected on the message

    @JavascriptInterface
    fun onError(errorMessage: String)

    fun onNoIntentActivitiesFoundFor(url: String)
    fun onError(error: Throwable)

    companion object
}

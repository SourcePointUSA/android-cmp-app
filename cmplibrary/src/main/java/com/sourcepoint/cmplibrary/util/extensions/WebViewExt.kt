package com.sourcepoint.cmplibrary.util.extensions

import android.util.Log
import android.webkit.WebView
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.model.exposed.toWebViewConsentsJsonObject

private const val EVENT_SP_PRELOAD = "sp.loadConsent"
private const val EVENT_SP_READY = "sp.readyForConsent"

fun WebView.preloadConsent(spConsents: SPConsents) {

    val consentsJsonObject = spConsents.toWebViewConsentsJsonObject()
    Log.d("DIA-1806", "consentsJsonObject || $consentsJsonObject")

    val postMessageString = """
        window.postMessage({
            name: "$EVENT_SP_PRELOAD",
            consent: $consentsJsonObject
        }, "*")
    """.trimIndent()

    Log.d("DIA-1806", "postMessageString || $postMessageString")

    val jsString = """
        $postMessageString
        window.addEventListener('message', (event) => {
            if (event && event.data && event.data.name === "$EVENT_SP_READY") {
                $postMessageString
            }
        })
    """.trimIndent()

    this.evaluateJavascript(jsString, null)
}

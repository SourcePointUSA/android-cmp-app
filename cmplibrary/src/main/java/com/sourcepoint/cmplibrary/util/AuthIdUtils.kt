@file:JvmName("AuthIdUtils")

package com.sourcepoint.cmplibrary.util

import android.content.Context
import android.webkit.WebView

/**
 * Set the authId parameter as a cookie in the webview
 *
 * @param authId
 * @param webView
 */
fun setAuthId(authId: String?, webView: WebView) {
    authId?.let {
        webView.evaluateJavascript(
            """
                    document.cookie = "authId=$it";
            """.trimIndent()
        ) { res ->
            if (res == "null") {
                setAuthIdOldApi(authId, webView)
            }
        }
    }
}

private fun setAuthIdOldApi(authId: String?, webView: WebView) {
    val sp = webView.context.getSharedPreferences("webview", Context.MODE_PRIVATE)
    authId?.let {
        val authKey = "isAuthIdSet"
        if (!sp.contains(authKey)) {
            webView.loadUrl("javascript:document.cookie = \"authId=$authId\";")
            sp.edit().putBoolean(authKey, true).apply()
            webView.loadUrl(webView.url)
        }
    }
}

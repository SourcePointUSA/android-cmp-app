@file:JvmName("WebViewUtils")

package com.sourcepoint.gdpr_cmplibrary

import android.content.Context.MODE_PRIVATE
import android.os.Build.VERSION_CODES.KITKAT
import android.webkit.WebView
import java.io.File

/**
 * Set the authId parameter as a cookie in the webview
 *
 * @param authId
 * @param webView
 */
fun setAuthId(authId: String?, webView: WebView) {
    if (android.os.Build.VERSION.SDK_INT >= KITKAT) {
        authId?.let {
            webView.evaluateJavascript("""
                        document.cookie = "authId=$it";
                    """.trimIndent()) { res ->
                if (res == "null") {
                    setAuthIdOldApi(authId, webView)
                }
            }
        }
    }
}

private fun setAuthIdOldApi(authId: String?, webView: WebView) {
    val sp = webView.context.getSharedPreferences("webview", MODE_PRIVATE)
    authId?.let {
        val authKey = "isAuthIdSet"
        if (!sp.contains(authKey)) {
            webView.loadUrl("javascript:document.cookie = \"authId=$authId\";")
            sp.edit().putBoolean(authKey, true).apply()
            webView.loadUrl(webView.url)
        }
    }
}
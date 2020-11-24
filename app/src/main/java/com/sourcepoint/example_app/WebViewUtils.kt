@file:JvmName("WebViewUtils")

package com.sourcepoint.example_app

import android.webkit.WebView

fun setAuthId(authId: String, webView: WebView) {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
        webView.evaluateJavascript("""
                        document.cookie = "authId=$authId";
                    """.trimIndent(), null)
    }

//    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//        webView.evaluateJavascript("enable();", null);
//    } else {
//        webView.loadUrl("javascript:enable();");
//    }
}
@file:JvmName("WebViewUtils")

package com.sourcepoint.example_app

import android.webkit.CookieManager
import android.webkit.WebView

fun setAuthId(authId : String, webView: WebView){
    CookieManager.getInstance().setCookie("http://192.168.1.59:8080", "authId=$authId;")
}

//fun setAuthIdGitHub(authId : String){
//    CookieManager.getInstance().setCookie("https://carmelo-iriti.github.io/authid.github.io", "authId=$authId;")
//}

fun setAuthIdGitHub(authId : String, webView: WebView){
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT){
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
package com.sourcepointmeta.metaapp.ui.demo

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.sourcepoint.cmplibrary.util.setAuthId
import com.sourcepointmeta.metaapp.R

class DemoActivityAuthId : AppCompatActivity() {

    private val authId: String? by lazy {
        intent.extras
            ?.getString(DemoFragment.AUTH_ID_KEY, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_auth_id)
        val wv = findViewById<WebView>(R.id.webview)
        wv.settings.javaScriptEnabled = true
        val url = "https://carmelo-iriti.github.io/authid.github.io"
        wv.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                setAuthId(authId, view)
            }
        }
        wv.loadUrl(url)
    }
}

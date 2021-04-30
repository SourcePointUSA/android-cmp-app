package com.sourcepoint.app.v6

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.sourcepoint.app.v6.core.DataProvider
import com.sourcepoint.cmplibrary.util.setAuthId
import org.koin.android.ext.android.inject

class MainActivityAuthId : AppCompatActivity() {

    private val dataProvider by inject<DataProvider>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_auth_id)
        val wv = findViewById<WebView>(R.id.webview)
        wv.settings.javaScriptEnabled = true
        val authId: String = dataProvider.authId?:""
        val url: String = dataProvider.url
        wv.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                setAuthId(authId, view)
            }
        }
        wv.loadUrl(url)
    }
}
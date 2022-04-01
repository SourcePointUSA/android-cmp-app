package com.sourcepoint.app.v6

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.sourcepoint.app.v6.core.DataProvider
import com.sourcepoint.cmplibrary.util.setAuthId
import org.koin.android.ext.android.inject

class MainActivityOttIssue : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_auth_id)
        val wv = findViewById<WebView>(R.id.webview)
        wv.settings.javaScriptEnabled = true
        wv.settings.loadsImagesAutomatically = true;
        wv.loadUrl("https://preprod-cdn.privacy-mgmt.com/privacy-manager-ott/index.html?message_id=623981");
    }
}
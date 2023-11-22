package com.sourcepointmeta.metaapp.ui.consent.transfer

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.util.extensions.preloadConsent
import com.sourcepoint.cmplibrary.util.userConsents
import com.sourcepointmeta.metaapp.R
import kotlinx.android.synthetic.main.consent_transfer_activity.*
import org.json.JSONObject

class ConsentTransferActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.consent_transfer_activity)

        val webSettings: WebSettings = webview.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webview.webViewClient = CustomWebViewClient()
        webview.loadUrl("file:///android_asset/ConsentTransfer.html") //TODO query param missing
    }

    private inner class CustomWebViewClient : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            webview.preloadConsent(SPConsents())
        }
    }
}

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
            setContentView(R.layout.activity_main)
            val webview = findViewById<WebView>(R.id.webview)
            webview.settings.loadsImagesAutomatically = true
            webview.settings.javaScriptEnabled = true

            // MARK Papito, here's the magic number we were all looking for.
            val density = resources.displayMetrics.densityDpi
            val scaleFactor = density - (density * 0.6).toInt()
            webview.setInitialScale(scaleFactor)

            val preProdRenderingAppHost = "https://preprod-cdn.privacy-mgmt.com"
            val ottPMPath = "/privacy-manager-ott/index.html"
            val messageId = "623981"
            webview.loadUrl(
                preProdRenderingAppHost +
                    ottPMPath +
                    "?message_id=" + messageId +
                    "&mms_origin=https://cdn.privacy-mgmt.com/mms/v2&cmpv2_origin=https://cdn.privacy-mgmt.com/consent/tcfv2"
            )
        }
}
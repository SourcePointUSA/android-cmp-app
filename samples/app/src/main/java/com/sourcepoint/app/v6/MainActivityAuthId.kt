package com.sourcepoint.app.v6

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.sourcepoint.app.v6.core.DataProvider
import com.sourcepoint.app.v6.databinding.ActivityMainAuthIdBinding
import com.sourcepoint.cmplibrary.util.setAuthId
import org.koin.android.ext.android.inject

class MainActivityAuthId : AppCompatActivity() {

    private val dataProvider by inject<DataProvider>()
    private lateinit var binding: ActivityMainAuthIdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainAuthIdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webview.apply {
            settings.javaScriptEnabled = true
            val authId: String = dataProvider.authId?:""
            val url: String = dataProvider.url
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    setAuthId(authId, view)
                }
            }
            loadUrl(url)
        }
    }
}
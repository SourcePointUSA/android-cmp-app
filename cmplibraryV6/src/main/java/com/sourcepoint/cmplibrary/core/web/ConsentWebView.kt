package com.sourcepoint.cmplibrary.core.web

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Message
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.sourcepoint.cmplibrary.data.network.converted.JsonConverter
import com.sourcepoint.cmplibrary.util.* // ktlint-disable
import com.sourcepoint.gdpr_cmplibrary.ConsentLibException
import com.sourcepoint.gdpr_cmplibrary.ConsentLibException.NoInternetConnectionException
import com.sourcepoint.gdpr_cmplibrary.exception.Logger

abstract class ConsentWebView(context: Context) : WebView(context) {

    init {
        setup()
    }

    private val tag = ConsentWebView::class.simpleName

    private val spWebViewClient: SPWebViewClient by lazy {
        SPWebViewClient(
            wv = this,
            log = logger,
            onError = { onError(it) },
            onNoIntentActivitiesFoundFor = { onNoIntentActivitiesFoundFor(it) }
        )
    }

    internal abstract val onNoIntentActivitiesFoundFor: (url: String) -> Unit
    internal abstract val onError: (error: ConsentLibException) -> Unit
    internal abstract val jsReceiver: JSReceiver
    internal abstract val logger: Logger
    internal abstract val connectionManager: ConnectionManager
    internal abstract val jsonConverter: JsonConverter

    private val chromeClient = object : WebChromeClient() {
        override fun onCreateWindow(view: WebView, dialog: Boolean, userGesture: Boolean, resultMsg: Message): Boolean {
            context.loadLinkOnExternalBrowser(getLinkUrl(view.hitTestResult)) {
                onNoIntentActivitiesFoundFor(it)
            }
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getLinkUrl(view.hitTestResult)))
            view.context.startActivity(browserIntent)
            return false
        }
    }

    private fun setup() {
        enableDebug()
        setStyle()
        webViewClient = spWebViewClient
        webChromeClient = chromeClient
        addJavascriptInterface(jsReceiver, "JSReceiver")
    }

    private fun setStyle() {
        settings.javaScriptEnabled = true
        setBackgroundColor(Color.TRANSPARENT)
        this.requestFocus()
    }

    private fun enableDebug() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (0 != context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) {
                setWebContentsDebuggingEnabled(true)
                enableSlowWholeDocumentDraw()
            }
        }
    }

    internal fun loadConsentUIFromUrl(url: String): Either<Boolean> = check {
        if (connectionManager.isConnected) throw NoInternetConnectionException()
        loadUrl(url)
        true
    }
}

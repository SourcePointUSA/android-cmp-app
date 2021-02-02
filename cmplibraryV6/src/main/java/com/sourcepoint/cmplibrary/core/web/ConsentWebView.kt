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
import com.sourcepoint.cmplibrary.util.* // ktlint-disable
import com.sourcepoint.gdpr_cmplibrary.ConsentLibException
import com.sourcepoint.gdpr_cmplibrary.ConsentLibException.NoInternetConnectionException
import com.sourcepoint.gdpr_cmplibrary.exception.Logger
import okhttp3.HttpUrl

internal class ConsentWebView(
    context: Context,
    private val jsReceiver: JSReceiver,
    private val logger: Logger,
    private val connectionManager: ConnectionManager
) : WebView(context), IConsentWebView {

    init {
        setup()
    }

    private val tag = ConsentWebView::class.simpleName

    override var onNoIntentActivitiesFoundFor: ((url: String) -> Unit)? = null
    override var onError: ((ConsentLibException) -> Unit)? = null

    private val chromeClient = object : WebChromeClient() {
        override fun onCreateWindow(view: WebView, dialog: Boolean, userGesture: Boolean, resultMsg: Message): Boolean {
            context.loadLinkOnExternalBrowser(getLinkUrl(view.hitTestResult)) {
                onNoIntentActivitiesFoundFor?.invoke(it)
            }
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getLinkUrl(view.hitTestResult)))
            view.context.startActivity(browserIntent)
            return false
        }
    }

    private fun setup() {
        enableDebug()
        setStyle()
        jsReceiver.wv = this
        webViewClient = SPWebViewClient(
            wv = this,
            log = logger,
            onError = { onError?.invoke(it) },
            onNoIntentActivitiesFoundFor = { onNoIntentActivitiesFoundFor?.invoke(it) }
        )
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

    override fun loadConsentUIFromUrl(url: HttpUrl): Either<Boolean> = check {
        if (!connectionManager.isConnected) throw NoInternetConnectionException()
        loadUrl(url.toString())
        true
    }
}

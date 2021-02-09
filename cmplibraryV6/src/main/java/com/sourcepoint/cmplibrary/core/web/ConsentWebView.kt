package com.sourcepoint.cmplibrary.core.web

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Message
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.exception.NoInternetConnectionException
import com.sourcepoint.cmplibrary.util.* // ktlint-disable
import okhttp3.HttpUrl
import org.json.JSONObject

internal class ConsentWebView(
    context: Context,
    private val jsClientLib: JSClientLib,
    private val logger: Logger,
    private val connectionManager: ConnectionManager
) : WebView(context), IConsentWebView {

    init {
        setup()
    }

    private val chromeClient = object : WebChromeClient() {
        override fun onCreateWindow(view: WebView, dialog: Boolean, userGesture: Boolean, resultMsg: Message): Boolean {
            context.loadLinkOnExternalBrowser(getLinkUrl(view.hitTestResult)) {
                jsClientLib.onNoIntentActivitiesFoundFor(it)
            }
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getLinkUrl(view.hitTestResult)))
            view.context.startActivity(browserIntent)
            return false
        }
    }

    private fun setup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            id = View.generateViewId()
        }
        enableDebug()
        setStyle()
        webChromeClient = chromeClient
        addJavascriptInterface(JSClientWebViewImpl(jsClientLib), "JSReceiver")
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

    override fun loadConsentUIFromUrl(url: HttpUrl, message: JSONObject): Either<Boolean> = check {
        if (!connectionManager.isConnected) throw NoInternetConnectionException(description = "No internet connection")
        loadUrl(url.toString())
        webViewClient = createWebViewClient(message)
        true
    }

    inner class JSClientWebViewImpl(jsClientLib: JSClientLib) : JSClientWebView, JSReceiver by jsClientLib {
        @JavascriptInterface
        override fun onConsentUIReady(isFromPM: Boolean) {
            logger.i("ConsentWebView", "js =================== onConsentUIReady")
            jsClientLib.onConsentUIReady(isFromPM, this@ConsentWebView)
        }

        @JavascriptInterface
        override fun onAction(actionData: String) {
            jsClientLib.onAction(actionData, this@ConsentWebView)
        }
    }

    /**
     * Delegate used to catch the events from the [ConsentWebView]
     * @param message this is the Message from the server used by the [ConsentWebView] to display its content
     * @return a [WebViewClient]
     */
    private fun createWebViewClient(message: JSONObject): WebViewClient {
        return SPWebViewClient(
            wv = this,
            onError = { jsClientLib.onError(it) },
            onNoIntentActivitiesFoundFor = { jsClientLib.onNoIntentActivitiesFoundFor(it) },
            onPageFinishedLambda = { view, url ->
                /**
                 * adding the parameter [sp.loadMessage] needed by the webpage to trigger the loadMessage event
                 */
                val obj = message.put("name", "sp.loadMessage")
                view.loadUrl("javascript: window.postMessage($obj);")
            }
        )
    }
}

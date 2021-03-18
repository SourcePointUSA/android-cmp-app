package com.sourcepoint.cmplibrary.core.web

import android.annotation.SuppressLint
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
import com.sourcepoint.cmplibrary.data.network.model.CampaignResp1203
import com.sourcepoint.cmplibrary.data.network.model.UnifiedMessageResp
import com.sourcepoint.cmplibrary.data.network.model.UnifiedMessageResp1203
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.exception.NoInternetConnectionException
import com.sourcepoint.cmplibrary.util.*  //ktlint-disable
import okhttp3.HttpUrl
import org.json.JSONObject

@SuppressLint("ViewConstructor")
internal class ConsentWebView(
    context: Context,
    private val jsClientLib: JSClientLib,
    private val logger: Logger,
    private val connectionManager: ConnectionManager,
    private val executorManager: ExecutorManager
) : WebView(context), IConsentWebView {

    init {
        setup()
    }

    private lateinit var spWebViewClient: SPWebViewClient

    private val chromeClient = object : WebChromeClient() {
        override fun onCreateWindow(view: WebView, dialog: Boolean, userGesture: Boolean, resultMsg: Message): Boolean {
            context.loadLinkOnExternalBrowser(getLinkUrl(view.hitTestResult)) {
                jsClientLib.onNoIntentActivitiesFoundFor(this@ConsentWebView, it)
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
        addJavascriptInterface(JSClientWebViewImpl(), "JSReceiver")
        spWebViewClient = SPWebViewClient(
            wv = this,
            onError = { jsClientLib.onError(this@ConsentWebView, it) },
            onNoIntentActivitiesFoundFor = { jsClientLib.onNoIntentActivitiesFoundFor(this@ConsentWebView, it) },
            timer = SpTimer.create(executorManager)
        )
        webViewClient = spWebViewClient
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

    private fun loadConsentUIFromUrl(url: HttpUrl, message: JSONObject): Boolean {
        if (!connectionManager.isConnected) throw NoInternetConnectionException(description = "No internet connection")
        spWebViewClient.onPageFinishedLambda = { view, url ->
            /**
             * adding the parameter [sp.loadMessage] needed by the webpage to trigger the loadMessage event
             */
            val obj = message.apply {
                put("name", "sp.loadMessage")
                put("fromNativeSDK", true)
            }
            view.loadUrl("javascript: window.postMessage($obj);")
        }
        loadUrl(url.toString())
        return true
    }

    override fun loadConsentUIFromUrl(url: HttpUrl): Either<Boolean> = check {
        if (!connectionManager.isConnected) throw NoInternetConnectionException(description = "No internet connection")
        spWebViewClient.onPageFinishedLambda = { _, _ -> }
        loadUrl(url.toString())
        true
    }

    override fun loadConsentUI(messageResp: UnifiedMessageResp, url: HttpUrl): Either<Boolean> = check {
        messageResp
            .campaigns
            .mapNotNull { it.message }
            .firstOrNull()
            ?.let { jsonMessages -> loadConsentUIFromUrl(url, jsonMessages) }
            ?: run {
                logMess("{message json} is null for all the legislations")
                false
            }
    }

    override fun loadConsentUI(messageResp: CampaignResp1203, url: HttpUrl): Either<Boolean> = check {
        loadConsentUIFromUrl(url, messageResp.message)
    }

    private fun logMess(mess: String) = logger.d(this::class.java.simpleName, "========>  $mess")

    inner class JSClientWebViewImpl : JSClientWebView {

        @JavascriptInterface
        override fun onConsentUIReady(isFromPM: Boolean) {
            logger.i("ConsentWebView", "js =================== onConsentUIReady")
            jsClientLib.onConsentUIReady(this@ConsentWebView, isFromPM)
        }

        @JavascriptInterface
        override fun onAction(actionData: String) {
            jsClientLib.onAction(this@ConsentWebView, actionData)
        }

        @JavascriptInterface
        override fun log(tag: String?, msg: String?) {
            jsClientLib.log(this@ConsentWebView, tag, msg)
        }

        @JavascriptInterface
        override fun log(msg: String?) {
            jsClientLib.log(this@ConsentWebView, msg)
        }

        @JavascriptInterface
        override fun onError(errorMessage: String) {
            jsClientLib.onError(this@ConsentWebView, errorMessage)
        }
    }
}

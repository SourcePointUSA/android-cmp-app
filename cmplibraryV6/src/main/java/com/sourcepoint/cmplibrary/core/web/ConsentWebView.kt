package com.sourcepoint.cmplibrary.core.web

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Message
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.network.converter.toConsentAction
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.exception.NoInternetConnectionException
import com.sourcepoint.cmplibrary.model.ActionType
import com.sourcepoint.cmplibrary.util.*  //ktlint-disable
import okhttp3.HttpUrl
import java.util.* // ktlint-disable

@SuppressLint("ViewConstructor")
internal class ConsentWebView(
    context: Context,
    private val jsClientLib: JSClientLib,
    private val logger: Logger,
    private val connectionManager: ConnectionManager,
    private val executorManager: ExecutorManager,
    private val campaignQueue: Queue<CampaignModel> = LinkedList()
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
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            id = View.generateViewId()
//        }
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

    private fun loadConsentUIFromUrl(url: HttpUrl, campaignModel: CampaignModel): Boolean {
        val legislation: Legislation = campaignModel.type
        if (!connectionManager.isConnected) throw NoInternetConnectionException(description = "No internet connection")
        spWebViewClient.onPageFinishedLambda = { view, _ ->
            /**
             * adding the parameter [sp.loadMessage] needed by the webpage to trigger the loadMessage event
             */
            val obj = campaignModel.message.apply {
                put("name", "sp.loadMessage")
                put("fromNativeSDK", true)
                /*
                "name": "sp.loadMessage",
                "fromNativeSDK": true
                 */
            }

            view.loadUrl("javascript: window.spLegislation = '${legislation.name}'; window.postMessage($obj);")
        }
        loadUrl(url.toString())
        return true
    }

    override fun loadConsentUIFromUrl(url: HttpUrl, legislation: Legislation): Either<Boolean> = check {
        if (!connectionManager.isConnected) throw NoInternetConnectionException(description = "No internet connection")
        spWebViewClient.onPageFinishedLambda = { view, url ->
            println()
        }
        loadUrl(url.toString())
        true
    }

    override fun loadConsentUI(messageResp: CampaignModel, url: HttpUrl, legislation: Legislation): Either<Boolean> = check {
        loadConsentUIFromUrl(url, messageResp)
    }

    private fun logMess(mess: String) = logger.d(this::class.java.simpleName, "$mess")

    inner class JSClientWebViewImpl : JSClientWebView {

        @JavascriptInterface
        override fun onConsentUIReady(isFromPM: Boolean) {
            logger.i("ConsentWebView", "JSClientWebViewImpl onConsentUIReady: isFromPM[$isFromPM]")
            jsClientLib.onConsentUIReady(this@ConsentWebView, isFromPM)
        }

        @JavascriptInterface
        override fun onAction(actionData: String) {
            checkWorkerThread("ConsentWebView on action")
            val action = actionData.toConsentAction()
            if (action.actionType != ActionType.SHOW_OPTIONS && campaignQueue.isNotEmpty()) {
                val campaign: CampaignModel = campaignQueue.poll()
                jsClientLib.onAction(this@ConsentWebView, actionData, campaign)
            } else {
                jsClientLib.onAction(this@ConsentWebView, actionData)
            }
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

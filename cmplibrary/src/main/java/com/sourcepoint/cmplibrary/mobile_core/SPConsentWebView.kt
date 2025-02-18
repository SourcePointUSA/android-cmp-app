package com.sourcepoint.cmplibrary.mobile_core

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Message
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.sourcepoint.cmplibrary.core.web.ConsentWebView.Companion.CONSENT_WEB_VIEW_TAG_NAME
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.ConsentActionImplOptimized
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.runOnMain
import com.sourcepoint.cmplibrary.util.getLinkUrl
import com.sourcepoint.cmplibrary.util.loadLinkOnExternalBrowser
import com.sourcepoint.cmplibrary.util.readFromAsset
import com.sourcepoint.mobile_core.models.MessageToDisplay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface SPMessageUIClient {
    fun loaded(view: View)
    fun onAction(view: View, action: ConsentAction)
    fun onError()
    fun finished(view: View)
}

interface SPMessageUI {
    fun load(message: MessageToDisplay, consents: SPConsents)
}

/**
 * These functions need to be added with the annotation `@JavascriptInterface` in order to
 * take effect.
 */
interface SPWebMessageUIClient: SPMessageUIClient {
    fun loaded()
    fun readyForMessagePreload()
    fun onAction(actionData: String)
    fun log(message: String)
    fun onError(error: String)
}

@SuppressLint("ViewConstructor", "SetJavaScriptEnabled")
class SPConsentWebView(
    context: Context,
    viewId: Int? = null,
    val messageUIClient: SPMessageUIClient,
): WebView(context), SPMessageUI, SPWebMessageUIClient {
    private var jsReceiver: String
    private lateinit var consents: SPConsents
    private lateinit var message: MessageToDisplay

    init {
        id = viewId ?: View.generateViewId()
        tag = CONSENT_WEB_VIEW_TAG_NAME
        settings.javaScriptEnabled = true
        setBackgroundColor(Color.TRANSPARENT)
        requestFocus()
        jsReceiver = context.readFromAsset("js_receiver.js")
        webChromeClient = object : WebChromeClient() {
            override fun onCreateWindow(
                view: WebView,
                dialog: Boolean,
                userGesture: Boolean,
                resultMsg: Message
            ): Boolean {
                context.loadLinkOnExternalBrowser(getLinkUrl(view.hitTestResult)) {
//                    jsClientLib.onNoIntentActivitiesFoundFor(this@ConsentWebView, it)
                }
                view.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getLinkUrl(view.hitTestResult))))
                return false
            }

            override fun onConsoleMessage(message: String?, lineNumber: Int, sourceID: String?) {
                super.onConsoleMessage(message, lineNumber, sourceID)
            }
        }
        webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                view.loadUrl("javascript:$jsReceiver")
            }
        }

        addJavascriptInterface(this, "JSReceiver")
    }

    override fun load(message: MessageToDisplay, consents: SPConsents) {
        this.consents = consents
        this.message = message
        runOnMain { loadUrl(message.url) }
    }

    override fun onAction(view: View, action: ConsentAction) {
        runOnMain {
            messageUIClient.onAction(view, action)
            finished(view)
        }
    }

    override fun loaded(view: View) {
        runOnMain { messageUIClient.loaded(view) }
    }

    override fun finished(view: View) {
        runOnMain { messageUIClient.finished(view) }
    }

    @JavascriptInterface
    override fun onAction(actionData: String) {
        onAction(this, JsonConverter.converter.decodeFromString<ConsentActionImplOptimized>(actionData))
    }

    @JavascriptInterface
    override fun loaded() {
        loaded(this)
    }

    @JavascriptInterface
    override fun onError() {
        runOnMain { messageUIClient.onError() }
    }

    @JavascriptInterface
    override fun readyForMessagePreload() {
        runOnMain {
            evaluateJavascript(
                """
                    window.postMessage(Object.assign(
                        {name: "sp.loadMessage"},
                        ${Json.encodeToString(message.message)}
                    ), "*");
                    window.spLegislation="${CampaignType.fromCore(message.type)}"
                    """,
                null
            )
        }
    }

    @JavascriptInterface
    override fun onError(error: String) {
        messageUIClient.onError()
    }

    @JavascriptInterface
    override fun log(message: String) {
        println(message)
    }
}

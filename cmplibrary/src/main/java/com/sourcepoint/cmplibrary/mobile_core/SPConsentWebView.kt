package com.sourcepoint.cmplibrary.mobile_core

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.sourcepoint.cmplibrary.buildPMUrl
import com.sourcepoint.cmplibrary.core.web.ConsentWebView.Companion.CONSENT_WEB_VIEW_TAG_NAME
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.launch
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.ConsentActionImplOptimized
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.runOnMain
import com.sourcepoint.cmplibrary.util.readFromAsset
import com.sourcepoint.mobile_core.models.consents.SPUserData
import com.sourcepoint.mobile_core.network.responses.MessagesResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException

interface SPMessageUIClient {
    fun loaded(view: View)
    fun onAction(view: View, action: ConsentAction)
    fun onError()
    fun finished(view: View)
}

interface SPMessageUI {
    fun load(
        message: MessagesResponse.Message,
        url: String,
        campaignType: CampaignType,
        userData: SPUserData
    )

    fun load(
        url: String,
        campaignType: CampaignType,
        userData: SPUserData
    )
}

/**
 * These functions need to be added with the annotation `@JavascriptInterface` in order to
 * take effect.
 */
interface SPWebMessageUIClient: SPMessageUIClient {
    fun loaded()
    fun readyForMessagePreload()
    fun readyForConsentPreload()
    fun onAction(actionData: String)
    fun log(message: String)
    fun onError(error: String)
}

fun Uri.Builder.appendQueryParameterIfPresent(name: String, value: String?): Uri.Builder {
    value?.let { appendQueryParameter(name, it) }
    return this
}

@SuppressLint("ViewConstructor", "SetJavaScriptEnabled")
class SPConsentWebView(
    context: Context,
    viewId: Int? = null,
    val propertyId: Int,
    val messageUIClient: SPMessageUIClient,
): WebView(context), SPMessageUI, SPWebMessageUIClient {
    private var jsReceiver = context.readFromAsset("js_receiver.js")
    private lateinit var consents: SPUserData
    private lateinit var campaignType: CampaignType
    private var message: MessagesResponse.Message? = null
    private var isPresenting = false
    private var isFirstLayer = true

    init {
        id = viewId ?: generateViewId()
        tag = CONSENT_WEB_VIEW_TAG_NAME
        settings.javaScriptEnabled = true
        setBackgroundColor(Color.TRANSPARENT)
        setWebContentsDebuggingEnabled(true)
        addJavascriptInterface(this, "JSReceiver")
        webChromeClient = object : WebChromeClient() {
//            override fun onCreateWindow(
//                view: WebView,
//                dialog: Boolean,
//                userGesture: Boolean,
//                resultMsg: Message
//            ): Boolean {
//                context.loadLinkOnExternalBrowser(getLinkUrl(view.hitTestResult)) {
////                    jsClientLib.onNoIntentActivitiesFoundFor(this@ConsentWebView, it)
//                }
//                view.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getLinkUrl(view.hitTestResult))))
//                return false
//            }
            override fun onConsoleMessage(message: String?, lineNumber: Int, sourceID: String?) {
                super.onConsoleMessage(message, lineNumber, sourceID)
            }
        }
    }

    override fun load(
        url: String,
        campaignType: CampaignType,
        userData: SPUserData
    ) {
        internalLoad(message = null, url, campaignType, userData)
    }

    override fun load(
        message: MessagesResponse.Message,
        url: String,
        campaignType: CampaignType,
        userData: SPUserData
    ) {
        internalLoad(message, url, campaignType, userData)
    }

    private fun internalLoad(
        message: MessagesResponse.Message? = null,
        url: String,
        campaignType: CampaignType,
        consents: SPUserData
    ) {
        this.consents = consents
        this.message = message
        this.campaignType = campaignType
        launch {
            try {
                val renderingAppWithJsReceiver = injectScriptInto(getRenderingApp(url), jsReceiver)
                runOnMain {
                    loadDataWithBaseURL(
                        url,
                        renderingAppWithJsReceiver,
                        "text/html",
                        "UTF-8",
                        null
                    )
                }
            } catch (error: Exception) {
                onError()
            }
        }
    }

    // TODO: cache/memoize rendering app's html per url
    @Throws(IOException::class)
    private fun getRenderingApp(url: String): String {
        try {
            var html: String? = null
            val response = OkHttpClient().newCall(Request.Builder().url(url).build()).execute()
            if (response.isSuccessful) {
                html = response.body?.string()
                if (html == null) throw IOException()
            }
            response.body?.close()
            return html!!
        } catch (error: IOException){
            throw error // TODO: create custom error for this case
        }
    }

    private fun injectScriptInto(html: String, script: String): String =
        html.replaceFirst("<head>", "<head><script>$script</script>")

    override fun onAction(view: View, action: ConsentAction) {
        runOnMain {
            messageUIClient.onAction(view, action)
            when (action.actionType) {
                ActionType.SHOW_OPTIONS -> loadPrivacyManagerFrom(action)
                ActionType.PM_DISMISS -> returnToFirstLayer()
                else -> finished(view)
            }
        }
    }

    private fun loadPrivacyManagerFrom(action: ConsentAction) {
        isFirstLayer = false
        action.pmUrl?.let {
            loadUrl(buildPMUrl(
                campaignType = campaignType,
                pmId = action.messageId,
                propertyId = propertyId,
                baseUrl = it,
                userData = consents,
                language = action.consentLanguage,
                pmTab = null
            ))
        }
    }

    private fun returnToFirstLayer() {
        if (canGoBack() && !isFirstLayer) {
            isFirstLayer = true
            goBack()
        } else {
            finished(this)
        }
    }

    private fun preloadConsents() {
        evaluateJavascript("""window.postMessage({
            name: "sp.loadConsent",
            consent: ${when (campaignType) {
                CampaignType.GDPR -> Json.encodeToJsonElement(consents.gdpr?.consents)
                CampaignType.CCPA -> Json.encodeToJsonElement(consents.ccpa?.consents)
                CampaignType.USNAT -> Json.encodeToJsonElement(consents.usnat?.consents)
                CampaignType.UNKNOWN -> null
            }}
        }, "*");""", null)
    }

    override fun loaded(view: View) {
        runOnMain {
            evaluateJavascript("""window.spLegislation="${campaignType.name}"""", null)
            if (!isPresenting) {
                isPresenting = true
                messageUIClient.loaded(view)
            }
        }
    }

    override fun finished(view: View) {
        isPresenting = false
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
                        ${Json.encodeToString(message)}
                    ), "*");
                    """,
                null
            )
        }
    }

    @JavascriptInterface
    override fun readyForConsentPreload() {
        runOnMain { preloadConsents() }
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

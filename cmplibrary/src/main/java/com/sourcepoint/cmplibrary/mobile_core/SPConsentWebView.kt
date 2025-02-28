package com.sourcepoint.cmplibrary.mobile_core

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Looper
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.sourcepoint.cmplibrary.core.web.ConsentWebView.Companion.CONSENT_WEB_VIEW_TAG_NAME
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.launch
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.runOnMain
import com.sourcepoint.cmplibrary.util.readFromAsset
import com.sourcepoint.mobile_core.models.consents.SPUserData
import com.sourcepoint.mobile_core.network.json
import com.sourcepoint.mobile_core.network.responses.MessagesResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.encodeToJsonElement
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException

interface SPMessageUIClient {
    fun loaded(view: View)
    fun onAction(view: View, action: ConsentAction)
    fun onError() // TODO: receive the error object
    fun finished(view: View)
}

interface SPMessageUI {
    var isFirstLayer: Boolean

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
    override var isFirstLayer = true

    companion object {
        fun create(
            context: Context,
            viewId: Int? = null,
            propertyId: Int,
            messageUIClient: SPMessageUIClient
        ) =
            if (Looper.myLooper() == Looper.getMainLooper()) {
                SPConsentWebView(
                    viewId = viewId,
                    context = context,
                    messageUIClient = messageUIClient,
                    propertyId = propertyId
                )
            } else {
                runBlocking(Dispatchers.Main) {
                    SPConsentWebView(
                        viewId = viewId,
                        context = context,
                        messageUIClient = messageUIClient,
                        propertyId = propertyId
                    )
                }
            }
    }

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
        loadRenderingApp(url, jsReceiver)
    }

    @Throws(Exception::class)
    private fun loadRenderingApp(url: String, script: String) {
        launch {
            try {
                val renderingAppWithJsReceiver = injectScriptInto(getRenderingApp(url), script)
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
            loadRenderingApp(buildPMUrl(
                campaignType = campaignType,
                pmId = action.messageId,
                propertyId = propertyId,
                baseUrl = it,
                userData = consents,
                language = action.consentLanguage,
                pmTab = null,
                useChildPmIfAvailable = false
            ), jsReceiver)
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
                CampaignType.GDPR -> json.encodeToJsonElement(consents.gdpr?.consents)
                CampaignType.CCPA -> json.encodeToJsonElement(consents.ccpa?.consents)
                CampaignType.USNAT -> json.encodeToJsonElement(consents.usnat?.consents)
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
        onAction(this, json.decodeFromString(actionData) as SPConsentAction)
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
                        ${json.encodeToString(message)}
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

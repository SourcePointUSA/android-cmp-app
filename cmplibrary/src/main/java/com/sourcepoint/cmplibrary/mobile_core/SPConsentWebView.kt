package com.sourcepoint.cmplibrary.mobile_core

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Looper
import android.os.Message
import android.view.KeyEvent
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.data.network.util.CampaignType
import com.sourcepoint.cmplibrary.exception.ConsentLibExceptionK
import com.sourcepoint.cmplibrary.exception.NoIntentFoundForUrl
import com.sourcepoint.cmplibrary.exception.RenderingAppException
import com.sourcepoint.cmplibrary.exception.UnableToDownloadRenderingApp
import com.sourcepoint.cmplibrary.exception.UnableToLoadRenderingApp
import com.sourcepoint.cmplibrary.launch
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.exposed.ActionType.MSG_CANCEL
import com.sourcepoint.cmplibrary.model.exposed.ActionType.PM_DISMISS
import com.sourcepoint.cmplibrary.model.exposed.ActionType.SHOW_OPTIONS
import com.sourcepoint.cmplibrary.model.exposed.MessageType
import com.sourcepoint.cmplibrary.model.exposed.MessageType.LEGACY_OTT
import com.sourcepoint.cmplibrary.model.exposed.MessageType.OTT
import com.sourcepoint.cmplibrary.runOnMain
import com.sourcepoint.cmplibrary.util.getLinkUrl
import com.sourcepoint.cmplibrary.util.loadLinkOnExternalBrowser
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
    fun onError(error: ConsentLibExceptionK)
    fun finished(view: View)
}

interface SPMessageUI {
    var isFirstLayer: Boolean
    var isPresenting: Boolean
    val onBackPressed: Boolean

    fun load(
        message: MessagesResponse.Message,
        messageType: MessageType,
        url: String,
        campaignType: CampaignType,
        userData: SPUserData
    )

    fun load(
        messageType: MessageType,
        url: String,
        campaignType: CampaignType,
        userData: SPUserData
    )

    fun dismiss()
}

/**
 * These functions need to be added with the annotation `@JavascriptInterface` in order to
 * take effect.
 */
interface SPWebMessageUIClient : SPMessageUIClient {
    fun loaded()
    fun readyForMessagePreload()
    fun readyForConsentPreload()
    fun onAction(actionData: String)
    fun log(message: String)
    fun onError(error: String)
    fun onMessageInactivityTimeout()
}

@SuppressLint("ViewConstructor", "SetJavaScriptEnabled")
class SPConsentWebView(
    context: Context,
    viewId: Int? = null,
    tagName: String = "consent-web-view",
    val propertyId: Int,
    val messageUIClient: SPMessageUIClient,
    override val onBackPressed: Boolean,
) : WebView(context), SPMessageUI, SPWebMessageUIClient {
    private var jsReceiver = context.assets
        .open("js_receiver.js")
        .reader()
        .readText()
    private lateinit var consents: SPUserData
    private lateinit var campaignType: CampaignType
    private lateinit var messageType: MessageType
    private var message: MessagesResponse.Message? = null
    override var isPresenting = false
    override var isFirstLayer = true

    companion object {
        fun create(
            context: Context,
            viewId: Int? = null,
            tagName: String = "consent-web-view",
            propertyId: Int,
            messageUIClient: SPMessageUIClient,
            onBackPressed: Boolean
        ) =
            if (Looper.myLooper() == Looper.getMainLooper()) {
                SPConsentWebView(
                    viewId = viewId,
                    tagName = tagName,
                    context = context,
                    messageUIClient = messageUIClient,
                    propertyId = propertyId,
                    onBackPressed = onBackPressed
                )
            } else {
                runBlocking(Dispatchers.Main) {
                    SPConsentWebView(
                        viewId = viewId,
                        tagName = tagName,
                        context = context,
                        messageUIClient = messageUIClient,
                        propertyId = propertyId,
                        onBackPressed = onBackPressed
                    )
                }
            }
    }

    init {
        id = viewId ?: generateViewId()
        tag = tagName
        settings.javaScriptEnabled = true
        setBackgroundColor(Color.TRANSPARENT)
        setWebContentsDebuggingEnabled(true)
        addJavascriptInterface(this, "JSReceiver")
        webChromeClient = object : WebChromeClient() {
            override fun onCreateWindow(
                view: WebView,
                dialog: Boolean,
                userGesture: Boolean,
                resultMsg: Message
            ): Boolean {
                context.loadLinkOnExternalBrowser(
                    getLinkUrl(view.hitTestResult),
                    onNoIntentActivitiesFound = {
                        messageUIClient.onError(NoIntentFoundForUrl(it))
                    }
                )
                view.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getLinkUrl(view.hitTestResult))))
                return false
            }
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if (messageType == OTT || messageType == LEGACY_OTT) {
                evaluateJavascript("window.postMessage({name:\"sp.BACK\"})", null)
            } else {
                if (onBackPressed) {
                    onAction(
                        view = this,
                        action = SPConsentAction(
                            actionType = if (isFirstLayer) MSG_CANCEL else PM_DISMISS,
                            campaignType = campaignType,
                            messageId = ""
                        )
                    )
                }
            }
            return true
        } else {
            return super.dispatchKeyEvent(event)
        }
    }

    override fun load(
        messageType: MessageType,
        url: String,
        campaignType: CampaignType,
        userData: SPUserData
    ) = internalLoad(message = null, messageType, url, campaignType, userData)

    override fun load(
        message: MessagesResponse.Message,
        messageType: MessageType,
        url: String,
        campaignType: CampaignType,
        userData: SPUserData
    ) = internalLoad(message, messageType, url, campaignType, userData)

    private fun internalLoad(
        message: MessagesResponse.Message? = null,
        messageType: MessageType,
        url: String,
        campaignType: CampaignType,
        consents: SPUserData
    ) {
        this.consents = consents
        this.message = message
        this.campaignType = campaignType
        this.messageType = messageType
        loadRenderingApp(url, jsReceiver)
    }

    private fun loadRenderingApp(url: String, script: String) = launch {
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
        } catch (error: UnableToDownloadRenderingApp) {
            onError(error)
        } catch (error: Throwable) {
            onError(UnableToLoadRenderingApp(cause = error))
        }
    }

    // TODO: cache/memoize rendering app's html per url
    @Throws(UnableToDownloadRenderingApp::class)
    private fun getRenderingApp(url: String): String = try {
        var html: String? = null
        val response = OkHttpClient().newCall(Request.Builder().url(url).build()).execute()
        if (response.isSuccessful) {
            html = response.body?.string()
            if (html == null) throw IOException()
        }
        response.body?.close()
        html!!
    } catch (error: Throwable) {
        throw UnableToDownloadRenderingApp(cause = error, url)
    }

    private fun injectScriptInto(html: String, script: String): String =
        html.replaceFirst("<head>", "<head><script>$script</script>")

    override fun onAction(view: View, action: ConsentAction) = runOnMain {
        messageUIClient.onAction(view, action)
        when (action.actionType) {
            SHOW_OPTIONS -> loadPrivacyManagerFrom(action)
            PM_DISMISS -> returnToFirstLayer()
            else -> {
                finished(view)
            }
        }
    }

    private fun loadPrivacyManagerFrom(action: ConsentAction) {
        isFirstLayer = false
        action.pmUrl?.let {
            loadRenderingApp(
                buildPMUrl(
                    campaignType = campaignType,
                    pmId = action.messageId,
                    propertyId = propertyId,
                    baseUrl = it,
                    userData = consents,
                    language = action.consentLanguage,
                    pmTab = null,
                    useChildPmIfAvailable = false
                ),
                jsReceiver
            )
        }
    }

    private fun returnToFirstLayer() =
        if (canGoBack() && !isFirstLayer) {
            isFirstLayer = true
            goBack()
        } else {}

    private fun preloadConsents() = evaluateJavascript(
        """window.postMessage({
            name: "sp.loadConsent",
            consent: ${when (campaignType) {
            CampaignType.GDPR -> json.encodeToJsonElement(consents.gdpr?.consents)
            CampaignType.CCPA -> json.encodeToJsonElement(consents.ccpa?.consents)
            CampaignType.USNAT -> json.encodeToJsonElement(consents.usnat?.consents)
            CampaignType.GLOBALCMP -> json.encodeToJsonElement(consents.globalcmp?.consents)
            CampaignType.PREFERENCES, CampaignType.UNKNOWN -> null
        }}
        }, "*");""",
        null
    )

    override fun dismiss() {
        onAction(
            view = this,
            action = SPConsentAction(
                actionType = MSG_CANCEL,
                campaignType = campaignType,
                messageId = ""
            )
        )
    }

    override fun loaded(view: View) = runOnMain {
        evaluateJavascript("""window.spLegislation="${campaignType.name}"""", null)
        if (!isPresenting) {
            isPresenting = true
            messageUIClient.loaded(view)
        }
    }

    override fun finished(view: View) {
        isPresenting = false
    }

    @JavascriptInterface
    override fun onAction(actionData: String) =
        onAction(this, json.decodeFromString(actionData) as SPConsentAction)

    @JavascriptInterface
    override fun loaded() = loaded(this)

    override fun onError(error: ConsentLibExceptionK) = runOnMain { messageUIClient.onError(error) }

    @JavascriptInterface
    override fun readyForMessagePreload() = runOnMain {
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

    @JavascriptInterface
    override fun readyForConsentPreload() = runOnMain { preloadConsents() }

    @JavascriptInterface
    override fun onError(error: String) {
        println(error)
        messageUIClient.onError(RenderingAppException())
    }

    @JavascriptInterface
    override fun log(message: String) = println(message)

    @JavascriptInterface
    override fun onMessageInactivityTimeout() = runOnMain {
        (messageUIClient as? SpClient)?.onMessageInactivityTimeout()
    }
}

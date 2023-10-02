package com.sourcepoint.cmplibrary.core.web

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.Color
import android.net.Uri
import android.os.Message
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.ExecutorManager
import com.sourcepoint.cmplibrary.core.executeOnLeft
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.network.model.toConsentActionOptimized
import com.sourcepoint.cmplibrary.data.network.util.isInternetConnected
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.exception.LoggerType.* // ktlint-disable
import com.sourcepoint.cmplibrary.exception.NoInternetConnectionException
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.MessageSubCategory
import com.sourcepoint.cmplibrary.util.* // ktlint-disable
import okhttp3.HttpUrl
import org.json.JSONObject
import java.util.* //ktlint-disable

@SuppressLint("ViewConstructor")
internal class ConsentWebView(
    context: Context,
    private val jsClientLib: JSClientLib,
    private val logger: Logger,
    private val messageTimeout: Long,
    private val executorManager: ExecutorManager,
    private val campaignQueue: Queue<CampaignModel> = LinkedList(),
    private val messSubCat: MessageSubCategory = MessageSubCategory.TCFv2,
    private val viewId: Int? = null
) : WebView(context), IConsentWebView {

    init {
        setup()
    }

    private var currentCampaignModel: CampaignModel? = null
    private lateinit var spWebViewClient: SPWebViewClient
    private val jsReceiver: String by lazy {
        context.readFromAsset("js_receiver.js")
    }

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
        id = viewId ?: View.generateViewId()
        tag = CONSENT_WEB_VIEW_TAG_NAME
        enableDebug()
        setStyle()
        if (messSubCat == MessageSubCategory.OTT) {
            val density = resources.displayMetrics.densityDpi
            val scaleFactor = density - (density * 0.5).toInt()
            setInitialScale(scaleFactor)
        }
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        webChromeClient = chromeClient
        addJavascriptInterface(JSClientWebViewImpl(), "JSReceiver")
        spWebViewClient = SPWebViewClient(
            wv = this,
            onError = { jsClientLib.onError(this@ConsentWebView, it) },
            onNoIntentActivitiesFoundFor = { jsClientLib.onNoIntentActivitiesFoundFor(this@ConsentWebView, it) },
            logger = logger,
            messageTimeout = messageTimeout,
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
        if (0 != context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) {
            setWebContentsDebuggingEnabled(true)
            enableSlowWholeDocumentDraw()
        }
    }

    override fun loadConsentUIFromUrlPreloadingOption(
        url: HttpUrl,
        campaignType: CampaignType,
        pmId: String?,
        consent: String?,
    ): Either<Boolean> = check {

        if (context.isInternetConnected().not()) throw NoInternetConnectionException()

        val ensuredConsentJson = consent?.let { JSONObject(it) } ?: JSONObject()

        spWebViewClient.jsReceiverConfig = {
            val sb = StringBuffer()

            val obj = JSONObject().apply {
                put("name", "sp.loadConsent")
                put("consent", ensuredConsentJson)
            }

            logger.flm(
                tag = "Preloading - $campaignType Privacy Manager",
                url = url.toString(),
                json = obj,
                type = "GET",
            )

            sb.append(
                """
                javascript: window.spLegislation = '${campaignType.name}'; 
                window.localPmId ='$pmId'; 
                $jsReceiver;
                window.postMessage($obj, "*");
                """.trimIndent()
            )
            sb.toString()
        }
        loadUrl(url.toString())
        true
    }

    override fun loadConsentUI(
        campaignModel: CampaignModel,
        url: HttpUrl,
        campaignType: CampaignType
    ): Either<Boolean> = check {

        if (context.isInternetConnected().not()) throw NoInternetConnectionException()

        currentCampaignModel = campaignModel
        val campaignType: CampaignType = campaignModel.type
        spWebViewClient.jsReceiverConfig = {
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

            logger.flm(
                tag = "$campaignType First Layer Message",
                url = url.toString(),
                json = obj,
                type = "GET"
            )

            """
                javascript: $jsReceiver;
                window.spLegislation = '${campaignType.name}'; 
                window.postMessage($obj, "*");
            """.trimIndent()
        }
        loadUrl(url.toString())
        true
    }

    inner class JSClientWebViewImpl : JSClientWebView {

        @JavascriptInterface
        override fun onConsentUIReady(isFromPM: Boolean) {
            spWebViewClient.cancelTimer()
            jsClientLib.onConsentUIReady(this@ConsentWebView, isFromPM)
        }

        @JavascriptInterface
        override fun onAction(actionData: String) {
            checkWorkerThread("ConsentWebView on action")

            val action = actionData.toConsentActionOptimized()
                .executeOnLeft {
                    logger.webAppAction(
                        tag = "Action from the RenderingApp",
                        msg = "Error during the parsing process",
                        json = JSONObject(actionData)
                    )
                    jsClientLib.onError(this@ConsentWebView, it)
                    jsClientLib.dismiss(this@ConsentWebView)
                }
                .getOrNull()
                ?: return

            if (action.actionType == ActionType.PM_DISMISS && currentCampaignModel != null) {
                jsClientLib.onAction(this@ConsentWebView, actionData, currentCampaignModel!!)
            } else if (action.actionType != ActionType.SHOW_OPTIONS && campaignQueue.isNotEmpty()) {
                val campaign: CampaignModel = campaignQueue.poll()
                jsClientLib.onAction(this@ConsentWebView, actionData, campaign)
            } else {
                jsClientLib.onAction(this@ConsentWebView, actionData)
            }
        }

        @JavascriptInterface
        override fun log(tag: String?, msg: String?) {
            if (msg != null && msg.length < 100_000) {
                jsClientLib.log(this@ConsentWebView, tag, msg)
            }
        }

        @JavascriptInterface
        override fun log(msg: String?) {
            if (msg != null && msg.length < 100_000) {
                jsClientLib.log(this@ConsentWebView, msg)
            }
        }

        @JavascriptInterface
        override fun onError(errorMessage: String) {
            jsClientLib.onError(this@ConsentWebView, errorMessage)
        }
    }

    companion object {
        const val CONSENT_WEB_VIEW_TAG_NAME = "consent-web-view"
    }
}

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
import android.webkit.WebSettings
import android.webkit.WebView
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.ExecutorManager
import com.sourcepoint.cmplibrary.data.network.connection.ConnectionManager
import com.sourcepoint.cmplibrary.data.network.model.toConsentAction
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.exception.LoggerType.* // ktlint-disable
import com.sourcepoint.cmplibrary.exception.NoInternetConnectionException
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.util.* // ktlint-disable
import okhttp3.HttpUrl
import java.util.* // ktlint-disable

@SuppressLint("ViewConstructor")
internal class ConsentWebView(
    context: Context,
    private val jsClientLib: JSClientLib,
    private val logger: Logger,
    private val messageTimeout: Long,
    private val connectionManager: ConnectionManager,
    private val executorManager: ExecutorManager,
    private val campaignQueue: Queue<CampaignModel> = LinkedList()
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            id = View.generateViewId()
        }
        enableDebug()
        setStyle()
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (0 != context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) {
                setWebContentsDebuggingEnabled(true)
                enableSlowWholeDocumentDraw()
            }
        }
    }

    override fun loadConsentUIFromUrl(url: HttpUrl, campaignType: CampaignType): Either<Boolean> = check {
        if (!connectionManager.isConnected) throw NoInternetConnectionException(description = "No internet connection")
        spWebViewClient.jsReceiverConfig = {
            """
                javascript: window.spLegislation = '${campaignType.name}';                 
                $jsReceiver;
            """.trimIndent()
        }
        logger.i("ConsentWebView", "loadConsentUIFromUrl${NL.t}legislation $campaignType${NL.t}{NL.t}url $url ")
        loadUrl(url.toString())
        true
    }

    override fun loadConsentUIFromUrl(
        url: HttpUrl,
        campaignType: CampaignType,
        pmId: String?,
        campaignModel: CampaignModel?
    ): Either<Boolean> = check {
        currentCampaignModel = campaignModel
        if (!connectionManager.isConnected) throw NoInternetConnectionException(description = "No internet connection")
        spWebViewClient.jsReceiverConfig = {
            val sb = StringBuffer()
            sb.append(
                """
                javascript: window.spLegislation = '${campaignType.name}'; window.localPmId ='$pmId';
                $jsReceiver;
                """.trimIndent()
            )
            sb.toString()
        }
        loadUrl(url.toString())
        true
    }

    override fun loadConsentUIFromUrl(url: HttpUrl, campaignType: CampaignType, pmId: String?, singleShot: Boolean): Either<Boolean> = check {
        if (!connectionManager.isConnected) throw NoInternetConnectionException(description = "No internet connection")
        spWebViewClient.jsReceiverConfig = {
            val sb = StringBuffer()
            sb.append(
                """
                javascript: window.spLegislation = '${campaignType.name}'; window.localPmId ='$pmId'; window.isSingleShot = $singleShot; 
                $jsReceiver;
                """.trimIndent()
            )
            sb.toString()
        }
        loadUrl(url.toString())
        true
    }

    override fun loadConsentUI(campaignModel: CampaignModel, url: HttpUrl, campaignType: CampaignType): Either<Boolean> = check {
        currentCampaignModel = campaignModel
        val campaignType: CampaignType = campaignModel.type
        if (!connectionManager.isConnected) throw NoInternetConnectionException(description = "No internet connection")
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
            """
                javascript: $jsReceiver;
                window.spLegislation = '${campaignType.name}'; 
                window.postMessage($obj, "*");
            """.trimIndent()
        }
        logger.i("ConsentWebView", "$campaignType loadConsentUIFromUrl${NL.t}url $url ")
//        loadUrl(url.toString())
        loadUrl("https://preprod-cdn.privacy-mgmt.com/privacy-manager-ott/index.html?message_id=629591")
        true
    }

    inner class JSClientWebViewImpl : JSClientWebView {

        @JavascriptInterface
        override fun onConsentUIReady(isFromPM: Boolean) {
            jsClientLib.onConsentUIReady(this@ConsentWebView, isFromPM)
        }

        @JavascriptInterface
        override fun onAction(actionData: String) {
            checkWorkerThread("ConsentWebView on action")
            val action = actionData.toConsentAction()
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

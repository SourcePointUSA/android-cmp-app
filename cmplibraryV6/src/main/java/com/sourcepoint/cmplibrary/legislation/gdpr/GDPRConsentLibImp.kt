package com.sourcepoint.cmplibrary.legislation.gdpr

import android.content.Context
import android.view.View
import android.webkit.JavascriptInterface
import com.sourcepoint.cmplibrary.Campaign
import com.sourcepoint.cmplibrary.core.web.ConsentWebView
import com.sourcepoint.cmplibrary.core.web.JSReceiver
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.NetworkClient
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.model.ConsentAction
import com.sourcepoint.cmplibrary.data.network.model.PmUrlConfig
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManager
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManagerSingleton
import com.sourcepoint.cmplibrary.toMessageReq
import com.sourcepoint.cmplibrary.util.* // ktlint-disable
import com.sourcepoint.gdpr_cmplibrary.* // ktlint-disable
import com.sourcepoint.gdpr_cmplibrary.exception.GenericSDKException
import com.sourcepoint.gdpr_cmplibrary.exception.Logger
import com.sourcepoint.gdpr_cmplibrary.exception.MissingClientException
import com.sourcepoint.gdpr_cmplibrary.exception.RenderingAppException

internal class GDPRConsentLibImpl(
    private val urlManager: HttpUrlManager = HttpUrlManagerSingleton,
    internal val campaign: Campaign,
    internal val pPrivacyManagerTab: PrivacyManagerTab,
    internal val context: Context,
    internal val pLogger: Logger,
    internal val pJsonConverter: JsonConverter,
    internal val pConnectionManager: ConnectionManager,
    internal val networkClient: NetworkClient,
    internal val dataStorage: DataStorage,
    private val viewManager: ViewsManager,
    private val executor: ExecutorManager
) : GDPRConsentLib {

    override var spGdprClient: SpGDPRClient? = null
    private val nativeMsgClient by lazy { NativeMsgDelegate() }

    /** Start Client's methods */
    override fun loadMessage(authId: String) {
        checkMainThread("loadMessage")
        checkClient()
        networkClient.getMessage(
            messageReq = campaign.toMessageReq(),
            success = { messageResp ->
                println()
            },
            error = { throwable ->
                println()
            }
        )
    }

    override fun loadMessage() {
        checkMainThread("loadMessage")
        checkClient()
        loadMessage("auth")
    }

    override fun loadMessage(nativeMessage: NativeMessage) {
        checkMainThread("loadMessage")
        checkClient()

        networkClient.getNativeMessage(
            campaign.toMessageReq(),
            { messageResp ->
                val jsonResult = messageResp.msgJSON
                executor.executeOnMain {
                    /** configuring onClickListener and set the parameters */
                    nativeMessage.setAttributes(NativeMessageAttrs(jsonResult, pLogger))
                    /** set the action callback */
                    nativeMessage.setActionClient(nativeMsgClient)
                    /** calling the client */
                    spGdprClient?.onConsentUIReady(nativeMessage)
                }
            },
            { throwable -> pLogger.error(throwable.toConsentLibException()) }
        )
    }

    override fun loadMessage(authId: String, nativeMessage: NativeMessage) {
        checkMainThread("loadMessage")
        checkClient()
        spGdprClient?.onConsentUIReady(nativeMessage)
        viewManager.showView(nativeMessage)
    }

    override fun loadPrivacyManager() {
        checkMainThread("loadPrivacyManager")
        checkClient()
        val webView = viewManager.createWebView(this)
        webView?.run {
            onError = { consentLibException -> }
            onNoIntentActivitiesFoundFor = { url -> }
        }
        val pmConfig = PmUrlConfig(
            consentUUID = "89b2d14b-70ee-4344-8cc2-1b7b281d0f2d",
            siteId = "7639",
            messageId = campaign.pmId
        )
        webView?.loadConsentUIFromUrl(urlManager.urlPm(pmConfig))
    }

    override fun loadPrivacyManager(authId: String) {
        checkMainThread("loadPrivacyManager")
        checkClient()
    }

    override fun showView(view: View) {
        checkMainThread("showView")
        viewManager.showView(view)
    }

    override fun removeView(view: View) {
        checkMainThread("removeView")
        viewManager.removeView(view)
    }

    /** end Client's methods */

    private fun createWebView(): com.sourcepoint.cmplibrary.core.web.ConsentWebView {
        return ConsentWebView(
            context = context,
            connectionManager = pConnectionManager,
            jsReceiver = JSReceiverDelegate(),
            logger = pLogger
        )
    }

    /** Start Receiver methods */
    inner class JSReceiverDelegate() : JSReceiver {

        override var wv: ConsentWebView? = null

        @JavascriptInterface
        override fun log(tag: String?, msg: String?) {
        }

        @JavascriptInterface
        override fun log(msg: String?) {
        }

        @JavascriptInterface
        override fun onConsentUIReady(isFromPM: Boolean) {
            wv?.let { viewManager.showView(it) } ?: throw GenericSDKException(description = "WebView is null")
        }

        @JavascriptInterface
        override fun onAction(actionData: String) {
            pJsonConverter
                .toConsentAction(actionData)
                .map { onActionFromWebViewClient(it) }
        }

        @JavascriptInterface
        override fun onError(errorMessage: String) {
            spGdprClient?.onError(ConsentLibException(errorMessage))
            pLogger.error(RenderingAppException(description = errorMessage, pCode = errorMessage))
        }
    }

    /** End Receiver methods */

    private fun checkClient() {
        spGdprClient ?: throw MissingClientException(description = "SpGDPRClient instance is missing")
    }

    /**
     * Receive the action performed by the user from the WebView
     */
    internal fun onActionFromWebViewClient(action: ConsentAction) {
        executor.executeOnMain { spGdprClient?.onAction(action.actionType) }
        when (action.actionType) {
            ActionTypes.ACCEPT_ALL -> {
            }
            ActionTypes.MSG_CANCEL -> {
            }
            ActionTypes.SAVE_AND_EXIT -> {
            }
            ActionTypes.SHOW_OPTIONS -> {
            }
            ActionTypes.REJECT_ALL -> {
            }
            ActionTypes.PM_DISMISS -> {
            }
        }
    }

    /**
     * Delegate used by the [NativeMessage] to catch events performed by the user
     */
    inner class NativeMsgDelegate : NativeMessageClient {
        /**
         * onclick listener connected to the acceptAll button in the NativeMessage View
         */
        override fun onClickAcceptAll(ca: com.sourcepoint.gdpr_cmplibrary.ConsentAction) {
            spGdprClient?.onAction(ActionTypes.ACCEPT_ALL)
        }

        /**
         * onclick listener connected to the RejectAll button in the NativeMessage View
         */
        override fun onClickRejectAll(ca: com.sourcepoint.gdpr_cmplibrary.ConsentAction) {
            spGdprClient?.onAction(ActionTypes.REJECT_ALL)
        }

        override fun onPmDismiss(ca: com.sourcepoint.gdpr_cmplibrary.ConsentAction) {}

        /**
         * onclick listener connected to the ShowOptions button in the NativeMessage View
         */
        override fun onClickShowOptions(ca: com.sourcepoint.gdpr_cmplibrary.ConsentAction) {
            spGdprClient?.onAction(ActionTypes.SHOW_OPTIONS)
        }

        /**
         * onclick listener connected to the Cancel button in the NativeMessage View
         */
        override fun onClickCancel(ca: com.sourcepoint.gdpr_cmplibrary.ConsentAction) {
            spGdprClient?.onAction(ActionTypes.MSG_CANCEL)
        }

        override fun onDefaultAction(ca: com.sourcepoint.gdpr_cmplibrary.ConsentAction) {
        }
    }
}

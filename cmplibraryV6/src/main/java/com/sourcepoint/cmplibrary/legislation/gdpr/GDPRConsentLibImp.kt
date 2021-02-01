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
import com.sourcepoint.cmplibrary.data.network.model.Categories
import com.sourcepoint.cmplibrary.data.network.model.GdprReq
import com.sourcepoint.cmplibrary.data.network.model.MessageReq
import com.sourcepoint.cmplibrary.util.* // ktlint-disable
import com.sourcepoint.gdpr_cmplibrary.* // ktlint-disable
import com.sourcepoint.gdpr_cmplibrary.exception.Logger
import com.sourcepoint.gdpr_cmplibrary.exception.MissingClientException
import com.sourcepoint.gdpr_cmplibrary.exception.RenderingAppException
import org.json.JSONObject

internal class GDPRConsentLibImpl(
    private val campaign: Campaign,
    private val pPrivacyManagerTab: PrivacyManagerTab,
    private val context: Context,
    private val pLogger: Logger,
    private val pJsonConverter: JsonConverter,
    private val pConnectionManager: ConnectionManager,
    private val networkClient: NetworkClient,
    private val dataStorage: DataStorage,
    private val viewManager: ViewsManager,
    private val executor: ExecutorManager
) : GDPRConsentLib, JSReceiver {

    override var spGdprClient: SpGDPRClient? = null

    /** Start Client's methods */
    override fun loadMessage(authId: String?) {
        checkMainThread("loadMessage")
        checkClient()
    }

    override fun loadMessage() {
        checkMainThread("loadMessage")
        checkClient()
    }

    override fun loadMessage(nativeMessage: NativeMessage) {
        checkMainThread("loadMessage")
        checkClient()

        val req = MessageReq(
            requestUUID = "test",
            categories = Categories(
                gdpr = GdprReq(
                    accountId = 22,
                    propertyId = 7639,
                    propertyHref = "https://tcfv2.mobile.webview"
                )
            )
        )

        networkClient.getNativeMessage(
            req,
            { messageResp ->

                val jsonResult = messageResp.msgJSON
//                val consentUUID = jsonResult.getString("uuid")
//                val metaData = jsonResult.getString("meta")
//                val jConsent = jsonResult.getJSONObject("userConsent")
//                jConsent.put("uuid", consentUUID)
//                val userConsent = GDPRUserConsent(jsonResult.getJSONObject("userConsent"), consentUUID, pLogger)
                executor.executeOnMain {
                    nativeMessage.getAcceptAll().button.setOnClickListener {
                        println("getAcceptAll")
                    }
                    nativeMessage.getRejectAll().button.setOnClickListener {
                        println("getRejectAll")
                    }
                    nativeMessage.getShowOptions().button.setOnClickListener {
                        println("getShowOptions")
                    }
                    nativeMessage.getCancel().button.setOnClickListener {
                        println("getCancel")
                    }
                    nativeMessage.setAttributes(NativeMessageAttrs(jsonResult, pLogger))
                    spGdprClient?.onConsentUIReady(nativeMessage)
                }
            },
            { throwable -> throwable.printStackTrace() }
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

    private fun createWebView(): ConsentWebView {
        return object : ConsentWebView(context) {
            override val jsReceiver: JSReceiver = this@GDPRConsentLibImpl
            override val logger: Logger = pLogger
            override val connectionManager: ConnectionManager = pConnectionManager
            override val jsonConverter: JsonConverter = pJsonConverter
            override val onNoIntentActivitiesFoundFor: (url: String) -> Unit = { url -> }
            override val onError: (error: ConsentLibException) -> Unit = { error -> }
        }
    }

    /** Start Receiver methods */
    @JavascriptInterface
    override fun log(tag: String?, msg: String?) {
    }

    @JavascriptInterface
    override fun log(msg: String?) {
    }

    @JavascriptInterface
    override fun onConsentUIReady(isFromPM: Boolean) {
    }

    @JavascriptInterface
    override fun onAction(actionData: String) {
        pJsonConverter
            .toConsentAction(actionData)
            .map { /** ConsentAction  */ }
    }

    @JavascriptInterface
    override fun onError(errorMessage: String) {
        spGdprClient?.onError(ConsentLibException(errorMessage))
        pLogger.error(RenderingAppException(description = errorMessage, pCode = errorMessage))
    }

    /** End Receiver methods */

    private fun checkClient() {
        spGdprClient ?: throw MissingClientException(description = "GDPR client is missing")
    }
}

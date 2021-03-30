package com.sourcepoint.cmplibrary

import android.content.Context
import android.view.View
import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.consent.ConsentManager
import com.sourcepoint.cmplibrary.core.layout.NativeMessageClient
import com.sourcepoint.cmplibrary.core.layout.nat.NativeMessage
import com.sourcepoint.cmplibrary.core.layout.nat.NativeMessageInternal
import com.sourcepoint.cmplibrary.core.web.IConsentWebView
import com.sourcepoint.cmplibrary.core.web.JSClientLib
import com.sourcepoint.cmplibrary.data.Service
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.toCCPAUserConsent
import com.sourcepoint.cmplibrary.data.network.converter.toGDPRUserConsent
import com.sourcepoint.cmplibrary.data.network.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManager
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManagerSingleton
import com.sourcepoint.cmplibrary.exception.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.ActionType.SHOW_OPTIONS
import com.sourcepoint.cmplibrary.util.* // ktlint-disable
import java.util.* // ktlint-disable

internal class SpConsentLibImpl(
    internal val pPrivacyManagerTab: PrivacyManagerTabK,
    internal val context: Context,
    internal val pLogger: Logger,
    internal val pJsonConverter: JsonConverter,
    internal val service: Service,
    internal val executor: ExecutorManager,
    private val pConnectionManager: ConnectionManager,
    private val viewManager: ViewsManager,
    private val campaignManager: CampaignManager,
    private val consentManager: ConsentManager,
    private val urlManager: HttpUrlManager = HttpUrlManagerSingleton
) : SpConsentLib {

    override var spClient: SpClient? = null
    private val nativeMsgClient by lazy { NativeMsgDelegate() }

    /** Start Client's methods */
    override fun loadMessage(authId: String) {
        checkMainThread("loadMessage")
        throwsExceptionIfClientIsNull()
        service.getMessage(
            messageReq = campaignManager.getMessageReq(),
            pSuccess = { messageResp -> },
            pError = { throwable -> }
        )
    }

    override fun loadMessage() {
        checkMainThread("loadMessage")
        throwsExceptionIfClientIsNull()

        if (viewManager.isViewInLayout) return

        service.getUnifiedMessage(
            messageReq = campaignManager.getUnifiedMessageReq(),
            pSuccess = { messageResp ->
                val campaignList = messageResp.campaigns
                if (campaignList.isEmpty()) return@getUnifiedMessage
                val firstCampaign2Process = campaignList.first()
                val remainingCampaigns: Queue<CampaignResp1203> = LinkedList(campaignList.drop(1))
                executor.executeOnMain {
                    /** create a instance of WebView */
                    val webView = viewManager.createWebView(this, JSReceiverDelegate(), remainingCampaigns)

                    /** inject the message into the WebView */
                    val legislation = Legislation.valueOf(firstCampaign2Process.type.toUpperCase())
                    webView?.loadConsentUI(firstCampaign2Process, urlManager.urlURenderingAppStage(), legislation)
                }
            },
            pError = { throwable ->
                (throwable as? ConsentLibExceptionK)?.let { pLogger.error(it) }
                spClient?.onError(throwable.toConsentLibException())
            }
        )
//        service.getMessage(
//            messageReq = campaignManager.getMessageReq(),
//            pSuccess = { messageResp ->
//                executor.executeOnMain {
//                    /** create a instance of WebView */
//                    val webView = viewManager.createWebView(this, JSReceiverDelegate())
//                    /** inject the message into the WebView */
//                    webView?.loadConsentUI(messageResp, urlManager.urlURenderingAppStage())
//                }
//            },
//            pError = { throwable -> spClient?.onError(throwable.toConsentLibException()) }
//        )
    }

    override fun loadMessage(nativeMessage: NativeMessage) {
        checkMainThread("loadMessage")
        throwsExceptionIfClientIsNull()

        service.getNativeMessageK(
            campaignManager.getMessageReq(),
            { messageResp ->
                executor.executeOnMain {
                    /** configuring onClickListener and set the parameters */
                    (nativeMessage as? NativeMessageInternal)?.setAttributes(messageResp.msg)
                    /** set the action callback */
                    (nativeMessage as? NativeMessageInternal)?.setActionClient(nativeMsgClient)
                    /** calling the client */
                    spClient?.onUIReady(nativeMessage)
                }
            },
            { throwable ->
                (throwable as? ConsentLibExceptionK)?.let { pLogger.error(it) }
                pLogger.error(throwable.toConsentLibException())
            }
        )
    }

    override fun loadGDPRPrivacyManager() {
        checkMainThread("loadPrivacyManager")
        throwsExceptionIfClientIsNull()
        val pmConfig = campaignManager.getGdprPmConfig()
        pmConfig
            .map {
                val webView = viewManager.createWebView(this, JSReceiverDelegate())
                webView?.loadConsentUIFromUrl(urlManager.urlPm(it))
            }
            .executeOnLeft { logMess("PmUrlConfig is null") }
    }

    override fun loadCCPAPrivacyManager() {
        checkMainThread("loadPrivacyManager")
        throwsExceptionIfClientIsNull()
    }

    override fun showView(view: View) {
        checkMainThread("showView")
        viewManager.showView(view)
    }

    override fun removeView(view: View) {
        checkMainThread("removeView")
        viewManager.removeView(view)
    }

    override fun dispose() {
        executor.dispose()
        viewManager.removeAllViews()
        campaignManager.clearConsents()
    }

    private fun throwsExceptionIfClientIsNull() {
        spClient ?: throw MissingClientException(description = "spClient instance is missing")
    }

    /**
     * Receive the action performed by the user from the WebView
     */
    internal fun onActionFromWebViewClient(action: ConsentAction, iConsentWebView: IConsentWebView) {
        val view: View = (iConsentWebView as? View) ?: kotlin.run { return }
        executor.executeOnMain {
            spClient?.onAction(view, action.actionType)
            when (action.actionType) {
                ActionType.MSG_CANCEL -> {
                    view.let { spClient?.onUIFinished(it) }
                }
                SHOW_OPTIONS -> {
                    showOption(action, iConsentWebView)
                }
                ActionType.PM_DISMISS -> {
                    view.let { spClient?.onUIFinished(it) }
                }
                ActionType.ACCEPT_ALL,
                ActionType.SAVE_AND_EXIT,
                ActionType.REJECT_ALL -> {
                    view.let { spClient?.onUIFinished(it) }
                    service.sendConsent(
                        action,
                        { consentResp ->
                            val map: Map<String, Any?> = consentResp.content.toTreeMap()
                            map.getMap("userConsent").also {
                                when (action.legislation) {
                                    Legislation.GDPR -> it?.toGDPRUserConsent()?.let { spClient?.onConsentReady(SPConsents(gdpr = SPGDPRConsent(consent = it, applies = true),)) }
                                    Legislation.CCPA -> it?.toCCPAUserConsent()?.let { spClient?.onConsentReady(SPConsents(ccpa = SPCCPAConsent(consent = it, applies = true),)) }
                                }
                            }
                            consentManager.saveGdprConsent(consentResp.content)
                        },
                        { throwable ->
                            spClient?.onError(throwable)
                            pLogger.error(throwable.toConsentLibException())
                        }
                    )
                    view.let { spClient?.onUIFinished(it) }
                }
            }
        }
    }

    private fun showOption(action: ConsentAction, iConsentWebView: IConsentWebView) {
        val view: View = (iConsentWebView as? View) ?: kotlin.run { return }
        when (action.legislation) {
            Legislation.GDPR -> {
                viewManager.removeView(iConsentWebView)
                campaignManager.getGdprPmConfig()
                    .map { pmUrlConfig ->
                        iConsentWebView.loadConsentUIFromUrl(urlManager.urlPm(pmUrlConfig))
                    }
                    .executeOnLeft { it.printStackTrace() }
            }
            Legislation.CCPA -> {
                viewManager.removeView(iConsentWebView)
                campaignManager.getCcpaPmConfig()
                    .map { pmUrlConfig ->
                        iConsentWebView.loadConsentUIFromUrl(urlManager.urlPm(pmUrlConfig))
                    }
                    .executeOnLeft { it.printStackTrace() }
            }
        }
    }

    private fun logMess(mess: String) = pLogger.d(this::class.java.simpleName, "========>  $mess")

    /**
     * Delegate used by the [NativeMessage] to catch events performed by the user
     */
    inner class NativeMsgDelegate : NativeMessageClient {

        override fun onClickAcceptAll(view: View, ca: ConsentAction) {
//            onActionFromWebViewClient(ca, view)
        }

        override fun onClickRejectAll(view: View, ca: ConsentAction) {
//            onActionFromWebViewClient(ca, view)
        }

        override fun onPmDismiss(view: View, ca: ConsentAction) {
//            onActionFromWebViewClient(ca, view)
        }

        override fun onClickShowOptions(view: View, ca: ConsentAction) {
//            onActionFromWebViewClient(ca, view)
        }

        override fun onClickCancel(view: View, ca: ConsentAction) {
//            onActionFromWebViewClient(ca, view)
        }

        override fun onDefaultAction(view: View, ca: ConsentAction) {
//            onActionFromWebViewClient(ca, view)
        }
    }

    //    /** Start Receiver methods */
    inner class JSReceiverDelegate() : JSClientLib {
        //
        override fun onConsentUIReady(view: View, isFromPM: Boolean) {
            executor.executeOnMain { spClient?.onUIReady(view) }
        }

        override fun log(view: View, tag: String?, msg: String?) {
            logMess("$tag $msg")
        }

        override fun log(view: View, msg: String?) {
            logMess("$msg")
        }

        override fun onError(view: View, errorMessage: String) {
            spClient?.onError(GenericSDKException(description = errorMessage))
            pLogger.error(RenderingAppException(description = errorMessage, pCode = errorMessage))
        }

        override fun onNoIntentActivitiesFoundFor(view: View, url: String) {
        }

        override fun onError(view: View, error: Throwable) {
            spClient?.onError(error)
        }

        override fun onAction(iConsentWebView: IConsentWebView, actionData: String, nextCampaign: CampaignResp1203) {
            /** spClient is called from [onActionFromWebViewClient] */
            (iConsentWebView as? View)?.let {
                /** spClient is called from [onActionFromWebViewClient] */
                pJsonConverter
                    .toConsentAction(actionData)
                    .map { ca ->
                        onActionFromWebViewClient(ca, iConsentWebView)
                        if (ca.actionType != SHOW_OPTIONS) {
                            val legislation = Legislation.valueOf(nextCampaign.type.toUpperCase())
                            executor.executeOnMain { iConsentWebView.loadConsentUI(nextCampaign, urlManager.urlURenderingAppProd(), legislation) }
                        }
                    }
                    .executeOnLeft { throw it }
            }
        }

        override fun onAction(view: View, actionData: String) {
            val iConsentWebView: IConsentWebView = (view as? IConsentWebView) ?: run { return }
            /** spClient is called from [onActionFromWebViewClient] */
            pJsonConverter
                .toConsentAction(actionData)
                .map { onActionFromWebViewClient(it, iConsentWebView) }
                .executeOnLeft { throw it }
        }
    }

    /** End Receiver methods */
}

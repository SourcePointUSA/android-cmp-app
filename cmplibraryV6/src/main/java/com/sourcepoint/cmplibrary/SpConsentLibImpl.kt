package com.sourcepoint.cmplibrary

import android.content.Context
import android.view.View
import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.consent.ConsentManager
import com.sourcepoint.cmplibrary.consent.LocalStateStatus
import com.sourcepoint.cmplibrary.core.ExecutorManager
import com.sourcepoint.cmplibrary.core.executeOnLeft
import com.sourcepoint.cmplibrary.core.layout.NativeMessageClient
import com.sourcepoint.cmplibrary.core.layout.nat.NativeMessage
import com.sourcepoint.cmplibrary.core.layout.nat.NativeMessageInternal
import com.sourcepoint.cmplibrary.core.map
import com.sourcepoint.cmplibrary.core.web.CampaignModel
import com.sourcepoint.cmplibrary.core.web.IConsentWebView
import com.sourcepoint.cmplibrary.core.web.JSClientLib
import com.sourcepoint.cmplibrary.data.Service
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManager
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManagerSingleton
import com.sourcepoint.cmplibrary.exception.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.CampaignResp
import com.sourcepoint.cmplibrary.model.UnifiedMessageResp
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.ActionType.SHOW_OPTIONS
import com.sourcepoint.cmplibrary.util.* // ktlint-disable
import java.util.* // ktlint-disable

internal class SpConsentLibImpl(
    internal val context: Context,
    internal val pLogger: Logger,
    internal val pJsonConverter: JsonConverter,
    internal val service: Service,
    internal val executor: ExecutorManager,
    private val viewManager: ViewsManager,
    private val campaignManager: CampaignManager,
    private val consentManager: ConsentManager,
    private val urlManager: HttpUrlManager = HttpUrlManagerSingleton,
    private val env: Env = Env.PROD
) : SpConsentLib {

    override var spClient: SpClient? = null
    private val nativeMsgClient by lazy { NativeMsgDelegate() }

    companion object {
        fun UnifiedMessageResp.toCampaignModelList(logger: Logger): List<CampaignModel> {
            val campaignList = this.campaigns
            if (campaignList.isEmpty()) return emptyList()

            val partition: Pair<List<CampaignResp>, List<CampaignResp>> = campaignList
                .partition { it.message != null && it.url != null }
            logger.d(
                this::class.java.name,
                """
                partitions: 
                    with Null message[${partition.second.size}]
                    with Not Null message[${partition.first.size}]
                """.trimIndent()
            )
            return partition.first.map {
                CampaignModel(
                    message = it.message!!,
                    messageMetaData = it.messageMetaData!!,
                    type = Legislation.valueOf(it.type),
                    url = it.url!!
                )
            }
        }
    }

    init {
        consentManager.sPConsentsSuccess = { spConsents ->
            executor.executeOnMain {
                spClient?.onConsentReady(spConsents)
            }
        }
        consentManager.sPConsentsError = { throwable ->
            throwable.printStackTrace()
            executor.executeOnMain {
                spClient?.onError(throwable)
            }
        }
    }

    /** Start Client's methods */
    override fun loadMessage(authId: String) {
        checkMainThread("loadMessage")
        throwsExceptionIfClientIsNull()
        // TODO
    }

    override fun loadMessage() {
        checkMainThread("loadMessage")
        throwsExceptionIfClientIsNull()

        if (viewManager.isViewInLayout) return

        service.getUnifiedMessage(
            messageReq = campaignManager.getUnifiedMessageReq(),
            pSuccess = { messageResp ->
                consentManager.localStateStatus = LocalStateStatus.Present(value = messageResp.localState)
                val list: List<CampaignModel> = messageResp.toCampaignModelList(logger = pLogger)
                if (list.isEmpty()) return@getUnifiedMessage
                val firstCampaign2Process = list.first()
                val remainingCampaigns: Queue<CampaignModel> = LinkedList(list.drop(1))
                executor.executeOnMain {
                    /** create a instance of WebView */
                    val webView = viewManager.createWebView(this, JSReceiverDelegate(), remainingCampaigns)

                    /** inject the message into the WebView */
                    val legislation = firstCampaign2Process.type
                    val url = firstCampaign2Process.url // urlManager.urlURenderingApp(env)//
                    webView?.loadConsentUI(firstCampaign2Process, url, legislation)
                }
            },
            pError = { throwable ->
                (throwable as? ConsentLibExceptionK)?.let { pLogger.error(it) }
                spClient?.onError(throwable.toConsentLibException())
            },
            env = env
        )
    }

    override fun loadMessage(nativeMessage: NativeMessage) {
        checkMainThread("loadMessage")
        throwsExceptionIfClientIsNull()

        service.getNativeMessageK(
            campaignManager.getUnifiedMessageReq(),
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

    override fun loadPrivacyManager(pmId: String, pmTab: PMTab, legislation: Legislation) {
        checkMainThread("loadPrivacyManager")
        throwsExceptionIfClientIsNull()
        val pmConfig = campaignManager.getPmConfig(legislation, pmId, pmTab)
        pmConfig
            .map {
                val webView = viewManager.createWebView(this, JSReceiverDelegate())
                webView?.loadConsentUIFromUrl(
                    url = urlManager.pmUrl(legislation = Legislation.GDPR, pmConfig = it, env = env),
                    legislation = Legislation.GDPR,
                    pmId = it.messageId
                )
            }
            .executeOnLeft { logMess("PmUrlConfig is null") }
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

    private fun logMess(mess: String) = pLogger.d(this::class.java.simpleName, "$mess")

    /**
     * Delegate used by the [NativeMessage] to catch events performed by the user
     */
    // TODO in progress
    inner class NativeMsgDelegate : NativeMessageClient {

        override fun onClickAcceptAll(view: View, ca: ConsentAction) {}

        override fun onClickRejectAll(view: View, ca: ConsentAction) {}

        override fun onPmDismiss(view: View, ca: ConsentAction) {}

        override fun onClickShowOptions(view: View, ca: ConsentAction) {}

        override fun onClickCancel(view: View, ca: ConsentAction) {}

        override fun onDefaultAction(view: View, ca: ConsentAction) {}
    }

    /** Start Receiver methods */
    inner class JSReceiverDelegate : JSClientLib {

        override fun onConsentUIReady(view: View, isFromPM: Boolean) {
            executor.executeOnMain { spClient?.onUIReady(view) }
        }

        override fun log(view: View, tag: String?, msg: String?) {
            logMess("JSReceiverDelegate log(view, tag, msg): $tag $msg")
        }

        override fun log(view: View, msg: String?) {
            logMess("JSReceiverDelegate log(view, msg): $msg")
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

        override fun onAction(iConsentWebView: IConsentWebView, actionData: String, nextCampaign: CampaignModel) {
            /** spClient is called from [onActionFromWebViewClient] */
            (iConsentWebView as? View)?.let {
                /** spClient is called from [onActionFromWebViewClient] */
                pJsonConverter
                    .toConsentAction(actionData)
                    .map { ca ->
                        onActionFromWebViewClient(ca, iConsentWebView)
                        if (ca.actionType != SHOW_OPTIONS) {
                            val legislation = nextCampaign.type
                            val url = nextCampaign.url
                            executor.executeOnMain { iConsentWebView.loadConsentUI(nextCampaign, url, legislation) }
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
            executor.executeOnMain {
                view.let { spClient?.onUIFinished(view) }
            }
        }
    }

    /** End Receiver methods */

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
                    consentManager.enqueueConsent(consentAction = action)
                }
            }
        }
    }

    private fun showOption(action: ConsentAction, iConsentWebView: IConsentWebView) {
        val view: View = (iConsentWebView as? View) ?: kotlin.run { return }
        when (val l = action.legislation) {
            Legislation.GDPR -> {
                viewManager.removeView(view)
                campaignManager.getPmConfig(l, action.privacyManagerId, PMTab.PURPOSES)
                    .map { pmUrlConfig ->
                        iConsentWebView.loadConsentUIFromUrl(
                            url = urlManager.pmUrl(legislation = action.legislation, pmConfig = pmUrlConfig, env = env),
                            legislation = action.legislation,
                            pmId = action.privacyManagerId
                        )
                    }
                    .executeOnLeft { it.printStackTrace() }
            }
            Legislation.CCPA -> {
                viewManager.removeView(view)
                campaignManager.getPmConfig(legislation = l, pmId = action.privacyManagerId, pmTab = null)
                    .map { pmUrlConfig ->
                        iConsentWebView.loadConsentUIFromUrl(
                            url = urlManager.pmUrl(legislation = action.legislation, pmConfig = pmUrlConfig, env = env),
                            legislation = action.legislation,
                            pmId = action.privacyManagerId
                        )
                    }
                    .executeOnLeft { it.printStackTrace() }
            }
        }
    }
}

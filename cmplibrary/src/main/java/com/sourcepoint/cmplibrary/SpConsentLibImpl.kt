package com.sourcepoint.cmplibrary

import android.content.Context
import android.view.View
import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.consent.ConsentManager
import com.sourcepoint.cmplibrary.consent.CustomConsentClient
import com.sourcepoint.cmplibrary.consent.LocalStateStatus
import com.sourcepoint.cmplibrary.core.Either
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
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManager
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManagerSingleton
import com.sourcepoint.cmplibrary.exception.* // ktlint-disable
import com.sourcepoint.cmplibrary.exception.LoggerType.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.CampaignResp
import com.sourcepoint.cmplibrary.model.UnifiedMessageResp
import com.sourcepoint.cmplibrary.model.exposed.ActionType.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.model.exposed.toJsonObject
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
    private val dataStorage: DataStorage,
    private val spClient: SpClient,
    private val urlManager: HttpUrlManager = HttpUrlManagerSingleton,
    private val env: Env = Env.PROD
) : SpConsentLib {

    private val nativeMsgClient by lazy { NativeMsgDelegate() }

    companion object {
        fun UnifiedMessageResp.toCampaignModelList(logger: Logger): List<CampaignModel> {
            val campaignList = this.campaigns
            if (campaignList.isEmpty()) return emptyList()

            val partition: Pair<List<CampaignResp>, List<CampaignResp>> = campaignList
                .partition { it.message != null && it.url != null }

            logger.computation(
                tag = "toCampaignModelList",
                msg = "parsed campaigns${NL.t}${partition.second.size} Null messages${NL.t}${partition.first.size} Not Null message"
            )

            return partition.first.map {
                CampaignModel(
                    message = it.message!!,
                    messageMetaData = it.messageMetaData!!,
                    type = CampaignType.valueOf(it.type),
                    url = it.url!!
                )
            }
        }
    }

    init {
        consentManager.sPConsentsSuccess = { spConsents ->
            val spConsentString = spConsents.toJsonObject().toString()
            executor.executeOnMain {
                spClient.onConsentReady(spConsents)
                pLogger.clientEvent(
                    event = "onConsentReady",
                    msg = "onConsentReady",
                    content = spConsentString
                )
                (spClient as? UnitySpClient)?.onConsentReady(spConsentString)
            }
        }
        consentManager.sPConsentsError = { throwable ->
            throwable.printStackTrace()
            executor.executeOnMain {
                spClient.onError(throwable)
                pLogger.clientEvent(
                    event = "onError",
                    msg = "${throwable.message}",
                    content = "${throwable.message}"
                )
            }
        }
    }

    /** Start Client's methods */
    override fun loadMessage(authId: String) {
        checkMainThread("loadMessage")
        throwsExceptionIfClientIsNull()

        if (viewManager.isViewInLayout) return

        service.getUnifiedMessage(
            messageReq = campaignManager.getUnifiedMessageReq(authId),
            pSuccess = { messageResp ->
                consentManager.localStateStatus = LocalStateStatus.Present(value = messageResp.localState)
                val list: List<CampaignModel> = messageResp.toCampaignModelList(logger = pLogger)
                if (list.isEmpty()) {
                    consentManager.sendStoredConsentToClient()
                    return@getUnifiedMessage
                }
                val firstCampaign2Process = list.first()
                val remainingCampaigns: Queue<CampaignModel> = LinkedList(list.drop(1))
                executor.executeOnMain {
                    /** create a instance of WebView */
                    val webView = viewManager.createWebView(this, JSReceiverDelegate(), remainingCampaigns)

                    /** inject the message into the WebView */
                    val legislation = firstCampaign2Process.type
                    val url = firstCampaign2Process.url
                    webView?.loadConsentUI(firstCampaign2Process, url, legislation)
                }
            },
            pError = { throwable ->
                (throwable as? ConsentLibExceptionK)?.let { pLogger.error(it) }
                val ex = throwable.toConsentLibException()
                spClient.onError(ex)
                pLogger.clientEvent(
                    event = "onError",
                    msg = "${throwable.message}",
                    content = "${throwable.message}"
                )
                pLogger.e("SpConsentLib", "onError${NL.t}${throwable.message}")
            },
            env = env
        )
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
                if (list.isEmpty()) {
                    consentManager.sendStoredConsentToClient()
                    return@getUnifiedMessage
                }
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
                val ex = throwable.toConsentLibException()
                spClient.onError(ex)
                pLogger.clientEvent(
                    event = "onError",
                    msg = "${throwable.message}",
                    content = "${throwable.message}"
                )
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
                    spClient.onUIReady(nativeMessage)
                }
            },
            { throwable ->
                (throwable as? ConsentLibExceptionK)?.let { pLogger.error(it) }
                pLogger.error(throwable.toConsentLibException())
            }
        )
    }

    override fun customConsentGDPR(
        vendors: List<String>,
        categories: List<String>,
        legIntCategories: List<String>,
        success: (SPConsents?) -> Unit,
    ) {
        val customConsentReq = CustomConsentReq(
            consentUUID = dataStorage.getGdprConsentUuid() ?: "",
            propertyId = dataStorage.getPropertyId(),
            categories = categories,
            legIntCategories = legIntCategories,
            vendors = vendors
        )
        executor.run {
            executeOnWorkerThread {
                val ccResp = service.sendCustomConsentServ(customConsentReq, env)
                executeOnMain {
                    when (ccResp) {
                        is Either.Right -> success(ccResp.r ?: SPConsents())
                        is Either.Left -> {
                            spClient.onError(ccResp.t)
                            pLogger.clientEvent(
                                event = "onError",
                                msg = "${ccResp.t.message}",
                                content = "${ccResp.t}"
                            )
                        }
                    }
                }
            }
        }
    }

    override fun customConsentGDPR(
        vendors: Array<String>,
        categories: Array<String>,
        legIntCategories: Array<String>,
        successCallback: CustomConsentClient
    ) {
        customConsentGDPR(
            vendors = vendors.toList(),
            categories = categories.toList(),
            legIntCategories = legIntCategories.toList(),
            success = {
                it?.let { spc ->
                    successCallback.transferCustomConsentToUnity(spc.toJsonObject().toString())
                } ?: run {
                    spClient.onError(RuntimeException("An error occurred during the custom consent request"))
                    pLogger.clientEvent(
                        event = "onError",
                        msg = "An error occurred during the custom consent request",
                        content = "An error occurred during the custom consent request"
                    )
                }
            }
        )
    }

    override fun loadPrivacyManager(pmId: String, pmTab: PMTab, campaignType: CampaignType) {
        checkMainThread("loadPrivacyManager")
        throwsExceptionIfClientIsNull()
        val pmConfig = campaignManager.getPmConfig(campaignType, pmId, pmTab)
        pmConfig
            .map { it ->
                val webView = viewManager.createWebView(this, JSReceiverDelegate())
                val url = urlManager.pmUrl(campaignType = campaignType, pmConfig = it, env = env)
                pLogger.req(
                    tag = "${campaignType.name} Privacy Manager",
                    url = url.toString(),
                    body = "pmId $pmId",
                    type = "GET"
                )
                pLogger.pm(
                    tag = "${campaignType.name} Privacy Manager",
                    url = url.toString(),
                    pmId = pmId,
                    type = "GET"
                )
                webView?.loadConsentUIFromUrl(
                    url = url,
                    campaignType = campaignType,
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
            executor.executeOnMain { spClient.onUIReady(view) }
        }

        override fun log(view: View, tag: String?, msg: String?) {
            pLogger.i(tag ?: "JSReceiverDelegate", "RenderingApp log${NL.t}$msg")
        }

        override fun log(view: View, msg: String?) {
            pLogger.i("JSReceiverDelegate", "RenderingApp log${NL.t}$msg")
        }

        override fun onError(view: View, errorMessage: String) {
            spClient.onError(GenericSDKException(description = errorMessage))
            pLogger.clientEvent(
                event = "onError",
                msg = errorMessage,
                content = ""
            )
        }

        override fun onNoIntentActivitiesFoundFor(view: View, url: String) {
        }

        override fun onError(view: View, error: Throwable) {
            spClient.onError(error)
            pLogger.clientEvent(
                event = "onError",
                msg = "${error.message}",
                content = "$error"
            )
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
                view.let {
                    spClient.onUIFinished(view)
                    pLogger.actionWebApp(
                        tag = "onUIFinished",
                        msg = "onUIFinished",
                        json = null
                    )
                }
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
            spClient.onAction(view, action.actionType)
            pLogger.actionWebApp(
                tag = "onActionFromWebViewClient",
                msg = action.actionType.name,
                json = action.thisContent
            )
            when (action.actionType) {
                ACCEPT_ALL,
                SAVE_AND_EXIT,
                REJECT_ALL -> {
                    consentManager.enqueueConsent(consentAction = action)
                }
                SHOW_OPTIONS -> {
                    showOption(action, iConsentWebView)
                }
                MSG_CANCEL,
                PM_DISMISS -> {
                }
            }
        }
    }

    private fun showOption(action: ConsentAction, iConsentWebView: IConsentWebView) {
        val view: View = (iConsentWebView as? View) ?: kotlin.run { return }
        when (val l = action.campaignType) {
            CampaignType.GDPR -> {
                viewManager.removeView(view)
                campaignManager.getPmConfig(l, action.privacyManagerId, PMTab.PURPOSES)
                    .map { pmUrlConfig ->
                        val url =
                            urlManager.pmUrl(campaignType = action.campaignType, pmConfig = pmUrlConfig, env = env)
                        pLogger.pm(
                            tag = "${action.campaignType.name} Privacy Manager",
                            url = url.toString(),
                            pmId = "${action.privacyManagerId}",
                            type = "GET"
                        )
                        iConsentWebView.loadConsentUIFromUrl(
                            url = url,
                            campaignType = action.campaignType,
                            pmId = action.privacyManagerId
                        )
                    }
                    .executeOnLeft { it.printStackTrace() }
            }
            CampaignType.CCPA -> {
                viewManager.removeView(view)
                campaignManager.getPmConfig(campaignType = l, pmId = action.privacyManagerId, pmTab = null)
                    .map { pmUrlConfig ->
                        val url =
                            urlManager.pmUrl(campaignType = action.campaignType, pmConfig = pmUrlConfig, env = env)
                        pLogger.pm(
                            tag = "${action.campaignType.name} Privacy Manager",
                            url = url.toString(),
                            pmId = "${action.privacyManagerId}",
                            type = "GET"
                        )
                        iConsentWebView.loadConsentUIFromUrl(
                            url = url,
                            campaignType = action.campaignType,
                            pmId = action.privacyManagerId
                        )
                    }
                    .executeOnLeft { it.printStackTrace() }
            }
        }
    }
}

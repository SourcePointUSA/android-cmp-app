package com.sourcepoint.cmplibrary

import android.content.Context
import android.view.View
import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.consent.* // ktlint-disable
import com.sourcepoint.cmplibrary.consent.ClientEventManager
import com.sourcepoint.cmplibrary.consent.ConsentManager
import com.sourcepoint.cmplibrary.consent.LocalStateStatus
import com.sourcepoint.cmplibrary.core.* // ktlint-disable
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.ExecutorManager
import com.sourcepoint.cmplibrary.core.executeOnLeft
import com.sourcepoint.cmplibrary.core.map
import com.sourcepoint.cmplibrary.core.nativemessage.toNativeMessageDTO
import com.sourcepoint.cmplibrary.core.web.CampaignModel
import com.sourcepoint.cmplibrary.core.web.IConsentWebView
import com.sourcepoint.cmplibrary.core.web.JSClientLib
import com.sourcepoint.cmplibrary.data.Service
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManager
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManagerSingleton
import com.sourcepoint.cmplibrary.exception.* //ktlint-disable
import com.sourcepoint.cmplibrary.exception.LoggerType.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.CampaignResp
import com.sourcepoint.cmplibrary.model.UnifiedMessageResp
import com.sourcepoint.cmplibrary.model.exposed.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.ActionType.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.MessageSubCategory.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.toJsonObject
import com.sourcepoint.cmplibrary.util.* // ktlint-disable
import org.json.JSONObject
import java.util.* //ktlint-disable

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
    private val clientEventManager: ClientEventManager,
    private val urlManager: HttpUrlManager = HttpUrlManagerSingleton,
    private val env: Env = Env.PROD,
) : SpConsentLib, NativeMessageController {

    private val remainingCampaigns: Queue<CampaignModel> = LinkedList()
    private var currentNativeMessageCampaign: CampaignModel? = null

    companion object {
        fun UnifiedMessageResp.toCampaignModelList(logger: Logger): List<CampaignModel> {
            val campaignList = this.campaigns
            if (campaignList.isEmpty()) return emptyList()

            val partition: Pair<List<CampaignResp>, List<CampaignResp>> = campaignList
                .partition { it.message != null && it.url != null && it.messageSubCategory != null }

            logger.computation(
                tag = "toCampaignModelList",
                msg = "parsed campaigns${NL.t}${partition.second.size} Null messages${NL.t}${partition.first.size} Not Null message"
            )

            return partition.first.map {

                CampaignModel(
                    message = it.message!!,
                    messageMetaData = it.messageMetaData!!,
                    type = CampaignType.valueOf(it.type),
                    url = it.url!!,
                    messageSubCategory = it.messageSubCategory!!,
                )
            }
        }
    }

    init {
        consentManager.sPConsentsSuccess = { spConsents ->
            val spConsentString = spConsents.toJsonObject().toString()
            executor.executeOnMain {
                pLogger.clientEvent(
                    event = "onConsentReady",
                    msg = "onConsentReady",
                    content = spConsentString
                )
                spClient.onConsentReady(spConsents)
                (spClient as? UnitySpClient)?.onConsentReady(spConsentString)
                executor.executeOnSingleThread {
                    clientEventManager.checkStatus()
                }
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

    override fun loadMessage() {
        localLoadMessage(authId = null, pubData = null, cmpViewId = null)
    }

    override fun loadMessage(cmpViewId: Int) {
        localLoadMessage(authId = null, pubData = null, cmpViewId = cmpViewId)
    }

    override fun loadMessage(authId: String?) {
        localLoadMessage(authId = authId, pubData = null, cmpViewId = null)
    }

    override fun loadMessage(pubData: JSONObject?) {
        localLoadMessage(authId = null, pubData = pubData, cmpViewId = null)
    }

    /** Start Client's methods */
    override fun loadMessage(authId: String?, pubData: JSONObject?, cmpViewId: Int?) {
        localLoadMessage(authId = authId, pubData = pubData, cmpViewId = cmpViewId)
    }

    private fun localLoadMessage(authId: String?, pubData: JSONObject?, cmpViewId: Int?) {
        checkMainThread("loadMessage")

        if (viewManager.isViewInLayout) return

        service.getUnifiedMessage(
            messageReq = campaignManager.getUnifiedMessageReq(authId, pubData),
            pSuccess = { messageResp ->
                consentManager.localStateStatus = LocalStateStatus.Present(value = messageResp.localState)
                val list: List<CampaignModel> = messageResp.toCampaignModelList(logger = pLogger)
                clientEventManager.setCampaignNumber(list.size)
                if (list.isEmpty()) {
                    consentManager.sendStoredConsentToClient()
                    return@getUnifiedMessage
                }
                val firstCampaign2Process: CampaignModel = list.first()
                remainingCampaigns.run {
                    clear()
                    addAll(LinkedList(list.drop(1)))
                }
                executor.executeOnMain {
                    val legislation = firstCampaign2Process.type
                    when (firstCampaign2Process.messageSubCategory) {
                        TCFv2, OTT, NATIVE_OTT -> {
                            /** create a instance of WebView */
                            val webView = viewManager.createWebView(
                                this,
                                JSReceiverDelegate(),
                                remainingCampaigns,
                                firstCampaign2Process.messageSubCategory,
                                cmpViewId
                            )
                                .executeOnLeft { spClient.onError(it) }
                                .getOrNull()

                            /** inject the message into the WebView */
                            val url = firstCampaign2Process.url
                            webView?.loadConsentUI(firstCampaign2Process, url, legislation)
                        }
                        NATIVE_IN_APP -> {
                            val nmDto = firstCampaign2Process.message.toNativeMessageDTO(legislation)
                            currentNativeMessageCampaign = firstCampaign2Process
                            spClient.onNativeMessageReady(nmDto, this)
                            pLogger.nativeMessageAction(
                                tag = "onNativeMessageReady",
                                msg = "onNativeMessageReady",
                                json = firstCampaign2Process.message
                            )
                        }
                    }
                }
            },
            pError = { throwable ->
                if (consentManager.storedConsent) {
                    executor.executeOnSingleThread {
                        consentManager.sendStoredConsentToClient()
                        clientEventManager.setAction(NativeMessageActionType.GET_MSG_ERROR)
                    }
                } else {
                    (throwable as? ConsentLibExceptionK)?.let { pLogger.error(it) }
                    val ex = throwable.toConsentLibException()
                    spClient.onError(ex)
                    pLogger.clientEvent(
                        event = "onError",
                        msg = "${throwable.message}",
                        content = "${throwable.message}"
                    )
                    pLogger.e(
                        "SpConsentLib",
                        """
                    onError
                    ${throwable.message}
                        """.trimIndent()
                    )
                }
            },
            env = env
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

    override fun deleteCustomConsentTo(
        vendors: List<String>,
        categories: List<String>,
        legIntCategories: List<String>,
        success: (SPConsents?) -> Unit
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
                val ccResp = service.deleteCustomConsentToServ(customConsentReq, env)
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

    override fun loadPrivacyManager(
        pmId: String,
        pmTab: PMTab,
        campaignType: CampaignType
    ) {
        loadPrivacyManager(pmId, pmTab, campaignType, false)
    }

    override fun loadPrivacyManager(
        pmId: String,
        pmTab: PMTab,
        campaignType: CampaignType,
        useGroupPmIfAvailable: Boolean
    ) {
        loadPm(
            pmId = pmId,
            pmTab = pmTab,
            campaignType = campaignType,
            messSubCat = campaignManager.getMessSubCategoryByCamp(campaignType),
            useGroupPmIfAvailable = useGroupPmIfAvailable
        )
    }

    override fun loadPrivacyManager(pmId: String, campaignType: CampaignType) {
        loadPm(
            pmId = pmId,
            pmTab = PMTab.DEFAULT,
            campaignType = campaignType,
            messSubCat = campaignManager.getMessSubCategoryByCamp(campaignType),
            useGroupPmIfAvailable = false
        )
    }

    private fun loadPm(
        pmId: String,
        pmTab: PMTab,
        campaignType: CampaignType,
        messSubCat: MessageSubCategory,
        useGroupPmIfAvailable: Boolean
    ) {
        checkMainThread("loadPrivacyManager")
        clientEventManager.executingLoadPM()

        val gdprGroupPmId = campaignManager.getGroupId(campaignType)

        // used for testing
        pLogger.i("loadPm - messSubCat: ", "${messSubCat.code}")

        val pmConfig = campaignManager.getPmConfig(campaignType, pmId, pmTab, useGroupPmIfAvailable, gdprGroupPmId)
        pmConfig
            .map {
                val webView = viewManager.createWebView(this, JSReceiverDelegate(), messSubCat, null)
                    .executeOnLeft { e -> spClient.onError(e) }
                    .getOrNull()
                val url = urlManager.pmUrl(env = env, campaignType = campaignType, pmConfig = it, messSubCat = messSubCat)
                pLogger.pm(
                    tag = "${campaignType.name} Privacy Manager",
                    url = url.toString(),
                    params = """
                        pmId [${it.messageId}]
                        consentLanguage [${it.consentLanguage}]
                        pmTab [${it.pmTab}]
                        siteId [${it.siteId}]
                    """.trimIndent(),
                    type = "GET"
                )
                webView?.loadConsentUIFromUrl(
                    url = url,
                    campaignType = campaignType,
                    pmId = it.messageId,
                    singleShot = true
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
    }

    private fun logMess(mess: String) = pLogger.d(this::class.java.simpleName, "$mess")

    /** Start Receiver methods */
    inner class JSReceiverDelegate : JSClientLib {

        override fun onConsentUIReady(view: View, isFromPM: Boolean) {
            executor.executeOnMain { spClient.onUIReady(view) }
        }

        override fun log(view: View, tag: String?, msg: String?) {
            check { JSONObject(msg).toString() }
                .getOrNull()
                ?.let {
//                    pLogger.clientEvent(
//                        event = "log",
//                        msg = "RenderingApp",
//                        content = it
//                    )
                }
        }

        override fun log(view: View, msg: String?) {
            check { JSONObject(msg).toString() }
                .getOrNull()
                ?.let {
//                    pLogger.clientEvent(
//                        event = "log",
//                        msg = "RenderingApp",
//                        content = it
//                    )
                }
        }

        override fun onError(view: View, errorMessage: String) {
            spClient.onError(RenderingAppException(description = errorMessage))
            pLogger.clientEvent(
                event = "onError",
                msg = errorMessage,
                content = ""
            )
        }

        override fun onNoIntentActivitiesFoundFor(view: View, url: String) {
            spClient.onNoIntentActivitiesFound(url)
            pLogger.clientEvent(
                event = "log",
                msg = "onNoIntentActivitiesFound",
                content = JSONObject().apply {
                    put("url", url)
                }.toString()
            )
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
                            when (nextCampaign.messageSubCategory) {
                                TCFv2 -> {
                                    executor.executeOnMain {
                                        iConsentWebView.loadConsentUI(
                                            nextCampaign,
                                            url,
                                            legislation
                                        )
                                    }
                                }
                                NATIVE_IN_APP -> {
                                    executor.executeOnMain {
                                        viewManager.removeView(iConsentWebView)
                                        currentNativeMessageCampaign = nextCampaign
                                        spClient.onNativeMessageReady(
                                            nextCampaign.message.toNativeMessageDTO(
                                                legislation
                                            ),
                                            this@SpConsentLibImpl
                                        )
                                        pLogger.nativeMessageAction(
                                            tag = "onNativeMessageReady",
                                            msg = "onNativeMessageReady",
                                            json = nextCampaign.message
                                        )
                                    }
                                }
                            }
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
                    pLogger.webAppAction(
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
    internal fun onActionFromWebViewClient(actionImpl: ConsentActionImpl, iConsentWebView: IConsentWebView?) {
        val view: View = (iConsentWebView as? View) ?: kotlin.run { return }
        pLogger.webAppAction(
            tag = "Action from the RenderingApp",
            msg = actionImpl.actionType.name,
            json = actionImpl.thisContent
        )
        when (actionImpl.actionType) {
            ACCEPT_ALL,
            SAVE_AND_EXIT,
            REJECT_ALL -> {
                executor.executeOnSingleThread {
                    val editedAction = spClient.onAction(view, actionImpl) as? ConsentActionImpl
                    editedAction?.let {
                        consentManager.enqueueConsent(consentActionImpl = editedAction)
                    }
                }
            }
            SHOW_OPTIONS -> {
                executor.executeOnMain { showOption(actionImpl, iConsentWebView) }
            }
            CUSTOM -> {
                executor.executeOnSingleThread {
                    spClient.onAction(view, actionImpl) as? ConsentActionImpl
                }
            }
            MSG_CANCEL,
            PM_DISMISS -> {
                executor.executeOnSingleThread {
                    spClient.onAction(view, actionImpl) as? ConsentActionImpl
                }
            }
        }
        clientEventManager.setAction(actionImpl)
    }

    private fun showOption(actionImpl: ConsentActionImpl, iConsentWebView: IConsentWebView) {
        val view: View = (iConsentWebView as? View) ?: kotlin.run { return }
        when (val l = actionImpl.campaignType) {
            CampaignType.GDPR -> {
                viewManager.removeView(view)
                campaignManager.getPmConfig(l, actionImpl.privacyManagerId, actionImpl.privacyManagerTab())
                    .map { pmUrlConfig ->
                        val url =
                            urlManager.pmUrl(
                                env = env,
                                campaignType = actionImpl.campaignType,
                                pmConfig = pmUrlConfig,
                                messSubCat = TCFv2
                            )
                        pLogger.pm(
                            tag = "${actionImpl.campaignType.name} Privacy Manager",
                            url = url.toString(),
                            params = "${actionImpl.privacyManagerId}",
                            type = "GET"
                        )
                        iConsentWebView.loadConsentUIFromUrl(
                            url = url,
                            campaignType = actionImpl.campaignType,
                            pmId = actionImpl.privacyManagerId,
                            singleShot = false
                        )
                    }
                    .executeOnLeft { spClient.onError(it) }
            }
            CampaignType.CCPA -> {
                viewManager.removeView(view)
                campaignManager.getPmConfig(campaignType = l, pmId = actionImpl.privacyManagerId, pmTab = null)
                    .map { pmUrlConfig ->
                        val url =
                            urlManager.pmUrl(
                                env = env,
                                campaignType = actionImpl.campaignType,
                                pmConfig = pmUrlConfig,
                                messSubCat = TCFv2
                            )
                        pLogger.pm(
                            tag = "${actionImpl.campaignType.name} Privacy Manager",
                            url = url.toString(),
                            params = "${actionImpl.privacyManagerId}",
                            type = "GET"
                        )
                        iConsentWebView.loadConsentUIFromUrl(
                            url = url,
                            campaignType = actionImpl.campaignType,
                            pmId = actionImpl.privacyManagerId,
                            singleShot = false
                        )
                    }
                    .executeOnLeft { spClient.onError(it) }
            }
        }
    }

    private fun nativeMessageShowOption(action: NativeConsentAction, iConsentWebView: IConsentWebView) {
        val view: View = (iConsentWebView as? View) ?: kotlin.run { return }
        when (val l = action.campaignType) {
            CampaignType.GDPR -> {
                viewManager.removeView(view)
                campaignManager.getPmConfig(l, action.privacyManagerId, PMTab.PURPOSES)
                    .map { pmUrlConfig ->
                        val url =
                            urlManager.pmUrl(
                                env = env,
                                campaignType = action.campaignType,
                                pmConfig = pmUrlConfig,
                                messSubCat = TCFv2
                            )
                        pLogger.pm(
                            tag = "${action.campaignType.name} Privacy Manager",
                            url = url.toString(),
                            params = "${action.privacyManagerId}",
                            type = "GET"
                        )
                        iConsentWebView.loadConsentUIFromUrl(
                            url = url,
                            campaignType = action.campaignType,
                            pmId = action.privacyManagerId,
                            campaignModel = currentNativeMessageCampaign!!,
                        )
                    }
                    .executeOnLeft { spClient.onError(it) }
            }
            CampaignType.CCPA -> {
                viewManager.removeView(view)
                campaignManager.getPmConfig(campaignType = l, pmId = action.privacyManagerId, pmTab = null)
                    .map { pmUrlConfig ->
                        val url =
                            urlManager.pmUrl(
                                env = env,
                                campaignType = action.campaignType,
                                pmConfig = pmUrlConfig,
                                messSubCat = TCFv2
                            )
                        pLogger.pm(
                            tag = "${action.campaignType.name} Privacy Manager",
                            url = url.toString(),
                            params = "${action.privacyManagerId}",
                            type = "GET"
                        )
                        iConsentWebView.loadConsentUIFromUrl(
                            url = url,
                            campaignType = action.campaignType,
                            pmId = action.privacyManagerId,
                            campaignModel = currentNativeMessageCampaign!!
                        )
                    }
                    .executeOnLeft { spClient.onError(it) }
            }
        }
    }

    override fun showOptionNativeMessage(campaignType: CampaignType, pmId: String) {
        val nca = NativeConsentAction(
            campaignType = campaignType,
            actionType = NativeMessageActionType.SHOW_OPTIONS,
            privacyManagerId = pmId
        )

        viewManager.createWebView(this, JSReceiverDelegate(), remainingCampaigns, TCFv2, null)
            .map { nativeMessageShowOption(nca, it) }
            .executeOnLeft { spClient.onError(it) }
    }

    override fun removeNativeView(view: View) {
        viewManager.removeView(view)
    }

    override fun showNativeView(view: View) {
        showView(view)
    }

    override fun sendConsent(action: NativeMessageActionType, campaignType: CampaignType) {

        clientEventManager.setAction(action)

        val nca = NativeConsentAction(
            campaignType = campaignType,
            actionType = action
        )

        when (nca.actionType) {
            NativeMessageActionType.SHOW_OPTIONS -> {
            }
            NativeMessageActionType.UNKNOWN,
            NativeMessageActionType.MSG_CANCEL -> {
                moveToNextCampaign(remainingCampaigns, viewManager, spClient)
            }
            NativeMessageActionType.ACCEPT_ALL,
            NativeMessageActionType.REJECT_ALL -> {
                consentManager.enqueueConsent(nativeConsentAction = nca)
                moveToNextCampaign(remainingCampaigns, viewManager, spClient)
            }
        }
    }

    private fun moveToNextCampaign(
        remainingCampaigns: Queue<CampaignModel>,
        viewManager: ViewsManager,
        spClient: SpClient
    ) {
        remainingCampaigns.poll()?.let {
            val legislation = it.type
            when (it.messageSubCategory) {
                NATIVE_IN_APP -> {
                    val nm = it.message.toNativeMessageDTO(legislation)
                    currentNativeMessageCampaign = it
                    spClient.onNativeMessageReady(nm, this@SpConsentLibImpl)
                    pLogger.nativeMessageAction(
                        tag = "onNativeMessageReady",
                        msg = "onNativeMessageReady",
                        json = it.message
                    )
                }
                TCFv2, OTT, NATIVE_OTT -> {
                    /** create a instance of WebView */
                    val webView = viewManager.createWebView(
                        this,
                        JSReceiverDelegate(),
                        remainingCampaigns,
                        it.messageSubCategory,
                        null
                    )
                        .executeOnLeft { e -> spClient.onError(e) }
                        .getOrNull()

                    /** inject the message into the WebView */
                    val url = it.url // urlManager.urlURenderingApp(env)//
                    webView?.loadConsentUI(it, url, legislation)
                }
            }
        }
    }
}

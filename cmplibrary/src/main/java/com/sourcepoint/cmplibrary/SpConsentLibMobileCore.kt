package com.sourcepoint.cmplibrary

import android.R.id.content
import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import com.sourcepoint.cmplibrary.consent.CustomConsentClient
import com.sourcepoint.cmplibrary.data.network.connection.ConnectionManager
import com.sourcepoint.cmplibrary.data.network.util.CampaignType
import com.sourcepoint.cmplibrary.exception.ConsentLibExceptionK
import com.sourcepoint.cmplibrary.exception.FailedToDeleteCustomConsent
import com.sourcepoint.cmplibrary.exception.FailedToLoadMessages
import com.sourcepoint.cmplibrary.exception.FailedToPostCustomConsent
import com.sourcepoint.cmplibrary.exception.NoIntentFoundForUrl
import com.sourcepoint.cmplibrary.exception.NoInternetConnectionException
import com.sourcepoint.cmplibrary.exception.RenderingAppException
import com.sourcepoint.cmplibrary.exception.ReportActionException
import com.sourcepoint.cmplibrary.exception.UnableToDownloadRenderingApp
import com.sourcepoint.cmplibrary.exception.UnableToLoadRenderingApp
import com.sourcepoint.cmplibrary.mobile_core.SPConsentAction
import com.sourcepoint.cmplibrary.mobile_core.SPConsentWebView
import com.sourcepoint.cmplibrary.mobile_core.SPMessageUI
import com.sourcepoint.cmplibrary.mobile_core.SPMessageUIClient
import com.sourcepoint.cmplibrary.mobile_core.buildPMUrl
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.exposed.ActionType.*
import com.sourcepoint.cmplibrary.model.exposed.MessageType
import com.sourcepoint.cmplibrary.model.exposed.MessageType.MOBILE
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.util.SpBackPressOttDelegate
import com.sourcepoint.cmplibrary.util.extensions.toJsonObject
import com.sourcepoint.mobile_core.ICoordinator
import com.sourcepoint.mobile_core.models.LoadMessagesException
import com.sourcepoint.mobile_core.models.MessageToDisplay
import com.sourcepoint.mobile_core.models.SPAction
import com.sourcepoint.mobile_core.models.SPActionType.RejectAll
import com.sourcepoint.mobile_core.models.SPError
import com.sourcepoint.mobile_core.models.SPMessageLanguage.ENGLISH
import com.sourcepoint.mobile_core.models.consents.SPUserData
import com.sourcepoint.mobile_core.network.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import org.json.JSONObject
import java.lang.ref.WeakReference

fun launch(task: suspend () -> Unit) {
    CoroutineScope(Dispatchers.Default).launch { task() }
}

fun runOnMain(task: () -> Unit) {
    Handler(Looper.getMainLooper()).post(task)
}

class SpConsentLibMobileCore(
    private val propertyId: Int,
    private val language: MessageLanguage,
    private val activity: WeakReference<Activity>?,
    private val context: Context,
    private val coordinator: ICoordinator,
    private val connectionManager: ConnectionManager,
    private val spClient: SpClient,
) : SpConsentLib, SPMessageUIClient {
    private var pendingActions: Int = 0
    private var messagesToDisplay: ArrayDeque<MessageToDisplay> = ArrayDeque(emptyList())
    private val mainView: ViewGroup? get() = activity?.get()?.findViewById(content)
    private val userData: SPUserData get() = coordinator.userData
    private val spConsents: SPConsents get() = SPConsents(userData)

    private val messageUI: SPMessageUI by lazy {
        SPConsentWebView.create(
            context = context,
            messageUIClient = this@SpConsentLibMobileCore,
            propertyId = propertyId
        )
    }

    override fun loadMessage() = loadMessage(authId = null, pubData = null, cmpViewId = null)

    override fun loadMessage(cmpViewId: Int) =
        loadMessage(authId = null, pubData = null, cmpViewId = cmpViewId)

    override fun loadMessage(pubData: JSONObject?) = loadMessage(authId = null, pubData = pubData)

    override fun loadMessage(authId: String?) = loadMessage(authId = authId, pubData = null)

    override fun loadMessage(authId: String?, pubData: JSONObject?, cmpViewId: Int?) = launch {
        if (!connectionManager.isConnected) {
            onError(NoInternetConnectionException())
            return@launch
        }

        try {
            messagesToDisplay = ArrayDeque(
                coordinator.loadMessages(
                    authId = authId,
                    pubData = pubData?.toJsonObject(),
                    language = language.toCore() ?: ENGLISH
                )
            )
            if (messagesToDisplay.isEmpty()) {
                spClient.onConsentReady(spConsents)
            }
            pendingActions = messagesToDisplay.size
            renderNextMessageIfAny()
        } catch (error: LoadMessagesException) {
            val consents = spConsents
            if (consents.gdpr != null || consents.ccpa != null || consents.usNat != null) {
                renderNextMessageIfAny()
            } else {
                onError(FailedToLoadMessages(error))
            }
        }
    }

    private fun renderNextMessageIfAny() =
        if (pendingActions == 0 && messagesToDisplay.isEmpty()) {
            spClient.onSpFinished(spConsents)
        } else if (messagesToDisplay.isNotEmpty()) {
            val messageToRender = messagesToDisplay.removeFirst()
            messageUI.load(
                message = messageToRender.message,
                messageType = MessageType.fromMessageSubCategory(messageToRender.metaData.subCategoryId),
                url = messageToRender.url,
                campaignType = CampaignType.fromCore(messageToRender.type),
                userData = userData
            )
        } else { }

    override fun customConsentGDPR(
        vendors: List<String>,
        categories: List<String>,
        legIntCategories: List<String>,
        success: (SPConsents?) -> Unit
    ) = launch {
        runCatching {
            coordinator.customConsentGDPR(
                vendors = vendors,
                categories = categories,
                legIntCategories = legIntCategories
            )
            success(spConsents)
        }.onFailure { onError(FailedToPostCustomConsent(cause = it)) }
    }

    override fun customConsentGDPR(
        vendors: Array<String>,
        categories: Array<String>,
        legIntCategories: Array<String>,
        successCallback: CustomConsentClient
    ) = customConsentGDPR(
        vendors = vendors.toList(),
        categories = categories.toList(),
        legIntCategories = legIntCategories.toList()
    ) { consents ->
        consents?.let { successCallback.transferCustomConsentToUnity(json.encodeToString(it)) }
    }

    override fun deleteCustomConsentTo(
        vendors: List<String>,
        categories: List<String>,
        legIntCategories: List<String>,
        success: (SPConsents?) -> Unit
    ) = launch {
        runCatching {
            coordinator.deleteCustomConsentGDPR(
                vendors = vendors,
                categories = categories,
                legIntCategories = legIntCategories
            )
            success(spConsents)
        }.onFailure { onError(FailedToDeleteCustomConsent(cause = it)) }
    }

    override fun deleteCustomConsentTo(
        vendors: Array<String>,
        categories: Array<String>,
        legIntCategories: Array<String>,
        successCallback: CustomConsentClient
    ) = deleteCustomConsentTo(
        vendors = vendors.toList(),
        categories = categories.toList(),
        legIntCategories = legIntCategories.toList()
    ) { consents ->
        consents?.let { successCallback.transferCustomConsentToUnity(json.encodeToString(it)) }
    }

    override fun rejectAll(campaignType: CampaignType) = launch {
        val action = SPAction(type = RejectAll, campaignType = campaignType.toCore())
        runCatching {
            coordinator.reportAction(action)
            val consents = SPConsents(userData)
            spClient.onConsentReady(consents)
            spClient.onSpFinished(consents)
        }.onFailure { onError(ReportActionException(cause = it, action = action)) }
    }

    override fun loadPrivacyManager(pmId: String, campaignType: CampaignType) =
        internalLoadPM(pmId = pmId, campaignType = campaignType)

    override fun loadPrivacyManager(pmId: String, pmTab: PMTab, campaignType: CampaignType) =
        internalLoadPM(pmId = pmId, campaignType = campaignType, pmTab = pmTab)

    override fun loadPrivacyManager(
        pmId: String,
        pmTab: PMTab,
        campaignType: CampaignType,
        useGroupPmIfAvailable: Boolean
    ) = internalLoadPM(
        pmId = pmId,
        pmTab = pmTab,
        campaignType = campaignType,
        useGroupPmIfAvailable = useGroupPmIfAvailable
    )

    override fun loadPrivacyManager(
        pmId: String,
        campaignType: CampaignType,
        messageType: MessageType
    ) = internalLoadPM(pmId = pmId, campaignType = campaignType, messageType = messageType)

    override fun loadPrivacyManager(
        pmId: String,
        pmTab: PMTab,
        campaignType: CampaignType,
        messageType: MessageType
    ) = internalLoadPM(pmId = pmId, pmTab = pmTab, campaignType = campaignType, messageType = messageType)

    override fun loadPrivacyManager(
        pmId: String,
        pmTab: PMTab,
        campaignType: CampaignType,
        useGroupPmIfAvailable: Boolean,
        messageType: MessageType
    ) = internalLoadPM(
        pmId = pmId,
        pmTab = pmTab,
        campaignType = campaignType,
        useGroupPmIfAvailable = useGroupPmIfAvailable,
        messageType = messageType
    )

    private fun internalLoadPM(
        pmId: String,
        campaignType: CampaignType,
        pmTab: PMTab? = null,
        useGroupPmIfAvailable: Boolean? = null,
        messageType: MessageType = MOBILE
    ) {
        if (!connectionManager.isConnected) {
            onError(NoInternetConnectionException())
            return
        }

        pendingActions++
        messageUI.load(
            messageType = messageType,
            url = buildPMUrl(
                campaignType = campaignType,
                pmId = pmId,
                propertyId = propertyId,
                userData = userData,
                language = language.value,
                pmTab = pmTab,
                pmType = messageType,
                useChildPmIfAvailable = useGroupPmIfAvailable == true
            ),
            campaignType = campaignType,
            userData = userData
        )
    }

    override fun showView(view: View) {
        view.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        view.bringToFront()
        view.requestLayout()
        view.requestFocus()
        mainView?.addView(view)
    }

    override fun removeView(view: View) {
        mainView?.removeView(view)
    }

    override fun dispose() {
        // TODO: deprecate
    }

    override fun handleOnBackPress(isMessageDismissible: Boolean, ottDelegate: SpBackPressOttDelegate) {
        handleOnBackPress(isMessageDismissible, ottDelegate::onHomePage)
    }

    override fun handleOnBackPress(isMessageDismissible: Boolean, onHomePage: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun clearLocalData() = coordinator.clearLocalData()

    override fun loaded(view: View) = spClient.onUIReady(view)

    override fun onAction(view: View, action: ConsentAction) = launch {
        val userAction = spClient.onAction(view, action) as SPConsentAction
        when (userAction.actionType) {
            ACCEPT_ALL, REJECT_ALL, SAVE_AND_EXIT -> {
                runCatching {
                    coordinator.reportAction(userAction.toCore())
                    spClient.onConsentReady(SPConsents(userData))
                }
                    .onFailure {
                        onError(ReportActionException(cause = it, action = userAction.toCore()))
                    }
                pendingActions--
                finished(view)
            }
            CUSTOM, UNKNOWN -> {
                pendingActions--
                finished(view)
            }
            PM_DISMISS -> {
                if (messageUI.isFirstLayer) {
                    pendingActions--
                }
            }
            MSG_CANCEL -> {
                pendingActions--
                // TODO: not call `finished` if the message is not dismissible
                finished(view)
            }
            else -> {}
        }
    }

    override fun onError(error: ConsentLibExceptionK) {
        pendingActions = 0
        messagesToDisplay = ArrayDeque(emptyList())

        when (error) {
            is NoIntentFoundForUrl -> {
                spClient.onNoIntentActivitiesFound(error.url ?: "")
                return
            }
            is UnableToDownloadRenderingApp, is UnableToLoadRenderingApp, is RenderingAppException -> {
                launch { coordinator.logError(SPError(code = error.code)) }
            }
            else -> {
                // NOTE: all other exceptions should have been logged by the `mobile-core` lib
            }
        }
        spClient.onError(error)
    }

    override fun finished(view: View) {
        runOnMain { spClient.onUIFinished(view) }
        renderNextMessageIfAny()
    }
}

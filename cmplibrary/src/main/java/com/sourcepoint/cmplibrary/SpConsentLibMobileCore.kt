package com.sourcepoint.cmplibrary

import android.R.id.content
import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import com.sourcepoint.cmplibrary.consent.CustomConsentClient
import com.sourcepoint.cmplibrary.data.network.connection.ConnectionManager
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.mobile_core.SPConsentAction
import com.sourcepoint.cmplibrary.mobile_core.SPConsentWebView
import com.sourcepoint.cmplibrary.mobile_core.SPMessageUI
import com.sourcepoint.cmplibrary.mobile_core.SPMessageUIClient
import com.sourcepoint.cmplibrary.mobile_core.buildPMUrl
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.MessageType
import com.sourcepoint.cmplibrary.model.exposed.MessageType.MOBILE
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.util.SpBackPressOttDelegate
import com.sourcepoint.cmplibrary.util.extensions.toJsonObject
import com.sourcepoint.mobile_core.ICoordinator
import com.sourcepoint.mobile_core.models.MessageToDisplay
import com.sourcepoint.mobile_core.models.SPAction
import com.sourcepoint.mobile_core.models.SPActionType.RejectAll
import com.sourcepoint.mobile_core.models.SPMessageLanguage.ENGLISH
import com.sourcepoint.mobile_core.models.consents.SPUserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    private val activity: WeakReference<Activity>,
    private val context: Context,
    private val coordinator: ICoordinator,
    private val connectionManager: ConnectionManager,
    private val spClient: SpClient,
): SpConsentLib, SPMessageUIClient {
    private var pendingActions: Int = 0
    private var messagesToDisplay: ArrayDeque<MessageToDisplay> = ArrayDeque(emptyList())
    private val mainView: ViewGroup? get() = activity.get()?.findViewById(content)
    private val userData: SPUserData get() = coordinator.userData
    private val spConsents: SPConsents get () = SPConsents(userData)

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
        if (connectionManager.isConnected) {
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
        } else {
            onError() // TODO: specify the error
        }
    }

    private fun renderNextMessageIfAny() =
        if(pendingActions == 0 && messagesToDisplay.isEmpty()) {
            spClient.onSpFinished(spConsents)
        } else if (messagesToDisplay.isNotEmpty()) {
            val messageToRender = messagesToDisplay.removeFirst()
            messageUI.load(
                message = messageToRender.message,
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
        coordinator.customConsentGDPR(
            vendors = vendors,
            categories = categories,
            legIntCategories = legIntCategories
        )
        success(spConsents)
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
    ) {
        // TODO: make sure to encode it to json string
        successCallback.transferCustomConsentToUnity(it.toString())
    }

    override fun deleteCustomConsentTo(
        vendors: List<String>,
        categories: List<String>,
        legIntCategories: List<String>,
        success: (SPConsents?) -> Unit
    ) = launch {
        coordinator.deleteCustomConsentGDPR(
            vendors = vendors,
            categories = categories,
            legIntCategories = legIntCategories
        )
        success(spConsents)
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
    ) {
        // TODO: make sure to encode it to json string
        successCallback.transferCustomConsentToUnity(it.toString())
    }

    override fun rejectAll(campaignType: CampaignType) = launch {
        coordinator.reportAction(SPAction(type = RejectAll, campaignType = campaignType.toCore()))
        val consents = SPConsents(userData)
        spClient.onConsentReady(consents)
        spClient.onSpFinished(consents)
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
        pendingActions++
        messageUI.load(
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
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
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

    override fun handleOnBackPress(
        isMessageDismissible: Boolean,
        ottDelegate: SpBackPressOttDelegate
    ) {
        TODO("Not yet implemented")
    }

    override fun handleOnBackPress(isMessageDismissible: Boolean, onHomePage: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun clearLocalData() = coordinator.clearLocalData()

    override fun loaded(view: View) = spClient.onUIReady(view)

    override fun onAction(view: View, action: ConsentAction) = launch {
        val userAction = spClient.onAction(view, action) as SPConsentAction
        when(userAction.actionType) {
            ActionType.ACCEPT_ALL, ActionType.REJECT_ALL, ActionType.SAVE_AND_EXIT -> {
                coordinator.reportAction(userAction.toCore()) // TODO: think about a way to get the results of GET faster?
                spClient.onConsentReady(SPConsents(userData))
                pendingActions--
            }
            ActionType.CUSTOM, ActionType.MSG_CANCEL, ActionType.UNKNOWN-> {
                pendingActions--
            }
            ActionType.PM_DISMISS -> {
                if(messageUI.isFirstLayer) {
                    pendingActions--
                }
            }
            else -> {}
        }
        if(pendingActions == 0 && messagesToDisplay.isEmpty()) {
           spClient.onSpFinished(SPConsents(userData))
        }
    }

    override fun onError() {
        pendingActions = 0
        messagesToDisplay = ArrayDeque(emptyList())
        spClient.onError(Exception())
    }

    override fun finished(view: View) {
        spClient.onUIFinished(view)
        renderNextMessageIfAny()
    }
}

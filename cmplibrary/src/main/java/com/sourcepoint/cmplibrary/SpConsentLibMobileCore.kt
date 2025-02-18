package com.sourcepoint.cmplibrary

import android.R.id.content
import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import com.sourcepoint.cmplibrary.consent.CustomConsentClient
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.mobile_core.SPConsentWebView
import com.sourcepoint.cmplibrary.mobile_core.SPMessageUI
import com.sourcepoint.cmplibrary.mobile_core.SPMessageUIClient
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.ConsentActionImplOptimized
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.exposed.MessageType
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.util.SpBackPressOttDelegate
import com.sourcepoint.cmplibrary.util.extensions.toJsonObject
import com.sourcepoint.mobile_core.Coordinator
import com.sourcepoint.mobile_core.models.MessageToDisplay
import com.sourcepoint.mobile_core.models.SPAction
import com.sourcepoint.mobile_core.models.SPActionType
import com.sourcepoint.mobile_core.models.SPCampaignType
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
//    private val accountId: Int,
//    private val propertyId: Int,
//    private val propertyName: SPPropertyName,
//    private val campaigns: SPCampaigns,
    private val activity: WeakReference<Activity>,
    private val context: Context,
    private val coordinator: Coordinator,
    private val spClient: SpClient,
): SpConsentLib, SPMessageUIClient {
    private var messagesToDisplay: ArrayDeque<MessageToDisplay> = ArrayDeque(emptyList())
    private val currentMessage: SPMessageUI by lazy {
        SPConsentWebView(context = context, messageUIClient = this)
    }
    private val mainView: ViewGroup?
        get() = activity.get()?.findViewById(content)

    override fun loadMessage() {
        loadMessage(authId = null, pubData = null, cmpViewId = null)
    }

    override fun loadMessage(cmpViewId: Int) {
        loadMessage(authId = null, pubData = null, cmpViewId = cmpViewId)
    }

    override fun loadMessage(pubData: JSONObject?) {
        loadMessage(authId = null, pubData = pubData)
    }

    override fun loadMessage(authId: String?) {
        loadMessage(authId = authId, pubData = null)
    }

    override fun loadMessage(authId: String?, pubData: JSONObject?, cmpViewId: Int?) {
        launch {
            messagesToDisplay = ArrayDeque(coordinator.loadMessages(authId = authId, pubData = pubData?.toJsonObject()))
            renderNextMessageIfAny()
        }
    }

    private fun renderNextMessageIfAny() {
        if(messagesToDisplay.isEmpty()) {
            spClient.onSpFinished(SPConsents()) // TODO: convert coordinator.userData to SPConsents
        } else {
            runOnMain {
                currentMessage.load(message = messagesToDisplay.removeFirst(), consents = SPConsents())
            }
        }
    }

    override fun customConsentGDPR(
        vendors: List<String>,
        categories: List<String>,
        legIntCategories: List<String>,
        success: (SPConsents?) -> Unit
    ) {
        launch {
            coordinator.customConsentGDPR(
                vendors = vendors,
                categories = categories,
                legIntCategories = legIntCategories
            )
            success(SPConsents()) // TODO: convert coordinator.userData to SPConsents
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
            legIntCategories = legIntCategories.toList()
        ) {
            // TODO: make sure to encode it to json string
            successCallback.transferCustomConsentToUnity(it.toString())
        }
    }

    override fun deleteCustomConsentTo(
        vendors: List<String>,
        categories: List<String>,
        legIntCategories: List<String>,
        success: (SPConsents?) -> Unit
    ) {
        launch {
            coordinator.deleteCustomConsentGDPR(
                vendors = vendors,
                categories = categories,
                legIntCategories = legIntCategories
            )
            success(SPConsents()) // TODO: convert coordinator.userData to SPConsents
        }
    }

    override fun deleteCustomConsentTo(
        vendors: Array<String>,
        categories: Array<String>,
        legIntCategories: Array<String>,
        successCallback: CustomConsentClient
    ) {
        deleteCustomConsentTo(
            vendors = vendors.toList(),
            categories = categories.toList(),
            legIntCategories = legIntCategories.toList()
        ) {
            // TODO: make sure to encode it to json string
            successCallback.transferCustomConsentToUnity(it.toString())
        }
    }

    override fun rejectAll(campaignType: CampaignType) {
        launch {
            coordinator.reportAction(SPAction(
                type = SPActionType.RejectAll,
                campaignType = SPCampaignType.Gdpr // TODO: make sure to convert from native to mobile core
            ))
        }
    }

    override fun loadPrivacyManager(pmId: String, campaignType: CampaignType) {
        loadPrivacyManager(pmId = pmId, campaignType = campaignType)
    }

    override fun loadPrivacyManager(pmId: String, pmTab: PMTab, campaignType: CampaignType) {
        loadPrivacyManager(pmId = pmId, campaignType = campaignType, pmTab = pmTab)
    }

    override fun loadPrivacyManager(
        pmId: String,
        pmTab: PMTab,
        campaignType: CampaignType,
        useGroupPmIfAvailable: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override fun loadPrivacyManager(
        pmId: String,
        campaignType: CampaignType,
        messageType: MessageType
    ) {
        loadPrivacyManager(pmId = pmId, campaignType = campaignType, messageType = messageType)
    }

    override fun loadPrivacyManager(
        pmId: String,
        pmTab: PMTab,
        campaignType: CampaignType,
        messageType: MessageType
    ) {
        loadPrivacyManager(pmId = pmId, campaignType = campaignType, messageType = messageType, pmTab = pmTab)
    }

    override fun loadPrivacyManager(
        pmId: String,
        pmTab: PMTab,
        campaignType: CampaignType,
        useGroupPmIfAvailable: Boolean,
        messageType: MessageType
    ) {
        TODO("Not yet implemented")
    }

    override fun showView(view: View) {
        mainView?.let {
            it.post {
                view.layoutParams = ViewGroup.LayoutParams(0, 0)
                view.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                view.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                view.bringToFront()
                view.requestLayout()
                it.addView(view)
            }
        }
    }

    override fun removeView(view: View) {
        mainView?.let { viewGroup ->
            viewGroup.post { viewGroup.removeView(view) }
        }
    }

    override fun dispose() {
        TODO("Not yet implemented")
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

    override fun loaded(view: View) {
        spClient.onUIReady(view)
    }

    override fun onAction(view: View, action: ConsentAction) {
        launch {
            coordinator.reportAction((spClient.onAction(view, action) as ConsentActionImplOptimized).toCore())
        }
    }

    override fun onError() {
        spClient.onError(Exception())
    }

    override fun finished(view: View) {
        spClient.onUIFinished(view)
        renderNextMessageIfAny()
    }
}

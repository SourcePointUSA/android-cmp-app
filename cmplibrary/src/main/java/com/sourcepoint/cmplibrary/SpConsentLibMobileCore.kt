package com.sourcepoint.cmplibrary

import android.view.View
import com.sourcepoint.cmplibrary.consent.CustomConsentClient
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.exposed.MessageType
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.util.SpBackPressOttDelegate
import com.sourcepoint.cmplibrary.util.extensions.toJsonObject
import com.sourcepoint.mobile_core.Coordinator
import com.sourcepoint.mobile_core.models.SPAction
import com.sourcepoint.mobile_core.models.SPActionType
import com.sourcepoint.mobile_core.models.SPCampaignType
import com.sourcepoint.mobile_core.models.SPCampaigns
import com.sourcepoint.mobile_core.models.SPPropertyName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

fun launch(task: suspend () -> Unit) {
    CoroutineScope(Dispatchers.Default).launch { task() }
}

class SpConsentLibMobileCore(
//    private val accountId: Int,
//    private val propertyId: Int,
//    private val propertyName: SPPropertyName,
//    private val campaigns: SPCampaigns,
    private val coordinator: Coordinator,
    private val spClient: SpClient,
): SpConsentLib {
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
            val messages = coordinator.loadMessages(authId = authId, pubData = pubData?.toJsonObject())
            if(messages.isEmpty()) {
                spClient.onSpFinished(SPConsents()) // TODO: convert coordinator.userData to SPConsents
            } else {
                messages.forEach {
                    // TODO: display the message using spClient.onUIReady()
                }
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
        TODO("Not yet implemented")
    }

    override fun removeView(view: View) {
        TODO("Not yet implemented")
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
}

package com.sourcepoint.cmplibrary

import android.view.View
import com.sourcepoint.cmplibrary.consent.CustomConsentClient
import com.sourcepoint.cmplibrary.data.network.util.CampaignType
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.exposed.MessageType
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.util.SpBackPressOttDelegate
import org.json.JSONObject

interface SpConsentLib {
    /**
     * Instructs the SDK to dismiss the message when it intercepts a back press event from
     * within the message view. It's true by default.
     */
    val dismissMessageOnBackPress: Boolean

    /**
     * Load the First Layer Message (FLM)
     */
    fun loadMessage()

    /**
     * Load the First Layer Message (FLM) using a custom ViewId
     */
    fun loadMessage(cmpViewId: Int)

    /**
     * Load the First Layer Message (FLM)
     * @param authId is used to get an already saved consent
     * @param pubData is used to save some data in the BE using a JSON object
     */
    fun loadMessage(authId: String? = null, pubData: JSONObject? = null, cmpViewId: Int? = null)

    /**
     * Load the First Layer Message (FLM)
     * @param pubData is used to save some data in the BE using a JSON object
     */
    fun loadMessage(pubData: JSONObject? = null)

    /**
     * Load the First Layer Message (FLM)
     * @param authId is used to get an already saved consent
     */
    fun loadMessage(authId: String? = null)

    fun customConsentGDPR(
        vendors: List<String>,
        categories: List<String>,
        legIntCategories: List<String>,
        success: (SPConsents?) -> Unit,
    )

    fun deleteCustomConsentTo(
        vendors: List<String>,
        categories: List<String>,
        legIntCategories: List<String>,
        success: (SPConsents?) -> Unit,
    )

    fun customConsentGDPR(
        vendors: Array<String>,
        categories: Array<String>,
        legIntCategories: Array<String>,
        successCallback: CustomConsentClient
    )

    fun deleteCustomConsentTo(
        vendors: Array<String>,
        categories: Array<String>,
        legIntCategories: Array<String>,
        successCallback: CustomConsentClient
    )

    fun rejectAll(campaignType: CampaignType)

    fun loadPrivacyManager(pmId: String, campaignType: CampaignType)
    fun loadPrivacyManager(pmId: String, pmTab: PMTab, campaignType: CampaignType)
    fun loadPrivacyManager(pmId: String, pmTab: PMTab, campaignType: CampaignType, useGroupPmIfAvailable: Boolean)

    fun loadPrivacyManager(pmId: String, campaignType: CampaignType, messageType: MessageType)
    fun loadPrivacyManager(pmId: String, pmTab: PMTab, campaignType: CampaignType, messageType: MessageType)
    fun loadPrivacyManager(pmId: String, pmTab: PMTab, campaignType: CampaignType, useGroupPmIfAvailable: Boolean, messageType: MessageType)

    fun showView(view: View)
    fun removeView(view: View)

    fun dismissMessage()

    @Deprecated(message = "This method is deprecated and has no effect.")
    fun dispose()

    @Deprecated(
        message = "This method is deprecated and has no effect.",
        replaceWith = ReplaceWith(
            """
            Set dismissMessageOnBackPress = false if you wish to prevent the user from dismissing the message
            when the back button is pressed.
        """"
        )
    )
    fun handleOnBackPress(isMessageDismissible: Boolean = true, ottDelegate: SpBackPressOttDelegate)

    @Deprecated(
        message = "This method is deprecated and has no effect.",
        replaceWith = ReplaceWith(
            """
            Set dismissMessageOnBackPress = false if you wish to prevent the user from dismissing the message
            when the back button is pressed.
        """"
        )
    )
    fun handleOnBackPress(isMessageDismissible: Boolean = true, onHomePage: () -> Unit)

    fun clearLocalData()
}

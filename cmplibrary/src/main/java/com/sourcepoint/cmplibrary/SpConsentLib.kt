package com.sourcepoint.cmplibrary

import android.view.View
import com.sourcepoint.cmplibrary.consent.CustomConsentClient
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.exposed.MessageType
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.util.SpBackPressOttDelegate
import org.json.JSONObject

interface SpConsentLib {

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

    fun loadPrivacyManager(pmId: String, campaignType: CampaignType)
    fun loadPrivacyManager(pmId: String, pmTab: PMTab, campaignType: CampaignType)
    fun loadPrivacyManager(pmId: String, pmTab: PMTab, campaignType: CampaignType, useGroupPmIfAvailable: Boolean)

    fun loadPrivacyManager(pmId: String, campaignType: CampaignType, messageType: MessageType)
    fun loadPrivacyManager(pmId: String, pmTab: PMTab, campaignType: CampaignType, messageType: MessageType)
    fun loadPrivacyManager(pmId: String, pmTab: PMTab, campaignType: CampaignType, useGroupPmIfAvailable: Boolean, messageType: MessageType)

    fun showView(view: View)
    fun removeView(view: View)

    fun dispose()

    /**
     * Method that verifies home page and delegates navigation between the message view and the
     * activity that utilizes the message, using functional interface.
     *
     * Applicable for Java and Kotlin implementations.
     *
     * @param ottDelegate functional interface that provides the mechanism to override onBackPress
     */
    fun handleOnBackPress(
        isMessageDismissible: Boolean = true,
        ottDelegate: SpBackPressOttDelegate,
    )

    /**
     * Method that verifies home page and delegates navigation between the message view and the
     * activity that utilizes the message, using lambda.
     *
     * Applicable for Kotlin implementation.
     *
     * @param onHomePage lambda that provides the mechanism to override onBackPress
     */
    fun handleOnBackPress(
        isMessageDismissible: Boolean = true,
        onHomePage: () -> Unit,
    )
}

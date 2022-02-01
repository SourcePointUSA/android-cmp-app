package com.sourcepoint.cmplibrary

import android.view.View
import com.sourcepoint.cmplibrary.consent.CustomConsentClient
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import org.json.JSONObject

interface SpConsentLib {

    /**
     * Load the First Layer Message (FLM)
     */
    fun loadMessage()

    /**
     * Load the First Layer Message (FLM)
     * @param authId is used to get an already saved consent
     * @param pubData is used to save some data in the BE using a JSON object
     */
    fun loadMessage(authId: String? = null, pubData: JSONObject? = null)

    fun customConsentGDPR(
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

    fun loadPrivacyManager(pmId: String, pmTab: PMTab, campaignType: CampaignType)
    fun loadOTTPrivacyManager(pmId: String, campaignType: CampaignType)

    fun showView(view: View)
    fun removeView(view: View)

    fun dispose()
}

package com.sourcepointmeta.metaapp

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.example.uitestutil.* // ktlint-disable
import com.sourcepointmeta.metaapp.TestData.ACCEPT
import com.sourcepointmeta.metaapp.TestData.ACCEPT_ALL
import com.sourcepointmeta.metaapp.TestData.CANCEL
import com.sourcepointmeta.metaapp.TestData.CCPA_CONSENT_LIST
import com.sourcepointmeta.metaapp.TestData.GDPR_CONSENT_LIST_2
import com.sourcepointmeta.metaapp.TestData.OPTIONS
import com.sourcepointmeta.metaapp.TestData.REJECT
import com.sourcepointmeta.metaapp.TestData.TITLE
import com.sourcepointmeta.metaapp.TestData.VENDORS_LIST_2
import com.sourcepointmeta.metaapp.data.localdatasource.toValueDB
import com.sourcepointmeta.metaapp.db.MetaAppDB
import com.sourcepointmeta.metaapp.ui.component.PropertyAdapter
import java.util.*  // ktlint-disable

class TestUseCaseMeta {
    companion object {

        fun checkMessageDisplayed() {
            isDisplayedAllOfByResId(resId = R.id.message)
        }

        fun tapFab() {
            performClickById(R.id.fab)
        }

        fun tapOptionWebView() {
            performClickOnWebViewByContent(OPTIONS)
        }

        fun tapCancelOnWebView() {
            performClickOnWebViewByContent(CANCEL)
        }

        fun checkWebViewDisplayedGDPRFirstLayerMessage() {
            checkTextInParagraph(TITLE)
        }

        fun tapAcceptAllOnWebView() {
            performClickOnWebViewByContent(ACCEPT_ALL)
        }

        fun tapPartnersOnWebView() {
            performClickOnLabelWebViewByContent("Partners")
        }

        fun tapFeaturesOnWebView() {
            performClickOnLabelWebViewByContent("Features")
        }

        fun tapPurposesOnWebView() {
            performClickOnLabelWebViewByContent("Purposes")
        }

        fun checkDeepLinkDisplayed() {
            isDisplayedAllOfByResId(resId = R.id.dl_tv)
        }

        fun checkGdprNativeTitle() {
            isDisplayedByResIdByText(resId = R.id.dl_tv, text = "GDPR Lorem Ipsum")
        }

        fun checkCcpaNativeTitle() {
            isDisplayedByResIdByText(resId = R.id.dl_tv, text = "CCPA Lorem Ipsum")
        }

        fun tapMetaDeepLinkOnWebView() {
            performClickOnLabelWebViewByContent("metanetwork")
        }

        fun tapNmDismiss() {
            performClickById(R.id.cancel)
        }

        fun tapNmAcceptAll() {
            performClickById(R.id.accept_all)
        }

        fun tapShowPmBtn() {
            performClickById(R.id.review_consents_gdpr_fr)
        }

        fun addTestProperty() {
            addProperty(
                propertyName = "mobile.multicampaign.demo",
                propertyId = 16893,
                accountId = "22",
                gdprPmId = "488393",
                ccpaPmId = "509688",
                gdprTps = listOf(Pair("a", "a"), Pair("b", "b")),
                ccpaTps = listOf(Pair("c", "c"))
            )
        }

        fun saveProperty() = scrollAndPerformClickById(R.id.save_btn)

        fun addProperty(
            propertyName: String,
            propertyId: Int,
            accountId: String,
            gdprPmId: String,
            ccpaPmId: String? = null,
            autId: String? = null,
            gdprTps: List<Pair<String, String>>? = null,
            ccpaTps: List<Pair<String, String>>? = null
        ) {
            addTextById(R.id.prop_id_ed, propertyId.toString())
            addTextById(R.id.prop_name_ed, propertyName)
            addTextById(R.id.account_id_ed, accountId)
            addTextById(R.id.gdpr_pm_id_ed, gdprPmId)
            ccpaPmId?.let { addTextById(R.id.ccpa_pm_id_ed, it) }
            autId?.let { addTextById(R.id.auth_id_ed, it) }
            gdprTps?.let {
                it.forEach { tp ->
                    scrollAndPerformClickById(R.id.btn_targeting_params_gdpr)
                    addTextById(R.id.tp_key_ed, tp.first)
                    addTextById(R.id.tp_value_et, tp.second)
                    pressAlertDialogBtn("CREATE")
                }
            }
            ccpaTps?.let {
                it.forEach { tp ->
                    scrollAndPerformClickById(R.id.btn_targeting_params_ccpa)
                    addTextById(R.id.tp_key_ed, tp.first)
                    addTextById(R.id.tp_value_et, tp.second)
                    pressAlertDialogBtn("CREATE")
                }
            }
        }

        fun clickFirstItem() {
            clickListItem<PropertyAdapter.Vh>(0, R.id.property_list)
        }

        fun runDemo() {
            clickElementListItem<PropertyAdapter.Vh>(R.id.play_demo_btn, R.id.property_list)
        }

        fun checkNumberOfNullMessage(position: Int = 1) {
            checkElementListItem<PropertyAdapter.Vh>(
                resId = R.id.log_body_1,
                content = "parsed campaigns\n" + "1 Null messages\n" + "0 Not Null message",
                recyclerViewId = R.id.log_list,
                position = position
            )
        }

        fun checkOnConsentReady(position: Int = 0) {
            checkElementListItem<PropertyAdapter.Vh>(
                resId = R.id.log_body,
                content = "onConsentReady",
                recyclerViewId = R.id.log_list,
                position = position
            )
        }

        fun checkOnSpFinish(position: Int = 0) {
            checkElementListItem<PropertyAdapter.Vh>(
                resId = R.id.log_body,
                content = "All campaigns have been processed.",
                recyclerViewId = R.id.log_list,
                position = position
            )
        }

        fun swipeLeftPager() {
            Espresso.onView(ViewMatchers.withId(R.id.pager))
                .perform(ViewActions.swipeLeft())
        }

        fun tapAcceptOnWebView() {
            performClickOnWebViewByContent(ACCEPT)
        }

        fun clickOnGdprReviewConsent() {
            performClickById(resId = R.id.review_consents_gdpr_fr)
        }

        fun clickOnCcpaReviewConsent() {
            performClickById(resId = R.id.review_consents_ccpa_fr)
        }

        fun checkAllGdprConsentsOn() {
            GDPR_CONSENT_LIST_2.forEach { consent ->
                checkConsentState(consent, true)
            }
        }

        fun checkAllVendorsOff() {
            VENDORS_LIST_2.forEach { consent ->
                checkConsentState(consent, false)
            }
        }

        fun checkFeaturesTab() {
            checkTextInParagraph("Features are a use of the data that you have already agreed to share with us")
        }

        fun checkPurposesTab() {
            checkTextInParagraph("You give an affirmative action to indicate that we can use your data for this purpose.")
        }

        fun checkAllCcpaConsentsOn() {
            CCPA_CONSENT_LIST.forEach { consent ->
                checkConsentState(consent, true)
            }
        }

        fun tapRejectOnWebView() {
            performClickOnWebViewByContent(REJECT)
        }

        fun MetaAppDB.addTestProperty(autId: String? = null, gdprEnabled: Boolean = true, ccpaEnabled: Boolean = false) {
            campaignQueries.insertProperty(
                property_name = "mobile.multicampaign.demo",
                account_id = 22,
                gdpr_pm_id = 488393L,
                ccpa_pm_id = 509688L,
                usnat_pm_id = 509688L,
                campaign_env = "prod",
                timeout = 3000L,
                timestamp = Date().time,
                is_staging = 0,
                message_type = "WebView",
                auth_Id = autId,
                pm_tab = "PURPOSES",
                message_language = "ENGLISH",
                group_pm_id = null,
                use_gdpr_groupid_if_available = null,
                property_id = null,
                preloading = null
            )
            campaignQueries.insertStatusCampaign(
                property_name = "mobile.multicampaign.demo",
                campaign_type = "GDPR",
                enabled = gdprEnabled.toValueDB()
            )
            campaignQueries.insertStatusCampaign(
                property_name = "mobile.multicampaign.demo",
                campaign_type = "CCPA",
                enabled = ccpaEnabled.toValueDB()
            )
        }

        fun MetaAppDB.addNativeTestProperty(autId: String? = null, gdprEnabled: Boolean = true, ccpaEnabled: Boolean = false) {
            campaignQueries.insertProperty(
                property_name = "mobile.multicampaign.fully.native",
                account_id = 22,
                gdpr_pm_id = 594218L,
                ccpa_pm_id = 594219L,
                usnat_pm_id = 594219L,
                campaign_env = "prod",
                timeout = 3000L,
                timestamp = Date().time,
                is_staging = 0,
                message_type = "WebView",
                auth_Id = autId,
                pm_tab = "PURPOSES",
                message_language = "ENGLISH",
                group_pm_id = null,
                use_gdpr_groupid_if_available = null,
                property_id = null,
                preloading = null
            )
            campaignQueries.insertStatusCampaign(
                property_name = "mobile.multicampaign.fully.native",
                campaign_type = "GDPR",
                enabled = gdprEnabled.toValueDB()
            )
            campaignQueries.insertStatusCampaign(
                property_name = "mobile.multicampaign.fully.native",
                campaign_type = "CCPA",
                enabled = ccpaEnabled.toValueDB()
            )
        }

        enum class CampEnv { prod, stage }

        fun MetaAppDB.addProperty(
            propertyName: String = "mobile.multicampaign.native.demo2",
            autId: String? = null,
            gdprEnabled: Boolean = true,
            ccpaEnabled: Boolean = false,
            campEnv: CampEnv = CampEnv.prod,
            gdprPmId: Long = 548285L,
            ccpaPmId: Long = 0L
        ) {
            campaignQueries.insertProperty(
                property_name = propertyName,
                account_id = 22,
                gdpr_pm_id = gdprPmId,
                ccpa_pm_id = ccpaPmId,
                usnat_pm_id = ccpaPmId,
                campaign_env = campEnv.name,
                timeout = 3000L,
                timestamp = Date().time,
                is_staging = 0,
                message_type = "WebView",
                auth_Id = autId,
                pm_tab = "PURPOSES",
                message_language = "ENGLISH",
                group_pm_id = null,
                use_gdpr_groupid_if_available = null,
                property_id = null,
                preloading = null
            )
            campaignQueries.insertStatusCampaign(
                property_name = propertyName,
                campaign_type = "GDPR",
                enabled = gdprEnabled.toValueDB()
            )
            campaignQueries.insertStatusCampaign(
                property_name = propertyName,
                campaign_type = "CCPA",
                enabled = ccpaEnabled.toValueDB()
            )
        }
    }
}

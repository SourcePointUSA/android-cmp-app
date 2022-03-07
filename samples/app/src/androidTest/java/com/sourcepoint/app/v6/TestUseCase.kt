package com.sourcepoint.app.v6

import android.webkit.CookieManager
import com.example.uitestutil.*
import com.sourcepoint.app.v6.TestData.ACCEPT
import com.sourcepoint.app.v6.TestData.ACCEPT_ALL
import com.sourcepoint.app.v6.TestData.CCPA_CONSENT_LIST
import com.sourcepoint.app.v6.TestData.CONSENT_LIST
import com.sourcepoint.app.v6.TestData.CONSENT_LIST_2
import com.sourcepoint.app.v6.TestData.FEATURES
import com.sourcepoint.app.v6.TestData.MESSAGE
import com.sourcepoint.app.v6.TestData.NETWORK
import com.sourcepoint.app.v6.TestData.OPTIONS
import com.sourcepoint.app.v6.TestData.PARTIAL_CONSENT_LIST
import com.sourcepoint.app.v6.TestData.PRIVACY_MANAGER
import com.sourcepoint.app.v6.TestData.PURPOSES
import com.sourcepoint.app.v6.TestData.REJECT
import com.sourcepoint.app.v6.TestData.REJECT_ALL
import com.sourcepoint.app.v6.TestData.SAVE_AND_EXIT
import com.sourcepoint.app.v6.TestData.SETTINGS_DE
import com.sourcepoint.app.v6.TestData.SITE_VENDORS
import com.sourcepoint.app.v6.TestData.VENDORS_LIST
import com.sourcepoint.app.v6.core.DataProvider
import com.sourcepoint.app.v6.di.customCategoriesDataProd
import com.sourcepoint.app.v6.di.customVendorDataListProd
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import kotlinx.android.synthetic.main.activity_main_consent.*
import org.koin.core.module.Module
import org.koin.dsl.module

class TestUseCase {

    companion object {

        fun checkConsentIsNotSelected() {
            CONSENT_LIST.forEach { consent ->
                checkConsentState(consent, false)
            }
        }

        fun checkConsentIsSelected() {
            CONSENT_LIST.forEach { consent ->
                checkConsentState(consent, true)
            }
        }

        fun tapToEnableAllConsent() {
            CONSENT_LIST_2.forEach { consent ->
                tapOnToggle(property = consent, tapOnlyWhen = false)
            }
        }

        fun tapAllConsent() {
            CONSENT_LIST_2.forEach { consent ->
                tapOnToggle(property = consent)
            }
        }

        fun tapToDisableAllConsent() {
            CONSENT_LIST_2.forEach { consent ->
                tapOnToggle(property = consent, tapOnlyWhen = true)
            }
        }

        fun checkAllConsentsOn() {
            CONSENT_LIST_2.forEach { consent ->
                checkConsentState(consent, true)
            }
        }

        fun checkAllConsentsOff() {
            CONSENT_LIST_2.forEach { consent ->
                checkConsentState(consent, false)
            }
        }

        fun checkAllCcpaConsentsOn() {
            CCPA_CONSENT_LIST.forEach { consent ->
                checkConsentState(consent, true)
            }
        }

        fun checkCustomCategoriesData() {
            // the customCategoriesData elements are enabled
            customCategoriesDataProd.map { it.second }.forEach { consent ->
                checkConsentState(consent, true)
            }
            // all CONSENT_LIST_2 elements are disabled except the customCategoriesData
            CONSENT_LIST_2.subtract(customCategoriesDataProd.map { it.second }).forEach { consent ->
                checkConsentState(consent, false)
            }
        }

        fun checkCustomVendorDataList() {
            // the customVendorDataList elements are enabled
            customVendorDataListProd.map { it.second }.forEach { consent ->
                checkConsentState(consent, true)
            }
            // all CONSENT_LIST_2 elements are disabled except the customCategoriesData
            VENDORS_LIST.subtract(customVendorDataListProd.map { it.second }).forEach { consent ->
                checkConsentState(consent, false)
            }
        }

        fun checkMainWebViewDisplayed() {
            isDisplayedAllOfByResId(resId = R.id.review_consents_gdpr)
        }

        fun checkDeepLinkDisplayed() {
            isDisplayedAllOfByResId(resId = R.id.app_dl_tv)
        }

        fun clickOnGdprReviewConsent() {
            performClickById(resId = R.id.review_consents_gdpr)
        }

        fun checkGdprNativeTitle() {
            isDisplayedByResIdByText(resId = R.id.title_nm, text = "GDPR Lorem Ipsum")
        }

        fun tapNmAcceptAll() {
            performClickById(R.id.accept_all)
        }

        fun clickOnCustomConsent() {
            performClickById(resId = R.id.custom_consent)
        }

        fun clickOnCcpaReviewConsent() {
            performClickById(resId = R.id.review_consents_ccpa)
        }

        fun clickOnConsentActivity() {
            performClickById(resId = R.id.consent_btn)
        }

        fun openAuthIdActivity() {
            performClickByIdCompletelyDisplayed(resId = R.id.auth_id_activity)
        }

        fun checkAuthIdIsDisplayed(autId : String) {
            checkElementWithText("authId", autId)
        }

        fun checkUUID(uuid : String) {
            containsText(R.id.consent_uuid, uuid)
        }

        fun checkAuthIdIsNotDisplayed() {
            checkElementWithText("authId", "no_auth_id")
        }

        fun checkWebViewDisplayedForMessage() {
            checkWebViewHasText(MESSAGE)
        }

        fun clickPMTabSelectedPurposes() {
            performClickPMTabSelected(PURPOSES)
        }

        fun clickPMTabSelectedFeatures() {
            performClickPMTabSelected(FEATURES)
        }

        fun tapOptionWebView() {
            performClickOnWebViewByContent(OPTIONS)
        }

        fun tapSiteVendorsWebView() {
            performClickPMTabSelected(SITE_VENDORS)
        }

        fun tapAcceptAllOnWebView() {
            performClickOnWebViewByContent(ACCEPT_ALL)
        }

        fun tapNetworkOnWebView() {
            performClickOnLabelWebViewByContent(NETWORK)
        }

        fun tapRejectOnWebView() {
            performClickOnWebViewByContent(REJECT)
        }

        fun tapSaveAndExitWebView() {
            performClickOnWebViewByContent(SAVE_AND_EXIT)
        }

        fun tapRejectAllWebView() {
            performClickOnWebViewByContent(REJECT_ALL)
        }

        fun tapDismissWebView() {
            performClickOnWebViewByClass("message-stacksclose")
        }

        fun tapAcceptOnWebView() {
            performClickOnWebViewByContent(ACCEPT)
        }

        fun tapSettingsOnWebViewDE() {
            performClickOnWebViewByContent(SETTINGS_DE)
        }

        fun tapAcceptCcpaOnWebView() {
            performClickOnWebViewByContent(ACCEPT_ALL)
        }

        fun setFocusOnLayoutActivity() {
            performClickByIdCompletelyDisplayed(resId = R.id.main_view)
        }

        fun checkWebViewDisplayedForPrivacyManager() {
            checkWebViewHasText(PRIVACY_MANAGER)
        }

        fun checkPartialConsentIsSelected() {
            PARTIAL_CONSENT_LIST.forEach { consent ->
                checkConsentState(consent, true)
            }
        }

        fun checkPartialConsentIsNotSelected() {
            PARTIAL_CONSENT_LIST.forEach { consent ->
                checkConsentState(consent, false)
            }
        }

        fun checkConsentAsSelectedFromPartialConsentList() {
            PARTIAL_CONSENT_LIST.forEach { consent ->
                tapOnToggle(consent)
            }
        }
        fun checkConsentAsSelectedConsentList() {
            CONSENT_LIST.forEach { consent ->
                tapOnToggle(consent)
            }
        }

        fun setConsent() {
            CONSENT_LIST.forEach { consent ->
                tapOnToggle(consent)
            }
        }

        fun checkPMTabSelectedFeatures() {
            checkPMTabSelected(FEATURES)
        }

        fun checkPMTabSelectedPurposes() {
            checkPMTabSelected(PURPOSES)
        }

        fun selectPartialConsentList() {
            PARTIAL_CONSENT_LIST.forEach { consent ->
                checkConsentWebView(consent)
            }
        }

        fun checkCookieExist(url : String, value : String){
            CookieManager.getInstance()
                .getCookie(url)
                .contains(value)
                .assertTrue()
        }

        fun checkCookieNotExist(url : String){
            CookieManager.getInstance()
                .getCookie(url)
                .contains("authId=")
                .assertFalse()
        }

        fun mockModule(
            spConfig: SpConfig,
            gdprPmId: String,
            ccpaPmId: String = "",
            uuid: String? = null,
            url: String = "",
            isOtt: Boolean = false,
            pResetAll: Boolean = true,
            spClientObserver: List<SpClient> = emptyList()
        ): Module {
            return module(override = true) {
                single<List<SpClient?>> { spClientObserver }
                single<DataProvider> {
                    object : DataProvider {
                        override val authId = uuid
                        override val resetAll = pResetAll
                        override val isOtt = isOtt
                        override val url = url
                        override val spConfig: SpConfig = spConfig
                        override val gdprPmId: String = gdprPmId
                        override val ccpaPmId: String = ccpaPmId
                        override val customVendorList: List<String> = customVendorDataListProd.map { it.first }
                        override val customCategories: List<String> = customCategoriesDataProd.map { it.first }
                    }
                }
            }
        }
    }
}
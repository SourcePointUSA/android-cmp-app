package com.sourcepoint.app.v6

import android.webkit.CookieManager
import com.example.uitestutil.* //ktlint-disable
import com.sourcepoint.app.v6.TestData.ACCEPT
import com.sourcepoint.app.v6.TestData.ACCEPT_ALL
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
import com.sourcepoint.app.v6.TestData.SITE_VENDORS
import com.sourcepoint.app.v6.TestData.VENDORS_LIST
import com.sourcepoint.app.v6.di.customCategoriesDataProd
import com.sourcepoint.app.v6.di.customVendorDataListProd

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

        fun clickOnCustomConsent() {
            performClickById(resId = R.id.custom_consent)
        }

        fun clickOnCcpaReviewConsent() {
            performClickById(resId = R.id.review_consents_ccpa)
        }

        fun openAuthIdActivity() {
            performClickByIdCompletelyDisplayed(resId = R.id.auth_id_activity)
        }

        fun checkAuthIdIsDisplayed(autId : String) {
            checkElementWithText("authId", autId)
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
    }
}
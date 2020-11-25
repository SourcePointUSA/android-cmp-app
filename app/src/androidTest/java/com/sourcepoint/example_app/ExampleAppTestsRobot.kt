package com.sourcepoint.example_app

import androidx.test.espresso.web.sugar.Web
import androidx.test.espresso.web.webdriver.DriverAtoms
import androidx.test.espresso.web.webdriver.Locator
import com.sourcepoint.example_app.TestData.*

class ExampleAppTestsRobot {

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

        fun checkMainWebViewDisplayed() {
            isDisplayedAllOfByResId(resId = R.id.review_consents)
        }

        fun clickOnReviewConsent() {
            performClickById(resId = R.id.review_consents)
        }

        fun openAuthIdActivity() {
            performClickById(resId = R.id.auth_id_activity)
        }

        fun checkWebViewDisplayedForMessage() {
            checkWebViewHasText(MESSAGE)
        }

        fun checkAuthIdIsDisplayed(autId : String) {
            checkElementWithText("authId", autId)
        }

        fun checkAuthIdIsNotDisplayed() {
            checkElementWithText("authId", "no_auth_id")
        }

        fun clickPMTabSelectedPurposes() {
            performClickPMTabSelected(PURPOSES)
        }

        fun tapOptionWebView() {
            performClickOnWebViewByContent(OPTIONS)
        }

        fun tapAcceptAllOnWebView() {
            performClickOnWebViewByContent(ACCEPT_ALL)
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

        fun setFocusOnLayoutActivity() {
            performClickById(resId = R.id.main_view)
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
                setCheckBoxTrue(consent)
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
    }

    suspend fun checkWebViewDisplayedForMessage(delay: Long = 0) = apply {
        wr(delay) {
            checkWebViewHasText(MESSAGE)
        }
    }

    suspend fun tapRejectOnWebView(delay: Long = 0) = apply {
        wr(delay) {
            performClickOnWebViewByContent(REJECT)
        }
    }

    suspend fun tapOptionWebView(delay: Long = 0) = apply {
        wr(delay) {
            performClickOnWebViewByContent(OPTIONS)
        }
    }

    suspend fun tapSaveAndExitWebView(delay: Long = 0) = apply {
        wr(delay) {
            performClickOnWebViewByContent(SAVE_AND_EXIT)
        }
    }

    suspend fun tapRejectAllWebView(delay: Long = 0) = apply {
        wr(delay) {
            performClickOnWebViewByContent(REJECT_ALL)
        }
    }

    suspend fun tapDismissWebView() = apply {
        wr {
            performClickOnWebViewByClass("message-stacksclose")
        }
    }

    suspend fun tapAcceptAllOnWebView(delay: Long = 0) = apply {
        wr(delay) {
            performClickOnWebViewByContent(ACCEPT_ALL)
        }

    }

    suspend fun tapAcceptOnWebView(delay: Long = 0) = apply {
        wr(delay) {
            performClickOnWebViewByContent(ACCEPT)
        }

    }

    suspend fun checkMainWebViewDisplayed(delay: Long = 0) = apply {
        wr(delay) {
            isDisplayedAllOfByResId(resId = R.id.review_consents)
        }
    }

    suspend fun setFocusOnLayoutActivity(delay: Long = 0) = apply {
        wr(delay) {
            performClickById(resId = R.id.main_view)
        }
    }

    suspend fun clickOnReviewConsent(delay: Long = 0) = apply {
        wr(delay) {
            performClickById(resId = R.id.review_consents)
        }
    }

    suspend fun checkWebViewDisplayedForPrivacyManager(delay: Long = 0) = apply {
        wr(delay) {
            checkWebViewHasText(PRIVACY_MANAGER)
        }
    }

    suspend fun checkConsentIsSelected(delay: Long = 0) = apply {
        wr(delay) {
            CONSENT_LIST.forEach { consent ->
                checkConsentState(consent, true)
            }
        }
    }

    suspend fun checkConsentIsNotSelected(delay: Long = 0) = apply {
        wr(delay) {
            CONSENT_LIST.forEach { consent ->
                checkConsentState(consent, false)
            }
        }
    }

    suspend fun checkPartialConsentIsSelected(delay: Long = 0) = apply {
        wr(delay) {
            PARTIAL_CONSENT_LIST.forEach { consent ->
                checkConsentState(consent, true)
            }
        }
    }

    suspend fun checkPartialConsentIsNotSelected() = apply {
        wr {
            PARTIAL_CONSENT_LIST.forEach { consent ->
                checkConsentState(consent, false)
            }
        }
    }

    suspend fun checkConsentAsSelectedFromPartialConsentList(delay: Long = 0) = apply {
        wr(delay) {
            PARTIAL_CONSENT_LIST.forEach { consent ->
//                setCheckBoxTrue(consent)
                Web.onWebView()
                    .withElement(DriverAtoms.findElement(Locator.XPATH, "//label[@aria-label='$consent']/span[@class='on']"))
                    .perform(DriverAtoms.webScrollIntoView())
                    .perform(DriverAtoms.webClick())
            }
        }
    }

    suspend fun checkPMTabSelectedFeatures(delay: Long = 0) = apply {
        wr(delay) {
            checkPMTabSelected(FEATURES)
        }
    }

    suspend fun checkPMTabSelectedPurposes() = apply {
        wr {
            checkPMTabSelected(PURPOSES)
        }
    }

    suspend fun clickPMTabSelectedPurposes(delay: Long = 0) = apply {
        wr(delay) {
            performClickPMTabSelected(PURPOSES)
        }
    }

    suspend fun selectPartialConsentList() = apply {
        PARTIAL_CONSENT_LIST.forEach { consent ->
            wr {
                checkConsentWebView(consent)
            }
        }
    }

}
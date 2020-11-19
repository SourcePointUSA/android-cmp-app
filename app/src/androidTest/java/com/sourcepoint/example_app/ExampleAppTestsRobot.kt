package com.sourcepoint.example_app

import androidx.test.espresso.web.sugar.Web
import androidx.test.espresso.web.webdriver.DriverAtoms
import androidx.test.espresso.web.webdriver.Locator
import com.sourcepoint.example_app.TestData.*

class ExampleAppTestsRobot {

    suspend fun checkWebViewDisplayedForMessage(delay : Long = 0) = apply {
        waitAndRetry(delay) {
            checkWebViewHasText(MESSAGE)
        }
    }

    suspend fun tapRejectOnWebView(delay : Long = 0) = apply {
        waitAndRetry(delay) {
            performClickOnWebViewByContent(REJECT)
        }
    }

    suspend fun tapOptionWebView(delay : Long = 0) = apply {
        waitAndRetry(delay) {
            performClickOnWebViewByContent(OPTIONS)
        }
    }

    suspend fun tapSaveAndExitWebView(delay : Long = 0) = apply {
        waitAndRetry(delay) {
            performClickOnWebViewByContent(SAVE_AND_EXIT)
        }
    }

    suspend fun tapRejectAllWebView(delay : Long = 0) = apply {
        waitAndRetry(delay) {
            performClickOnWebViewByContent(REJECT_ALL)
        }
    }

    suspend fun tapDismissWebView() = apply {
        waitAndRetry {
            performClickOnWebViewByClass("message-stacksclose")
        }
    }

    suspend fun tapAcceptAllOnWebView(delay : Long = 0) = apply {
        waitAndRetry(delay) {
            performClickOnWebViewByContent(ACCEPT_ALL)
        }

    }

    suspend fun tapAcceptOnWebView(delay : Long = 0) = apply {
        waitAndRetry(delay) {
            performClickOnWebViewByContent(ACCEPT)
        }

    }

    suspend fun checkMainWebViewDisplayed(delay : Long = 0) = apply {
        waitAndRetry(delay) {
            isDisplayedAllOfByResId(resId = R.id.review_consents)
        }
    }

    suspend fun setFocusOnLayoutActivity(delay : Long = 0) = apply {
            waitAndRetry(delay) {
                performClickById(resId = R.id.main_view)
            }
    }

    suspend fun clickOnReviewConsent(delay : Long = 0) = apply {
            waitAndRetry(delay) {
                performClickById(resId = R.id.review_consents)
            }
    }

    suspend fun checkWebViewDisplayedForPrivacyManager(delay : Long = 0) = apply {
        waitAndRetry(delay) {
            checkWebViewHasText(PRIVACY_MANAGER)
        }
    }

    suspend fun checkConsentIsSelected(delay : Long = 0) = apply {
        waitAndRetry(delay) {
            CONSENT_LIST.forEach { consent ->
                checkConsentState(consent, true)
            }
        }
    }

    suspend fun checkConsentIsNotSelected(delay : Long = 0) = apply {
        waitAndRetry(delay) {
            CONSENT_LIST.forEach { consent ->
                checkConsentState(consent, false)
            }
        }
    }

    suspend fun checkPartialConsentIsSelected(delay : Long = 0) = apply {
        waitAndRetry(delay) {
            PARTIAL_CONSENT_LIST.forEach { consent ->
                checkConsentState(consent, true)
            }
        }
    }

    suspend fun checkPartialConsentIsNotSelected() = apply {
        waitAndRetry {
            PARTIAL_CONSENT_LIST.forEach { consent ->
                checkConsentState(consent, false)
            }
        }
    }

    suspend fun checkConsentAsSelectedFromPartialConsentList(delay : Long = 0) = apply {
        waitAndRetry(delay) {
            PARTIAL_CONSENT_LIST.forEach { consent ->
//                setCheckBoxTrue(consent)
                Web.onWebView()
                    .forceJavascriptEnabled()
                    .withElement(DriverAtoms.findElement(Locator.XPATH, "//label[@aria-label='$consent']/span[@class='on']"))
                    .perform(DriverAtoms.webScrollIntoView())
                    .perform(DriverAtoms.webClick())
            }
        }
    }

    suspend fun checkPMTabSelectedFeatures(delay : Long = 0) = apply {
        waitAndRetry(delay) {
            checkPMTabSelected(FEATURES)
        }
    }

    suspend fun checkPMTabSelectedPurposes() = apply {
        waitAndRetry {
            checkPMTabSelected(PURPOSES)
        }
    }

    suspend fun clickPMTabSelectedPurposes(delay : Long = 0) = apply {
        waitAndRetry(delay) {
            performClickPMTabSelected(PURPOSES)
        }
    }

    suspend fun selectPartialConsentList() = apply {
        PARTIAL_CONSENT_LIST.forEach { consent ->
            waitAndRetry {
                checkConsentWebView(consent)
            }
        }
    }

}
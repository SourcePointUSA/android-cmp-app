package com.sourcepoint.example_app

import androidx.test.espresso.web.sugar.Web
import androidx.test.espresso.web.webdriver.DriverAtoms
import androidx.test.espresso.web.webdriver.Locator
import com.sourcepoint.example_app.TestData.*

class ExampleAppTestsRobot {

    suspend fun checkWebViewDisplayedForMessage() = apply {
        waitAndRetry {
            checkWebViewHasText(TestData.MESSAGE)
        }
    }

    suspend fun tapRejectOnWebView(delayExecution : Long = 0) = apply {
        waitAndRetry(delayExecution) {
            performClickOnWebViewByContent(TestData.REJECT)
        }
    }

    suspend fun tapOptionWebView() = apply {
        waitAndRetry {
            performClickOnWebViewByContent(TestData.OPTIONS)
        }
    }

    suspend fun tapSaveAndExitWebView(delayExecution : Long = 0) = apply {
        waitAndRetry(delayExecution) {
            performClickOnWebViewByContent(TestData.SAVE_AND_EXIT)
        }
    }

    suspend fun tapRejectAllWebView() = apply {
        waitAndRetry {
            performClickOnWebViewByContent(TestData.REJECT_ALL)
        }
    }

    suspend fun tapAcceptAllOnWebView(delayExecution : Long = 0) = apply {
        waitAndRetry(delayExecution) {
            performClickOnWebViewByContent(TestData.ACCEPT_ALL)
        }

    }

    suspend fun checkMainWebViewDisplayed(delayExecution : Long = 0) = apply {
        waitAndRetry(delayExecution) {
            isDisplayedAllOfByResId(resId = R.id.review_consents)
        }
    }

    suspend fun clickOnReviewConsent(delayExecution : Long = 0) = apply {
            waitAndRetry(delayExecution) {
                performClickById(resId = R.id.review_consents)
            }
    }

    suspend fun checkWebViewDisplayedForPrivacyManager(delayBeforeExecute : Long = 0) = apply {
        waitAndRetry(delayBeforeExecute) {
            checkWebViewHasText(TestData.PRIVACY_MANAGER)
        }
    }

    suspend fun checkConsentAsSelectedFromConsentList() = apply {
        waitAndRetry {
            CONSENT_LIST.forEach { consent ->
                checkConsentAsSelected(consent)
            }
        }
    }

    suspend fun checkConsentAsSelectedFromPartialConsentList(delayBeforeExecute : Long = 0) = apply {
        waitAndRetry(delayBeforeExecute) {
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

    suspend fun checkPMTabSelectedFeatures() = apply {
        waitAndRetry {
            checkPMTabSelected(TestData.FEATURES)
        }
    }

    suspend fun checkPMTabSelectedPurposes() = apply {
        waitAndRetry {
            checkPMTabSelected(TestData.PURPOSES)
        }
    }

    suspend fun clickPMTabSelectedPurposes() = apply {
        waitAndRetry {
            performClickPMTabSelected(TestData.PURPOSES)
        }
    }

    suspend fun selectPartialConsentList() = apply {
        TestData.PARTIAL_CONSENT_LIST.forEach { consent ->
            waitAndRetry {
                checkConsentWebView(consent)
            }
        }
    }

}
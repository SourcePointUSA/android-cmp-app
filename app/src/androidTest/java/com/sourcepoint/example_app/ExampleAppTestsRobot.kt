package com.sourcepoint.example_app

import androidx.test.espresso.web.sugar.Web
import androidx.test.espresso.web.webdriver.DriverAtoms
import androidx.test.espresso.web.webdriver.Locator
import com.sourcepoint.example_app.TestData.*

class ExampleAppTestsRobot {

    suspend fun checkWebViewDisplayedForMessage() = apply {
        waitAndRetry {
            checkWebViewHasText(MESSAGE)
        }
    }

    suspend fun tapRejectOnWebView(delayExecution : Long = 0) = apply {
        waitAndRetry(delayExecution) {
            performClickOnWebViewByContent(REJECT)
        }
    }

    suspend fun tapOptionWebView() = apply {
        waitAndRetry {
            performClickOnWebViewByContent(OPTIONS)
        }
    }

    suspend fun tapSaveAndExitWebView(delayExecution : Long = 0) = apply {
        waitAndRetry(delayExecution) {
            performClickOnWebViewByContent(SAVE_AND_EXIT)
        }
    }

    suspend fun tapRejectAllWebView() = apply {
        waitAndRetry {
            performClickOnWebViewByContent(REJECT_ALL)
        }
    }

    suspend fun tapAcceptAllOnWebView(delayExecution : Long = 0) = apply {
        waitAndRetry(delayExecution) {
            performClickOnWebViewByContent(ACCEPT_ALL)
        }

    }

    suspend fun tapAcceptOnWebView(delayExecution : Long = 0) = apply {
        waitAndRetry(delayExecution) {
            performClickOnWebViewByContent(ACCEPT)
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
            checkWebViewHasText(PRIVACY_MANAGER)
        }
    }

    suspend fun checkConsentIsSelected() = apply {
        waitAndRetry {
            CONSENT_LIST.forEach { consent ->
                checkConsentState(consent, true)
            }
        }
    }

    suspend fun checkConsentIsNotSelected() = apply {
        waitAndRetry {
            CONSENT_LIST.forEach { consent ->
                checkConsentState(consent, false)
            }
        }
    }

    suspend fun checkPartialConsentIsSelected() = apply {
        waitAndRetry {
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
            checkPMTabSelected(FEATURES)
        }
    }

    suspend fun checkPMTabSelectedPurposes() = apply {
        waitAndRetry {
            checkPMTabSelected(PURPOSES)
        }
    }

    suspend fun clickPMTabSelectedPurposes() = apply {
        waitAndRetry {
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
package com.sourcepoint.example_app

import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers
import com.sourcepoint.example_app.TestData.CONSENT_LIST
import org.hamcrest.core.AllOf

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

    suspend fun clickOnReviewConsent() = apply {
            waitAndRetry {
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

}
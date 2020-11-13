package com.sourcepoint.example_app

import com.sourcepoint.example_app.TestData.CONSENT_LIST
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

class ExampleAppTestsRobot {

    suspend fun checkWebViewDisplayedForMessage() = apply {
        waitAndRetry {
            checkWebViewHasText(TestData.MESSAGE)
        }
    }

    suspend fun tapRejectOnWebView() = apply {
        waitAndRetry {
            performClickOnWebViewByContent(TestData.REJECT)
        }
    }

    suspend fun tapAcceptAllOnWebView() = apply {
        waitAndRetry {
            performClickOnWebViewByContent(TestData.ACCEPT_ALL)
        }
    }

    suspend fun checkMainWebViewDisplayed() = apply {
        waitAndRetry {
            isDisplayedAllOfByResIdAndContent(resId = R.id.review_consents, content = "Review Consents")
        }
    }

    suspend fun clickOnReviewConsent(delay : Long = 400) = apply {
        coroutineScope {
            delay(delay)
            waitAndRetry {
                performClickById(resId = R.id.review_consents)
            }
        }

    }

    suspend fun checkWebViewDisplayedForPrivacyManager() = apply {
        waitAndRetry {
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
package com.sourcepoint.example_app

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class ExampleAppTestsK {

    lateinit var scenario: ActivityScenario<MainActivity>

    @After
    fun cleanup() {
        scenario.close()
    }

    @Test
    fun checkAcceptActionFromDirectPrivacyManager() = runBlocking <Unit> {

        scenario = launchActivity()

        ExampleAppTestsRobot()
            .checkWebViewDisplayedForMessage()          // Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
            .tapRejectOnWebView()                       // chooseAction(REJECT);
            .checkMainWebViewDisplayed()                // Assert.assertTrue(checkMainWebViewDisplayed());
            .clickOnReviewConsent()                     // clickOnReviewConsent();
            .checkWebViewDisplayedForPrivacyManager()   // Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
            .tapAcceptAllOnWebView()                    // chooseAction(ACCEPT_ALL);
            .checkMainWebViewDisplayed()                // Assert.assertTrue(checkMainWebViewDisplayed());
            .clickOnReviewConsent()                     // clickOnReviewConsent();
            .checkWebViewDisplayedForPrivacyManager()   // Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
            .checkConsentAsSelectedFromConsentList()    // Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
    }
}
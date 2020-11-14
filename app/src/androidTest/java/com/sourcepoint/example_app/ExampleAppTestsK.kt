package com.sourcepoint.example_app

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import kotlinx.coroutines.delay
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
            .checkMainWebViewDisplayed(400)                // Assert.assertTrue(checkMainWebViewDisplayed());
            .clickOnReviewConsent()                     // clickOnReviewConsent();
            .checkWebViewDisplayedForPrivacyManager()   // Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
            .tapAcceptAllOnWebView()                    // chooseAction(ACCEPT_ALL);
            .checkMainWebViewDisplayed(400)                // Assert.assertTrue(checkMainWebViewDisplayed());
            .clickOnReviewConsent()                     // clickOnReviewConsent();
            .checkWebViewDisplayedForPrivacyManager()   // Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
//            .checkConsentAsSelectedFromConsentList()    // Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    fun checkAcceptActionFromPrivacyManager() = runBlocking<Unit> {

        scenario = launchActivity()

        ExampleAppTestsRobot()
            .checkWebViewDisplayedForMessage()          // Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE))
            .tapOptionWebView()                         // chooseAction(OPTIONS);
            .checkWebViewDisplayedForPrivacyManager()   // Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
//            .checkConsentAsSelectedFromConsentList()    // Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
            .tapAcceptAllOnWebView()                    // chooseAction(ACCEPT_ALL);
            .checkMainWebViewDisplayed(400)                // Assert.assertTrue(checkMainWebViewDisplayed());
            .clickOnReviewConsent()                     // clickOnReviewConsent();
            .checkWebViewDisplayedForPrivacyManager()   // Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
            .checkConsentAsSelectedFromConsentList()    // Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
    }

//    @Test
    fun checkShowOptionsFromMessage() = runBlocking<Unit> {
        // assert.asserttrue(checkwebviewdisplayedfor(message));
        // chooseAction(OPTIONS);
        // Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        // Assert.assertTrue(checkPMTabSelected(FEATURES));
        //
        // chooseAction(SAVE_AND_EXIT);
        // Assert.assertTrue(checkMainWebViewDisplayed());
        // clickOnReviewConsent();
        // Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        // Assert.assertTrue(checkPMTabSelected(PURPOSES));
    }

//    @Test
    fun checkSaveAndExitActionFromPrivacyManager() = runBlocking<Unit> {
        // Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        // chooseAction(OPTIONS);
        // Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        // Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
        // chooseAction(REJECT_ALL);
        // Assert.assertTrue(checkMainWebViewDisplayed());
        // clickOnReviewConsent();
        // Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        // selectConsents(PARTIAL_CONSENT_LIST);
        // chooseAction(SAVE_AND_EXIT);
        // assert.assertTrue(checkMainWebViewDisplayed());
        // clickOnReviewConsent();
        // Assert.assertTrue(checkWebViewDisplayedFor(privacy_manager));
        // Assert.assertTrue(checkConsentsAsSelected(PARTIAL_CONSENT_LIST));
    }

//    @Test
    fun checkAcceptActionFromMessage() = runBlocking<Unit> {
        // Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        // chooseAction(ACCEPT);
        // Assert.assertTrue(checkMainWebViewDisplayed());
        // clickOnReviewConsent();
        // Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        // Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
    }

//    @Test
    fun checkRejectActionFromDirectPrivacyManager() = runBlocking<Unit> {
        // Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        // chooseAction(REJECT);
        // Assert.assertTrue(checkMainWebViewDisplayed());
        // clickOnReviewConsent();
        // Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        // chooseAction(REJECT_ALL);
        // Assert.assertTrue(checkMainWebViewDisplayed());
        // clickOnReviewConsent();
        // Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        // Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
    }
}
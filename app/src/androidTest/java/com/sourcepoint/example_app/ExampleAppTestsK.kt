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
            .checkWebViewDisplayedForMessage(200)   // Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
            .tapRejectOnWebView()                               // chooseAction(REJECT);
            .checkMainWebViewDisplayed(400)         // Assert.assertTrue(checkMainWebViewDisplayed());
            .clickOnReviewConsent()                             // clickOnReviewConsent();
            .tapAcceptAllOnWebView()                            // chooseAction(ACCEPT_ALL);
            .checkMainWebViewDisplayed(400)         // Assert.assertTrue(checkMainWebViewDisplayed());
            .clickOnReviewConsent()                             // clickOnReviewConsent();
            .checkConsentIsSelected()                           // Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    fun checkAcceptActionFromPrivacyManager() = runBlocking<Unit> {

        scenario = launchActivity()

        ExampleAppTestsRobot()
            .checkWebViewDisplayedForMessage(200)   // Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE))
            .tapOptionWebView()                                 // chooseAction(OPTIONS);
            .clickPMTabSelectedPurposes(200)
            .checkConsentIsNotSelected()                        // Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
            .tapAcceptAllOnWebView()                            // chooseAction(ACCEPT_ALL);
            .checkMainWebViewDisplayed(400)         // Assert.assertTrue(checkMainWebViewDisplayed());
            .clickOnReviewConsent()                             // clickOnReviewConsent();
            .clickPMTabSelectedPurposes()
            .checkConsentIsSelected()                           // Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    fun checkShowOptionsFromMessage() = runBlocking<Unit> {

        scenario = launchActivity()

        ExampleAppTestsRobot()
            .checkWebViewDisplayedForMessage(200)       // Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
            .tapOptionWebView()                                     // chooseAction(OPTIONS);
            .checkPMTabSelectedFeatures(200)           // Assert.assertTrue(checkPMTabSelected(FEATURES));
            .tapSaveAndExitWebView()                                // chooseAction(SAVE_AND_EXIT);
            .checkMainWebViewDisplayed(200)             // Assert.assertTrue(checkMainWebViewDisplayed());
            .clickOnReviewConsent(400)                  // clickOnReviewConsent();
            .checkPMTabSelectedPurposes()                           // Assert.assertTrue(checkPMTabSelected(PURPOSES));
    }

    @Test
    fun checkSaveAndExitActionFromPrivacyManager() = runBlocking<Unit> {

        scenario = launchActivity()

        ExampleAppTestsRobot()
            .checkWebViewDisplayedForMessage(200)       // Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
            .tapOptionWebView()                                     // chooseAction(OPTIONS);
            .clickPMTabSelectedPurposes()
            .checkConsentIsNotSelected()                            // Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
            .tapRejectAllWebView()                                  // chooseAction(REJECT_ALL);
            .checkMainWebViewDisplayed(200)             // Assert.assertTrue(checkMainWebViewDisplayed());
            .clickOnReviewConsent(400)                  // clickOnReviewConsent();
            .clickPMTabSelectedPurposes()
            .checkConsentAsSelectedFromPartialConsentList()         // selectConsents(PARTIAL_CONSENT_LIST);
            .tapSaveAndExitWebView()                                // chooseAction(SAVE_AND_EXIT);
            .checkMainWebViewDisplayed(200)             // assert.assertTrue(checkMainWebViewDisplayed());
            .clickOnReviewConsent(400)                  // clickOnReviewConsent();
            .checkPartialConsentIsSelected(200)     // Assert.assertTrue(checkConsentsAsSelected(PARTIAL_CONSENT_LIST));
    }

    @Test
    fun checkAcceptActionFromMessage() = runBlocking<Unit> {

        scenario = launchActivity()

        ExampleAppTestsRobot()
            .checkWebViewDisplayedForMessage(400)   // Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
            .tapAcceptOnWebView()                               // chooseAction(ACCEPT);
            .checkMainWebViewDisplayed(200)         // Assert.assertTrue(checkMainWebViewDisplayed());
            .setFocusOnLayoutActivity()
            .clickOnReviewConsent()                             // clickOnReviewConsent();
            .checkWebViewDisplayedForPrivacyManager()           // Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
            .checkConsentIsSelected()                           // Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    fun checkRejectActionFromDirectPrivacyManager() = runBlocking<Unit> {

        scenario = launchActivity()

        ExampleAppTestsRobot()
            .checkWebViewDisplayedForMessage(200)   // Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
            .tapRejectOnWebView()                               // chooseAction(REJECT);
            .checkMainWebViewDisplayed(200)         // Assert.assertTrue(checkMainWebViewDisplayed());
            .clickOnReviewConsent()                             // clickOnReviewConsent();
//            .checkWebViewDisplayedForPrivacyManager()         // Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
            .tapRejectAllWebView()                              // chooseAction(REJECT_ALL);
            .checkMainWebViewDisplayed(200)         // Assert.assertTrue(checkMainWebViewDisplayed());
            .clickOnReviewConsent(400)              // clickOnReviewConsent();
//            .checkWebViewDisplayedForPrivacyManager()         // Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
            .checkConsentIsNotSelected()                        // Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    fun checkSaveAndExitActionFromDirectPrivacyManager() = runBlocking<Unit> {

        scenario = launchActivity()

        ExampleAppTestsRobot()
            .checkWebViewDisplayedForMessage(200)   //   Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
            .tapRejectOnWebView()                               //   chooseAction(REJECT);
            .checkMainWebViewDisplayed(400)         //   Assert.assertTrue(checkMainWebViewDisplayed());
            .clickOnReviewConsent()                             //   clickOnReviewConsent();
            .checkWebViewDisplayedForPrivacyManager()           //   Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
            .checkConsentAsSelectedFromPartialConsentList()     //   selectConsents(PARTIAL_CONSENT_LIST);
            .tapSaveAndExitWebView()                            //   chooseAction(SAVE_AND_EXIT);
            .checkMainWebViewDisplayed()                        //   Assert.assertTrue(checkMainWebViewDisplayed());
            .setFocusOnLayoutActivity()
            .clickOnReviewConsent(400)              //   clickOnReviewConsent();
            .checkPartialConsentIsSelected()                    //   Assert.assertTrue(checkConsentsAsSelected(PARTIAL_CONSENT_LIST));
    }

    @Test
    fun checkMessageDismiss() = runBlocking<Unit> {

        scenario = launchActivity()

        ExampleAppTestsRobot()
            .checkWebViewDisplayedForMessage(200)   //  Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
            .tapDismissWebView()                                //  chooseDismiss();
            .checkMainWebViewDisplayed()                        //  Assert.assertTrue(checkMainWebViewDisplayed());
    }

}
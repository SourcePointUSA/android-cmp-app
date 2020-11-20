package com.sourcepoint.example_app

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.sourcepoint.example_app.ExampleAppTestsRobot.Companion.checkConsentAsSelectedFromPartialConsentList
import com.sourcepoint.example_app.ExampleAppTestsRobot.Companion.checkConsentIsNotSelected
import com.sourcepoint.example_app.ExampleAppTestsRobot.Companion.checkConsentIsSelected
import com.sourcepoint.example_app.ExampleAppTestsRobot.Companion.checkMainWebViewDisplayed
import com.sourcepoint.example_app.ExampleAppTestsRobot.Companion.checkPMTabSelectedFeatures
import com.sourcepoint.example_app.ExampleAppTestsRobot.Companion.checkPMTabSelectedPurposes
import com.sourcepoint.example_app.ExampleAppTestsRobot.Companion.checkPartialConsentIsSelected
import com.sourcepoint.example_app.ExampleAppTestsRobot.Companion.checkWebViewDisplayedForMessage
import com.sourcepoint.example_app.ExampleAppTestsRobot.Companion.checkWebViewDisplayedForPrivacyManager
import com.sourcepoint.example_app.ExampleAppTestsRobot.Companion.clickOnReviewConsent
import com.sourcepoint.example_app.ExampleAppTestsRobot.Companion.clickPMTabSelectedPurposes
import com.sourcepoint.example_app.ExampleAppTestsRobot.Companion.setFocusOnLayoutActivity
import com.sourcepoint.example_app.ExampleAppTestsRobot.Companion.tapAcceptAllOnWebView
import com.sourcepoint.example_app.ExampleAppTestsRobot.Companion.tapAcceptOnWebView
import com.sourcepoint.example_app.ExampleAppTestsRobot.Companion.tapDismissWebView
import com.sourcepoint.example_app.ExampleAppTestsRobot.Companion.tapOptionWebView
import com.sourcepoint.example_app.ExampleAppTestsRobot.Companion.tapRejectAllWebView
import com.sourcepoint.example_app.ExampleAppTestsRobot.Companion.tapRejectOnWebView
import com.sourcepoint.example_app.ExampleAppTestsRobot.Companion.tapSaveAndExitWebView
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

    private val d = 5000L

    @Test
    fun checkAcceptActionFromDirectPrivacyManager() = runBlocking<Unit> {

        scenario = launchActivity()

        wr(d) { checkWebViewDisplayedForMessage() } // Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        wr { tapRejectOnWebView() }                 // chooseAction(REJECT);
        wr { checkMainWebViewDisplayed() }          // Assert.assertTrue(checkMainWebViewDisplayed());
        wr { clickOnReviewConsent() }               // clickOnReviewConsent();
        wr { tapAcceptAllOnWebView() }              // chooseAction(ACCEPT_ALL);
        wr { checkMainWebViewDisplayed() }          // Assert.assertTrue(checkMainWebViewDisplayed());
        wr { clickOnReviewConsent() }               // clickOnReviewConsent();
        wr { checkConsentIsSelected() }             // Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    fun checkAcceptActionFromPrivacyManager() = runBlocking<Unit> {

        scenario = launchActivity()

        wr(d) { checkWebViewDisplayedForMessage() }         // Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE))
        wr { tapOptionWebView() }                           // chooseAction(OPTIONS);
        wr { clickPMTabSelectedPurposes() }
        wr { checkConsentIsNotSelected() }                  // Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
        wr { tapAcceptAllOnWebView() }                      // chooseAction(ACCEPT_ALL);
        wr { checkMainWebViewDisplayed() }                  // Assert.assertTrue(checkMainWebViewDisplayed());
        wr { clickOnReviewConsent() }                       // clickOnReviewConsent();
        wr { clickPMTabSelectedPurposes() }
        wr { checkConsentIsSelected() }                     // Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    fun checkShowOptionsFromMessage() = runBlocking<Unit> {

        scenario = launchActivity()

        wr(d) { checkWebViewDisplayedForMessage() }     // Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        wr { tapOptionWebView() }                       // chooseAction(OPTIONS);
        wr { checkPMTabSelectedFeatures() }             // Assert.assertTrue(checkPMTabSelected(FEATURES));
        wr { tapSaveAndExitWebView() }                  // chooseAction(SAVE_AND_EXIT);
        wr { checkMainWebViewDisplayed() }              // Assert.assertTrue(checkMainWebViewDisplayed());
        wr { clickOnReviewConsent() }                   // clickOnReviewConsent();
        wr { checkPMTabSelectedPurposes() }             // Assert.assertTrue(checkPMTabSelected(PURPOSES));
    }

    @Test
    fun checkSaveAndExitActionFromPrivacyManager() = runBlocking<Unit> {

        scenario = launchActivity()

        wr(d) { checkWebViewDisplayedForMessage()}              // Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        wr { tapOptionWebView()}                                // chooseAction(OPTIONS);
        wr { clickPMTabSelectedPurposes()}
        wr { checkConsentIsNotSelected()}                       // Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
        wr { tapRejectAllWebView()}                             // chooseAction(REJECT_ALL);
        wr { checkMainWebViewDisplayed()}                       // Assert.assertTrue(checkMainWebViewDisplayed());
        wr { clickOnReviewConsent()}                            // clickOnReviewConsent();
        wr { clickPMTabSelectedPurposes()}
        wr { checkConsentAsSelectedFromPartialConsentList() }   // selectConsents(PARTIAL_CONSENT_LIST);
        wr { tapSaveAndExitWebView()}                           // chooseAction(SAVE_AND_EXIT);
        wr { checkMainWebViewDisplayed()}                       // assert.assertTrue(checkMainWebViewDisplayed());
        wr { clickOnReviewConsent()}                            // clickOnReviewConsent();
        wr { checkPartialConsentIsSelected()}                   // Assert.assertTrue(checkConsentsAsSelected(PARTIAL_CONSENT_LIST));
    }

    @Test
    fun checkAcceptActionFromMessage() = runBlocking<Unit> {

        scenario = launchActivity()

        wr(d) { checkWebViewDisplayedForMessage()}          // Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        wr { tapAcceptOnWebView()}                          // chooseAction(ACCEPT);
        wr { checkMainWebViewDisplayed()}                   // Assert.assertTrue(checkMainWebViewDisplayed());
        wr { setFocusOnLayoutActivity()}
        wr { clickOnReviewConsent()}                        // clickOnReviewConsent();
        wr { checkWebViewDisplayedForPrivacyManager()}      // Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        wr { checkConsentIsSelected()  }                    // Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    fun checkRejectActionFromDirectPrivacyManager() = runBlocking<Unit> {

        scenario = launchActivity()

        wr(d) { checkWebViewDisplayedForMessage()}  // Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        wr { tapRejectOnWebView()}                  // chooseAction(REJECT);
        wr { checkMainWebViewDisplayed()}           // Assert.assertTrue(checkMainWebViewDisplayed());
        wr { clickOnReviewConsent()}                // clickOnReviewConsent();
//.checkWebViewDisplayedForPrivacyManager()         // Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        wr { tapRejectAllWebView()}                 // chooseAction(REJECT_ALL);
        wr { checkMainWebViewDisplayed()}           // Assert.assertTrue(checkMainWebViewDisplayed());
        wr { clickOnReviewConsent() }               // clickOnReviewConsent();
//.checkWebViewDisplayedForPrivacyManager()         // Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        wr { checkConsentIsNotSelected()}           // Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    fun checkSaveAndExitActionFromDirectPrivacyManager() = runBlocking<Unit> {

        scenario = launchActivity()

        wr(d) { checkWebViewDisplayedForMessage()}              //   Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        wr { tapRejectOnWebView()}                              //   chooseAction(REJECT);
        wr { checkMainWebViewDisplayed()}                       //   Assert.assertTrue(checkMainWebViewDisplayed());
        wr { clickOnReviewConsent()}                            //   clickOnReviewConsent();
        wr { checkWebViewDisplayedForPrivacyManager()}          //   Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        wr { checkConsentAsSelectedFromPartialConsentList()}    //   selectConsents(PARTIAL_CONSENT_LIST);
        wr { tapSaveAndExitWebView()}                           //   chooseAction(SAVE_AND_EXIT);
        wr { checkMainWebViewDisplayed()}                       //   Assert.assertTrue(checkMainWebViewDisplayed());
        wr { setFocusOnLayoutActivity()}
        wr { clickOnReviewConsent()}                            //   clickOnReviewConsent();
        wr { checkPartialConsentIsSelected()}                   //   Assert.assertTrue(checkConsentsAsSelected(PARTIAL_CONSENT_LIST));
    }

    @Test
    fun checkMessageDismiss() = runBlocking<Unit> {

        scenario = launchActivity()

        wr(d) { checkWebViewDisplayedForMessage()}          //  Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        wr { tapDismissWebView()}                           //  chooseDismiss();
        wr { checkMainWebViewDisplayed()}                   //  Assert.assertTrue(checkMainWebViewDisplayed());
    }

}
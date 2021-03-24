package com.example.myapplication

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.uitestutil.wr
import com.example.myapplication.TestUseCase.Companion.checkConsentAsSelectedFromPartialConsentList
import com.example.myapplication.TestUseCase.Companion.checkConsentIsNotSelected
import com.example.myapplication.TestUseCase.Companion.checkConsentIsSelected
import com.example.myapplication.TestUseCase.Companion.checkMainWebViewDisplayed
import com.example.myapplication.TestUseCase.Companion.checkPMTabSelectedFeatures
import com.example.myapplication.TestUseCase.Companion.checkPMTabSelectedPurposes
import com.example.myapplication.TestUseCase.Companion.checkPartialConsentIsSelected
import com.example.myapplication.TestUseCase.Companion.checkWebViewDisplayedForMessage
import com.example.myapplication.TestUseCase.Companion.checkWebViewDisplayedForPrivacyManager
import com.example.myapplication.TestUseCase.Companion.clickOnReviewConsent
import com.example.myapplication.TestUseCase.Companion.clickPMTabSelectedPurposes
import com.example.myapplication.TestUseCase.Companion.setFocusOnLayoutActivity
import com.example.myapplication.TestUseCase.Companion.tapAcceptAllOnWebView
import com.example.myapplication.TestUseCase.Companion.tapAcceptOnWebView
import com.example.myapplication.TestUseCase.Companion.tapDismissWebView
import com.example.myapplication.TestUseCase.Companion.tapOptionWebView
import com.example.myapplication.TestUseCase.Companion.tapRejectAllWebView
import com.example.myapplication.TestUseCase.Companion.tapRejectOnWebView
import com.example.myapplication.TestUseCase.Companion.tapSaveAndExitWebView
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class ExampleAppTests {

    lateinit var scenario: ActivityScenario<MainActivityV6>

    @After
    fun cleanup() {
        if(this::scenario.isLateinit) scenario.close()
    }

    private val d = 1000L

    @Test
    fun GIVEN_a_campaign_chain_click_accept_all() = runBlocking<Unit> {

        scenario = launchActivity()

        wr { checkWebViewDisplayedForMessage() }
        wr { tapAcceptOnWebView() }
        wr { tapAcceptOnWebView() }
    }

    @Test
    fun GIVEN_a_campaign_chain_click_option_then_accept_all() = runBlocking<Unit> {

        scenario = launchActivity()

        wr { checkWebViewDisplayedForMessage() }
        wr { tapOptionWebView() }
        wr { tapAcceptOnWebView() }
        wr { tapOptionWebView() }
        wr { tapAcceptOnWebView() }
    }
/*
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

        wr(d) { checkWebViewDisplayedForMessage() }              // Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        wr { tapOptionWebView() }                                // chooseAction(OPTIONS);
        wr { clickPMTabSelectedPurposes() }
        wr { checkConsentIsNotSelected() }                       // Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
        wr { tapRejectAllWebView() }                             // chooseAction(REJECT_ALL);
        wr { checkMainWebViewDisplayed() }                       // Assert.assertTrue(checkMainWebViewDisplayed());
        wr { clickOnReviewConsent() }                            // clickOnReviewConsent();
        wr { clickPMTabSelectedPurposes() }
        wr { checkConsentAsSelectedFromPartialConsentList() }   // selectConsents(PARTIAL_CONSENT_LIST);
        wr { tapSaveAndExitWebView() }                           // chooseAction(SAVE_AND_EXIT);
        wr { checkMainWebViewDisplayed() }                       // assert.assertTrue(checkMainWebViewDisplayed());
        wr { clickOnReviewConsent() }                            // clickOnReviewConsent();
        wr { checkPartialConsentIsSelected() }                   // Assert.assertTrue(checkConsentsAsSelected(PARTIAL_CONSENT_LIST));
    }

    @Test
    fun checkAcceptActionFromMessage() = runBlocking<Unit> {

        scenario = launchActivity()

        wr(d) { checkWebViewDisplayedForMessage() }          // Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        wr { tapAcceptOnWebView() }                          // chooseAction(ACCEPT);
        wr { checkMainWebViewDisplayed() }                   // Assert.assertTrue(checkMainWebViewDisplayed());
        wr { setFocusOnLayoutActivity() }
        wr { clickOnReviewConsent() }                        // clickOnReviewConsent();
        wr { checkWebViewDisplayedForPrivacyManager() }      // Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        wr { checkConsentIsSelected() }                    // Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    fun checkRejectActionFromDirectPrivacyManager() = runBlocking<Unit> {

        scenario = launchActivity()

        wr(d) { checkWebViewDisplayedForMessage() }  // Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        wr { tapRejectOnWebView() }                  // chooseAction(REJECT);
        wr { checkMainWebViewDisplayed() }           // Assert.assertTrue(checkMainWebViewDisplayed());
        wr { clickOnReviewConsent() }                // clickOnReviewConsent();
//.checkWebViewDisplayedForPrivacyManager()         // Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        wr { tapRejectAllWebView() }                 // chooseAction(REJECT_ALL);
        wr { checkMainWebViewDisplayed() }           // Assert.assertTrue(checkMainWebViewDisplayed());
        wr { clickOnReviewConsent() }               // clickOnReviewConsent();
//.checkWebViewDisplayedForPrivacyManager()         // Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        wr { checkConsentIsNotSelected() }           // Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    fun checkSaveAndExitActionFromDirectPrivacyManager() = runBlocking<Unit> {

        scenario = launchActivity()

        wr(d) { checkWebViewDisplayedForMessage() }              //   Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        wr { tapRejectOnWebView() }                              //   chooseAction(REJECT);
        wr { checkMainWebViewDisplayed() }                       //   Assert.assertTrue(checkMainWebViewDisplayed());
        wr { clickOnReviewConsent() }                            //   clickOnReviewConsent();
        wr { checkWebViewDisplayedForPrivacyManager() }          //   Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        wr { checkConsentAsSelectedFromPartialConsentList() }    //   selectConsents(PARTIAL_CONSENT_LIST);
        wr { tapSaveAndExitWebView() }                           //   chooseAction(SAVE_AND_EXIT);
        wr { checkMainWebViewDisplayed() }                       //   Assert.assertTrue(checkMainWebViewDisplayed());
        wr { setFocusOnLayoutActivity() }
        wr { clickOnReviewConsent() }                            //   clickOnReviewConsent();
        wr { checkPartialConsentIsSelected() }                   //   Assert.assertTrue(checkConsentsAsSelected(PARTIAL_CONSENT_LIST));
    }

    @Test
    fun checkMessageDismiss() = runBlocking<Unit> {

        scenario = launchActivity()

        wr(d) { checkWebViewDisplayedForMessage() }          //  Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        wr { tapDismissWebView() }                           //  chooseDismiss();
        wr { checkMainWebViewDisplayed() }                   //  Assert.assertTrue(checkMainWebViewDisplayed());
    }

    @Test
    fun checkRejectActionFromMessage() = runBlocking<Unit> {

        scenario = launchActivity()

        wr(d) { checkWebViewDisplayedForMessage() }         //  Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        wr { tapRejectOnWebView() }                         //  chooseAction(REJECT);
        wr { checkMainWebViewDisplayed() }                  //  Assert.assertTrue(checkMainWebViewDisplayed());
        wr { clickOnReviewConsent() }                       //  clickOnReviewConsent();
        wr { checkWebViewDisplayedForPrivacyManager() }     //  Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        wr { checkConsentIsNotSelected() }                  //  Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    fun checkRejectActionFromPrivacyManager() = runBlocking<Unit> {

        scenario = launchActivity()

        wr(d) { checkWebViewDisplayedForMessage() }         //  Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        wr { tapOptionWebView() }                           //  chooseAction(OPTIONS);
        wr { checkWebViewDisplayedForPrivacyManager() }     //  Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        wr { clickPMTabSelectedPurposes() }
        wr { checkConsentIsNotSelected() }                  //  Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
        wr { tapRejectAllWebView() }                        //  chooseAction(REJECT_ALL);
        wr { checkMainWebViewDisplayed() }                  //  Assert.assertTrue(checkMainWebViewDisplayed());
        wr { clickOnReviewConsent() }                       //  clickOnReviewConsent();
        wr { checkWebViewDisplayedForPrivacyManager() }     //  Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        wr { checkConsentIsNotSelected() }                  //  Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
    }
*/
}
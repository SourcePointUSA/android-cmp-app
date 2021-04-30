package com.sourcepoint.app.v6

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.uitestutil.wr
import com.sourcepoint.app.v6.TestUseCase.Companion.tapAcceptAllOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapAcceptCcpaOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapAcceptOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapOptionWebView
import com.sourcepoint.app.v6.core.DataProvider
import com.sourcepoint.cmplibrary.creation.config
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

@RunWith(AndroidJUnit4ClassRunner::class)
class ExampleAppV6Tests {

    lateinit var scenario: ActivityScenario<MainActivity4test>

    @After
    fun cleanup() {
        if (this::scenario.isLateinit) scenario.close()
    }

    private val spConfFull = config {
        accountId = 22
        propertyName = "mobile.multicampaign.demo"
        +(CampaignType.CCPA to listOf(("location" to "US")))
        +(CampaignType.GDPR) //to listOf(("location" to "EU")))
    }

    private val spConfGdpr = config {
        accountId = 22
        propertyName = "mobile.multicampaign.demo"
        +(CampaignType.GDPR)
    }

    @Test
    fun GIVEN_a_camapignList_ACCEPT_all_legislation() = runBlocking<Unit> {

        loadKoinModules(mockModule(onlyPm = false, spConfig = spConfFull, gdprPmId = "13111"))

        scenario = launchActivity()

//        wr { tapAcceptOnWebView() }
//        wr { tapAcceptCcpaOnWebView() }
    }

    @Test
    fun GIVEN_a_camapignList_tap_SETTINGS_all_legislation() = runBlocking<Unit> {

        loadKoinModules(mockModule(onlyPm = false, spConfig = spConfFull, gdprPmId = "13111"))

        scenario = launchActivity()

//        wr { tapOptionWebView() }
//        wr { tapAcceptAllOnWebView() }
//        wr { tapAcceptCcpaOnWebView() }
    }

//    @Test
//    fun GIVEN_consent_USING_gdpr_pm() = runBlocking<Unit> {
//
//        loadKoinModules(mockModule(onlyPm = false, spConfig = spConfFull, gdprPmId = "13111"))
//
//        scenario = launchActivity()
//
//        wr { tapAcceptOnWebView() }
//        wr { clickOnGdprReviewConsent() }
//        wr(backup = { clickOnGdprReviewConsent() }) { tapAcceptAllOnWebView() }
//    }
//
//    @Test
//    fun GIVEN_a_gdpr_consent_ACCEPT_ALL() = runBlocking<Unit> {
//
//        loadKoinModules(mockModule(onlyPm = false, spConfig = spConfGdpr, gdprPmId = "13111"))
//
//        scenario = launchActivity()
//
//        wr { tapRejectOnWebView() }
////        wr { clickOnGdprReviewConsent() }
////        wr(backup = { clickOnGdprReviewConsent() }) { tapAcceptAllOnWebView() }
////        wr { checkAllConsentsOn() }
//    }
//
//    @Test
//    fun SAVE_AND_EXIT_action() = runBlocking<Unit> {
//
//        loadKoinModules(mockModule(onlyPm = false, spConfig = spConfFull, gdprPmId = "13111"))
//
//        scenario = launchActivity()
//
//        wr { tapAcceptOnWebView() }
//        wr { tapAcceptCcpaOnWebView() }
//        wr { clickOnGdprReviewConsent() }
//        wr(backup = { clickOnGdprReviewConsent() }) { tapAcceptAllOnWebView() }
//        wr { clickOnGdprReviewConsent() }
//        wr { tapToDisableAllConsent() }
//        wr { tapSaveAndExitWebView() }
//        wr { clickOnGdprReviewConsent() }
//        wr { checkAllConsentsOff() }
//    }
//
//    @Test
//    fun SAVE_AND_EXIT_action_2() = runBlocking<Unit> {
//
//        loadKoinModules(mockModule(onlyPm = false, spConfig = spConfFull, gdprPmId = "13111"))
//
//        scenario = launchActivity()
//
//        wr { tapRejectOnWebView() }
//        wr { tapAcceptCcpaOnWebView() }
//        wr { clickOnGdprReviewConsent() }
//        wr(backup = { clickOnGdprReviewConsent() }) { tapRejectOnWebView() }
//        wr { clickOnGdprReviewConsent() }
//        wr { tapToEnableAllConsent() }
//        wr { tapSaveAndExitWebView() }
//        wr { clickOnGdprReviewConsent() }
//        wr { checkAllConsentsOn() }
//    }

/*
    @Test
    fun checkAcceptActionFromDirectPrivacyManager() = runBlocking<Unit> {

        scenario = launchActivity()

        wr(d) { checkWebViewDisplayedForMessage() } // Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        wr { tapRejectOnWebView() }                 // chooseAction(REJECT);
        wr { checkMainWebViewDisplayed() }          // Assert.assertTrue(checkMainWebViewDisplayed());
        wr { clickOnReviewConsent() }               // clickOnReviewConsent();
        wr { tapAcceptAllOnWebView() }              // chooseAGIVEN_a_camapignList_tap_SETTINGS_all_legislationction(ACCEPT_ALL);
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

    private fun mockModule(
        spConfig: SpConfig,
        gdprPmId: String,
        uuid: String? = null,
        url: String = "",
        onlyPm: Boolean = false
    ): Module {
        return module(override = true) {
            single<DataProvider> {
                object : DataProvider {
                    override val authId = uuid
                    override val url = url
                    override val onlyPm: Boolean = onlyPm
                    override val spConfig: SpConfig = spConfig
                    override val gdprPmId: String = gdprPmId
                }
            }
        }
    }

}
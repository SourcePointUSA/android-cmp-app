package com.sourcepointmeta.metaapp

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.addNativeMessagePropertyDetails
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.addPropertyFor
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.addPropertyWithAllFields
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.checkConsentListNotSelected
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.checkConsentNotDisplayed
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.checkForConsentAreDisplayed
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.checkForConsentsAreDisplayed
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.checkForPropertyInfoInList
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.checkForPropertyInfoScreen
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.checkInsertedProperty
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.checkNativeMessageDisplayed
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.checkWebViewDisplayedForMessage
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.checkWebViewDisplayedForPrivacyManager
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.checkWebViewDoesNotDisplayTheMessage
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.loadPrivacyManagerDirect
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.navigateBackToListView
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.selectNativeMessageConsentList
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.selectPartialConsentList
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.swipeAndChooseResetAction
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.tapAcceptAll
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.tapAcceptAllOnWebView
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.tapDismissWebView
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.tapManagePreferencesOnWebView
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.tapOnAddProperty
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.tapOnProperty
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.tapOnSave
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.tapPMAcceptAllOnWebView
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.tapRejectAll
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.tapRejectAllOnWebView
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.tapSaveAndExitOnWebView
import com.sourcepointmeta.metaapp.MetaAppTestsKRobot.Companion.tapShowOption
import com.sourcepointmeta.metaapp.TestData.*
import com.sourcepointmeta.metaapp.ui.SplashScreenActivity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MetaAppTestsK {

    lateinit var scenario: ActivityScenario<SplashScreenActivity>

    @After
    fun cleanup() {
        scenario.close()
    }

    @Test
    fun checkNativeMessageRejectAllFromPMViaMessage() = runBlocking {

        scenario = launchActivity()

        wr { tapOnAddProperty() }                           // tapOnAddProperty();
        wr { addNativeMessagePropertyDetails() }            // addNativeMessagePropertyDetails();
        wr { tapOnSave() }                                  // tapOnSave();
        wr { checkNativeMessageDisplayed() }                // Assert.assertTrue(checkNativeMessageDisplayed());
        wr { tapShowOption() }                              // chooseNativeMessageAction(R.id.ShowOption);
        wr { checkWebViewDisplayedForPrivacyManager() }     // Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        wr { tapRejectAllOnWebView() }                      // chooseAction(PM_REJECT_ALL);
        wr { navigateBackToListView() }                     // navigateBackToListView();
        wr { tapOnProperty() }                              // tapOnProperty();
        wr { checkNativeMessageDisplayed() }                // Assert.assertTrue(checkNativeMessageDisplayed());
        wr { navigateBackToListView() }                     // navigateBackToListView();
        wr { checkForPropertyInfoInList() }                 // Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
    }

    @Test
    fun checkNativeMessageSave_ExitWithFewPurposesDirectPMLoad() = runBlocking {

        scenario = launchActivity()

        wr { tapOnAddProperty() }                           //  tapOnAddProperty();
        wr { addNativeMessagePropertyDetails() }            //  addNativeMessagePropertyDetails();
        wr { tapOnSave() }                                  //  tapOnSave();
        wr { checkNativeMessageDisplayed() }                //  Assert.assertTrue(checkNativeMessageDisplayed());
        wr { tapRejectAll() }                               //  chooseNativeMessageAction(R.id.RejectAll);
        wr { checkForPropertyInfoScreen() }                 //  Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
        wr { loadPrivacyManagerDirect() }                   //  loadPrivacyManagerDirect();
        wr { checkWebViewDisplayedForPrivacyManager() }     //  Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        wr { selectNativeMessageConsentList() }             //  Assert.assertFalse(checkConsentsAsSelected(NATIVE_MESSAGE_CONSENT_LIST));
        wr { selectPartialConsentList() }                   //  selectConsents(PARTIAL_CONSENT_LIST);
        wr { tapSaveAndExitOnWebView() }                    //  chooseAction(PM_SAVE_AND_EXIT);
        wr { checkForPropertyInfoScreen() }                 //  Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
        wr { loadPrivacyManagerDirect() }                   //  loadPrivacyManagerDirect();
        wr { checkWebViewDisplayedForPrivacyManager() }     //  Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        wr { selectPartialConsentList() }                   //  Assert.assertTrue(checkConsentsAsSelected(PARTIAL_CONSENT_LIST));
    }

    @Test
    fun checkNativeMessageSave_ExitDirectPMLoad() = runBlocking {

        scenario = launchActivity()

        wr { tapOnAddProperty() }                         //  tapOnAddProperty();
        wr { addNativeMessagePropertyDetails() }          //  addNativeMessagePropertyDetails();
        wr { tapOnSave() }                                //  tapOnSave();
        wr { checkNativeMessageDisplayed() }              //  Assert.assertTrue(checkNativeMessageDisplayed());
        wr { tapRejectAll() }                             //  chooseNativeMessageAction(R.id.RejectAll);
        wr { checkForPropertyInfoScreen() }               //  Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
        wr { loadPrivacyManagerDirect() }                 //  loadPrivacyManagerDirect();
        wr { selectNativeMessageConsentList() }           //  Assert.assertFalse(checkConsentsAsSelected(NATIVE_MESSAGE_CONSENT_LIST));
        wr { tapSaveAndExitOnWebView() }                  //  chooseAction(PM_SAVE_AND_EXIT);
        wr { navigateBackToListView() }                   //  navigateBackToListView();
        wr { checkInsertedProperty() }                    //  Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
        wr { tapOnProperty() }                            //  tapOnProperty();
    }

    @Test
    fun checkNativeMessageAcceptAllFromPM() = runBlocking {

        scenario = launchActivity()

        wr { tapOnAddProperty() }                   //  tapOnAddProperty();
        wr { addNativeMessagePropertyDetails() }    //  addNativeMessagePropertyDetails();
        wr { tapOnSave() }                          //  tapOnSave();
        wr { checkNativeMessageDisplayed() }        //  Assert.assertTrue(checkNativeMessageDisplayed());
        wr { tapShowOption() }                      // chooseNativeMessageAction(R.id.ShowOption);
        wr { tapPMAcceptAllOnWebView() }            //  chooseAction(PM_ACCEPT_ALL);
        wr { checkForPropertyInfoScreen() }         //  Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
        wr { navigateBackToListView() }             //  navigateBackToListView();
        wr { checkInsertedProperty() }              //  Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
        wr { tapOnProperty() }                      //  tapOnProperty();
        wr { checkNativeMessageDisplayed() }        //  Assert.assertTrue(checkNativeMessageDisplayed());
        wr { navigateBackToListView() }             //  navigateBackToListView();
        wr { checkForPropertyInfoInList() }         //  Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
    }

    @Test
    fun checkConsentWithSaveAndExitActionFromPrivacyManager() = runBlocking {

        scenario = launchActivity()

        wr { addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION) }
        wr { checkWebViewDisplayedForMessage() }
        wr { tapManagePreferencesOnWebView() }
        wr { selectPartialConsentList() }
        wr { tapSaveAndExitOnWebView() }
        wr { checkForConsentAreDisplayed() }
        wr { navigateBackToListView() }
        wr { checkInsertedProperty() }
        wr { tapOnProperty() }
        wr { checkWebViewDisplayedForMessage() }
        wr { tapManagePreferencesOnWebView() }
        wr { selectPartialConsentList() }                                  // Assert.assertTrue(checkConsentsAsSelected(PARTIAL_CONSENT_LIST))
    }

    @Test
    fun checkNativeMessageAcceptAllDirectPMLoad() = runBlocking {

        scenario = launchActivity()

        wr { tapOnAddProperty() }
        wr { addNativeMessagePropertyDetails() }
        wr { tapOnSave() }
        wr { checkNativeMessageDisplayed() }
        wr { tapAcceptAll() }
        wr { checkForPropertyInfoScreen() }
        wr { loadPrivacyManagerDirect() }
        wr { checkWebViewDisplayedForPrivacyManager() }
        wr { selectNativeMessageConsentList() }
        wr { tapRejectAllOnWebView() }
        wr { checkForPropertyInfoScreen() }
        wr { loadPrivacyManagerDirect() }
        wr { checkWebViewDisplayedForPrivacyManager() }
        wr { selectNativeMessageConsentList() }
    }

    @Test
    fun checkNativeMessageRejectAllDirectPMLoad() = runBlocking {

        scenario = launchActivity()

        wr { tapOnAddProperty() }                         //    tapOnAddProperty();
        wr { addNativeMessagePropertyDetails() }          //    addNativeMessagePropertyDetails();
        wr { tapOnSave() }                                //    tapOnSave();
        wr { checkNativeMessageDisplayed() }              //    Assert.assertTrue(checkNativeMessageDisplayed());
        wr { tapRejectAll() }                             //    chooseNativeMessageAction(R.id.RejectAll);
        wr { checkForPropertyInfoScreen() }               //    Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
        wr { loadPrivacyManagerDirect() }                 //    loadPrivacyManagerDirect();
        wr { checkWebViewDisplayedForPrivacyManager() }   //    Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        wr { selectNativeMessageConsentList() }           //    Assert.assertFalse(checkConsentsAsSelected(NATIVE_MESSAGE_CONSENT_LIST));
        wr { tapPMAcceptAllOnWebView() }                  //    chooseAction(PM_ACCEPT_ALL);
        wr { checkForPropertyInfoScreen() }               //    Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
        wr { loadPrivacyManagerDirect() }                 //    loadPrivacyManagerDirect();
        wr { checkWebViewDisplayedForPrivacyManager() }   //    Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        wr { selectNativeMessageConsentList() }           //    Assert.assertTrue(checkConsentsAsSelected(NATIVE_MESSAGE_CONSENT_LIST));
    }

    @Test
    fun checkNativeMessageSave_ExitFromPMViaMessage() = runBlocking {

        scenario = launchActivity()

        wr { tapOnAddProperty() }
        wr { addNativeMessagePropertyDetails() }
        wr { tapOnSave() }
        wr { checkNativeMessageDisplayed() }
        wr { tapShowOption() }
        wr { checkWebViewDisplayedForPrivacyManager() }
        wr { tapSaveAndExitOnWebView() }
        wr { checkForPropertyInfoScreen() }
        wr { navigateBackToListView() }
        wr { tapOnProperty() }
        wr { checkNativeMessageDisplayed() }
        wr { navigateBackToListView() }
        wr { checkForPropertyInfoInList() }               // Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
    }

    @Test
    fun checkConsentsOnMessageDismiss() = runBlocking {

        scenario = launchActivity()

        wr { tapOnAddProperty() }                         //  tapOnAddProperty();
        wr { addPropertyWithAllFields() }                 //  addPropertyWith(ALL_FIELDS);
        wr { tapOnSave() }                                //  tapOnSave();
        wr { checkWebViewDisplayedForMessage() }          //  Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        wr { tapDismissWebView() }                        //  chooseDismiss();
        wr { checkForPropertyInfoScreen() }               //  Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
        wr { checkConsentNotDisplayed() }                 //  Assert.assertTrue(checkFor(CONSENTS_ARE_NOT_DISPLAYED));
    }

    @Test
    fun resetConsentDataAndCheckForMessageWithShowMessageOnce() = runBlocking {

        scenario = launchActivity()

        wr { addPropertyFor(SHOW_MESSAGE_ONCE, NO_AUTHENTICATION) }   //  addPropertyFor(SHOW_MESSAGE_ONCE, NO_AUTHENTICATION);
        wr { checkWebViewDisplayedForMessage() }                      //  Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        wr { tapManagePreferencesOnWebView() }                        //  chooseAction(MANAGE_PREFERENCES);
        wr { checkWebViewDisplayedForPrivacyManager() }               //  Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        wr { checkConsentListNotSelected() }                          //  Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
        wr { tapPMAcceptAllOnWebView() }                              //  chooseAction(PM_ACCEPT_ALL);
        wr { checkForConsentsAreDisplayed() }                         //  Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
        wr { navigateBackToListView() }                               //  navigateBackToListView();
        wr { checkInsertedProperty() }                                //  Assert.assertTrue(checkForPropertyListScrren());
        wr { tapOnProperty() }                                        //  tapOnProperty();
        wr { checkWebViewDoesNotDisplayTheMessage() }                 //  Assert.assertFalse(checkWebViewDisplayedFor(MESSAGE));
        wr { checkForConsentsAreDisplayed() }                         //  Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
        wr { navigateBackToListView() }                               //  navigateBackToListView();
        wr { swipeAndChooseResetAction() }                            //  swipeAndChooseAction(RESET_ACTION, YES);
        wr { checkWebViewDisplayedForMessage() }                      //  Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        wr(400) { tapManagePreferencesOnWebView() }                        //  chooseAction(MANAGE_PREFERENCES);
        wr { checkWebViewDisplayedForPrivacyManager() }               //  Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        wr { checkConsentListNotSelected() }                          //  Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    fun checkConsentForPropertyWithDifferentAuthenticationAlwaysWithDifferentAuthID() = runBlocking {

        scenario = launchActivity()

        wr { addPropertyFor(SHOW_MESSAGE_ALWAYS, UNIQUE_AUTHENTICATION) }     //        addPropertyFor(SHOW_MESSAGE_ALWAYS, UNIQUE_AUTHENTICATION);
        wr { checkWebViewDisplayedForMessage() }                              //        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        wr { tapAcceptAllOnWebView() }                                        //        chooseAction(ACCEPT_ALL);
        wr { checkForConsentsAreDisplayed() }                                 //        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
        wr { navigateBackToListView() }                                       //        navigateBackToListView();
        wr { addPropertyFor(SHOW_MESSAGE_ALWAYS, UNIQUE_AUTHENTICATION) }     //        addPropertyFor(SHOW_MESSAGE_ALWAYS, UNIQUE_AUTHENTICATION);
        wr { checkWebViewDisplayedForMessage() }                              //        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        wr { tapManagePreferencesOnWebView() }                                //        chooseAction(MANAGE_PREFERENCES);
        wr { checkWebViewDisplayedForPrivacyManager() }                       //        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        wr { checkConsentListNotSelected() }                                  //        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
    }

}
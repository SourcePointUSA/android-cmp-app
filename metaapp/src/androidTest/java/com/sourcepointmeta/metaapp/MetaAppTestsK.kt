package com.sourcepointmeta.metaapp

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
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
    fun checkNativeMessageRejectAllFromPMViaMessage() = runBlocking<Unit> {

        scenario = launchActivity()

        MetaAppTestsKRobot()
            .tapOnAddProperty()                         // tapOnAddProperty();
            .addNativeMessagePropertyDetails()          // addNativeMessagePropertyDetails();
            .tapOnSave()                                // tapOnSave();
            .checkNativeMessageDisplayed()              // Assert.assertTrue(checkNativeMessageDisplayed());
            .tapShowOption()                            // chooseNativeMessageAction(R.id.ShowOption);
//            .checkWebViewDisplayedForPrivacyManager()   // Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
            .tapRejectAllOnWebView()                    // chooseAction(PM_REJECT_ALL);
            .navigateBackToListView()    // navigateBackToListView();
            .checkInsertedProperty(200)     // Assert.assertTrue(checkForPropertyListScrren());
            .tapOnProperty()                            // tapOnProperty();
            .checkNativeMessageDisplayed()              // Assert.assertTrue(checkNativeMessageDisplayed());
            .navigateBackToListView()
            .checkForPropertyInfoInList()               // Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
    }

    @Test
    fun checkNativeMessageSave_ExitWithFewPurposesDirectPMLoad() = runBlocking<Unit> {

        scenario = launchActivity()

        MetaAppTestsKRobot()
            .tapOnAddProperty()                         //  tapOnAddProperty();
            .addNativeMessagePropertyDetails()          //  addNativeMessagePropertyDetails();
            .tapOnSave()                                //  tapOnSave();
            .checkNativeMessageDisplayed()              //  Assert.assertTrue(checkNativeMessageDisplayed());
            .tapRejectAll()                             //  chooseNativeMessageAction(R.id.RejectAll);
            .checkForPropertyInfoScreen()               //  Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
            .loadPrivacyManagerDirect(400) //  loadPrivacyManagerDirect();
//            .checkWebViewDisplayedForPrivacyManager()   //  Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
            .selectNativeMessageConsentList(400)           //  Assert.assertFalse(checkConsentsAsSelected(NATIVE_MESSAGE_CONSENT_LIST));
            .selectPartialConsentList(400)                 //  selectConsents(PARTIAL_CONSENT_LIST);
            .tapSaveAndExitOnWebView()                  //  chooseAction(PM_SAVE_AND_EXIT);
            .checkForPropertyInfoScreen()               //  Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
            .loadPrivacyManagerDirect()                 //  loadPrivacyManagerDirect();
//            .checkWebViewDisplayedForPrivacyManager()   //  Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
            .selectPartialConsentList(400)                 //  Assert.assertTrue(checkConsentsAsSelected(PARTIAL_CONSENT_LIST));
    }

    @Test
    fun checkNativeMessageSave_ExitDirectPMLoad() = runBlocking<Unit> {

        scenario = launchActivity()

        MetaAppTestsKRobot()
            .tapOnAddProperty()                         //  tapOnAddProperty();
            .addNativeMessagePropertyDetails()          //  addNativeMessagePropertyDetails();
            .tapOnSave()                                //  tapOnSave();
            .checkNativeMessageDisplayed()              //  Assert.assertTrue(checkNativeMessageDisplayed());
            .tapRejectAll()                             //  chooseNativeMessageAction(R.id.RejectAll);
            .checkForPropertyInfoScreen()               //  Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
            .loadPrivacyManagerDirect()                 //  loadPrivacyManagerDirect();
            .selectNativeMessageConsentList()           //  Assert.assertFalse(checkConsentsAsSelected(NATIVE_MESSAGE_CONSENT_LIST));
            .tapSaveAndExitOnWebView()                  //  chooseAction(PM_SAVE_AND_EXIT);
            .navigateBackToListView()                   //  navigateBackToListView();
            .checkInsertedProperty()                    //  Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
            .tapOnProperty()                            //  tapOnProperty();
    }

    @Test
    fun checkNativeMessageAcceptAllFromPM() = runBlocking<Unit> {

        scenario = launchActivity()

        MetaAppTestsKRobot()
            .tapOnAddProperty()                         //  tapOnAddProperty();
            .addNativeMessagePropertyDetails()          //  addNativeMessagePropertyDetails();
            .tapOnSave()                                //  tapOnSave();
            .checkNativeMessageDisplayed()              //  Assert.assertTrue(checkNativeMessageDisplayed());
            .tapShowOption()                            // chooseNativeMessageAction(R.id.ShowOption);
            .tapPMAcceptAllOnWebView()                  //  chooseAction(PM_ACCEPT_ALL);
            .checkForPropertyInfoScreen()               //  Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
            .navigateBackToListView()                   //  navigateBackToListView();
            .checkInsertedProperty(200)     //  Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
            .tapOnProperty()                            //  tapOnProperty();
            .checkNativeMessageDisplayed()              //  Assert.assertTrue(checkNativeMessageDisplayed());
            .navigateBackToListView()
            .checkForPropertyInfoInList()               //  Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
    }

    @Test
    fun checkConsentWithSaveAndExitActionFromPrivacyManager() = runBlocking<Unit> {

        scenario = launchActivity()

        MetaAppTestsKRobot()
            .addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION)
            .checkWebViewDisplayedForMessage(200)
            .tapManagePreferencesOnWebView()
            .selectPartialConsentList()
            .tapSaveAndExitOnWebView()
            .checkForConsentAreDisplayed()
            .navigateBackToListView(400)
            .checkInsertedProperty()
            .tapOnProperty()
            .checkWebViewDisplayedForMessage(200)
            .tapManagePreferencesOnWebView()
            .selectPartialConsentList()                 // Assert.assertTrue(checkConsentsAsSelected(PARTIAL_CONSENT_LIST))
    }

    @Test
    fun checkNativeMessageAcceptAllDirectPMLoad() = runBlocking<Unit> {

        scenario = launchActivity()

        MetaAppTestsKRobot()
            .tapOnAddProperty()
            .addNativeMessagePropertyDetails()
            .tapOnSave()
            .checkNativeMessageDisplayed()
            .tapAcceptAll()
            .checkForPropertyInfoScreen()
            .loadPrivacyManagerDirect()
//            .checkWebViewDisplayedForPrivacyManager()
            .selectNativeMessageConsentList()
            .tapRejectAllOnWebView()
            .checkForPropertyInfoScreen()
            .loadPrivacyManagerDirect()
//            .checkWebViewDisplayedForPrivacyManager()
            .selectNativeMessageConsentList()
    }

    @Test
    fun checkNativeMessageRejectAllDirectPMLoad() = runBlocking<Unit> {

        scenario = launchActivity()

        MetaAppTestsKRobot()
            .tapOnAddProperty()                         //    tapOnAddProperty();
            .addNativeMessagePropertyDetails()          //    addNativeMessagePropertyDetails();
            .tapOnSave()                                //    tapOnSave();
            .checkNativeMessageDisplayed()              //    Assert.assertTrue(checkNativeMessageDisplayed());
            .tapRejectAll()                             //    chooseNativeMessageAction(R.id.RejectAll);
            .checkForPropertyInfoScreen()               //    Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
            .loadPrivacyManagerDirect()                 //    loadPrivacyManagerDirect();
//            .checkWebViewDisplayedForPrivacyManager()   //    Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
            .selectNativeMessageConsentList()           //    Assert.assertFalse(checkConsentsAsSelected(NATIVE_MESSAGE_CONSENT_LIST));
            .tapPMAcceptAllOnWebView()                    //    chooseAction(PM_ACCEPT_ALL);
            .checkForPropertyInfoScreen()               //    Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
            .loadPrivacyManagerDirect()                 //    loadPrivacyManagerDirect();
//            .checkWebViewDisplayedForPrivacyManager()   //    Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
            .selectNativeMessageConsentList()           //    Assert.assertTrue(checkConsentsAsSelected(NATIVE_MESSAGE_CONSENT_LIST));
    }

    @Test
    fun checkNativeMessageSave_ExitFromPMViaMessage() = runBlocking<Unit> {

        scenario = launchActivity()

        MetaAppTestsKRobot()
            .tapOnAddProperty()
            .addNativeMessagePropertyDetails()
            .tapOnSave()
            .checkNativeMessageDisplayed()
            .tapShowOption()
            .checkWebViewDisplayedForPrivacyManager()
            .tapSaveAndExitOnWebView()
            .checkForPropertyInfoScreen()
            .navigateBackToListView()
            .tapOnProperty()
            .checkNativeMessageDisplayed()
            .navigateBackToListView()
            .checkForPropertyInfoInList()               // Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
    }

    @Test
    fun checkConsentsOnMessageDismiss() = runBlocking<Unit> {

        scenario = launchActivity()

        MetaAppTestsKRobot()
            .tapOnAddProperty()                         //  tapOnAddProperty();
            .addPropertyWithAllFields()                 //  addPropertyWith(ALL_FIELDS);
            .tapOnSave()                                //  tapOnSave();
            .checkWebViewDisplayedForMessage()          //  Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
            .tapDismissWebView()                        //  chooseDismiss();
            .checkForPropertyInfoScreen()               //  Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
            .checkConsentNotDisplayed()                 //  Assert.assertTrue(checkFor(CONSENTS_ARE_NOT_DISPLAYED));
    }

    @Test
    fun resetConsentDataAndCheckForMessageWithShowMessageOnce() = runBlocking<Unit> {

        scenario = launchActivity()

        MetaAppTestsKRobot()
            .addPropertyFor(SHOW_MESSAGE_ONCE, NO_AUTHENTICATION)   //  addPropertyFor(SHOW_MESSAGE_ONCE, NO_AUTHENTICATION);
            .checkWebViewDisplayedForMessage()                      //  Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
            .tapManagePreferencesOnWebView()                        //  chooseAction(MANAGE_PREFERENCES);
//            .checkWebViewDisplayedForPrivacyManager()               //  Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
            .checkConsentListNotSelected()                          //  Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
            .tapPMAcceptAllOnWebView()                                //  chooseAction(PM_ACCEPT_ALL);
            .checkForConsentsAreDisplayed()                         //  Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
            .navigateBackToListView()                               //  navigateBackToListView();
            .checkInsertedProperty()                                //  Assert.assertTrue(checkForPropertyListScrren());
            .tapOnProperty()                                        //  tapOnProperty();
            .checkWebViewDoesNotDisplayTheMessage()                 //  Assert.assertFalse(checkWebViewDisplayedFor(MESSAGE));
            .checkForConsentsAreDisplayed()                         //  Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
            .navigateBackToListView()                               //  navigateBackToListView();
            .swipeAndChooseResetAction()                            //  swipeAndChooseAction(RESET_ACTION, YES);
            .checkWebViewDisplayedForMessage()                      //  Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
            .tapManagePreferencesOnWebView()                        //  chooseAction(MANAGE_PREFERENCES);
            .checkWebViewDisplayedForPrivacyManager()               //  Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
            .checkConsentListNotSelected()                          //  Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    fun checkConsentForPropertyWithDifferentAuthenticationAlwaysWithDifferentAuthID() = runBlocking<Unit> {

        scenario = launchActivity()

        MetaAppTestsKRobot()
            .addPropertyFor(SHOW_MESSAGE_ALWAYS, UNIQUE_AUTHENTICATION)     //        addPropertyFor(SHOW_MESSAGE_ALWAYS, UNIQUE_AUTHENTICATION);
            .checkWebViewDisplayedForMessage()                              //        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
            .tapAcceptAllOnWebView()                                        //        chooseAction(ACCEPT_ALL);
            .checkForConsentsAreDisplayed()                                 //        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
            .navigateBackToListView()                                       //        navigateBackToListView();
            .addPropertyFor(SHOW_MESSAGE_ALWAYS, UNIQUE_AUTHENTICATION)     //        addPropertyFor(SHOW_MESSAGE_ALWAYS, UNIQUE_AUTHENTICATION);
            .checkWebViewDisplayedForMessage()                              //        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
            .tapManagePreferencesOnWebView()                                //        chooseAction(MANAGE_PREFERENCES);
            //        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
            .checkConsentListNotSelected()                                  //        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
    }

}
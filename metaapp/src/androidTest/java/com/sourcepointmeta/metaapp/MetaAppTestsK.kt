package com.sourcepointmeta.metaapp

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.sourcepointmeta.metaapp.TestData.NO_AUTHENTICATION
import com.sourcepointmeta.metaapp.TestData.SHOW_MESSAGE_ALWAYS
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
            .checkWebViewDisplayedForPrivacyManager()   // Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
            .tapRejectAllOnWebView()                    // chooseAction(PM_REJECT_ALL);
            .checkForPropertyInfoScreen()               // Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
            .navigateBackToListView()                   // navigateBackToListView();
            .checkInsertedProperty()                    // Assert.assertTrue(checkForPropertyListScrren());
            .tapOnProperty()                            // tapOnProperty();
            .checkNativeMessageDisplayed()              // Assert.assertTrue(checkNativeMessageDisplayed());
            .checkForPropertyInfoScreen()               // Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
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
            .loadPrivacyManagerDirect()                 //  loadPrivacyManagerDirect();
            .checkWebViewDisplayedForPrivacyManager()   //  Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
            .selectNativeMessageConsentList()           //  Assert.assertFalse(checkConsentsAsSelected(NATIVE_MESSAGE_CONSENT_LIST));
            .selectPartialConsentList()                 //  selectConsents(PARTIAL_CONSENT_LIST);
            .tapSaveAndExitOnWebView()                  //  chooseAction(PM_SAVE_AND_EXIT);
            .checkForPropertyInfoScreen()               //  Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
            .loadPrivacyManagerDirect()                 //  loadPrivacyManagerDirect();
            .checkWebViewDisplayedForPrivacyManager()   //  Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
            .selectPartialConsentList()                 //  Assert.assertTrue(checkConsentsAsSelected(PARTIAL_CONSENT_LIST));
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
            .checkWebViewDisplayedForPrivacyManager()   //  Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
            .selectNativeMessageConsentList()           //  Assert.assertFalse(checkConsentsAsSelected(NATIVE_MESSAGE_CONSENT_LIST));
            .tapSaveAndExitOnWebView()                  //  chooseAction(PM_SAVE_AND_EXIT);
            .navigateBackToListView()                   //  navigateBackToListView();
            .checkInsertedProperty()                    //  Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
            .tapOnProperty()                            //  tapOnProperty();
            .checkForPropertyInfoScreen()               //  Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
    }

    @Test
    fun checkNativeMessageAcceptAllFromPM() = runBlocking<Unit> {

        scenario = launchActivity()

        MetaAppTestsKRobot()
            .tapOnAddProperty()
            .addNativeMessagePropertyDetails()
            .tapOnSave()
            .checkNativeMessageDisplayed()
            .tapShowOption()
            .checkWebViewDisplayedForPrivacyManager()
            .tapAcceptAllOnWebView()
            .checkForPropertyInfoScreen()
            .navigateBackToListView()
            .checkInsertedProperty()
            .tapOnProperty()
            .checkNativeMessageDisplayed()
            .checkForPropertyInfoScreen()
    }

    @Test
    fun checkConsentWithSaveAndExitActionFromPrivacyManager() = runBlocking<Unit> {

        scenario = launchActivity()

        MetaAppTestsKRobot()
            .addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION)
            .checkWebViewDisplayedForMessage()
            .tapManagePreferencesOnWebView()
            .checkWebViewDisplayedForPrivacyManager()
            .selectPartialConsentList()
            .tapSaveAndExitOnWebView()
            .checkForConsentAreDisplayed()
            .navigateBackToListView()
            .checkInsertedProperty()
            .tapOnProperty()
            .checkWebViewDisplayedForMessage()
            .tapManagePreferencesOnWebView()
            .checkWebViewDisplayedForPrivacyManager()
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
            .checkWebViewDisplayedForPrivacyManager()
            .selectNativeMessageConsentList()
            .tapRejectAllOnWebView()
            .checkForPropertyInfoScreen()
            .loadPrivacyManagerDirect()
            .checkWebViewDisplayedForPrivacyManager()
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
            .checkWebViewDisplayedForPrivacyManager()   //    Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
            .selectNativeMessageConsentList()           //    Assert.assertFalse(checkConsentsAsSelected(NATIVE_MESSAGE_CONSENT_LIST));
            .tapAcceptAllOnWebView()                    //    chooseAction(PM_ACCEPT_ALL);
            .checkForPropertyInfoScreen()               //    Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
            .loadPrivacyManagerDirect()                 //    loadPrivacyManagerDirect();
            .checkWebViewDisplayedForPrivacyManager()   //    Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
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
            .checkInsertedProperty()
            .tapOnProperty()
            .checkNativeMessageDisplayed()
            .checkForPropertyInfoScreen()
    }

}
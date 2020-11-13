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
            .tapOnAddProperty()
            .addNativeMessagePropertyDetails()
            .tapOnSave()
            .checkNativeMessageDisplayed()
            .tapShowOption()
            .checkWebViewDisplayedForPrivacyManager()
            .tapRejectAllOnWebView()
            .checkForPropertyInfoScreen()
            .navigateBackToListView()
            .checkInsertedProperty()
            .tapOnProperty()
            .checkNativeMessageDisplayed()
            .checkForPropertyInfoScreen()
    }

    @Test
    fun checkNativeMessageSave_ExitWithFewPurposesDirectPMLoad() = runBlocking<Unit> {

        scenario = launchActivity()

        MetaAppTestsKRobot()
            .tapOnAddProperty()
            .addNativeMessagePropertyDetails()
            .tapOnSave()
            .checkNativeMessageDisplayed()
            .tapRejectAll()
            .checkForPropertyInfoScreen()
            .loadPrivacyManagerDirect()
            .checkWebViewDisplayedForPrivacyManager()
            .selectNativeMessageConsentList()
            .selectPartialConsentList()
            .tapSaveAndExitOnWebView()
            .checkForPropertyInfoScreen()
            .loadPrivacyManagerDirect()
            .checkWebViewDisplayedForPrivacyManager()
            .selectPartialConsentList()
    }

    @Test
    fun checkNativeMessageSave_ExitDirectPMLoad() = runBlocking<Unit> {

        scenario = launchActivity()

        MetaAppTestsKRobot()
            .tapOnAddProperty()
            .addNativeMessagePropertyDetails()
            .tapOnSave()
            .checkNativeMessageDisplayed()
            .tapRejectAll()
            .checkForPropertyInfoScreen()
            .loadPrivacyManagerDirect()
            .checkWebViewDisplayedForPrivacyManager()
            .selectNativeMessageConsentList()
            .tapSaveAndExitOnWebView()
            .navigateBackToListView()
            .checkInsertedProperty()
            .tapOnProperty()
            .checkForPropertyInfoScreen()
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
            .selectPartialConsentList()
    }

    @Test
    fun checkNativeMessageAcceptAllDirectPMLoad() = runBlocking<Unit> {

        scenario = launchActivity()

        MetaAppTestsKRobot()
            .tapOnAddProperty()
            .addNativeMessagePropertyDetails()
            .tapOnSave()
            .checkNativeMessageDisplayed()
            .tapRejectAll()
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
            .tapOnAddProperty()
            .addNativeMessagePropertyDetails()
            .tapOnSave()
            .checkNativeMessageDisplayed()
            .tapRejectAll()
            .checkForPropertyInfoScreen()
            .loadPrivacyManagerDirect()
            .checkWebViewDisplayedForPrivacyManager()
            .selectNativeMessageConsentList()
            .tapAcceptAllOnWebView()
            .checkForPropertyInfoScreen()
            .loadPrivacyManagerDirect()
            .checkWebViewDisplayedForPrivacyManager()
            .selectNativeMessageConsentList()
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

    /*
    @Test
    public void checkNativeMessageRejectAllDirectPMLoad() throws InterruptedException {
        tapOnAddProperty();
        addNativeMessagePropertyDetails();
        tapOnSave();
        Assert.assertTrue(checkNativeMessageDisplayed());
        chooseNativeMessageAction(R.id.RejectAll);
        Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
        loadPrivacyManagerDirect();
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertFalse(checkConsentsAsSelected(NATIVE_MESSAGE_CONSENT_LIST));
        chooseAction(PM_ACCEPT_ALL);
        Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
        CountDownLatch signal = new CountDownLatch(1);
        signal.await(1, TimeUnit.SECONDS);
        loadPrivacyManagerDirect();
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertTrue(checkConsentsAsSelected(NATIVE_MESSAGE_CONSENT_LIST));
    }
     */

}
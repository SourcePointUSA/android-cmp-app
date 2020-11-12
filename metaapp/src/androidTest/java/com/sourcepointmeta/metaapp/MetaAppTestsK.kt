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

    /*
@Test
    public void checkConsentWithSaveAndExitActionFromPrivacyManager() {
        addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION); #
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE)); #
        chooseAction(MANAGE_PREFERENCES); #
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER)); #
        Assert.assertFalse(checkConsentsAsSelected(PARTIAL_CONSENT_LIST));#
        selectConsents(PARTIAL_CONSENT_LIST);#
        chooseAction(PM_SAVE_AND_EXIT);#
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));#
        navigateBackToListView();
        Assert.assertTrue(checkForPropertyListScrren());
        tapOnProperty();
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(MANAGE_PREFERENCES);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertTrue(checkConsentsAsSelected(PARTIAL_CONSENT_LIST));
    }
     */

}
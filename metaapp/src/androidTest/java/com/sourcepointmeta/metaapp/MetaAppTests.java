package com.sourcepointmeta.metaapp;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.sourcepointmeta.metaapp.ui.SplashScreenActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MetaAppTests extends Utility{

    @Rule
    public ActivityTestRule<SplashScreenActivity> mActivityTestRule = new ActivityTestRule<>(SplashScreenActivity.class);

    @Before
    public void setup() {
        mActivityTestRule.getActivity();
    }

    @Test
    public void checkForConsentWithAcceptActionFromMessage() {
        addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(ACCEPT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
        navigateBackToListView();
        tapOnProperty();
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(MANAGE_PREFERENCES);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    public void checkNoConsentsWithRejectActionFromMessage() {
        addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(REJECT_ALL);
        Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
        Assert.assertTrue(checkFor(CONSENTS_ARE_NOT_DISPLAYED));
        navigateBackToListView();
        tapOnProperty();
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(MANAGE_PREFERENCES);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    public void checkConsentsWithAcceptActionFromPrivacyManager() {
        addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(MANAGE_PREFERENCES);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
        chooseAction(PM_ACCEPT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
        navigateBackToListView();
        tapOnProperty();
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(MANAGE_PREFERENCES);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    public void checkConsentsWithRejectActionFromPrivacyManager() {
        addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(MANAGE_PREFERENCES);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
        chooseAction(PM_REJECT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_NOT_DISPLAYED));
        navigateBackToListView();
        tapOnProperty();
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(MANAGE_PREFERENCES);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    public void checkConsentWithSaveAndExitActionFromPrivacyManager() {
        addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(MANAGE_PREFERENCES);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertFalse(checkConsentsAsSelected(PARTIAL_CONSENT_LIST));
        selectConsents(PARTIAL_CONSENT_LIST);
        chooseAction(PM_SAVE_AND_EXIT);
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
        navigateBackToListView();
        tapOnProperty();
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(MANAGE_PREFERENCES);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertTrue(checkConsentsAsSelected(PARTIAL_CONSENT_LIST));
    }

    @Test
    public void checkCancelActionFromPrivacyManager() {
        addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(MANAGE_PREFERENCES);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
        chooseAction(PM_CANCEL);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
    }

    @Test
    public void checkConsentFromDirectPrivacyManager() {
        addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(MANAGE_PREFERENCES);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
        chooseAction(PM_ACCEPT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
        loadPrivacyManagerDirect();
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    public void checkCancelFromDirectPrivacyManager() {
        addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(MANAGE_PREFERENCES);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
        chooseAction(PM_ACCEPT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
        loadPrivacyManagerDirect();
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
        chooseAction(PM_CANCEL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
    }

    @Test
    public void checkNoMessageDisplayWithShowMessageOnce() {
        addPropertyFor(SHOW_MESSAGE_ONCE, NO_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(ACCEPT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
        navigateBackToListView();
        tapOnProperty();
        Assert.assertFalse(checkWebViewDisplayedFor(MESSAGE));
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
    }

    @Test
    public void resetConsentDataAndCheckForMessageWithShowMessageOnce() {
        addPropertyFor(SHOW_MESSAGE_ONCE, NO_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(MANAGE_PREFERENCES);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
        chooseAction(PM_ACCEPT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
        navigateBackToListView();
        tapOnProperty();
        Assert.assertFalse(checkWebViewDisplayedFor(MESSAGE));
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
        navigateBackToListView();
        swipeAndChooseAction(RESET_ACTION, YES);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(MANAGE_PREFERENCES);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    public void resetConsentDataAndCheckForConsentWithShowMessageAlways() {
        addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(MANAGE_PREFERENCES);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
        chooseAction(PM_ACCEPT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
        navigateBackToListView();
        swipeAndChooseAction(RESET_ACTION, YES);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(MANAGE_PREFERENCES);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    public void deleteProperty() {
        addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(REJECT_ALL);
        Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
        Assert.assertTrue(checkFor(CONSENTS_ARE_NOT_DISPLAYED));
        navigateBackToListView();
        swipeAndChooseAction(DELETE_ACTION, NO);
        Assert.assertTrue(checkPropertyPresent());
        swipeAndChooseAction(DELETE_ACTION, YES);
        Assert.assertFalse(checkPropertyPresent());
    }

    @Test
    public void checkMessageAfterEditProperty() {
        addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(ACCEPT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
        navigateBackToListView();
        swipeAndChooseAction(EDIT_ACTION, PARAM_VALUE );
        Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
        loadPrivacyManagerDirect();
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    public void checkConsentWithAuthenticationToShowMessageAlways()  {
        addPropertyFor(SHOW_MESSAGE_ALWAYS, UNIQUE_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(ACCEPT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
        navigateBackToListView();
        tapOnProperty();
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
        chooseAction(MANAGE_PREFERENCES);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    public void checkConsentWithAuthenticationToShowMessageOnce()  {
        addPropertyFor(SHOW_MESSAGE_ONCE, UNIQUE_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(ACCEPT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
        navigateBackToListView();
        tapOnProperty();
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
        loadPrivacyManagerDirect();
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    public void checkConsentForPropertyWithDifferentAuthenticationAlwaysWithDifferentAuthID()  {
        addPropertyFor(SHOW_MESSAGE_ALWAYS, UNIQUE_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(ACCEPT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
        navigateBackToListView();
        addPropertyFor(SHOW_MESSAGE_ALWAYS, UNIQUE_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(MANAGE_PREFERENCES);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    public void checkConsentWithSameAuthenticationWhenPropertyDeleteAndRecreate()  {
        addPropertyFor(SHOW_MESSAGE_ALWAYS, UNIQUE_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(ACCEPT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
        navigateBackToListView();
        swipeAndChooseAction(DELETE_ACTION, YES);
        addPropertyFor(SHOW_MESSAGE_ALWAYS, EXISTING_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(MANAGE_PREFERENCES);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    public void checkConsentOnDirectPMLoadWhenPMConfiguredAsMessage()  {
        addPropertyFor(PM_AS_FIRST_LAYER_MESSAGE, UNIQUE_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
        chooseAction(PM_ACCEPT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
        loadPrivacyManagerDirect();
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
    }

    @Test
    public void checkConsentWithAuthenticationFromPrivacyManagerAsMessage()  {
        addPropertyFor(PM_AS_FIRST_LAYER_MESSAGE, UNIQUE_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
        chooseAction(PM_ACCEPT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
        navigateBackToListView();
        tapOnProperty();
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
    }

    @Test
    public void checkCancelFromPrivacyManagerDisplayedAsFirstLayerMessage()  {
        addPropertyFor(PM_AS_FIRST_LAYER_MESSAGE, UNIQUE_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
        chooseAction(PM_ACCEPT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
        loadPrivacyManagerDirect();
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
        chooseAction(PM_CANCEL);
        Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
    }

    @Test
    public void checkNoMessageAfterLoggedInWithAuthIDWhenConsentAlreadyGiven() {
        addPropertyFor(SHOW_MESSAGE_ONCE, NO_AUTHENTICATION);
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(ACCEPT_ALL);
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
        navigateBackToListView();
        swipeAndChooseAction(EDIT_ACTION, UNIQUE_AUTHENTICATION);
        Assert.assertFalse(checkWebViewDisplayedFor(MESSAGE));
        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
    }

    @Test
    public void checkConsentsOnMessageDismiss() {
        tapOnAddProperty();
        addPropertyWith(ALL_FIELDS);
        tapOnSave();
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseDismiss();
        Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
        Assert.assertTrue(checkFor(CONSENTS_ARE_NOT_DISPLAYED));
    }

    @Test
    public void checkPMTabSelected(){
        tapOnAddProperty();
        addPropertyDetails(Example_accountID, Example_propertyID, Example_propertyName, Example_pmID);
        tapOnSave();
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(OPTIONS);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        checkPMTabSelected(FEATURES);
        chooseAction(PM_SAVE_AND_EXIT);
        Assert.assertTrue(checkFor(PROPERTY_INFO_SCREEN));
        loadPrivacyManagerDirect();
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        checkPMTabSelected(PURPOSES);
    }
}

package com.sourcepointmeta.metaapp

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.uitestutil.wr
import com.sourcepointmeta.metaapp.MetaAppTestCases.Companion.addPropertyFor
import com.sourcepointmeta.metaapp.MetaAppTestCases.Companion.addPropertyNoAccountId
import com.sourcepointmeta.metaapp.MetaAppTestCases.Companion.addPropertyNoParamKey
import com.sourcepointmeta.metaapp.MetaAppTestCases.Companion.addPropertyNoParamKeyValue
import com.sourcepointmeta.metaapp.MetaAppTestCases.Companion.addPropertyNoParamValue
import com.sourcepointmeta.metaapp.MetaAppTestCases.Companion.addPropertyNoPmId
import com.sourcepointmeta.metaapp.MetaAppTestCases.Companion.addPropertyNoPropertyId
import com.sourcepointmeta.metaapp.MetaAppTestCases.Companion.addPropertyNoPropertyName
import com.sourcepointmeta.metaapp.MetaAppTestCases.Companion.addPropertyWithAllFieldsBlank
import com.sourcepointmeta.metaapp.MetaAppTestCases.Companion.addPropertyWrongAccountId
import com.sourcepointmeta.metaapp.MetaAppTestCases.Companion.addPropertyWrongCampaign
import com.sourcepointmeta.metaapp.MetaAppTestCases.Companion.addPropertyWrongPrivacyManager
import com.sourcepointmeta.metaapp.MetaAppTestCases.Companion.addPropertyWrongPropertyName
import com.sourcepointmeta.metaapp.MetaAppTestCases.Companion.checkConsentNotDisplayed
import com.sourcepointmeta.metaapp.MetaAppTestCases.Companion.checkForConsentsAreDisplayed
import com.sourcepointmeta.metaapp.MetaAppTestCases.Companion.checkWebViewDisplayedForMessage
import com.sourcepointmeta.metaapp.MetaAppTestCases.Companion.checkWebViewDoesNotDisplayTheMessage
import com.sourcepointmeta.metaapp.MetaAppTestCases.Companion.loadPrivacyManagerDirect
import com.sourcepointmeta.metaapp.MetaAppTestCases.Companion.navigateBackToListView
import com.sourcepointmeta.metaapp.MetaAppTestCases.Companion.tapAcceptAllOnWebView
import com.sourcepointmeta.metaapp.MetaAppTestCases.Companion.tapOkPopupError
import com.sourcepointmeta.metaapp.MetaAppTestCases.Companion.tapOkPopupErrorParameter
import com.sourcepointmeta.metaapp.MetaAppTestCases.Companion.tapOkPopupErrorPropertyExist
import com.sourcepointmeta.metaapp.MetaAppTestCases.Companion.tapOkPopupErrorUnableLoadPm
import com.sourcepointmeta.metaapp.MetaAppTestCases.Companion.tapOnAddProperty
import com.sourcepointmeta.metaapp.MetaAppTestCases.Companion.tapOnSave
import com.sourcepointmeta.metaapp.TestData.NO_AUTHENTICATION
import com.sourcepointmeta.metaapp.TestData.SHOW_MESSAGE_ALWAYS
import com.sourcepointmeta.metaapp.ui.SplashScreenActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MetaAppValidationTestsK {

    lateinit var scenario: ActivityScenario<SplashScreenActivity>

    @After
    fun cleanup() {
        scenario.close()
    }

    @Test
    fun checkErrorForMandatoryFieldsWhileCreatingProperty() = runBlocking {

        scenario = launchActivity()

        tapOnAddProperty()                 //        tapOnAddProperty();
        addPropertyWithAllFieldsBlank()    //        addPropertyWith(ALL_FIELDS_BLANK);
        tapOnSave()                        //        tapOnSave();
        tapOkPopupError()                  //        Assert.assertTrue(checkErrorFor(MANDATORY_FIELDS));
        addPropertyNoAccountId()           //        addPropertyWith(NO_ACCOUNT_ID);
        tapOnSave()                        //        tapOnSave();
        tapOkPopupError()                  //        Assert.assertTrue(checkErrorFor(MANDATORY_FIELDS));
        addPropertyNoPropertyId()          //        addPropertyWith(NO_PROPERTY_ID);
        tapOnSave()                        //        tapOnSave();
        tapOkPopupError()                  //        Assert.assertTrue(checkErrorFor(MANDATORY_FIELDS));
        addPropertyNoPropertyName()        //        addPropertyWith(NO_PROPERTY_NAME);
        tapOnSave()                        //        tapOnSave();
        tapOkPopupError()                  //        Assert.assertTrue(checkErrorFor(MANDATORY_FIELDS));
        addPropertyNoPmId()                //        addPropertyWith(NO_PM_ID);
        tapOnSave()                        //        tapOnSave();
        tapOkPopupError()                  //        Assert.assertTrue(checkErrorFor(MANDATORY_FIELDS));
    }

    @Test
    fun checkErrorForTargetingParamFieldsWhileCreatingProperty() = runBlocking {

        scenario = launchActivity()

        tapOnAddProperty()                  //        tapOnAddProperty();
        addPropertyNoParamKeyValue()        //        addPropertyWith(NO_PARAMETER_KEY_VALUE);
        tapOkPopupErrorParameter()          //        Assert.assertTrue(checkErrorFor(TARGETING_PARAMETER_FIELDS));
        addPropertyNoParamKey()             //        addPropertyWith(NO_PARAMETER_KEY);
        tapOkPopupErrorParameter()          //        Assert.assertTrue(checkErrorFor(TARGETING_PARAMETER_FIELDS));
        addPropertyNoParamValue()           //        addPropertyWith(NO_PARAMETER_VALUE);
        wr{ tapOkPopupErrorParameter() }         //        Assert.assertTrue(checkErrorFor(TARGETING_PARAMETER_FIELDS));
    }

    @Test
    fun checkNoMessageDisplayedForWrongCampaign() = runBlocking {

        scenario = launchActivity()

        tapOnAddProperty()                          //        tapOnAddProperty();
        addPropertyWrongCampaign()                  //        addPropertyWith(WRONG_CAMPAIGN);
        tapOnSave()                                 //        tapOnSave();
        wr { checkConsentNotDisplayed() }           //        Assert.assertTrue(checkFor(CONSENTS_ARE_NOT_DISPLAYED));
        checkWebViewDoesNotDisplayTheMessage()      //        Assert.assertFalse(checkWebViewDisplayedFor(MESSAGE));
    }

    @Test
    fun checkNoMessageDisplayedForWrongAccountId() = runBlocking {

        scenario = launchActivity()

        tapOnAddProperty()                          //        tapOnAddProperty();
        addPropertyWrongAccountId()                 //        addPropertyWith(WRONG_ACCOUNT_ID);
        tapOnSave()                                 //        tapOnSave();
        wr { checkConsentNotDisplayed() }           //        Assert.assertTrue(checkFor(CONSENTS_ARE_NOT_DISPLAYED));
        checkWebViewDoesNotDisplayTheMessage()      //        Assert.assertFalse(checkWebViewDisplayedFor(MESSAGE));
    }

    @Test
    fun checkNoMessageDisplayedForWrongPropertyName() = runBlocking {

        scenario = launchActivity()

        tapOnAddProperty()                          //        tapOnAddProperty();
        addPropertyWrongPropertyName()              //        addPropertyWith(WRONG_PROPERTY_NAME);
        tapOnSave()                                 //        tapOnSave();
        wr { checkConsentNotDisplayed() }           //        Assert.assertTrue(checkFor(CONSENTS_ARE_NOT_DISPLAYED));
        checkWebViewDoesNotDisplayTheMessage()      //        Assert.assertFalse(checkWebViewDisplayedFor(MESSAGE));
    }

    @Test
    fun checkErrorMessageForDuplicateProperty() = runBlocking {

        scenario = launchActivity()

        addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION)          //        addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION);
        wr { checkWebViewDisplayedForMessage() }                        //        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        wr { tapAcceptAllOnWebView() }                                  //        chooseAction(ACCEPT_ALL);
        wr { checkForConsentsAreDisplayed() }                           //        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
        wr { navigateBackToListView() }                                 //        navigateBackToListView();
        wr { addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION) }   //        addPropertyFor(SHOW_MESSAGE_ALWAYS, NO_AUTHENTICATION);
        wr { tapOkPopupErrorPropertyExist() }                                //        Assert.assertTrue(checkErrorFor(PROPERTY_EXITS_ERROR));
    }

    @Test
    fun checkMessageForWrongPrivacyManagerId() = runBlocking {

        scenario = launchActivity()

        tapOnAddProperty()                          //        tapOnAddProperty();
        addPropertyWrongPrivacyManager()            //        addPropertyWith(WRONG_PRIVACY_MANAGER);
        tapOnSave()                                 //        tapOnSave();
        wr { checkWebViewDisplayedForMessage() }    //        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
    }

    @Test
    fun checkDirectPMLoadWithWrongPrivacyManagerId() = runBlocking {

        scenario = launchActivity()

        tapOnAddProperty()                                  //        tapOnAddProperty();
        addPropertyWrongPrivacyManager()                    //        addPropertyWith(WRONG_PRIVACY_MANAGER);
        tapOnSave()                                         //        tapOnSave();
        wr { checkWebViewDisplayedForMessage() }            //        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        wr { tapAcceptAllOnWebView() }                      //        chooseAction(ACCEPT_ALL);
        wr { checkForConsentsAreDisplayed() }               //        Assert.assertTrue(checkFor(CONSENTS_ARE_DISPLAYED));
        wr { loadPrivacyManagerDirect() }                   //        loadPrivacyManagerDirect();
        wr(times = 150) { tapOkPopupErrorUnableLoadPm() }   //        Assert.assertTrue(checkErrorFor(UNABLE_TO_LOAD_PM_ERROR));
    }
}
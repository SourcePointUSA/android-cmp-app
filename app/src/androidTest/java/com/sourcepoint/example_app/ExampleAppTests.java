package com.sourcepoint.example_app;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class ExampleAppTests extends Utility {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setup() {
        mActivityTestRule.getActivity();
        }

//    @Test
//    public void checkAcceptActionFromMessage(){
//        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
//        chooseAction(ACCEPT);
//        Assert.assertTrue(checkMainWebViewDisplayed());
//        clickOnReviewConsent();
//        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
//        Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
//    }

  //  @Test
    public void checkRejectActionFromMessage(){
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(REJECT);
        Assert.assertTrue(checkMainWebViewDisplayed());
        clickOnReviewConsent();
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
    }

//    @Test
//    public void checkAcceptActionFromPrivacyManager(){
//        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
//        chooseAction(OPTIONS);
//        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
//        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
//        chooseAction(ACCEPT_ALL);
//        Assert.assertTrue(checkMainWebViewDisplayed());
//        clickOnReviewConsent();
//        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
//        Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
//    }

 //   @Test
    public void checkRejectActionFromPrivacyManager(){
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction(OPTIONS);
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
        chooseAction(REJECT_ALL);
        Assert.assertTrue(checkMainWebViewDisplayed());
        clickOnReviewConsent();
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
    }

//    @Test
//    public void checkSaveAndExitActionFromPrivacyManager(){
//        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
//        chooseAction(OPTIONS);
//        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
//        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
//        chooseAction(REJECT_ALL);
//        Assert.assertTrue(checkMainWebViewDisplayed());
//        clickOnReviewConsent();
//        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
//        selectConsents(PARTIAL_CONSENT_LIST);
//        chooseAction(SAVE_AND_EXIT);
//        Assert.assertTrue(checkMainWebViewDisplayed());
//        clickOnReviewConsent();
//        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
//        Assert.assertTrue(checkConsentsAsSelected(PARTIAL_CONSENT_LIST));
//    }

//    @Test
//    public void checkAcceptActionFromDirectPrivacyManager(){
//        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
//        chooseAction(REJECT);
//        Assert.assertTrue(checkMainWebViewDisplayed());
//        clickOnReviewConsent();
//        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
//        chooseAction(ACCEPT_ALL);
//        Assert.assertTrue(checkMainWebViewDisplayed());
//        clickOnReviewConsent();
//        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
//        Assert.assertTrue(checkConsentsAsSelected(CONSENT_LIST));
//    }

//    @Test
//    public void checkRejectActionFromDirectPrivacyManager(){
//        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
//        chooseAction(REJECT);
//        Assert.assertTrue(checkMainWebViewDisplayed());
//        clickOnReviewConsent();
//        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
//        chooseAction(REJECT_ALL);
//        Assert.assertTrue(checkMainWebViewDisplayed());
//        clickOnReviewConsent();
//        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
//        Assert.assertFalse(checkConsentsAsSelected(CONSENT_LIST));
//    }

//    @Test
//    public void checkSaveAndExitActionFromDirectPrivacyManager(){
//        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
//        chooseAction(REJECT);
//        Assert.assertTrue(checkMainWebViewDisplayed());
//        clickOnReviewConsent();
//        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
//        selectConsents(PARTIAL_CONSENT_LIST);
//        chooseAction(SAVE_AND_EXIT);
//        Assert.assertTrue(checkMainWebViewDisplayed());
//        clickOnReviewConsent();
//        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
//        Assert.assertTrue(checkConsentsAsSelected(PARTIAL_CONSENT_LIST));
//    }

//    @Test
//    public void checkMessageDismiss() {
//        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
//        chooseDismiss();
//        Assert.assertTrue(checkMainWebViewDisplayed());
//        clickOnReviewConsent();
//        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
//    }

//    @Test
//    public void checkShowOptionsFromMessage(){
//        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
//        chooseAction(OPTIONS);
//        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
//        Assert.assertTrue(checkPMTabSelected(FEATURES));
//
//        chooseAction(SAVE_AND_EXIT);
//        Assert.assertTrue(checkMainWebViewDisplayed());
//        clickOnReviewConsent();
//        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
//        Assert.assertTrue(checkPMTabSelected(PURPOSES));
//    }
}

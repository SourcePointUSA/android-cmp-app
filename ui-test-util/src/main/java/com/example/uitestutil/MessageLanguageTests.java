package com.sourcepointmeta.metaapp;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.sourcepointmeta.metaapp.ui.SplashScreenActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MessageLanguageTests extends Utility {

    @Rule
    public ActivityTestRule<SplashScreenActivity> mActivityTestRule = new ActivityTestRule<>(SplashScreenActivity.class);

    @Before
    public void setup() {
        mActivityTestRule.getActivity();
    }

    public void addMessageLanguageProperty() {
        String accountId = "22";
        String propertyId= "7639";
        String propertyName= "tcfv2.mobile.webview";
        String pmId = "122058";
        onView(allOf(withId(R.id.etAccountID), isDisplayed()))
                .perform(clearText(), replaceText(accountId), closeSoftKeyboard());

        onView(allOf(withId(R.id.etPropertyId), isDisplayed()))
                .perform(clearText(), replaceText(propertyId), closeSoftKeyboard());

        onView(allOf(withId(R.id.etPropertyName), isDisplayed()))
                .perform(clearText(), replaceText(propertyName), closeSoftKeyboard());

        onView(allOf(withId(R.id.etPMId), isDisplayed()))
                .perform(clearText(), replaceText(pmId), closeSoftKeyboard());

        onView(allOf(withId(R.id.spinnerMessageLanguage), isDisplayed()))
                .perform(click());
        onData(allOf(is(instanceOf(String.class)), is("FRENCH"))).perform(click());
    }

    @Test
    public void acceptAllActionOnFrenchLanguage(){
        tapOnAddProperty();
        addMessageLanguageProperty();
        tapOnSave();
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction("Accepter tout");
    }

    @Test
    public void rejectAllActionOnFrenchLanguage(){
        tapOnAddProperty();
        addMessageLanguageProperty();
        tapOnSave();
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction("Tout rejeter");
    }

    @Test
    public void tapOnOptionsOnFrenchLanguage(){
        tapOnAddProperty();
        addMessageLanguageProperty();
        tapOnSave();
        Assert.assertTrue(checkWebViewDisplayedFor(MESSAGE));
        chooseAction("Options");
        Assert.assertTrue(checkWebViewDisplayedFor(PRIVACY_MANAGER));
    }
}

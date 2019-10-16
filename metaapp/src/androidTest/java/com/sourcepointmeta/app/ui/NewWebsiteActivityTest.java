package com.sourcepointmeta.app.ui;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.sourcepointmeta.app.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class NewWebsiteActivityTest {

    @Rule
    public ActivityTestRule<NewWebsiteActivity> mActivityRule = new ActivityTestRule<NewWebsiteActivity>(
            NewWebsiteActivity.class);

    @Before
    public void init() {
        mActivityRule.getActivity();
    }

    @Test
    public void TestEditTextAccountID() {
        onView(withId(R.id.etAccountID)).check(matches((isDisplayed())));

        onView(withId(R.id.etAccountID)).perform(clearText(),typeText("Amr"));
    }

    @Test
    public void TestEditTextSiteName() {
        onView(withId(R.id.etSiteName)).check(matches((isDisplayed())));

        onView(withId(R.id.etSiteName)).perform(clearText(),typeText("mobile.demo"));
    }

    @Test
    public void TestStagingCampaignSwitch() {
        onView(withId(R.id.toggleStaging)).check(matches((isDisplayed())));

        onView(withId(R.id.etSiteName)).perform(clearText(),click());
    }

}

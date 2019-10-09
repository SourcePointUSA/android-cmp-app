package com.sourcepointmeta.app.ui;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.sourcepointmeta.app.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


@RunWith(AndroidJUnit4.class)
public class WebsiteListActivityTest {

    @Rule
    public ActivityTestRule<WebsiteListActivity> mActivityRule = new ActivityTestRule<WebsiteListActivity>(
            WebsiteListActivity.class);



    @Before
    public void init() {
        mActivityRule.getActivity();
    }


    @Test
    public void TestRecyclerViewItemClick() {
        onView(withId(R.id.websiteListRecycleView)).check(matches((isDisplayed())));
    }
}

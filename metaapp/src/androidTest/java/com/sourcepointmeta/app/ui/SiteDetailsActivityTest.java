package com.sourcepointmeta.app.ui;


import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.sourcepointmeta.app.R;
import com.sourcepointmeta.app.database.entity.Website;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class SiteDetailsActivityTest {

    @Rule
    public ActivityTestRule<SiteDetailsActivity> mActivityRule = new ActivityTestRule<SiteDetailsActivity>(SiteDetailsActivity.class){
        @Override
        protected Intent getActivityIntent() {
            Intent intent = new Intent(InstrumentationRegistry.getTargetContext(),SiteDetailsActivity.class);
            Website website = new Website(22,"mobile.demo",false);
            intent.putExtra("Website",website);
            return intent;
        }
    };

    @Test
    public void TestSiteDetailsComponentDisplayed(){
       // mActivityRule.getActivity();
        onView(withId(R.id.tvAccountID)).check(matches((isDisplayed())));
        onView(withId(R.id.tvSiteName)).check(matches((isDisplayed())));
        onView(withId(R.id.toggleStaging)).check(matches((isDisplayed())));

    }

    @Test
    public void TestSiteDetailsAreCorrect(){
       // mActivityRule.getActivity();
        onView(withId(R.id.tvAccountID)).check(matches(withText(containsString("22"))));
        onView(withId(R.id.tvSiteName)).check(matches(withText(containsString("mobile.demo"))));
        onView(withId(R.id.toggleStaging)).check(matches(not(isChecked())));
    }

}

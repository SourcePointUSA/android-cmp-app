package com.sourcepoint.example_app;

import androidx.test.espresso.web.webdriver.Locator;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.web.assertion.WebViewAssertions.webMatches;
import static androidx.test.espresso.web.model.Atoms.getCurrentUrl;
import static androidx.test.espresso.web.sugar.Web.onWebView;
import static androidx.test.espresso.web.webdriver.DriverAtoms.findElement;
import static androidx.test.espresso.web.webdriver.DriverAtoms.webClick;
import static androidx.test.espresso.web.webdriver.DriverAtoms.webScrollIntoView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.StringContains.containsString;

public class Utility extends TestData{
    final CountDownLatch signal = new CountDownLatch(1);

    public boolean checkWebViewDisplayedFor(String type) {
        int i = 0;
        boolean value = false;
        do {
            try {
                signal.await(3, TimeUnit.SECONDS);
                onWebView().forceJavascriptEnabled().check(webMatches(getCurrentUrl(), containsString(type)));
                value = true;
                break;
            } catch (Exception e) {
                i++;
            }
        } while (i < 10);
        return value;
    }

    public boolean checkMainWebViewDisplayed(){
        int i = 0;
        boolean value = false;
        do {
            try {
                signal.await(3, TimeUnit.SECONDS);
                onView(
                        allOf(withId(R.id.review_consents), withText("Review Consents"),
                                isDisplayed()));
                value = true;
                break;
            } catch (Exception e) {
                i++;
            }
        } while (i < 10);
        return value;
    }

    public void chooseAction(String option) {
        try {
            onWebView().forceJavascriptEnabled().withElement(findElement(Locator.XPATH, "//button[contains('" + option + "',text())]"))
                    .perform(webScrollIntoView())
                    .perform(webClick());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void chooseDismiss() {
        onWebView().forceJavascriptEnabled()
                .withElement(findElement(Locator.CLASS_NAME, "message-stacksclose"))
                .perform(webScrollIntoView())
                .perform(webClick());
    }

    public void clickOnReviewConsent(){
        try{
            onView(
                    allOf(withId(R.id.review_consents), withText("Review Consents"),
                            isDisplayed())).perform(click());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public boolean checkConsentsAsSelected(String[] userConsentArray) {
        boolean check = true;
        for (String s : userConsentArray) {
            try {
                onWebView().forceJavascriptEnabled()
                        .withElement(findElement(Locator.XPATH, "//label[@aria-label='" + s + "']"))
                        .withElement(findElement(Locator.XPATH, "//label[@aria-checked='true']"));
                check = true;
            } catch (Exception e) {
                e.printStackTrace();
                check = false;
            }
        }
        return check;
    }

    public void selectConsents(String[] userConsentArray) {
        for (String s : userConsentArray) {
            onWebView().forceJavascriptEnabled()
                    .withElement(findElement(Locator.XPATH, "//label[@aria-label='" + s + "']/span[@class='on']"))
                    .perform(webScrollIntoView())
                    .perform(webClick());
        }
    }

    public boolean checkPMTabSelected(String expected){
        boolean check = false;
        try {
            onWebView().forceJavascriptEnabled()
                    .withElement(findElement(Locator.XPATH, "//div[contains(@class, 'pm-tab active') and text()='"+ expected +"']"));
            check = true;
        } catch (Exception e) {
            e.printStackTrace();
            check = false;
        }
        return check;
    }
}

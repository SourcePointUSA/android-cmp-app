package com.sourcepointmeta.metaapp;

import androidx.test.espresso.web.webdriver.Locator;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
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

public class Utility extends TestData {
    final CountDownLatch signal = new CountDownLatch(1);

    public void tapOnAddProperty() {
        onView(allOf(withId(R.id.action_addProperty), withContentDescription("Add Property"), isDisplayed()))
                .perform(click());
    }

    public void addPropertyFor(String messageType, String authentication) {
        tapOnAddProperty();
        addPropertyDetails(accountID, propertyID, propertyName, pmID);
        if (messageType.equals(SHOW_MESSAGE_ALWAYS))
            addParameterWithAuthentication(keyParam, valueParamFrench, authentication);
        else if (messageType.equals(SHOW_MESSAGE_ONCE))
            addParameterWithAuthentication(keyParamShowOnce, valueParamShowOnce, authentication);
        else if (messageType.equals(PM_AS_FIRST_LAYER_MESSAGE))
            addParameterWithAuthentication(keyParamForPMAsMessage, valueParamForPMAsMessage, authentication);
        else if (messageType.equals(WRONG_CAMPAIGN))
            chooseCampaign(campaign);
        tapOnSave();
    }

    public void addPropertyWith(String field) {
        if (field.equals(ALL_FIELDS)){
            addPropertyDetails(accountID, propertyID, propertyName, pmID);
            addParameterWithAuthentication(keyParam, valueParamEnglish, NO_AUTHENTICATION);}
        else if (field.equals(ALL_FIELDS_BLANK))
            addPropertyDetails("", "", "", "");
        else if (field.equals(NO_ACCOUNT_ID))
            addPropertyDetails("", propertyID, propertyName, pmID);
        else if (field.equals(NO_PROPERTY_ID))
            addPropertyDetails(accountID, "", propertyName, pmID);
        else if (field.equals(NO_PROPERTY_NAME))
            addPropertyDetails(accountID, propertyID, "", pmID);
        else if (field.equals(NO_PM_ID))
            addPropertyDetails(accountID, propertyID, propertyName, "");
        else if (field.equals(NO_PARAMETER_KEY))
            addParameterWithAuthentication("", valueParamFrench, NO_AUTHENTICATION);
        else if (field.equals(NO_PARAMETER_VALUE))
            addParameterWithAuthentication(keyParam, "", NO_AUTHENTICATION);
        else if (field.equals(NO_PARAMETER_KEY_VALUE))
            addParameterWithAuthentication("", "", NO_AUTHENTICATION);
        else if (field.equals(WRONG_CAMPAIGN)){
            addPropertyDetails(accountID, propertyID, propertyName, pmID);
            chooseCampaign(STAGING_CAMPAIGN);}
        else if (field.equals(WRONG_ACCOUNT_ID))
            addPropertyDetails(accountID + "111", propertyID, propertyName, pmID);
        else if (field.equals(WRONG_PROPERTY_ID))
            addPropertyDetails(accountID, propertyID + "111", propertyName, pmID);
        else if (field.equals(WRONG_PROPERTY_NAME))
            addPropertyDetails(accountID, propertyID, propertyName + "111", pmID);
        else if (field.equals(WRONG_PRIVACY_MANAGER))
            addPropertyDetails(accountID, propertyID, propertyName, pmID + "111");
    }

    public void addParameterWithAuthentication(String key, String value, String authentication) {
        if (!authentication.equals(NO_AUTHENTICATION)) {
            addAuthentication(authentication);
        }
        onView(allOf(withId(R.id.etKey), isDisplayed()))
                .perform(clearText(), typeText(key), closeSoftKeyboard());

        onView(allOf(withId(R.id.etValue), isDisplayed()))
                .perform(clearText(), typeText(value), closeSoftKeyboard());

        onView(allOf(withId(R.id.btn_addParams), withText("Add"), isDisplayed()))
                .perform(click());
    }

    public void addAuthentication(String authentication) {
        if (authentication.equals(UNIQUE_AUTHENTICATION)) {
            onView(allOf(withId(R.id.etAuthID), isDisplayed()))
                    .perform(clearText(), typeText(authID()), closeSoftKeyboard());
        } else {
            onView(allOf(withId(R.id.etAuthID), isDisplayed()))
                    .perform(clearText(), typeText(authIdValue), closeSoftKeyboard());
        }
    }

    public void addPropertyDetails(String accountId, String propertyId, String propertyName, String pmId) {
        onView(allOf(withId(R.id.etAccountID), isDisplayed()))
                .perform(clearText(), replaceText(accountId), closeSoftKeyboard());

        onView(allOf(withId(R.id.etPropertyId), isDisplayed()))
                .perform(clearText(), replaceText(propertyId), closeSoftKeyboard());

        onView(allOf(withId(R.id.etPropertyName), isDisplayed()))
                .perform(clearText(), replaceText(propertyName), closeSoftKeyboard());

        onView(allOf(withId(R.id.etPMId), isDisplayed()))
                .perform(clearText(), replaceText(pmId), closeSoftKeyboard());
    }

    public void chooseCampaign(String option) {
        if (option.equals(STAGING_CAMPAIGN)) {
            onView(allOf(withId(R.id.toggleStaging), isDisplayed()))
                    .perform(click());
        }
    }

    public void tapOnSave() {
        onView(allOf(withId(R.id.action_saveProperty), withText("Save"), isDisplayed()))
                .perform(click());
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

    public boolean checkFor(String details) {
        int i = 0;
        while (i++ < 10) {
            try {
                if (details.equals(CONSENTS_ARE_DISPLAYED) || details.equals(PROPERTY_INFO_SCREEN))
                    onView(withId(R.id.consentRecyclerView)).check(matches(isDisplayed()));
                if (details.equals(CONSENTS_ARE_NOT_DISPLAYED))
                    onView(withId(R.id.tv_consentsNotAvailable)).check(matches(isDisplayed()));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    signal.await(3, TimeUnit.SECONDS);
                } catch (Exception e1) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public void navigateBackToListView() {
        onView(allOf(withContentDescription("Navigate up"), isDisplayed()))
                .perform(click());
    }

    public void tapOnProperty() {
        onView(allOf(withId(R.id.item_view), isDisplayed()))
                .perform(click());
    }

    public void loadPrivacyManagerDirect() {
        onView(allOf(withId(R.id.action_showPM), isDisplayed()))
                .perform(click());
    }

    public void swipeAndChooseAction(String action, String field) throws InterruptedException {
        onView(allOf(withId(R.id.item_view), isDisplayed())).perform(swipeLeft());
        if (action.equals(RESET_ACTION)) {
            onView(allOf(withId(R.id.reset_button), isDisplayed())).perform(click());
            signal.await(1, TimeUnit.SECONDS);
            onView(withText(field)).perform(scrollTo(), click());
        } else if (action.equals(DELETE_ACTION)) {
            onView(allOf(withId(R.id.delete_button), isDisplayed())).perform(click());
            signal.await(1, TimeUnit.SECONDS);
            onView(withText(field)).perform(scrollTo(), click());
        } else if (action.equals(EDIT_ACTION)) {
            onView(allOf(withId(R.id.edit_button), isDisplayed())).perform(click());
            signal.await(1, TimeUnit.SECONDS);
            if (field.equals(PARAM_VALUE)) {
                addParameterWithAuthentication(keyParam, valueParamEnglish, NO_AUTHENTICATION);
            } else {
                addAuthentication(UNIQUE_AUTHENTICATION);
            }
            tapOnSave();
        }
    }

    public boolean checkPropertyPresent() {
        try {
            onView(withId(R.id.item_view)).check(matches(isDisplayed()));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void selectConsents(String[] userConsentArray) {
        for (String s : userConsentArray) {
            onWebView().forceJavascriptEnabled()
                    .withElement(findElement(Locator.XPATH, "//label[@aria-label='" + s + "']/span[@class='slider round']"))
                    .perform(webScrollIntoView())
                    .perform(webClick());
        }
    }

    public boolean checkConsentsAsSelected(String[] userConsentArray) {
        boolean check = true;
        for (String s : userConsentArray) {
            try {
                onWebView().forceJavascriptEnabled()
                        .withElement(findElement(Locator.XPATH, "//label[@aria-label='" + s + "']"))
                        .withElement(findElement(Locator.CLASS_NAME, "checked"));
                check = true;
            } catch (Exception e) {
                e.printStackTrace();
                check = false;
            }
        }
        return check;
    }

    public boolean checkErrorFor(String type) {
        int i = 0;
        boolean value = false;
        do {
            try {
                signal.await(3, TimeUnit.SECONDS);
                onView(allOf(withId(R.id.message), withText(type), isDisplayed()));
                onView(withText("OK")).perform(click());
                value = true;
                break;
            } catch (Exception e) {
                i++;
            }
        } while (i < 10);
        return value;
    }

    public void addNativeMessagePropertyDetails() {
        String accountId = "22";
        String propertyId= "7094";
        String propertyName= "tcfv2.mobile.demo";
        String pmId = "179657";
        onView(allOf(withId(R.id.etAccountID), isDisplayed()))
                .perform(clearText(), replaceText(accountId), closeSoftKeyboard());

        onView(allOf(withId(R.id.etPropertyId), isDisplayed()))
                .perform(clearText(), replaceText(propertyId), closeSoftKeyboard());

        onView(allOf(withId(R.id.etPropertyName), isDisplayed()))
                .perform(clearText(), replaceText(propertyName), closeSoftKeyboard());

        onView(allOf(withId(R.id.etPMId), isDisplayed()))
                .perform(clearText(), replaceText(pmId), closeSoftKeyboard());

        onView(allOf(withId(R.id.toggleNativeMessage), isDisplayed()))
                .perform(click());
    }

    public boolean checkNativeMessageDisplayed(){
        int i = 0 ;
        boolean value = false;
        do {
            try {
                signal.await(3, TimeUnit.SECONDS);
                onView(allOf(withId(R.id.Title), isDisplayed()));
                value = true;
                break;
            }catch (Exception e){
                i++;
            }
        }while (i < 10);
        return value;
    }

    public void chooseNativeMessageAction(int actionId){
        onView(allOf(withId(actionId), isDisplayed()))
                .perform(click());
    }

    public boolean checkForPropertyListScrren() {
        int i = 0;
        while (i++ < 10) {
            try {
                onView(withId(R.id.action_addProperty)).check(matches(isDisplayed()));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    signal.await(3, TimeUnit.SECONDS);
                } catch (Exception e1) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}

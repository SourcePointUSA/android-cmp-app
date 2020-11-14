package com.sourcepoint.example_app

import androidx.annotation.IdRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.web.assertion.WebViewAssertions.webMatches
import androidx.test.espresso.web.model.Atoms
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.espresso.web.webdriver.DriverAtoms
import androidx.test.espresso.web.webdriver.DriverAtoms.findElement
import androidx.test.espresso.web.webdriver.Locator
import org.hamcrest.core.AllOf.allOf
import org.hamcrest.core.StringContains

fun isDisplayedAllOfByResId(
    @IdRes resId: Int
) {
    onView(allOf(withId(resId), isDisplayed()))
}


fun performClickById(
    @IdRes resId: Int
) {
    onView(
        allOf(
            withId(resId),
            isDisplayingAtLeast(100)
        )
    ).perform(ViewActions.click())
}

fun checkWebViewHasText(text: String) {
    onWebView()
        .forceJavascriptEnabled()
        .check(
            webMatches(
                Atoms.getCurrentUrl(),
                StringContains.containsString(text)
            )
        )
}

fun performClickOnWebViewByContent(text: String) {
    onWebView()
        .forceJavascriptEnabled().withElement(findElement(Locator.XPATH, "//button[contains(text(), '$text')]"))
        .perform(DriverAtoms.webScrollIntoView())
        .perform(DriverAtoms.webClick())
}

fun checkConsentAsSelected(consent: String) {
    onWebView().forceJavascriptEnabled()
        .withElement(findElement(Locator.XPATH, "//label[@aria-label='$consent']"))
        .withElement(findElement(Locator.XPATH, "//label[@aria-checked='true']"))
}
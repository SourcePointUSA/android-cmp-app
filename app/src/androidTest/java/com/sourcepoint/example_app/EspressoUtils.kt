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
            isCompletelyDisplayed()
        )
    ).perform(ViewActions.click())
}

fun checkWebViewHasText(text: String) {
    onWebView()
//        .forceJavascriptEnabled()
        .check(
            webMatches(
                Atoms.getCurrentUrl(),
                StringContains.containsString(text)
            )
        )
}

fun performClickOnWebViewByContent(text: String) {
    onWebView()
//        .forceJavascriptEnabled()
        .withElement(findElement(Locator.XPATH, "//button[contains(text(), '$text')]"))
        .perform(DriverAtoms.webScrollIntoView())
        .perform(DriverAtoms.webClick())
}

fun performClickOnWebViewByClass(classValue: String) {
    onWebView()
//        .forceJavascriptEnabled()
        .withElement(findElement(Locator.CLASS_NAME, classValue))
        .perform(DriverAtoms.webScrollIntoView())
        .perform(DriverAtoms.webClick())
}

fun checkConsentState(consent: String, selected : Boolean) {
    onWebView()
//        .forceJavascriptEnabled()
        .withElement(findElement(Locator.XPATH, "//label[@aria-label='$consent']"))
        .withElement(findElement(Locator.XPATH, "//label[@aria-checked='$selected']"))
}

fun checkPMTabSelected( expected : String){
    onWebView()
//        .forceJavascriptEnabled()
        .withElement(findElement(Locator.XPATH, "//div[contains(@class, 'pm-tab active') and text()='$expected']"))
}

fun performClickPMTabSelected( expected : String){
    onWebView()
//        .forceJavascriptEnabled()
        .withElement(findElement(Locator.XPATH, "//div[contains(@class, 'pm-tab') and text()='$expected']"))
        .perform(DriverAtoms.webScrollIntoView())
        .perform(DriverAtoms.webClick())
}

fun setCheckBoxTrue(property : String){
    onWebView()
//        .forceJavascriptEnabled()
        .withElement(findElement(Locator.XPATH, "//label[@aria-label='$property']/span[@class='on']"))
        .perform(DriverAtoms.webScrollIntoView())
        .perform(DriverAtoms.webClick())
}

fun checkConsentWebView(consent: String) {
    onWebView()
//        .forceJavascriptEnabled()
        .withElement(findElement(Locator.XPATH, "//label[@aria-label='$consent']/span[@class='slider round']"))
        .perform(DriverAtoms.webScrollIntoView())
        .perform(DriverAtoms.webClick())
}
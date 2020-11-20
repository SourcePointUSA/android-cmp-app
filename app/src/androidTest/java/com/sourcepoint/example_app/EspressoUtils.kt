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
import kotlin.jvm.Throws

@Throws(Throwable::class)
fun isDisplayedAllOfByResId(
    @IdRes resId: Int
) {
    onView(allOf(withId(resId), isDisplayed()))
}

@Throws(Throwable::class)
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

@Throws(Throwable::class)
fun checkWebViewHasText(text: String) {
    onWebView()
        .check(
            webMatches(
                Atoms.getCurrentUrl(),
                StringContains.containsString(text)
            )
        )
}

@Throws(Throwable::class)
fun performClickOnWebViewByContent(text: String) {
    onWebView()
        .withElement(findElement(Locator.XPATH, "//button[contains(text(), '$text')]"))
        .perform(DriverAtoms.webScrollIntoView())
        .perform(DriverAtoms.webClick())
}

@Throws(Throwable::class)
fun performClickOnWebViewByClass(classValue: String) {
    onWebView()
        .withElement(findElement(Locator.CLASS_NAME, classValue))
        .perform(DriverAtoms.webScrollIntoView())
        .perform(DriverAtoms.webClick())
}

@Throws(Throwable::class)
fun checkConsentState(consent: String, selected : Boolean) {
    onWebView()
        .withElement(findElement(Locator.XPATH, "//label[@aria-label='$consent']"))
        .withElement(findElement(Locator.XPATH, "//label[@aria-checked='$selected']"))
}

@Throws(Throwable::class)
fun checkPMTabSelected( expected : String){
    onWebView()
        .withElement(findElement(Locator.XPATH, "//div[contains(@class, 'pm-tab active') and text()='$expected']"))
}

@Throws(Throwable::class)
fun performClickPMTabSelected( expected : String){
    onWebView()
        .withElement(findElement(Locator.XPATH, "//div[contains(@class, 'pm-tab') and text()='$expected']"))
        .perform(DriverAtoms.webScrollIntoView())
        .perform(DriverAtoms.webClick())
}

@Throws(Throwable::class)
fun setCheckBoxTrue(property : String){
    onWebView()
        .withElement(findElement(Locator.XPATH, "//label[@aria-label='$property']/span[@class='on']"))
        .perform(DriverAtoms.webScrollIntoView())
        .perform(DriverAtoms.webClick())
}

@Throws(Throwable::class)
fun checkConsentWebView(consent: String) {
    onWebView()
        .withElement(findElement(Locator.XPATH, "//label[@aria-label='$consent']/span[@class='slider round']"))
        .perform(DriverAtoms.webScrollIntoView())
        .perform(DriverAtoms.webClick())
}
package com.sourcepointmeta.metaapp

import androidx.annotation.IdRes
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.web.assertion.WebViewAssertions.webMatches
import androidx.test.espresso.web.model.Atoms
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.espresso.web.webdriver.DriverAtoms
import androidx.test.espresso.web.webdriver.DriverAtoms.findElement
import androidx.test.espresso.web.webdriver.Locator
import org.hamcrest.core.AllOf.allOf
import org.hamcrest.core.StringContains
import java.security.InvalidParameterException

fun performClickByIdAndContent(
    @IdRes resId: Int,
    contentDescription: String
) {
    Espresso.onView(
        allOf(
            withId(resId),
            withContentDescription(contentDescription),
            isDisplayed()
        )
    ).perform(ViewActions.click())
}

fun performClickContent(
    contentDescription: String
) {
    Espresso.onView(
        allOf(
            withContentDescription(contentDescription),
            isDisplayed()
        )
    ).perform(ViewActions.click())
}

fun isDisplayedAllOf(@IdRes resId: Int) {
    Espresso
        .onView(allOf(withId(resId), isDisplayed()))
}

fun isDisplayedByResId(@IdRes resId: Int) {
    Espresso
        .onView(withId(resId))
        .check(ViewAssertions.matches(isDisplayed()))
}

fun performClickById(
    @IdRes resId: Int
) {
    Espresso.onView(
        allOf(
            withId(resId),
            isDisplayed()
        )
    ).perform(ViewActions.click())
}

fun insertTextByResId(
    @IdRes propId: Int,
    text: String
) {
    Espresso.onView(
        allOf(
            withId(propId),
            isDisplayed())
    )
        .perform(
            ViewActions.clearText(),
            ViewActions.replaceText(text),
            ViewActions.closeSoftKeyboard()
        )
}

fun checkWebViewHasText(text: String) {
    onWebView()
        .check(
            webMatches(
                Atoms.getCurrentUrl(),
                StringContains.containsString(text)
            )
        )
}

fun performClickOnWebViewByContent(text: String) {
    onWebView()
        .withElement(findElement(Locator.XPATH, "//button[contains(text(), '$text')]"))
        .perform(DriverAtoms.webScrollIntoView())
        .perform(DriverAtoms.webClick())
}

fun checkConsentWebView(consent: String) {
    onWebView()
        .withElement(findElement(Locator.XPATH, "//label[@aria-label='$consent']/span[@class='slider round']"))
        .perform(DriverAtoms.webScrollIntoView())
        .perform(DriverAtoms.webClick())
}

fun performClickOnWebViewByClass(classValue: String) {
    onWebView()
        .withElement(findElement(Locator.CLASS_NAME, classValue))
        .perform(DriverAtoms.webScrollIntoView())
        .perform(DriverAtoms.webClick())
}

fun checkConsentState(consent: String, selected : Boolean) {
    onWebView()
        .withElement(findElement(Locator.XPATH, "//label[@aria-label='$consent']"))
        .withElement(findElement(Locator.XPATH, "//label[@aria-checked='$selected']"))
}

fun checkWebViewDoesNotHasText(text: String) {
    try {
        onWebView()
            .check(
                webMatches(
                    Atoms.getCurrentUrl(),
                    StringContains.containsString(text)
                )
            )

        throw InvalidParameterException("""
            The current view with text {$text} is displayed. 
        """.trimIndent())
    } catch (e: Exception) { /** This is the success case */ }
}

fun swipeAndChooseAction(
    @IdRes resId: Int,
    field: String
) {
    Espresso.onView(allOf(withId(R.id.item_view), isDisplayed())).perform(ViewActions.swipeLeft())
//    Espresso.onView(allOf(withId(resId), isDisplayed())).perform(ViewActions.click())
    performClickById(resId)
    Espresso.onView(withText(field)).perform(ViewActions.scrollTo(), ViewActions.click())
}
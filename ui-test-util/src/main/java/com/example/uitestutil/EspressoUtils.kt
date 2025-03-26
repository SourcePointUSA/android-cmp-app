package com.example.uitestutil

import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.espresso.web.webdriver.DriverAtoms.*
import androidx.test.espresso.web.webdriver.Locator
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf.allOf


@Throws(Throwable::class)
fun isDisplayedAllOfByResId(@IdRes resId: Int) {
    onView(allOf(withId(resId), isDisplayed()))
}

fun isDisplayedByResIdByText(@IdRes resId: Int, text: String) {
    onView(allOf(withId(resId), withText(text), isDisplayed()))
}

@Throws(Throwable::class)
fun performClickById(@IdRes resId: Int) {
    onView(allOf(withId(resId), isDisplayed()))
        .perform(ViewActions.click())
}

fun scrollAndPerformClickById(@IdRes resId: Int) {
    onView(allOf(withId(resId)))
        .perform(ViewActions.scrollTo(), ViewActions.click())
}

@Throws(Throwable::class)
fun pressAlertDialogBtn(content: String) {
    onView(withText(content))
        .inRoot(RootMatchers.isDialog())
        .check(ViewAssertions.matches(isDisplayed()))
        .perform(ViewActions.click())
}

@Throws(Throwable::class)
fun addTextById(@IdRes resId: Int, text: String) {
    onView(withId(resId))
        .perform(ViewActions.click())
        .perform(ViewActions.typeText(text))
        .perform(ViewActions.closeSoftKeyboard())
}

@Throws(Throwable::class)
fun performClickOnWebViewByContent(text: String) {
    onWebView()
        .withElement(findElement(Locator.XPATH, "//button[contains(text(), '$text')]"))
        .perform(webScrollIntoView())
        .perform(webClick())
}

@Throws(Throwable::class)
fun checkTextInParagraph(text: String) {
    onWebView()
        .withElement(findElement(Locator.XPATH, "//p[contains(text(), '$text')]"))
        .perform(webScrollIntoView())
        .perform(webClick())
}

@Throws(Throwable::class)
fun performClickOnLabelWebViewByContent(text: String) {
    onWebView()
        .withElement(findElement(Locator.XPATH, "//a[contains(text(), '$text')]"))
        .perform(webScrollIntoView())
        .perform(webClick())
}

@Throws(Throwable::class)
fun checkConsentState(consent: String, selected: Boolean) {
    onWebView()
        .withElement(findElement(Locator.XPATH, "//div[contains(@class, 'stack-row')]"))
        .withElement( findElement(Locator.XPATH, "//span[(text()='$consent') ]"))
        .withElement(findElement(Locator.XPATH, "//span[@aria-checked='$selected' and @class='slider round']"))
        .perform(webScrollIntoView())
}

@Throws(Throwable::class)
fun checkConsentState(consent: String, selected: Boolean, stackType: String) {
    onWebView()
        .withElement(findElement(Locator.XPATH, "//div[@class='$stackType']"))
        .withContextualElement(findElement(Locator.XPATH, "//button[@aria-checked='$selected' and @aria-label='$consent']"))
        .perform(webScrollIntoView())
}


@Throws(Throwable::class)
fun checkConsentStateCCPA(consent: String, selected: Boolean, stackType: String) {
    onWebView()
        .withElement(findElement(Locator.XPATH, "//div[@class='$stackType']"))
        .withContextualElement(findElement(Locator.XPATH, "//button[@aria-checked='$selected']"))
        .withElement(findElement(Locator.XPATH, "//span[text()='$consent']"))
        .perform(webScrollIntoView())
}

@Throws(Throwable::class)
fun checkConsentStateVendor(selected: Boolean, stackType: String) {
    onWebView()
        .withElement(findElement(Locator.XPATH, "//div[@class='$stackType']"))
        .withElement(findElement(Locator.XPATH, "//button[@aria-checked='$selected']"))
        .perform(webScrollIntoView())
}

@Throws(Throwable::class)
fun performClickPMTabSelected(expected: String) {
    onWebView()
        .withElement(findElement(Locator.XPATH, "//div[contains(@class, 'pm-tab') and text()='$expected']"))
        .perform(webScrollIntoView())
        .perform(webClick())
}

@Throws(Throwable::class)
fun tapOnToggle2(property: String, tapOnlyWhen: Boolean) {
    onWebView()
        .withElement(findElement(Locator.XPATH, "//div[@class='tcfv2-stack']"))
        .withContextualElement(findElement(Locator.XPATH, "//button[@aria-checked='$tapOnlyWhen' and @aria-label='$property']"))
        .perform(webScrollIntoView())
        .perform(webClick())
}

fun <T : RecyclerView.ViewHolder> clickListItem(position: Int, @IdRes recyclerViewId: Int) {
    onView(withId(recyclerViewId))
        .perform(RecyclerViewActions.actionOnItemAtPosition<T>(position, ViewActions.click()))
}

fun <T : RecyclerView.ViewHolder> clickElementListItem(
    @IdRes resId: Int,
    @IdRes recyclerViewId: Int,
    position: Int = 0
) {
    onView(withId(recyclerViewId))
        .perform(RecyclerViewActions.actionOnItemAtPosition<T>(position, clickChildViewWithId(resId)))
}

fun checkElementListItem(
    @IdRes resId: Int,
    content: String,
    @IdRes recyclerViewId: Int,
    position: Int = 0
) {
    onView(withId(recyclerViewId)).check(
        ViewAssertions.matches(
            childOfViewAtPositionWithMatcher(
                childId = resId,
                position = position,
                childMatcher = withText(content)
            )
        )
    )
}

/**
 * checks that the matcher childMatcher matches a view having a given id
 * inside a RecyclerView's item (given its position)
 */
fun childOfViewAtPositionWithMatcher(childId: Int, position: Int, childMatcher: Matcher<View>) : Matcher<View> {
    return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
        override fun describeTo(description: Description?) {
            description?.appendText("Checks that the matcher childMatcher matches" +
                " with a view having a given id inside a RecyclerView's item (given its position)")
        }

        override fun matchesSafely(recyclerView: RecyclerView?): Boolean {
            val viewHolder = recyclerView?.findViewHolderForAdapterPosition(position)
            val matcher = hasDescendant(allOf(withId(childId), childMatcher))
            return viewHolder != null && matcher.matches(viewHolder.itemView)
        }

    }
}

fun clickChildViewWithId(id: Int): ViewAction {
    return object : ViewAction {
        override fun getConstraints() = null

        override fun getDescription() = "Click on a child view with specified id."

        override fun perform(uiController: UiController, view: View) {
            view.findViewById<View>(id).performClick()
        }
    }
}

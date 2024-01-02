package com.example.uitestutil

import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.web.assertion.WebViewAssertions.webMatches
import androidx.test.espresso.web.model.Atoms
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.espresso.web.webdriver.DriverAtoms.*
import androidx.test.espresso.web.webdriver.Locator
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.AllOf.allOf
import org.hamcrest.core.StringContains
import org.hamcrest.core.StringContains.containsString
import java.security.InvalidParameterException


@Throws(Throwable::class)
fun isDisplayedAllOfByResId(
        @IdRes resId: Int
) {
    onView(allOf(withId(resId), isDisplayed()))
}

fun isDisplayedByResIdByText(
        @IdRes resId: Int,
        text: String
) {
    onView(
            allOf(
                    withId(resId),
                    withText(text),
                    isDisplayed()
            )
    )
}

fun performClickByIdAndContent(
        @IdRes resId: Int,
        contentDescription: String
) {
    onView(
            allOf(
                    withId(resId),
                    withContentDescription(contentDescription),
                    isDisplayed()
            )
    ).perform(ViewActions.click())
}

@Throws(Throwable::class)
fun performClickByIdCompletelyDisplayed(
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
fun performClickById(
        @IdRes resId: Int
) {
    onView(
            allOf(
                    withId(resId),
                    isDisplayed()
            )
    ).perform(ViewActions.click())
}

fun scrollAndPerformClickById(
        @IdRes resId: Int
) {
    onView(allOf(withId(resId)))
            .perform(ViewActions.scrollTo(), ViewActions.click())
}

@Throws(Throwable::class)
fun pressAlertDialogBtn(
        content: String
) {
    onView(withText(content))
            .inRoot(RootMatchers.isDialog())
            .check(ViewAssertions.matches(isDisplayed()))
            .perform(ViewActions.click())
}

@Throws(Throwable::class)
fun addTextById(
        @IdRes resId: Int,
        text: String
) {
    onView(withId(resId))
            .perform(ViewActions.click())
            .perform(ViewActions.typeText(text))
            .perform(ViewActions.closeSoftKeyboard())
}

@Throws(Throwable::class)
fun addTextByIdInDialog(
        @IdRes resId: Int,
        text: String
) {
    onView(withId(resId))
            .inRoot(RootMatchers.isDialog())
            .perform(ViewActions.click())
            .perform(ViewActions.typeText(text))
            .perform(ViewActions.closeSoftKeyboard())
}

@Throws(Throwable::class)
fun performClickByText(
        text: String
) {
    onView(
            allOf(
                    withText(text),
                    isDisplayed()
            )
    ).perform(ViewActions.click())
}

fun performClickContent(
        contentDescription: String
) {
    onView(
            allOf(
                    withContentDescription(contentDescription),
                    isDisplayed()
            )
    ).perform(ViewActions.click())
}

fun performSpinnerItemSelection(@IdRes resId: Int, contentDescription: String) {
    performClickById(resId)
    Espresso.onData(
            CoreMatchers.allOf(
                    CoreMatchers.`is`(CoreMatchers.instanceOf(String::class.java)),
                    CoreMatchers.`is`(contentDescription)
            )
    )
            .perform(ViewActions.click())
}

@Throws(Throwable::class)
fun isDisplayedAllOf(@IdRes resId: Int) {
    onView(allOf(withId(resId), isDisplayed()))
}

@Throws(Throwable::class)
fun isDisplayedByResId(@IdRes resId: Int) {
    onView(withId(resId))
            .check(ViewAssertions.matches(isDisplayed()))
}

@Throws(Throwable::class)
fun containsText(@IdRes resId: Int, content: String) {
    onView(withId(resId))
            .check(ViewAssertions.matches(withText(containsString(content))))
}

@Throws(Throwable::class)
fun insertTextByResId(
        @IdRes propId: Int,
        text: String
) {
    onView(
            allOf(
                    withId(propId),
                    isDisplayed()
            )
    )
            .perform(
                    ViewActions.clearText(),
                    ViewActions.replaceText(text),
                    ViewActions.closeSoftKeyboard()
            )
}

@Throws(Throwable::class)
fun checkWebViewHasText(text: String) {
    onWebView()
            .check(
                    webMatches(
                            Atoms.getCurrentUrl(),
                            containsString(text)
                    )
            )
}

@Throws(Throwable::class)
fun checkWebViewContains(text: String) {
    onWebView()
        .check(
            webMatches(
                getText(),
                containsString(text)
            )
        )
}

@Throws(Throwable::class)
fun checkElementWithText(id: String, expected: String) {
    onWebView()
            .withElement(findElement(Locator.ID, id))
            .check(webMatches(getText(), containsString(expected)));
}

@Throws(Throwable::class)
fun performClickOnWebViewByContent(text: String) {
    onWebView()
            .withElement(findElement(Locator.XPATH, "//button[contains(text(), '$text')]"))
            .perform(webScrollIntoView())
            .perform(webClick())
}

@Throws(Throwable::class)
fun clickOnButtonByTextOnWebViewByTag(
    tag: String,
    text: String,
) {
    onWebView(withTagValue(CoreMatchers.equalTo(tag)))
        .withElement(findElement(Locator.XPATH, "//button[contains(text(), '$text')]"))
        .perform(webScrollIntoView())
        .perform(webClick())
}

@Throws(Throwable::class)
fun assertButtonWithTextIsPresentInWebViewByTag(
    webViewTag: String,
    text: String,
) {
    onWebView(withTagValue(CoreMatchers.equalTo(webViewTag)))
        .withElement(findElement(Locator.CSS_SELECTOR, "button"))
        .check(webMatches(getText(), containsString(text)))
}

@Throws(Throwable::class)
fun assertTextInWebViewByTagName(
    tagName: String,
    text: String?
) {
    if (text == null) throw Exception("Assertion failed, text can't be found in the web view with tagName=$tagName")

    onWebView()
        .withElement(findElement(Locator.TAG_NAME, tagName))
        .check(webMatches(getText(), containsString(text)))
}

@Throws(Throwable::class)
fun assertTextInWebViewById(
    id: String,
    text: String?
) {
    if (text == null) throw Exception("Assertion failed, text can't be found in the web view with id=$id")

    onWebView()
        .withElement(findElement(Locator.ID, id))
        .check(webMatches(getText(), containsString(text)))
}

@Throws(Throwable::class)
fun readTextFromTextViewById(
    @IdRes id: Int,
): String? {
    var text: String? = null

    onView(withId(id))
        .check(matches(isDisplayed()))
        .check { view: View, _ -> text = (view as? TextView)?.text.toString() }

    return text
}

@Throws(Throwable::class)
fun checkTextInTextView(
    @IdRes id: Int,
    text: String,
) {
    onView(withId(id)).check(matches(withText(text)))
}

@Throws(Throwable::class)
fun checkTextNotInTextView(
    @IdRes id: Int,
    text: String,
) {
    onView(withId(id)).check(matches(not(withText(text))))
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
fun performClickOnWebViewByClass(classValue: String) {
    onWebView()
            .withElement(findElement(Locator.CLASS_NAME, classValue))
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
//        .withElement( findElement(Locator.XPATH, "//span[(text()='$consent') ]"))
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
fun checkConsentStateVendor(consent: String, selected: Boolean, stackType: String) {
    onWebView()
        .withElement(findElement(Locator.XPATH, "//div[@class='$stackType']"))
        .withElement(findElement(Locator.XPATH, "//button[@aria-checked='$selected']"))
//        .withElement(findElement(Locator.XPATH, "//span[text()='$consent']"))
        .perform(webScrollIntoView())
}

@Throws(Throwable::class)
fun checkPMTabSelected(expected: String) {
    onWebView()
            .withElement(findElement(Locator.XPATH, "//div[contains(@class, 'pm-tab active') and text()='$expected']"))
}

@Throws(Throwable::class)
fun performClickPMTabSelected(expected: String) {
    onWebView()
            .withElement(findElement(Locator.XPATH, "//div[contains(@class, 'pm-tab') and text()='$expected']"))
            .perform(webScrollIntoView())
            .perform(webClick())
}

@Throws(Throwable::class)
fun tapOnToggle(property: String) {
    onWebView()
            .withElement(findElement(Locator.XPATH, "//span[@aria-label='$property'and @class='slider round']"))
            .perform(webScrollIntoView())
            .perform(webClick())
}

@Throws(Throwable::class)
fun tapOnToggle(property: String, tapOnlyWhen: Boolean) {
    onWebView()
            .withElement(
                    findElement(
                            Locator.XPATH,
                            "//span[@aria-label='$property'and @aria-checked='$tapOnlyWhen' and @class='slider round']"
                    )
            )
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

@Throws(Throwable::class)
fun checkConsentWebView(consent: String) {
    onWebView()
            .withElement(findElement(Locator.XPATH, "//label[@aria-label='$consent']/span[@class='slider round']"))
            .perform(webScrollIntoView())
            .perform(webClick())
}

fun swipeAndChooseAction(
        @IdRes resId: Int,
        @IdRes resIdListItem: Int,
        field: String
) {
    onView(allOf(withId(resIdListItem), isDisplayed())).perform(ViewActions.swipeLeft())
    performClickById(resId)
    onView(withText(field)).perform(ViewActions.scrollTo(), ViewActions.click())
}

fun swipeAndChooseActionEdit(
        @IdRes resId: Int,
        @IdRes resIdListItem: Int,
) {
    onView(allOf(withId(resIdListItem), isDisplayed())).perform(ViewActions.swipeLeft())
    performClickById(resId)
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

        throw InvalidParameterException(
                """
            The current view with text {$text} is displayed. 
        """.trimIndent()
        )
    } catch (e: Exception) {
        /** This is the success case */
    }
}

fun <T : RecyclerView.ViewHolder> clickListItem(
        position: Int,
        @IdRes recyclerViewId: Int
) {
    onView(withId(recyclerViewId))
            .perform(RecyclerViewActions.actionOnItemAtPosition<T>(position, ViewActions.click()));
}

fun <T : RecyclerView.ViewHolder> clickElementListItem(
        @IdRes resId: Int,
        @IdRes recyclerViewId: Int,
        position: Int = 0
) {
    onView(withId(recyclerViewId))
            .perform(RecyclerViewActions.actionOnItemAtPosition<T>(position, clickChildViewWithId(resId)))
}

fun <T : RecyclerView.ViewHolder> checkElementListItem(
        @IdRes resId: Int,
        content: String,
        @IdRes recyclerViewId: Int,
        position: Int = 0
) {
    onView(withId(recyclerViewId))
            .check(
                    ViewAssertions.matches(
                            childOfViewAtPositionWithMatcher(
                                    childId = resId,
                                    position = position,
                                    childMatcher = withText(content)
                            )
                    )
            )
}

fun <T : RecyclerView.ViewHolder> swipeLeft(
        @IdRes resId: Int,
        content: String,
        @IdRes recyclerViewId: Int,
        position: Int = 0
) {
    onView(withId(resId))
            .perform(swipeLeft())
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
        override fun getConstraints(): Matcher<View>? {
            return null
        }

        override fun getDescription(): String {
            return "Click on a child view with specified id."
        }

        override fun perform(uiController: UiController, view: View) {
            val v = view.findViewById<View>(id)
            v.performClick()
        }
    }
}

fun <T : RecyclerView.ViewHolder> isInPosition(position: Int): Matcher<T> {
    return object : TypeSafeMatcher<T>() {
        override fun matchesSafely(customHolder: T): Boolean {
            return customHolder.adapterPosition == position
        }

        override fun describeTo(description: Description) {
            description.appendText("Item in the middle")
        }
    }
}
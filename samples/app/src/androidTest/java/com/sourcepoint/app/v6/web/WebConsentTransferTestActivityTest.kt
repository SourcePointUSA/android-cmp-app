package com.sourcepoint.app.v6.web

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.uitestutil.assertTextInWebViewByTagName
import com.example.uitestutil.wr
import com.sourcepoint.app.v6.R
import com.sourcepoint.app.v6.TestUseCase
import com.sourcepoint.app.v6.TestUseCase.Companion.assertTextInWebViewByContainerId
import com.sourcepoint.app.v6.TestUseCase.Companion.checkTextDoesNotMatchInView
import com.sourcepoint.app.v6.TestUseCase.Companion.checkTextMatchesInView
import com.sourcepoint.app.v6.TestUseCase.Companion.clickAcceptAllOnConsentWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOptionsOnConsentWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.clickRejectAllOnConsentWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.clickSaveAndExitOnConsentWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.readTextFromTextView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapOn
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.creation.config
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules

/**
 * This test suite contains test cases that test the behaviour of the API that transfers user's
 * consent to the web view. If the user does not accept the consent for either of the campaigns, the
 * API should show correspondent message (e.g. if the user does not accept the consent for ccpa and
 * transfers theirs consent to the web view - then ccpa message should be shown).
 */
@RunWith(AndroidJUnit4ClassRunner::class)
class WebConsentTransferTestActivityTest {

    private val sourcePointClient: SpClient
        get() = mockk()

    private lateinit var scenario: ActivityScenario<WebConsentTransferTestActivity>

    private val sourcePointConfig = config {
        accountId = MOCK_ACCOUNT_ID
        propertyId = MOCK_PROPERTY_ID
        propertyName = MOCK_PROPERTY_NAME
        messLanguage = MessageLanguage.ENGLISH
        messageTimeout = MOCK_MESSAGE_TIMEOUT
        +(CampaignType.GDPR)
        +(CampaignType.CCPA)
    }

    @Before
    fun setUp() = runBlocking {
        loadKoinModules(
            TestUseCase.mockModule(
                spConfig = sourcePointConfig,
                gdprPmId = MOCK_GDPR_PM_ID,
                ccpaPmId = MOCK_CCPA_PM_ID,
                spClientObserver = listOf(sourcePointClient)
            )
        )

        scenario = launchActivity()
    }

    @After
    fun tearDown() = runBlocking {
        if (this@WebConsentTransferTestActivityTest::scenario.isLateinit) scenario.close()
    }

    /**
     * Test case that verifies the flow of:
     * 1. user choosing accept all for the GDPR
     * 2. user choosing accept all for the CCPA
     * 3. user transferring the consent to the web view
     * 4. user seeing the consent for both GDPR and CCPA and no message
     */
    @Test
    fun user_consents_gdpr_as_accept_all_and_ccpa_as_accept_all(): Unit = runBlocking {
        // clear data before the test
        onView(withId(R.id.web_consent_clear_data_button))
            .perform(click())

        // check if CCPA and GDPR UUIDs are empty
        wr(delay = 1000L) {
            checkTextMatchesInView(
                id = R.id.ccpa_uuid_value_text_view,
                text = "",
            )
            checkTextMatchesInView(
                id = R.id.gdpr_uuid_value_text_view,
                text = "",
            )
        }

        // check if the web view contains readyForConsent callback
        assertTextInWebViewByTagName(tagName = "h1", CONSENT_TRANSFER_TEST_HEADER)

        // load messages for CCPA and GDPR
        onView(withId(R.id.web_consent_refresh_button))
            .perform(click())

        // choose ACCEPT ALL for GDPR
        wr(delay = 1000L) {
            clickAcceptAllOnConsentWebView()
        }

        // choose ACCEPT ALL for CCPA
        wr(delay = 1000L) {
            clickAcceptAllOnConsentWebView()
        }

        // check if the sdk returned proper CCPA and GDPR UUIDs
        wr(delay = 1000L) {
            checkTextDoesNotMatchInView(id = R.id.ccpa_uuid_value_text_view, text = "")
            checkTextDoesNotMatchInView(id = R.id.ccpa_uuid_value_text_view, text = "NULL")
            checkTextDoesNotMatchInView(id = R.id.gdpr_uuid_value_text_view, text = "")
            checkTextDoesNotMatchInView(id = R.id.gdpr_uuid_value_text_view, text = "NULL")
        }

        // transfer consent to the web view
        wr(delay = 1000L) { tapOn(R.id.to_web_view_consent_action) }

        // check if the callbacks are returned inside the web view
        assertTextInWebViewByContainerId(id = CONSENT_WEB_VIEW_CONTAINER_ID, text = EVENT_SP_LOAD_CONSENT)
        assertTextInWebViewByContainerId(id = CONSENT_WEB_VIEW_CONTAINER_ID, text = EVENT_SP_ON_CONSENT_READY)

        // check if CCPA and GDPR UUIDs are present in the postMessage response
        val ccpaUuid = readTextFromTextView(R.id.ccpa_uuid_value_text_view)
        val gdprUuid = readTextFromTextView(R.id.gdpr_uuid_value_text_view)
        assertTextInWebViewByContainerId(id = CONSENT_WEB_VIEW_CONTAINER_ID, text = ccpaUuid)
        assertTextInWebViewByContainerId(id = CONSENT_WEB_VIEW_CONTAINER_ID, text = gdprUuid)
    }

    /**
     * Test case that verifies the flow of:
     * 1. user choosing more options and then save and exit for the GDPR
     * 2. user choosing accept all for the CCPA
     * 3. user transferring the consent to the web view
     * 4. user seeing the consent for both GDPR and CCPA and no message
     */
    @Test
    fun user_consents_gdpr_as_save_and_exit_and_ccpa_as_accept_all(): Unit = runBlocking {
        // clear data before the test
        onView(withId(R.id.web_consent_clear_data_button))
            .perform(click())

        // check if CCPA and GDPR UUIDs are empty
        wr(delay = 1000L) {
            checkTextMatchesInView(
                id = R.id.ccpa_uuid_value_text_view,
                text = "",
            )
            checkTextMatchesInView(
                id = R.id.gdpr_uuid_value_text_view,
                text = "",
            )
        }

        // check if the web view contains readyForConsent callback
        assertTextInWebViewByTagName(tagName = "h1", CONSENT_TRANSFER_TEST_HEADER)

        // load messages for CCPA and GDPR
        onView(withId(R.id.web_consent_refresh_button))
            .perform(click())

        // choose OPTIONS and then SAVE AND EXIT for GDPR
        wr(delay = 1000L) {
            clickOptionsOnConsentWebView()
            clickSaveAndExitOnConsentWebView()
        }

        // choose ACCEPT ALL for CCPA
        wr(delay = 1000L) {
            clickAcceptAllOnConsentWebView()
        }

        // check if the sdk returned proper CCPA and GDPR UUIDs
        wr(delay = 1000L) {
            checkTextDoesNotMatchInView(id = R.id.ccpa_uuid_value_text_view, text = "")
            checkTextDoesNotMatchInView(id = R.id.ccpa_uuid_value_text_view, text = "NULL")
            checkTextDoesNotMatchInView(id = R.id.gdpr_uuid_value_text_view, text = "")
            checkTextDoesNotMatchInView(id = R.id.gdpr_uuid_value_text_view, text = "NULL")
        }

        // transfer consent to the web view
        wr(delay = 1000L) { tapOn(R.id.to_web_view_consent_action) }

        // check if the callbacks are returned inside the web view
        assertTextInWebViewByContainerId(id = CONSENT_WEB_VIEW_CONTAINER_ID, text = EVENT_SP_LOAD_CONSENT)
        assertTextInWebViewByContainerId(id = CONSENT_WEB_VIEW_CONTAINER_ID, text = EVENT_SP_ON_CONSENT_READY)

        // check if CCPA and GDPR UUIDs are present in the postMessage response
        val ccpaUuid = readTextFromTextView(R.id.ccpa_uuid_value_text_view)
        val gdprUuid = readTextFromTextView(R.id.gdpr_uuid_value_text_view)
        assertTextInWebViewByContainerId(id = CONSENT_WEB_VIEW_CONTAINER_ID, text = ccpaUuid)
        assertTextInWebViewByContainerId(id = CONSENT_WEB_VIEW_CONTAINER_ID, text = gdprUuid)
    }

    /**
     * Test case that verifies the flow of:
     * 1. user choosing accept all for the GDPR
     * 2. user choosing more options and then save and exit for the CCPA
     * 3. user transferring the consent to the web view
     * 4. user seeing the consent for both GDPR and CCPA and no message
     */
    @Test
    fun user_consents_gdpr_as_accept_all_and_ccpa_as_save_and_exit(): Unit = runBlocking {
        // clear data before the test
        onView(withId(R.id.web_consent_clear_data_button))
            .perform(click())

        // check if CCPA and GDPR UUIDs are empty
        wr(delay = 1000L) {
            checkTextMatchesInView(
                id = R.id.ccpa_uuid_value_text_view,
                text = "",
            )
            checkTextMatchesInView(
                id = R.id.gdpr_uuid_value_text_view,
                text = "",
            )
        }

        // check if the web view contains readyForConsent callback
        assertTextInWebViewByTagName(tagName = "h1", CONSENT_TRANSFER_TEST_HEADER)

        // load messages for CCPA and GDPR
        onView(withId(R.id.web_consent_refresh_button))
            .perform(click())

        // choose ACCEPT ALL for GDPR
        wr(delay = 1000L) {
            clickAcceptAllOnConsentWebView()
        }

        // choose OPTIONS and then SAVE AND EXIT for CCPA
        wr(delay = 1000L) {
            clickOptionsOnConsentWebView()
            clickSaveAndExitOnConsentWebView()
        }

        // check if the sdk returned proper CCPA and GDPR UUIDs
        wr(delay = 1000L) {
            checkTextDoesNotMatchInView(id = R.id.ccpa_uuid_value_text_view, text = "")
            checkTextDoesNotMatchInView(id = R.id.ccpa_uuid_value_text_view, text = "NULL")
            checkTextDoesNotMatchInView(id = R.id.gdpr_uuid_value_text_view, text = "")
            checkTextDoesNotMatchInView(id = R.id.gdpr_uuid_value_text_view, text = "NULL")
        }

        // transfer consent to the web view
        wr(delay = 1000L) { tapOn(R.id.to_web_view_consent_action) }

        // check if the callbacks are returned inside the web view
        assertTextInWebViewByContainerId(id = CONSENT_WEB_VIEW_CONTAINER_ID, text = EVENT_SP_LOAD_CONSENT)
        assertTextInWebViewByContainerId(id = CONSENT_WEB_VIEW_CONTAINER_ID, text = EVENT_SP_ON_CONSENT_READY)

        // check if CCPA and GDPR UUIDs are present in the postMessage response
        val ccpaUuid = readTextFromTextView(R.id.ccpa_uuid_value_text_view)
        val gdprUuid = readTextFromTextView(R.id.gdpr_uuid_value_text_view)
        assertTextInWebViewByContainerId(id = CONSENT_WEB_VIEW_CONTAINER_ID, text = ccpaUuid)
        assertTextInWebViewByContainerId(id = CONSENT_WEB_VIEW_CONTAINER_ID, text = gdprUuid)
    }

    /**
     * Test case that verifies the flow of:
     * 1. user choosing reject all for the GDPR
     * 2. user choosing reject all for the CCPA
     * 3. user transferring the consent to the web view
     * 4. user seeing the consent for both GDPR and CCPA and no message
     */
    @Test
    fun user_consents_gdpr_as_reject_all_and_ccpa_as_reject_all(): Unit = runBlocking {
        // clear data before the test
        onView(withId(R.id.web_consent_clear_data_button))
            .perform(click())

        // check if CCPA and GDPR UUIDs are empty
        wr(delay = 1000L) {
            checkTextMatchesInView(
                id = R.id.ccpa_uuid_value_text_view,
                text = "",
            )
            checkTextMatchesInView(
                id = R.id.gdpr_uuid_value_text_view,
                text = "",
            )
        }

        // check if the web view contains readyForConsent callback
        assertTextInWebViewByTagName(tagName = "h1", CONSENT_TRANSFER_TEST_HEADER)

        // load messages for CCPA and GDPR
        onView(withId(R.id.web_consent_refresh_button))
            .perform(click())

        // choose REJECT ALL for GDPR
        wr(delay = 1000L) {
            clickRejectAllOnConsentWebView()
        }

        // choose REJECT ALL for CCPA
        wr(delay = 1000L) {
            clickRejectAllOnConsentWebView()
        }

        // check if the sdk returned proper CCPA and GDPR UUIDs
        wr(delay = 1000L) {
            checkTextDoesNotMatchInView(id = R.id.ccpa_uuid_value_text_view, text = "")
            checkTextDoesNotMatchInView(id = R.id.ccpa_uuid_value_text_view, text = "NULL")
            checkTextDoesNotMatchInView(id = R.id.gdpr_uuid_value_text_view, text = "")
            checkTextDoesNotMatchInView(id = R.id.gdpr_uuid_value_text_view, text = "NULL")
        }

        // transfer consent to the web view
        wr(delay = 1000L) { tapOn(R.id.to_web_view_consent_action) }

        // check if the callbacks are returned inside the web view
        assertTextInWebViewByContainerId(id = CONSENT_WEB_VIEW_CONTAINER_ID, text = EVENT_SP_LOAD_CONSENT)
        assertTextInWebViewByContainerId(id = CONSENT_WEB_VIEW_CONTAINER_ID, text = EVENT_SP_ON_CONSENT_READY)

        // check if CCPA and GDPR UUIDs are present in the postMessage response
        val ccpaUuid = readTextFromTextView(R.id.ccpa_uuid_value_text_view)
        val gdprUuid = readTextFromTextView(R.id.gdpr_uuid_value_text_view)
        assertTextInWebViewByContainerId(id = CONSENT_WEB_VIEW_CONTAINER_ID, text = ccpaUuid)
        assertTextInWebViewByContainerId(id = CONSENT_WEB_VIEW_CONTAINER_ID, text = gdprUuid)
    }

    companion object {
        private const val MOCK_ACCOUNT_ID: Int = 22
        private const val MOCK_PROPERTY_ID: Int = 22
        private const val MOCK_PROPERTY_NAME: String = "mobile.multicampaign.demo"
        private const val MOCK_MESSAGE_TIMEOUT: Long = 3000
        private const val MOCK_GDPR_PM_ID: String = "488393"
        private const val MOCK_CCPA_PM_ID: String = "509688"

        private const val CONSENT_TRANSFER_TEST_HEADER = "Consent Transfer Test"
        private const val CONSENT_WEB_VIEW_CONTAINER_ID = "postMessage"

        private const val EVENT_SP_LOAD_CONSENT = "sp.loadConsent"
        private const val EVENT_SP_READY_FOR_CONSENT = "sp.readyForConsent"
        private const val EVENT_SP_ON_CONSENT_READY = "onConsentReady"
    }
}

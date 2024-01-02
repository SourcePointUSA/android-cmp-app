package com.sourcepoint.app.v6

import android.preference.PreferenceManager
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.example.uitestutil.*
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnGdprReviewConsent
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnRefreshBtnActivity
import com.sourcepoint.app.v6.TestUseCase.Companion.mockModule
import com.sourcepoint.app.v6.TestUseCase.Companion.tapAcceptAllOnWebView
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.creation.config
import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.exposed.MessageType
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityKotlinOttTest {

    private val CONSENT_WEB_VIEW_TAG_NAME = "consent-web-view"

    lateinit var scenario: ActivityScenario<MainActivityKotlin>

    private val device by lazy { UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()) }

    @After
    fun cleanup() {
        if (this::scenario.isLateinit) scenario.close()
    }

    private val spConfOtt = config {
        accountId = 22
        propertyName = "ott.test.suite"
        propertyId = 22231
        campaignsEnv = CampaignsEnv.PUBLIC
        messLanguage = MessageLanguage.ENGLISH
        +(CampaignType.GDPR)
    }

    private val spNewWebPmConfig = config {
        accountId = 22
        propertyId = 27927
        propertyName = "sca-ott-newwebpm"
        messLanguage = MessageLanguage.ENGLISH
        +(CampaignType.GDPR)
    }

    @Test
    fun GIVEN_an_OTT_campaign_SHOW_message_and_ACCEPT_ALL() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfOtt,
                gdprPmId = "579231",
                ccpaPmId = "1",
                messageType = MessageType.LEGACY_OTT,
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        wr(backup = { clickOnRefreshBtnActivity() })  {
            tapAcceptAllOnWebView()
            device.pressEnter()
        }

        verify(exactly = 0) { spClient.onError(any()) }
        wr{ verify(exactly = 1) { spClient.onConsentReady(any()) } }
        verify { spClient.onAction(any(), withArg { it.pubData["pb_key"].assertEquals("pb_value") }) }

        wr {
            verify {
                spClient.run {
                    onUIReady(any())
                    onUIFinished(any())
                    onAction(any(), any())
                    onConsentReady(any())
                }
            }
        }


        scenario.onActivity { activity ->
            val IABTCF_TCString = PreferenceManager.getDefaultSharedPreferences(activity)
                .getString("IABTCF_TCString", null)
            IABTCF_TCString.assertNotNull()
        }

    }

    @Test
    fun GIVEN_an_OTT_campaign_SHOW_message_and_ACCEPT_ALL_from_PM() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfOtt,
                gdprPmId = "579231",
                ccpaPmId = "1",
                messageType = MessageType.LEGACY_OTT,
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        wr(backup = { clickOnRefreshBtnActivity() })  {
            tapAcceptAllOnWebView()
            device.pressEnter()
        }

        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }){
            tapAcceptAllOnWebView()
            device.pressEnter()
        }

        verify(exactly = 0) { spClient.onError(any()) }
        wr{ verify(atLeast = 2) { spClient.onConsentReady(any()) } }
        verify { spClient.onAction(any(), withArg { it.pubData["pb_key"].assertEquals("pb_value") }) }

        wr {
            verify {
                spClient.run {
                    onUIReady(any())
                    onUIFinished(any())
                    onAction(any(), any())
                    onConsentReady(any())
                }
            }
        }


        scenario.onActivity { activity ->
            val IABTCF_TCString = PreferenceManager.getDefaultSharedPreferences(activity)
                .getString("IABTCF_TCString", null)
            IABTCF_TCString.assertNotNull()
        }

    }

    @Test
    fun GIVEN_user_clicks_back_button_in_message_SHOULD_return_to_the_previous_screen_and_not_close_the_activity() = runBlocking {

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spNewWebPmConfig,
                gdprPmId = "898241",
                messageType = MessageType.OTT,
                spClientObserver = listOf(spClient),
            )
        )

        scenario = launchActivity()

        wr {

            // verify that FLM appears by checking if proper buttons are in the web view
            assertButtonWithTextIsPresentInWebViewByTag(
                webViewTag = CONSENT_WEB_VIEW_TAG_NAME,
                text = "Accept All",
            )
            assertButtonWithTextIsPresentInWebViewByTag(
                webViewTag = CONSENT_WEB_VIEW_TAG_NAME,
                text = "Reject All",
            )
            assertButtonWithTextIsPresentInWebViewByTag(
                webViewTag = CONSENT_WEB_VIEW_TAG_NAME,
                text = "Manage Preferences",
            )

            // click manage preferences and verify if it opens up
            performClickOnLabelWebViewByContent(
                text = "Manage Preferences",
            )
            assertButtonWithTextIsPresentInWebViewByTag(
                webViewTag = CONSENT_WEB_VIEW_TAG_NAME,
                text = "Home",
            )
            assertButtonWithTextIsPresentInWebViewByTag(
                webViewTag = CONSENT_WEB_VIEW_TAG_NAME,
                text = "Consent",
            )
            assertButtonWithTextIsPresentInWebViewByTag(
                webViewTag = CONSENT_WEB_VIEW_TAG_NAME,
                text = "Legitimate Interest",
            )

            // press system's back button
            device.pressBack()

            // verify that the web view returned to the home page
            assertButtonWithTextIsPresentInWebViewByTag(
                webViewTag = CONSENT_WEB_VIEW_TAG_NAME,
                text = "Accept All",
            )
            assertButtonWithTextIsPresentInWebViewByTag(
                webViewTag = CONSENT_WEB_VIEW_TAG_NAME,
                text = "Reject All",
            )
            assertButtonWithTextIsPresentInWebViewByTag(
                webViewTag = CONSENT_WEB_VIEW_TAG_NAME,
                text = "Manage Preferences",
            )

            // press system's back button again on home page
            device.pressBack()

            // assert that web view is still present on a home page and the activity was not destroyed
            assertButtonWithTextIsPresentInWebViewByTag(
                webViewTag = CONSENT_WEB_VIEW_TAG_NAME,
                text = "Accept All",
            )
            assertButtonWithTextIsPresentInWebViewByTag(
                webViewTag = CONSENT_WEB_VIEW_TAG_NAME,
                text = "Reject All",
            )
            assertButtonWithTextIsPresentInWebViewByTag(
                webViewTag = CONSENT_WEB_VIEW_TAG_NAME,
                text = "Manage Preferences",
            )
            scenario.state.assertNotEquals(Lifecycle.State.DESTROYED)
        }

        // TODO not sure about this implementation...
    }
}
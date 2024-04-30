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
import com.sourcepoint.app.v6.TestUseCase.Companion.mockModule
import com.sourcepoint.app.v6.TestUseCase.Companion.tapAcceptAllOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapSettingsOnWebView
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.creation.config
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.exposed.MessageType
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.koin.core.context.loadKoinModules

class MainActivityKotlinOttTest {

    lateinit var scenario: ActivityScenario<MainActivityKotlin>

    private val device by lazy { UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()) }

    @After
    fun cleanup() {
        if (this::scenario.isLateinit) scenario.close()
    }

    private val ottConfig = config {
        accountId = 22
        propertyId = 17935
        propertyName = "appletv.demo"
        messLanguage = MessageLanguage.ENGLISH
        +(CampaignType.GDPR)
    }

    private fun loadModules(client: SpClient = mockk<SpClient>(relaxed = true)) {
        loadKoinModules(
            mockModule(
                spConfig = ottConfig,
                gdprPmId = "1116373",
                ccpaPmId = "",
                messageType = MessageType.OTT,
                spClientObserver = listOf(client)
            )
        )
    }

    private fun assert_IAB_TCString_notNull() {
        scenario.onActivity {
            PreferenceManager.getDefaultSharedPreferences(it)
                    .getString("IABTCF_TCString", null)
                    .assertNotNull()
        }
    }


    @Test
    fun given_an_OTT_campaign_SHOW_message_and_ACCEPT_ALL() = runBlocking<Unit> {
        val spClient = mockk<SpClient>(relaxed = true)
        loadModules(spClient)

        scenario = launchActivity()

        wr {
            tapAcceptAllOnWebView()
            device.pressEnter()
        }

        verify(exactly = 0) { spClient.onError(any()) }
        wr{ verify(exactly = 1) { spClient.onConsentReady(any()) } }
        verify { spClient.onAction(any(), any()) }

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

        assert_IAB_TCString_notNull()
    }

    @Test
    fun given_an_OTT_campaign_SHOW_message_and_ACCEPT_ALL_from_PM() = runBlocking<Unit> {
        val spClient = mockk<SpClient>(relaxed = true)
        loadModules(spClient)

        scenario = launchActivity()

        wr {
            tapAcceptAllOnWebView()
            device.pressEnter()
        }

        wr { clickOnGdprReviewConsent() }
        wr {
            tapAcceptAllOnWebView()
            device.pressEnter()
        }

        wr { verify(exactly = 0) { spClient.onError(any()) } }
        wr { verify(atLeast = 2) { spClient.onConsentReady(any()) } }
        wr { verify { spClient.onAction(any(), any()) } }

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

        assert_IAB_TCString_notNull()
    }

    @Test
    fun given_user_clicks_back_button_in_message_SHOULD_not_close_the_activity() = runBlocking {
        loadModules()

        scenario = launchActivity()

        wr { tapSettingsOnWebView() }
        wr { device.pressBack() }
        wr { scenario.state.assertNotEquals(Lifecycle.State.DESTROYED) }
        wr { device.pressBack() }
        wr { scenario.state.assertNotEquals(Lifecycle.State.DESTROYED) }
    }
}
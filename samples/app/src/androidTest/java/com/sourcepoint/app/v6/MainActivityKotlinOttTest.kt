package com.sourcepoint.app.v6

import android.preference.PreferenceManager
import androidx.lifecycle.Lifecycle
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.example.uitestutil.*
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnGdprReviewConsent
import com.sourcepoint.app.v6.TestUseCase.Companion.mockModule
import com.sourcepoint.app.v6.TestUseCase.Companion.tapAcceptAllOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapSettingsOnWebView
import com.sourcepoint.app.v6.utils.LazyActivityScenario
import com.sourcepoint.app.v6.utils.ScreenshotTakingRule
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.creation.config
import com.sourcepoint.cmplibrary.data.network.util.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.exposed.MessageType
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.koin.core.context.loadKoinModules

class MainActivityKotlinOttTest {
    private val rule = LazyActivityScenario(launchActivity = false, MainActivityKotlin::class.java)

    private val device by lazy { UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()) }

    @get:Rule
    val ruleChain: RuleChain = RuleChain
            .outerRule(rule)
            .around(ScreenshotTakingRule())

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
        rule.getScenario().onActivity {
            PreferenceManager.getDefaultSharedPreferences(it)
                    .getString("IABTCF_TCString", null)
                    .assertNotNull()
        }
    }


    @Test
    fun given_an_OTT_campaign_SHOW_message_and_ACCEPT_ALL() = runBlocking {
        val spClient = mockk<SpClient>(relaxed = true)
        loadModules(spClient)

        rule.launch()

        wr {
            tapAcceptAllOnWebView()
            device.pressEnter()
        }

        // TODO: uncomment when https://sourcepoint.atlassian.net/browse/DIA-5350 is solved
//        verify(exactly = 0) { spClient.onError(any()) }
//        wr{ verify(exactly = 1) { spClient.onConsentReady(any()) } }
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
    fun given_an_OTT_campaign_SHOW_message_and_ACCEPT_ALL_from_PM() = runBlocking {
        val spClient = mockk<SpClient>(relaxed = true)
        loadModules(spClient)

        rule.launch()

        wr {
            tapAcceptAllOnWebView()
            device.pressEnter()
        }

        wr { clickOnGdprReviewConsent() }
        wr {
            tapAcceptAllOnWebView()
            device.pressEnter()
        }

        // TODO: uncomment when https://sourcepoint.atlassian.net/browse/DIA-5350 is solved
//        wr { verify(exactly = 0) { spClient.onError(any()) } }
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

        rule.launch()

        wr { tapSettingsOnWebView() }
        wr { device.pressBack() }
        wr { rule.getScenario().state.assertNotEquals(Lifecycle.State.DESTROYED) }
        wr { device.pressBack() }
        wr { rule.getScenario().state.assertNotEquals(Lifecycle.State.DESTROYED) }
    }
}

package com.sourcepoint.app.v6

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import com.example.uitestutil.*
import com.sourcepoint.app.v6.TestUseCase.Companion.mockModule
import com.sourcepoint.app.v6.TestUseCase.Companion.tapAcceptCcpaOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapAcceptOnWebView
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.creation.config
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.After
import org.junit.Test
import org.koin.core.context.loadKoinModules

class MainActivityExpirationConsent {

    lateinit var scenario: ActivityScenario<MainActivityKotlin>

    @After
    fun cleanup() {
        if (this::scenario.isLateinit) scenario.close()
    }

    private val spConf = config {
        accountId = 22
        propertyId = 16893
        propertyName = "mobile.multicampaign.demo"
        messLanguage = MessageLanguage.ENGLISH
        messageTimeout = 10000
        +(CampaignType.GDPR)
        +(CampaignType.CCPA)
    }

    private fun loadModulesWithState(state: JSONObject, client: SpClient) {
        loadKoinModules(mockModule(
                spConfig = spConf,
                gdprPmId = "488393",
                ccpaPmId = "509688",
                spClientObserver = listOf(client),
                diagnostic = state.toList()
        ))
    }

    @Test
    fun given_a_CCPA_and_GDPR_expired_consents_assert_messages_show_again() = runBlocking<Unit> {
        val spClient = mockk<SpClient>(relaxed = true)
        loadModulesWithState(JSONObject(TestData.expiredStoredConsentV743), spClient)

        scenario = launchActivity()

        wr { tapAcceptOnWebView() }
        wr { tapAcceptCcpaOnWebView() }
        wr { verify(exactly = 2) { spClient.onUIReady(any()) } }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
    }

    @Test
    fun given_an_expired_GDPR_consent_assert_GDPR_message_shows_again() = runBlocking<Unit> {
        val spClient = mockk<SpClient>(relaxed = true)
        loadModulesWithState(JSONObject(TestData.expiredGdprStoredConsentV743), spClient)

        scenario = launchActivity()

        wr { tapAcceptOnWebView() }

        wr { verify(exactly = 1) { spClient.onUIReady(any()) } }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
    }

    @Test
    fun given_an_expired_CCPA_consent_assert_CCPA_message_shows_again() = runBlocking<Unit> {
        val spClient = mockk<SpClient>(relaxed = true)
        loadModulesWithState(JSONObject(TestData.expiredCcpaStoredConsentV743), spClient)

        scenario = launchActivity()

        wr { tapAcceptCcpaOnWebView() }

        wr { verify(exactly = 1) { spClient.onUIReady(any()) } }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
    }
}
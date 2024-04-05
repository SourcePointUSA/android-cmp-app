package com.sourcepoint.app.v6

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.uitestutil.*
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.creation.config
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.exposed.SpCampaign
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules


@RunWith(AndroidJUnit4ClassRunner::class)
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
        messageTimeout = 5000
        +(CampaignType.GDPR)
        +(CampaignType.CCPA)
    }

    @Test
    fun GIVEN_a_multicampaign_expired_consent_VERIFY_that_the_gdpr_consent_gets_deleted_and_downloaded_again() = runBlocking<Unit> {

        val v7Consent = JSONObject(TestData.expiredStoredConsentV743)

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            TestUseCase.mockModule(
                spConfig = spConf,
                gdprPmId = "488393",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient),
                diagnostic = v7Consent.toList()
            )
        )

        scenario = launchActivity()

        wr(backup = { TestUseCase.clickOnRefreshBtnActivity() }) { TestUseCase.tapAcceptOnWebView() }
        wr { TestUseCase.tapAcceptCcpaOnWebView() }

        verify(exactly = 0) { spClient.onError(any()) }
        wr {
            verify(exactly = 1) {
                spClient.onSpFinished(withArg {
                    it.gdpr!!.consent.consentStatus!!.consentedAll.assertNotNull()
                    it.gdpr!!.consent.uuid.assertNotNull()
                })
            }
        }
        wr { verify(exactly = 2) { spClient.onConsentReady(any()) } }
        wr { verify(atLeast = 2) { spClient.onUIReady(any()) } }
        wr { verify(exactly = 2) { spClient.onAction(any(),any()) }}
        verify(exactly = 1) { spClient.onUIFinished(any()) }
    }

    @Test
    fun GIVEN_an_expired_GDPR_consent_RELOAD_the_consent() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        val v7CCPALocalState = JSONObject(TestData.expiredGdprStoredConsentV743)

        loadKoinModules(
            TestUseCase.mockModule(
                spConfig = spConf,
                gdprPmId = "488393",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient),
                diagnostic = v7CCPALocalState.toList()
            )
        )

        scenario = launchActivity()

        wr(backup = { TestUseCase.clickOnRefreshBtnActivity() }) { TestUseCase.tapAcceptOnWebView() }

        verify(exactly = 0) { spClient.onError(any()) }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
        wr { verify(exactly = 1) { spClient.onConsentReady(any()) } }
        wr { verify(atLeast = 1) { spClient.onUIReady(any()) } }

        verify {
            spClient.run {
                onSpFinished(withArg {
                    it.ccpa!!.consent.applies.assertTrue()
                    it.ccpa!!.consent.uuid.assertNotNull()
                })
            }
        }

    }

    @Test
    fun GIVEN_an_expired_CCPA_consent_RELOAD_the_consent() = runBlocking<Unit> {
        val spClient = mockk<SpClient>(relaxed = true)

        val v7CCPALocalState = JSONObject(TestData.expiredCcpaStoredConsentV743)

        loadKoinModules(
            TestUseCase.mockModule(
                spConfig = spConf,
                gdprPmId = "488393",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient),
                diagnostic = v7CCPALocalState.toList()
            )
        )

        scenario = launchActivity()

        wr(backup = { TestUseCase.clickOnRefreshBtnActivity() }) { TestUseCase.tapAcceptOnWebView() }

        verify(exactly = 0) { spClient.onError(any()) }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
        wr { verify(exactly = 1) { spClient.onConsentReady(any()) } }
        wr { verify(atLeast = 1) { spClient.onUIReady(any()) } }

        verify {
            spClient.run {
                onSpFinished(withArg {
                    it.ccpa!!.consent.applies.assertTrue()
                    it.ccpa!!.consent.uuid.assertNotNull()
                })
            }
        }
    }
}
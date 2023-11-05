package com.sourcepoint.app.v6

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.example.uitestutil.*
import com.sourcepoint.app.v6.TestUseCase.Companion.checkAllGdprConsentsOn
import com.sourcepoint.app.v6.TestUseCase.Companion.checkAllTogglesOFF
import com.sourcepoint.app.v6.TestUseCase.Companion.checkAllTogglesOn
import com.sourcepoint.app.v6.TestUseCase.Companion.checkSomeConsentsOff
import com.sourcepoint.app.v6.TestUseCase.Companion.checkSomeConsentsOn
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnGdprReviewConsent
import com.sourcepoint.app.v6.TestUseCase.Companion.mockModule
import com.sourcepoint.app.v6.TestUseCase.Companion.tapAcceptAllOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapCancelOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapRejectAllWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapSaveAndExitWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapToEnableSomeOption
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.creation.config
import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityKotlinLoadPmTest {

    lateinit var scenario: ActivityScenario<MainActivityKotlin>

    private val device by lazy { UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()) }

    private val spConfOtt = config {
        accountId = 22
        propertyName = "ott.test.suite"
        propertyId = 22231
        campaignsEnv = CampaignsEnv.PUBLIC
        messLanguage = MessageLanguage.ENGLISH
        +(CampaignType.GDPR)
    }

    private val spConfGdprNoMessage = config {
        accountId = 22
        propertyId = 29498
        propertyName = "ott-ccpa-22"
        messLanguage = MessageLanguage.ENGLISH
        messageTimeout = 5000
        +(CampaignType.GDPR)
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

    private val spConfNative = config {
        accountId = 22
        propertyId = 18958
        propertyName = "mobile.multicampaign.native.demo" // gdprPmId = 545258
        messLanguage = MessageLanguage.ENGLISH
        messageTimeout = 5000
        +(CampaignType.GDPR)
    }

    @After
    fun cleanup() {
        if (this::scenario.isLateinit) scenario.close()
    }

    @Test
    fun GIVEN_a_campaign_SHOW_using_the_GDPR_PM_SaveAndExit_and_ACCEPT_ALL_() = runBlocking<Unit> {
        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConf,
                gdprPmId = "488393",
                ccpaPmId = "509688",
                runLoadMessage = false,
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { tapSaveAndExitWebView() }
        wr(backup = { clickOnGdprReviewConsent() }) { tapToEnableSomeOption() }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }

        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { tapAcceptAllOnWebView() }
        wr { verify(exactly = 2) { spClient.onSpFinished(any()) } }


        wr(backup = { clickOnGdprReviewConsent() }) { checkAllTogglesOn() }
        wr { tapCancelOnWebView() }


        verify(exactly = 0) { spClient.onError(any()) }
        wr {
            verify {
                spClient.onConsentReady(withArg {
//                    it.gdpr!!.consent.applies.assertTrue()
                    it.gdpr!!.consent.consentStatus!!.consentedAll!!.assertTrue()
                })
            }
        }
        wr {
            verify {
                spClient.onSpFinished(withArg {
//                    it.gdpr!!.consent.applies.assertTrue()
                    it.gdpr!!.consent.consentStatus!!.consentedAll!!.assertTrue()
                })
            }
        }

    }

    @Test
    fun GIVEN_a_campaign_SHOW_using_the_GDPR_PM_SaveAndExit_and_REJECT_ALL_() = runBlocking<Unit> {
        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConf,
                gdprPmId = "488393",
                ccpaPmId = "509688",
                runLoadMessage = false,
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { tapSaveAndExitWebView() }
        wr(backup = { clickOnGdprReviewConsent() }) { tapToEnableSomeOption() }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }

        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { checkSomeConsentsOn() }
        wr(backup = { clickOnGdprReviewConsent() }) { tapRejectAllWebView() }
        wr { verify(exactly = 2) { spClient.onSpFinished(any()) } }


        wr(backup = { clickOnGdprReviewConsent() }) { checkAllTogglesOFF() }
        wr { tapCancelOnWebView() }


        verify(exactly = 0) { spClient.onError(any()) }
        wr {
            verify {
                spClient.onConsentReady(withArg {
//                    it.gdpr!!.consent.applies.assertTrue()
                    it.gdpr!!.consent.consentStatus!!.consentedAll!!.assertFalse()
                })
            }
        }
        wr {
            verify {
                spClient.onSpFinished(withArg {
//                    it.gdpr!!.consent.applies.assertTrue()
                    it.gdpr!!.consent.consentStatus!!.consentedAll!!.assertFalse()
                })
            }
        }

    }

    @Test
    fun GIVEN_a_campaign_SHOW_using_the_GDPR_PM_ACCEPT_ALL_and_SaveAndExit() = runBlocking<Unit> {
        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConf,
                gdprPmId = "488393",
                ccpaPmId = "509688",
                runLoadMessage = false,
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { tapAcceptAllOnWebView() }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }


        verify(exactly = 0) { spClient.onError(any()) }
        wr {
            verify {
                spClient.onConsentReady(withArg {
//                    it.gdpr!!.consent.applies.assertTrue()
                    it.gdpr!!.consent.consentStatus!!.consentedAll.assertNotNull()
                })
            }
        }
        wr {
            verify {
                spClient.onSpFinished(withArg {
//                    it.gdpr!!.consent.applies.assertTrue()
                    it.gdpr!!.consent.consentStatus!!.consentedAll.assertNotNull()
                })
            }
        }

    }
}
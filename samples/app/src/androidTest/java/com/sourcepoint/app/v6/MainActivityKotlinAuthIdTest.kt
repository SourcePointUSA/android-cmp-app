package com.sourcepoint.app.v6

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.uitestutil.*
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnRefreshBtnActivity
import com.sourcepoint.app.v6.TestUseCase.Companion.mockModule
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.creation.ConfigOption
import com.sourcepoint.cmplibrary.creation.config
import com.sourcepoint.cmplibrary.creation.to
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.exposed.SpCampaign
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonPrimitive
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityKotlinAuthIdTest {

    lateinit var scenario: ActivityScenario<MainActivityKotlin>

    @After
    fun cleanup() {
        if (this::scenario.isLateinit) scenario.close()
    }

    private val spConfCcpa = config {
        accountId = 22
        propertyId = 16893
        propertyName = "mobile.multicampaign.demo"
        messLanguage = MessageLanguage.ENGLISH
        +(CampaignType.CCPA)
    }

    private val spConfGdpr = config {
        accountId = 22
        propertyId = 16893
        propertyName = "mobile.multicampaign.demo"
        messLanguage = MessageLanguage.ENGLISH
        +(CampaignType.GDPR)
    }

    private val toggoConfig = config {
        accountId = 1631
        propertyId = 18893
        propertyName = "TOGGO-App-iOS"
        messLanguage = MessageLanguage.ENGLISH
        messageTimeout = 5000
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

    private val spConfGdprGroupId = config {
        accountId = 22
        propertyId = 24188
        propertyName = "mobile.prop-1"
        messLanguage = MessageLanguage.ENGLISH
        messageTimeout = 5000
        +SpCampaign(campaignType = CampaignType.GDPR, groupPmId = "613056" )
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

    private val spConfUSNAT = config {
        accountId = 22
        propertyId = 16893
        propertyName = "mobile.multicampaign.demo"
        messLanguage = MessageLanguage.ENGLISH
        messageTimeout = 5000
        +(CampaignType.USNAT to setOf(ConfigOption.SUPPORT_LEGACY_USPSTRING))
    }

    private val spConfNative = config {
        accountId = 22
        propertyId = 18958
        propertyName = "mobile.multicampaign.native.demo" // gdprPmId = 545258
        messLanguage = MessageLanguage.ENGLISH
        messageTimeout = 5000
        +(CampaignType.GDPR)
    }

    @Test
    fun GIVEN_an_authId_VERIFY_onError_is_NOT_called() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConf,
                gdprPmId = "488393",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient),
                pAuthId = "ee7ea3b8-9609-4ba4-be07-0986d32cdd1e"
            )
        )

        scenario = launchActivity()

        wr { scenario.onResumeOrThrow() }
        wr { clickOnRefreshBtnActivity() }
        wr { clickOnRefreshBtnActivity() }

        verify(exactly = 0) { spClient.onError(any()) }
        wr { verify(exactly = 3) { spClient.onSpFinished(any()) } }
        wr { verify(exactly = 3) { spClient.onConsentReady(any()) } }
        wr { verify(exactly = 0) { spClient.onUIReady(any()) } }
    }

    private fun ActivityScenario<MainActivityKotlin>.onResumeOrThrow(){
        if(this.state != Lifecycle.State.RESUMED) throw RuntimeException()
    }

    @Test
    fun GIVEN_a_usnat_authId_VERIFY_onError_is_NOT_called() = runBlocking<Unit> {
        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfUSNAT,
                gdprPmId = "488393",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient),
                pAuthId = "ee7ea3b8-USNAT"
            )
        )

        scenario = launchActivity()

        wr { scenario.onResumeOrThrow() }

        verify(exactly = 0) { spClient.onError(any()) }
        wr { verify(exactly = 1) { spClient.onConsentReady(any()) } }
        wr { verify(exactly = 0) { spClient.onUIReady(any()) } }
        wr { verify(exactly = 1) { spClient.onSpFinished( withArg {
            it.usNat!!.consent.run {
                (gppData["IABUSPrivacy_String"] as JsonPrimitive).content.assertEquals("1YNN")
                applies.assertTrue()
                statuses.consentedToAll!!.assertTrue()
                uuid.assertNotNull()
            }
        })}}
    }
}
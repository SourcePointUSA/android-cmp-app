package com.sourcepoint.app.v6

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.periodicWr
import com.example.uitestutil.recreateAndResume
import com.example.uitestutil.wr
import com.sourcepoint.app.v6.TestUseCase.Companion.checkAllGdprConsentsOn
import com.sourcepoint.app.v6.TestUseCase.Companion.checkGdprNativeTitle
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnGdprReviewConsent
import com.sourcepoint.app.v6.TestUseCase.Companion.mockModule
import com.sourcepoint.app.v6.TestUseCase.Companion.tapNmAcceptAll
import com.sourcepoint.app.v6.TestUseCase.Companion.tapNmDismiss
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.creation.config
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
class MainActivityNativeMessTest {

    lateinit var scenario: ActivityScenario<NativeMessageActivity>
    private val appContext by lazy { InstrumentationRegistry.getInstrumentation().targetContext }

    @After
    fun cleanup() {
        if (this::scenario.isLateinit) scenario.close()
    }

    private val spConfGdpr = config {
        accountId = 22
        propertyName = "mobile.multicampaign.fully.native"  //594218 - 22758 propId
        messLanguage = MessageLanguage.ENGLISH
        messageTimeout = 3000
        +(CampaignType.GDPR)
    }

    private val spConf = config {
        accountId = 22
        propertyName = "mobile.multicampaign.fully.native"  //594218
        messLanguage = MessageLanguage.ENGLISH
        messageTimeout = 3000
        +(CampaignType.GDPR)
        +(CampaignType.CCPA)
    }

//    @Test
    fun GIVEN_a_native_message_DISMISS_all_messages() = runBlocking<Unit> {
        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConf,
                gdprPmId = "594218",
                ccpaPmId = "594219",
                pResetAll = false,
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        periodicWr(backup = { scenario.recreateAndResume() }) { checkGdprNativeTitle() }
        wr { tapNmDismiss() }
        wr { tapNmDismiss() }

        verify(exactly = 2) { spClient.onNativeMessageReady(any(), any()) }
        verify(exactly = 1) { spClient.onSpFinished(any()) }
        verify(exactly = 0) { spClient.onConsentReady(any()) }
        verify(exactly = 0) { spClient.onUIReady(any()) }
        verify(exactly = 0) { spClient.onError(any()) }
        verify(exactly = 0) { spClient.onUIFinished(any()) }
        verify(exactly = 0) { spClient.onNoIntentActivitiesFound(any()) }
        verify(exactly = 0) { spClient.onAction(any(), any()) }
    }

    @Test
    fun GIVEN_a_gdpr_native_message_ACCEPT_ALL_and_verify() = runBlocking<Unit> {
        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr,
                gdprPmId = "594218",
                ccpaPmId = "594219",
                pResetAll = false,
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        periodicWr(backup = { scenario.recreateAndResume() }) { checkGdprNativeTitle() }
        wr { tapNmAcceptAll() }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { checkAllGdprConsentsOn() }

        wr {
            verify(atLeast = 1) { spClient.onSpFinished(any()) }
        }
        verify(atLeast = 1) { spClient.onNativeMessageReady(any(), any()) }
        verify(atLeast = 1) { spClient.onConsentReady(any()) }
        verify(atLeast = 1) { spClient.onUIReady(any()) }
        verify(exactly = 0) { spClient.onError(any()) }
        verify(exactly = 0) { spClient.onUIFinished(any()) }
        verify(exactly = 0) { spClient.onNoIntentActivitiesFound(any()) }
        verify(exactly = 0) { spClient.onAction(any(), any()) }
    }

    @Test
    fun GIVEN_an_authId_VERIFY_no_first_layer_mess_gets_called() = runBlocking<Unit> {
        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr,
                gdprPmId = "594218",
                ccpaPmId = "594219",
                pAuthId = "test",
                pResetAll = false,
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
        verify(exactly = 1) { spClient.onConsentReady(any()) }
        verify(exactly = 0) { spClient.onUIReady(any()) }
        verify(exactly = 0) { spClient.onError(any()) }
        verify(exactly = 0) { spClient.onUIFinished(any()) }
    }
}

package com.sourcepoint.app.v6

import android.preference.PreferenceManager
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.assertTrue
import com.example.uitestutil.wr
import com.sourcepoint.app.v6.TestUseCase.Companion.checkAllGdprConsentsOn
import com.sourcepoint.app.v6.TestUseCase.Companion.checkGdprNativeTitle
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnGdprReviewConsent
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnRefreshBtnActivity
import com.sourcepoint.app.v6.TestUseCase.Companion.mockModule
import com.sourcepoint.app.v6.TestUseCase.Companion.tapAcceptOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapNmAcceptAll
import com.sourcepoint.app.v6.TestUseCase.Companion.tapNmDismiss
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.creation.config
import com.sourcepoint.cmplibrary.data.network.util.CampaignType
import com.sourcepoint.cmplibrary.model.CampaignsEnv
import com.sourcepoint.cmplibrary.model.MessageLanguage
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import java.util.UUID

//@RunWith(AndroidJUnit4ClassRunner::class)
//class MainActivityNativeMessTest {
//
//    lateinit var scenario: ActivityScenario<NativeMessageActivity>
//    private val appContext by lazy { InstrumentationRegistry.getInstrumentation().targetContext }
//
//    @After
//    fun cleanup() {
//        if (this::scenario.isLateinit) scenario.close()
//    }
//
//    private val spConfGdpr = config {
//        accountId = 22
//        propertyName = "mobile.multicampaign.fully.native"  //594218
//        propertyId = 22758
//        messLanguage = MessageLanguage.ENGLISH
//        +(CampaignType.GDPR)
//    }
//
//    private val spConf = config {
//        accountId = 22
//        propertyName = "mobile.multicampaign.fully.native"  //594218
//        propertyId = 22758
//        messLanguage = MessageLanguage.ENGLISH
//        +(CampaignType.GDPR)
//        +(CampaignType.CCPA)
//    }
//
//    private val spConf2 = config {
//        accountId = 22
//        propertyName = "mobile.multicampaign.native.demo2"  //594218
//        propertyId = 19210
//        messLanguage = MessageLanguage.ENGLISH
//        +(CampaignType.GDPR)
//        +(CampaignType.CCPA)
//    }
//
////    @Test
//    fun GIVEN_a_native_message_DISMISS_all_messages() = runBlocking<Unit> {
//        val spClient = mockk<SpClient>(relaxed = true)
//
//        loadKoinModules(
//            mockModule(
//                spConfig = spConf,
//                gdprPmId = "594218",
//                ccpaPmId = "594219",
//                pResetAll = false,
//                spClientObserver = listOf(spClient)
//            )
//        )
//
//        scenario = launchActivity()
//
//        wr(backup = { clickOnRefreshBtnActivity() })  { checkGdprNativeTitle() }
//        wr { tapNmDismiss() }
//        wr { tapNmDismiss() }
//
//        verify(exactly = 2) { spClient.onNativeMessageReady(any(), any()) }
//        verify(exactly = 1) { spClient.onSpFinished(any()) }
//        verify(exactly = 0) { spClient.onConsentReady(any()) }
//        verify(exactly = 0) { spClient.onUIReady(any()) }
//        verify(exactly = 0) { spClient.onError(any()) }
//        verify(exactly = 0) { spClient.onUIFinished(any()) }
//        verify(exactly = 0) { spClient.onNoIntentActivitiesFound(any()) }
//        verify(exactly = 0) { spClient.onAction(any(), any()) }
//    }
//
//    @Test
//    fun GIVEN_a_gdpr_and_ccpa_native_message_ACCEPT_ALL_and_verify() = runBlocking<Unit> {
//        val spClient = mockk<SpClient>(relaxed = true)
//
//        loadKoinModules(
//            mockModule(
//                spConfig = spConf,
//                gdprPmId = "594218",
//                ccpaPmId = "594219",
//                pResetAll = true,
//                spClientObserver = listOf(spClient)
//            )
//        )
//
//        scenario = launchActivity()
//
//        wr(backup = { clickOnRefreshBtnActivity() })  { checkGdprNativeTitle() }
//        wr { tapNmAcceptAll() }
//        wr { tapNmAcceptAll() }
//
//        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
//        verify(exactly = 2) { spClient.onNativeMessageReady(any(), any()) }
//        wr { verify(exactly = 2) { spClient.onConsentReady(any()) } }
//        verify(exactly = 0) { spClient.onUIReady(any()) }
//        verify(exactly = 0) { spClient.onError(any()) }
//        verify(exactly = 0) { spClient.onUIFinished(any()) }
//        verify(exactly = 0) { spClient.onNoIntentActivitiesFound(any()) }
//        verify(exactly = 0) { spClient.onAction(any(), any()) }
//    }
//
////    @Test
//    fun GIVEN_a_gdpr_native_message_ACCEPT_ALL_and_verify() = runBlocking<Unit> {
//        val spClient = mockk<SpClient>(relaxed = true)
//
//        loadKoinModules(
//            mockModule(
//                spConfig = spConfGdpr,
//                gdprPmId = "594218",
//                ccpaPmId = "594219",
//                pResetAll = false,
//                spClientObserver = listOf(spClient)
//            )
//        )
//
//        scenario = launchActivity()
//
//        wr(backup = { clickOnRefreshBtnActivity() })  { checkGdprNativeTitle() }
//        wr { tapNmAcceptAll() }
//        wr {
//            scenario.onActivity { activity ->
//                PreferenceManager.getDefaultSharedPreferences(activity).contains("sp.gdpr.consentUUID").assertTrue()
//            }
//        }
//        wr { clickOnGdprReviewConsent() }
//        wr(backup = { clickOnGdprReviewConsent() }) { checkAllGdprConsentsOn() }
//
//        wr { verify(atLeast = 1) { spClient.onSpFinished(any()) }        }
//        verify { spClient.onNativeMessageReady(any(), any()) }
//        verify { spClient.onConsentReady(any()) }
//        verify { spClient.onUIReady(any()) }
//        verify(exactly = 0) { spClient.onError(any()) }
//        verify(exactly = 0) { spClient.onUIFinished(any()) }
//        verify(exactly = 0) { spClient.onNoIntentActivitiesFound(any()) }
//        verify(exactly = 0) { spClient.onAction(any(), any()) }
//    }
//
//    @Test
//    fun VERIFY_that_the_stage_and_prod_configuration_work() = runBlocking<Unit> {
//        val spClient = mockk<SpClient>(relaxed = true)
//
//        loadKoinModules(
//            mockModule(
//                spConfig = spConf2.copy(campaignsEnv = CampaignsEnv.STAGE),
//                gdprPmId = "598486",
//                ccpaPmId = "598492",
//                spClientObserver = listOf(spClient)
//            )
//        )
//
//        scenario = launchActivity()
//
//        wr(backup = { clickOnRefreshBtnActivity() })  { tapAcceptOnWebView() }
//        wr { checkGdprNativeTitle() } // The order matters because in the stage env the CCPA is configured before the GDPR
//        wr { tapNmAcceptAll() }
//    }
//
//    @Test
//    fun GIVEN_an_authId_VERIFY_no_first_layer_mess_gets_called() = runBlocking<Unit> {
//        val spClient = mockk<SpClient>(relaxed = true)
//
//        loadKoinModules(
//            mockModule(
//                spConfig = spConfGdpr,
//                gdprPmId = "594218",
//                ccpaPmId = "594219",
//                pAuthId = UUID.randomUUID().toString(),
//                pResetAll = false,
//                spClientObserver = listOf(spClient)
//            )
//        )
//
//        scenario = launchActivity()
//        wr { tapNmAcceptAll() }
//        wr(delay = 1000) { clickOnRefreshBtnActivity() }
//
//        wr { verify(exactly = 2) { spClient.onSpFinished(any()) } }
//        verify(exactly = 2) { spClient.onConsentReady(any()) }
//        verify(exactly = 1) { spClient.onNativeMessageReady(any(), any()) }
//        verify(exactly = 0) { spClient.onError(any()) }
//        verify(exactly = 0) { spClient.onUIFinished(any()) }
//    }
//}

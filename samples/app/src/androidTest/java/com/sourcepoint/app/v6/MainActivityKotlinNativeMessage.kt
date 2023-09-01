package com.sourcepoint.app.v6

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
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
import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.util.clearAllData
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityNativeMessTest {

    lateinit var scenario: ActivityScenario<NativeMessageActivity>

    @MockK
    lateinit var spClient: SpClient
    private var appIdlingResource: IdlingResource? = null

    @JvmField
    @Rule
    var retry = Retry(3, onRetry = {
        cleanLocalStorage()
        setupMocks()
    })

    @Before
    fun setupMocks() {
        MockKAnnotations.init(this, relaxed = true)
    }

    @Before
    fun cleanLocalStorage() {
        clearAllData(ApplicationProvider.getApplicationContext())
    }

    @After
    fun cleanup() {
        if (this::scenario.isLateinit) scenario.close()
        IdlingRegistry.getInstance().unregister(appIdlingResource)
    }

    private val spConfGdpr = config {
        accountId = 22
        propertyName = "mobile.multicampaign.fully.native"  //594218
        propertyId = 22758
        messLanguage = MessageLanguage.ENGLISH
        +(CampaignType.GDPR)
    }

    private val spConf = config {
        accountId = 22
        propertyName = "mobile.multicampaign.fully.native"  //594218
        propertyId = 22758
        messLanguage = MessageLanguage.ENGLISH
        +(CampaignType.GDPR)
        +(CampaignType.CCPA)
    }

    private val spConf2 = config {
        accountId = 22
        propertyName = "mobile.multicampaign.native.demo2"  //594218
        propertyId = 19210
        messLanguage = MessageLanguage.ENGLISH
        +(CampaignType.GDPR)
        +(CampaignType.CCPA)
    }

    private fun launchApp() {
        scenario = launchActivity()
        scenario.onActivity { activity ->
            appIdlingResource = activity.appIdlingResource
            IdlingRegistry.getInstance().register(appIdlingResource)
        }
    }

//    @Test
//    fun GIVEN_a_native_message_DISMISS_all_messages() = runBlocking<Unit> {
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
//        launchApp()
//
//        wr { checkGdprNativeTitle() }
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

    @Test
    fun GIVEN_a_gdpr_and_ccpa_native_message_ACCEPT_ALL_and_verify() = runBlocking<Unit> {
        loadKoinModules(
            mockModule(
                spConfig = spConf,
                gdprPmId = "594218",
                ccpaPmId = "594219",
                pResetAll = true,
                spClientObserver = listOf(spClient)
            )
        )

        launchApp()

        checkGdprNativeTitle()
        tapNmAcceptAll() // once for GDPR
        tapNmAcceptAll() // once for CCPA

        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
        wr { verify(exactly = 2) { spClient.onNativeMessageReady(any(), any()) } }
        wr { verify(exactly = 2) { spClient.onConsentReady(any()) } }
        wr { verify(exactly = 0) { spClient.onUIReady(any()) } }
        wr { verify(exactly = 0) { spClient.onError(any()) } }
        wr { verify(exactly = 0) { spClient.onUIFinished(any()) } }
        wr { verify(exactly = 0) { spClient.onNoIntentActivitiesFound(any()) } }
        wr { verify(exactly = 0) { spClient.onAction(any(), any()) } }
    }

//    @Test
//    fun GIVEN_a_gdpr_native_message_ACCEPT_ALL_and_verify() = runBlocking<Unit> {
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
//        launchApp()
//
//        wr { checkGdprNativeTitle() }
//        wr { tapNmAcceptAll() }
//
//        clickOnGdprReviewConsent()
//        checkAllGdprConsentsOn()
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

    @Test
    fun GIVEN_an_authId_VERIFY_no_first_layer_mess_gets_called() = runBlocking<Unit> {
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

        launchApp()

        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
        wr { verify(exactly = 1) { spClient.onConsentReady(any()) } }
        wr { verify(exactly = 0) { spClient.onUIReady(any()) } }
        wr { verify(exactly = 0) { spClient.onError(any()) } }
        wr { verify(exactly = 0) { spClient.onUIFinished(any()) } }
    }
}

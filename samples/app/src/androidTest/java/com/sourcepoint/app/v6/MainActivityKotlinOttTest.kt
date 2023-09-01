package com.sourcepoint.app.v6

import android.preference.PreferenceManager
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.filters.LargeTest
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
@LargeTest
class MainActivityKotlinOttTest {

    lateinit var scenario: ActivityScenario<MainActivityKotlin>

    private var appIdlingResource: IdlingResource? = null

    private val device by lazy { UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()) }

    private var sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())

    @MockK
    lateinit var spClient: SpClient

    @Before
    fun cleanLocalStorage() {
        clearAllData(ApplicationProvider.getApplicationContext())
    }

    @Before
    fun setupMocks() {
        MockKAnnotations.init(this, relaxed = true)
    }

    @After
    fun cleanup() {
        if (this::scenario.isLateinit) scenario.close()
        IdlingRegistry.getInstance().unregister(appIdlingResource)
    }

    @JvmField
    @Rule
    var retry = Retry(3, onRetry = {
        cleanLocalStorage()
        setupMocks()
    })

    private val spConfOtt = config {
        accountId = 22
        propertyName = "ott.test.suite"
        propertyId = 22231
        campaignsEnv = CampaignsEnv.PUBLIC
        messLanguage = MessageLanguage.ENGLISH
        +(CampaignType.GDPR)
    }

    /**
     * Use [to launch and get access to the activity.][ActivityScenario.onActivity]
     */
    private fun launchApp() {
        scenario = launchActivity()
        scenario.onActivity { activity ->
            appIdlingResource = activity.appIdlingResource
            IdlingRegistry.getInstance().register(appIdlingResource)
        }
    }

    @Test
    fun GIVEN_an_OTT_campaign_SHOW_message_and_ACCEPT_ALL() = runBlocking<Unit> {
        loadKoinModules(
            mockModule(
                spConfig = spConfOtt,
                gdprPmId = "579231",
                ccpaPmId = "1",
                spClientObserver = listOf(spClient)
            )
        )

        launchApp()

        tapAcceptAllOnWebView()
        device.pressEnter()

        wr { verify(exactly = 0) { spClient.onError(any()) } }
        wr { verify(exactly = 1) { spClient.onConsentReady(any()) } }
        wr { verify { spClient.onAction(any(), withArg { it.pubData["pb_key"].assertEquals("pb_value") }) } }

        sharedPrefs.run {
            getString("IABTCF_TCString", null).assertNotNull()
        }
    }

    @Test
    fun GIVEN_an_OTT_campaign_SHOW_message_and_ACCEPT_ALL_from_PM() = runBlocking<Unit> {
        loadKoinModules(
            mockModule(
                spConfig = spConfOtt,
                gdprPmId = "579231",
                ccpaPmId = "1",
                spClientObserver = listOf(spClient)
            )
        )

        launchApp()

        tapAcceptAllOnWebView()
        device.pressEnter()

        clickOnGdprReviewConsent()
        tapAcceptAllOnWebView()
        device.pressEnter()

        wr { verify(exactly = 0) { spClient.onError(any()) } }
        wr { verify(atLeast = 2) { spClient.onConsentReady(any()) } }
        wr { verify { spClient.onAction(any(), withArg { it.pubData["pb_key"].assertEquals("pb_value") }) } }

        sharedPrefs.run {
            getString("IABTCF_TCString", null).assertNotNull()
        }
    }
}
package com.sourcepoint.app.v6

import android.preference.PreferenceManager
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.uitestutil.*
import com.sourcepoint.app.v6.MainActivityKotlin.Companion.CLIENT_PREF_KEY
import com.sourcepoint.app.v6.MainActivityKotlin.Companion.CLIENT_PREF_VAL
import com.sourcepoint.app.v6.TestUseCase.Companion.checkAllCcpaConsentsOn
import com.sourcepoint.app.v6.TestUseCase.Companion.checkAllConsentsOff
import com.sourcepoint.app.v6.TestUseCase.Companion.checkAllConsentsOn
import com.sourcepoint.app.v6.TestUseCase.Companion.checkAllVendorsOff
import com.sourcepoint.app.v6.TestUseCase.Companion.checkCustomCategoriesData
import com.sourcepoint.app.v6.TestUseCase.Companion.checkCustomVendorDataList
import com.sourcepoint.app.v6.TestUseCase.Companion.checkDeepLinkDisplayed
import com.sourcepoint.app.v6.TestUseCase.Companion.checkFeaturesTab
import com.sourcepoint.app.v6.TestUseCase.Companion.checkPurposesTab
import com.sourcepoint.app.v6.TestUseCase.Companion.checkUUID
import com.sourcepoint.app.v6.TestUseCase.Companion.checkWebViewDisplayedGDPRFirstLayerMessage
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnCcpaReviewConsent
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnClearConsent
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnConsentActivity
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnCustomConsent
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnGdprReviewConsent
import com.sourcepoint.app.v6.TestUseCase.Companion.mockModule
import com.sourcepoint.app.v6.TestUseCase.Companion.tapAcceptAllOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapAcceptCcpaOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapAcceptOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapCancelOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapFeaturesOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapNetworkOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapOptionWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapPartnersOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapPurposesOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapRejectOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapSaveAndExitWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapSiteVendorsWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapToDisableAllConsent
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
class MainActivityKotlinTest {

    lateinit var scenario: ActivityScenario<MainActivityKotlin>

    @After
    fun cleanup() {
        if (this::scenario.isLateinit) scenario.close()
    }

    private val spConfCcpa = config {
        accountId = 22
        propertyName = "mobile.multicampaign.demo"
        messLanguage = MessageLanguage.ENGLISH
        messageTimeout = 3000
        +(CampaignType.CCPA)
    }

    private val spConfGdpr = config {
        accountId = 22
        propertyName = "mobile.multicampaign.demo"
        messLanguage = MessageLanguage.ENGLISH
        messageTimeout = 3000
        +(CampaignType.GDPR)
    }

    private val spConfGdprGroupId = config {
        accountId = 22
        propertyName = "mobile.prop-1"
        messLanguage = MessageLanguage.ENGLISH
        messageTimeout = 3000
        +(CampaignType.GDPR)
    }

    private val spConf = config {
        accountId = 22
        propertyName = "mobile.multicampaign.demo"
        messLanguage = MessageLanguage.ENGLISH
        messageTimeout = 3000
        +(CampaignType.GDPR)
        +(CampaignType.CCPA)
    }

    private val spConfNative = config {
        accountId = 22
        propertyName = "mobile.multicampaign.native.demo" // gdprPmId = 545258
        messLanguage = MessageLanguage.ENGLISH
        messageTimeout = 3000
        +(CampaignType.GDPR)
    }

    @Test
    fun GIVEN_a_gdpr_campaign_SHOW_message_and_ACCEPT_ALL() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)
        val categoriesTester = listOf(
            "608bad95d08d3112188e0e29",
            "608bad95d08d3112188e0e36",
            "608bad96d08d3112188e0e59",
            "60b65857619abe242bed971e",
            "608bad95d08d3112188e0e2f"
        ).sorted()

        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr,
                gdprPmId = "488393",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        periodicWr(backup = { scenario.recreateAndResume() }) { tapAcceptOnWebView() }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { checkAllConsentsOn() }

        verify(exactly = 0) { spClient.onError(any()) }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
        verify { spClient.onAction(any(), withArg { it.pubData["pb_key"].assertEquals("pb_value") }) }

        verify {
            spClient.run {
                onUIReady(any())
                onUIFinished(any())
                onAction(any(), any())
                onConsentReady(withArg {
                    it.gdpr?.consent?.acceptedCategories?.sorted()?.assertEquals(categoriesTester)
                })
            }
        }

        scenario.onActivity { activity ->
            val IABTCF_TCString = PreferenceManager.getDefaultSharedPreferences(activity)
                .getString("IABTCF_TCString", null)
            IABTCF_TCString.assertNotNull()
        }

    }

    @Test
    fun GIVEN_a_gdpr_campaign_CHECK_the_consent_from_a_second_activity() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr,
                gdprPmId = "488393",
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        periodicWr(backup = { scenario.recreateAndResume() }) { tapAcceptOnWebView() }
        wr {
            verify {
                spClient.onSpFinished(withArg {
                    val uuid = it.gdpr?.consent?.uuid.assertNotNull()!!
                    clickOnConsentActivity()
                    checkUUID(uuid)
                })
            }
        }

    }

    @Test
    fun GIVEN_a_ccpa_campaign_SHOW_message_and_ACCEPT_ALL() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfCcpa,
                gdprPmId = "488393",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        periodicWr(backup = { scenario.recreateAndResume() }) { tapAcceptOnWebView() }
        wr { clickOnCcpaReviewConsent() }
        wr(backup = { clickOnCcpaReviewConsent() }) { checkAllCcpaConsentsOn() }

        verify(exactly = 0) { spClient.onError(any()) }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
        verify { spClient.onAction(any(), withArg { it.pubData["pb_key"].assertEquals("pb_value") }) }

        verify {
            spClient.run {
                onUIReady(any())
                onUIFinished(any())
                onAction(any(), any())
                onConsentReady(any())
            }

        }
    }

    @Test
    fun GIVEN_a_dgpr_campaign_SHOW_message_and_REJECT_ALL() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr,
                gdprPmId = "488393",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        periodicWr(backup = { scenario.recreateAndResume() }) { tapRejectOnWebView() }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { checkAllConsentsOff() }

        verify(exactly = 0) { spClient.onError(any()) }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
        verify { spClient.onAction(any(), withArg { it.pubData["pb_key"].assertEquals("pb_value") }) }

        verify {
            spClient.run {
                onUIReady(any())
                onUIFinished(any())
                onAction(any(), any())
                onConsentReady(withArg {
                    it.gdpr?.consent?.acceptedCategories?.sorted()?.assertEquals(emptyList())
                    it.gdpr?.consent?.grants?.values?.forEach { el -> el.granted.assertFalse() }
                })
            }
        }
    }

    @Test
    fun GIVEN_a_campaignList_ACCEPT_all_legislation() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConf,
                gdprPmId = "488393",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient),
            )
        )

        scenario = launchActivity()

        periodicWr(period = 2000, backup = { scenario.recreateAndResume() }) { tapAcceptOnWebView() }
        wr { tapAcceptCcpaOnWebView() }

        verify(exactly = 0) { spClient.onError(any()) }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
        wr { verify(exactly = 2) { spClient.onConsentReady(any()) } }
        wr { verify(atLeast = 2) { spClient.onUIReady(any()) } }
        wr {
            verify(exactly = 2) {
                spClient.onAction(
                    any(),
                    withArg { it.pubData["pb_key"].assertEquals("pb_value") })
            }
        }
        verify(exactly = 1) { spClient.onUIFinished(any()) }

        verify {
            spClient.run {
                onUIReady(any())
                onAction(any(), any())
                onUIReady(any())
                onConsentReady(any())
                onUIFinished(any())
            }
        }
    }

    @Test
    fun GIVEN_a_camapignList_ACCEPT_all_legislation_from_option_button() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConf,
                gdprPmId = "488393",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        periodicWr(backup = { scenario.recreateAndResume() }) { tapOptionWebView() }
        wr { tapAcceptAllOnWebView() }
        wr { tapOptionWebView() }
        wr { tapAcceptAllOnWebView() }

        verify(exactly = 0) { spClient.onError(any()) }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
        wr { verify(exactly = 2) { spClient.onConsentReady(any()) } }
        wr { verify(atLeast = 4) { spClient.onUIReady(any()) } }
        wr { verify(exactly = 2) { spClient.onAction(any(), any()) } }
        verify(exactly = 3) { spClient.onUIFinished(any()) }

        verify {
            spClient.run {
                onUIReady(any())
                onUIFinished(any())
                onUIReady(any())
                onAction(any(), any())
                onUIReady(any())
                onConsentReady(any())
            }
        }
    }

    @Test
    fun GIVEN_consent_USING_gdpr_pm() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(mockModule(
            spConfig = spConfGdpr,
            gdprPmId = "488393",
            spClientObserver = listOf(spClient)
        ))

        scenario = launchActivity()

        periodicWr(backup = { scenario.recreateAndResume() }) { tapAcceptOnWebView() }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { tapAcceptAllOnWebView() }

        verify(exactly = 0) { spClient.onError(any()) }
        wr { verify(atLeast = 2) { spClient.onSpFinished(any()) } }

        verify {
            spClient.run {
                onUIReady(any())
                onUIFinished(any())
                onAction(any(), any())
                onConsentReady(any())
            }
        }
    }

    @Test
    fun GIVEN_a_gdpr_consent_ACCEPT_ALL() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(mockModule(
            spConfig = spConfGdpr,
            gdprPmId = "488393",
            spClientObserver = listOf(spClient)
        ))

        scenario = launchActivity()

        periodicWr(backup = { scenario.recreateAndResume() }) { tapRejectOnWebView() }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { tapAcceptAllOnWebView() }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { checkAllConsentsOn() }

        verify(exactly = 0) { spClient.onError(any()) }
        wr { verify(exactly = 2) { spClient.onSpFinished(any()) } }

        verify {
            spClient.run {
                onUIReady(any())
                onUIFinished(any())
                onAction(any(), any())
                onConsentReady(withArg {
                    it.gdpr?.consent?.grants?.values?.forEach { el -> el.granted.assertTrue() }
                })
            }
        }
    }

    @Test
    fun GIVEN_a_deeplink_OPEN_an_activity() = runBlocking<Unit> {

        loadKoinModules(mockModule(spConfig = spConfGdpr, gdprPmId = "488393"))

        scenario = launchActivity()

        periodicWr(backup = { scenario.recreateAndResume() }) { tapRejectOnWebView() }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { tapNetworkOnWebView() }
        wr { checkDeepLinkDisplayed() }
    }

    @Test
    fun SAVE_AND_EXIT_action() = runBlocking<Unit> {

        loadKoinModules(mockModule(spConfig = spConfGdpr, gdprPmId = "488393"))

        scenario = launchActivity()

        periodicWr(backup = { scenario.recreateAndResume() }) { tapAcceptOnWebView() }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { tapToDisableAllConsent() }
        wr { tapSaveAndExitWebView() }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { checkAllConsentsOff() }
    }

    @Test
    fun customConsentAction() = runBlocking<Unit> {

        loadKoinModules(mockModule(spConfig = spConfGdpr, gdprPmId = "488393"))

        scenario = launchActivity()

        periodicWr(backup = { scenario.recreateAndResume() }) { tapRejectOnWebView() }
        wr { clickOnCustomConsent() }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { checkCustomCategoriesData() }
        wr { tapSiteVendorsWebView() }
        wr { checkCustomVendorDataList() }
    }

    @Test
    fun GIVEN_a_camapignList_VERIFY_back_btn() = runBlocking<Unit> {
        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConf,
                gdprPmId = "488393",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        periodicWr(backup = { scenario.recreateAndResume() }) { tapOptionWebView() }
        wr { tapCancelOnWebView() }
        wr { checkWebViewDisplayedGDPRFirstLayerMessage() }
        wr { tapAcceptAllOnWebView() }
        wr { tapOptionWebView() }
        wr { tapCancelOnWebView() }
        wr { tapAcceptAllOnWebView() }

        verify(atLeast = 4) { spClient.onAction(any(), any()) }
    }

    @Test
    fun GIVEN_a_camapignList_PRESS_cancel_VERIFY_onConsentReady_NOT_called() = runBlocking<Unit> {
        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfGdprGroupId,
                gdprPmId = "613057",
                ccpaPmId = "-",
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        periodicWr(backup = { scenario.recreateAndResume() }) { tapCancelOnWebView() }

        verify(exactly = 0) { spClient.onConsentReady(any()) }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
    }

    @Test
    fun TAPPING_on_aVENDORS_link_SHOW_the_PM_VENDORS_tab() = runBlocking<Unit> {
        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfNative,
                gdprPmId = "545258",
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        // Vendors
        wr { tapPartnersOnWebView() }
        wr { checkAllVendorsOff() }
        wr { tapCancelOnWebView() }

        // Features
        wr { tapFeaturesOnWebView() }
        wr { checkFeaturesTab() }
        wr { tapCancelOnWebView() }

        // Purposes
        wr { tapPurposesOnWebView() }
        wr { checkPurposesTab() }
    }

    @Test
    fun GIVEN_a_saved_consent_CLEAR_all_SDK_variables() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr,
                gdprPmId = "488393",
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        periodicWr(backup = { scenario.recreateAndResume() }) { tapAcceptOnWebView() }

        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
        wr { clickOnClearConsent() }



        wr {
            scenario.onActivity { activity ->
                val sp = PreferenceManager.getDefaultSharedPreferences(activity)
                val numberOfItemInSP = sp.all.size
                numberOfItemInSP.assertEquals(1)
                sp.getString(CLIENT_PREF_KEY, "").assertEquals(CLIENT_PREF_VAL)
            }
        }


    }

}
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
import com.sourcepoint.app.v6.TestUseCase.Companion.checkDeletedCustomCategoriesData
import com.sourcepoint.app.v6.TestUseCase.Companion.checkDeletedCustomVendorDataList
import com.sourcepoint.app.v6.TestUseCase.Companion.checkEuconsent
import com.sourcepoint.app.v6.TestUseCase.Companion.checkFeaturesTab
import com.sourcepoint.app.v6.TestUseCase.Companion.checkGdprApplies
import com.sourcepoint.app.v6.TestUseCase.Companion.checkPurposesTab
import com.sourcepoint.app.v6.TestUseCase.Companion.checkWebViewDisplayedGDPRFirstLayerMessage
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnCcpaReviewConsent
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnClearConsent
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnConsentActivity
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnCustomConsent
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnDeleteCustomConsent
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnGdprReviewConsent
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnRefreshBtnActivity
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
import com.sourcepoint.app.v6.TestUseCase.Companion.tapRejectAllWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapRejectOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapSaveAndExitWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapSiteVendorsWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapToDisableAllConsent
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.creation.config
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.exposed.CcpaStatus
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
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
        propertyId = 16893
        propertyName = "mobile.multicampaign.demo"
        messLanguage = MessageLanguage.ENGLISH
        messageTimeout = 3000
        +(CampaignType.CCPA)
    }

    private val spConfGdpr = config {
        accountId = 22
        propertyId = 16893
        propertyName = "mobile.multicampaign.demo"
        messLanguage = MessageLanguage.ENGLISH
        messageTimeout = 3000
        +(CampaignType.GDPR)
    }

    private val spConfGdprGroupId = config {
        accountId = 22
        propertyId = 24188
        propertyName = "mobile.prop-1"
        messLanguage = MessageLanguage.ENGLISH
        messageTimeout = 3000
        +(CampaignType.GDPR)
    }

    private val spConf = config {
        accountId = 22
        propertyId = 16893
        propertyName = "mobile.multicampaign.demo"
        messLanguage = MessageLanguage.ENGLISH
        messageTimeout = 3000
        +(CampaignType.GDPR)
        +(CampaignType.CCPA)
    }

    private val spConfNative = config {
        accountId = 22
        propertyId = 18958
        propertyName = "mobile.multicampaign.native.demo" // gdprPmId = 545258
        messLanguage = MessageLanguage.ENGLISH
        messageTimeout = 3000
        +(CampaignType.GDPR)
    }

    @Test
    fun GIVEN_a_gdpr_campaign_SHOW_message_and_ACCEPT_ALL() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)
        val grantsTester = listOf(
            "5ff4d000a228633ac048be41",
            "5f1b2fbeb8e05c306f2a1eb9",
            "5e7ced57b8e05c485246cce0",
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

        wr(backup = { clickOnRefreshBtnActivity() })  { tapAcceptOnWebView() }

        wr {
            scenario.onActivity { activity ->
                PreferenceManager.getDefaultSharedPreferences(activity).contains("sp.gdpr.consentUUID").assertTrue()
            }
        }

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
                    it.gdpr!!.consent.grants.map { k -> k.key }.sorted().assertEquals(grantsTester)
                })
            }
        }

        scenario.onActivity { activity ->
            PreferenceManager.getDefaultSharedPreferences(activity).run {
                getString("IABTCF_TCString", null).assertNotNull()
                getInt("IABTCF_CmpSdkVersion", -1).assertNotEquals(-1)
                getInt("IABTCF_CmpSdkID", -1).assertNotEquals(-1)
                getInt("IABTCF_PolicyVersion", -1).assertNotEquals(-1)
                getInt("IABTCF_UseNonStandardStacks", -1).assertNotEquals(-1)
                getInt("IABTCF_gdprApplies", -1).assertNotEquals(-1)
                getInt("IABTCF_PurposeOneTreatment", -1).assertNotEquals(-1)
                getString("IABTCF_PurposeConsents", null).assertNotNull()
                getString("IABTCF_TCString", null).assertNotNull()
                getString("IABTCF_PublisherRestrictions10", null).assertNotNull()
                getString("IABTCF_PublisherLegitimateInterests", null).assertEquals("0000000000")
                getString("IABTCF_SpecialFeaturesOptIns", null).assertNotNull()
                getString("IABTCF_PublisherCC", null).assertNotNull()
                getString("IABTCF_VendorConsents", null).assertNotNull()
                getString("IABTCF_PublisherCustomPurposesLegitimateInterests", null).assertNotNull()
                getString("IABTCF_PurposeLegitimateInterests", null).assertNotNull()
                getString("IABTCF_AddtlConsent", null).assertEquals("1~899")
                getString("IABTCF_PublisherCustomPurposesConsents", null).assertNotNull()
                getString("IABTCF_PublisherRestrictions7", null).assertNotNull()
                getString("IABTCF_PublisherRestrictions2", null).assertNotNull()
                getString("IABTCF_PublisherRestrictions4", null).assertNotNull()
                getString("IABTCF_PublisherConsent", null).assertNotNull()
            }
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

        wr(backup = { clickOnRefreshBtnActivity() })  { tapAcceptOnWebView() }
        wr {
            verify {
                spClient.onSpFinished(withArg {
                    val euconsent = it.gdpr!!.consent.euconsent.assertNotNull()
                    val gdprApplies = it.gdpr!!.consent.applies.toString().assertNotNull()
                    clickOnConsentActivity()
                    checkEuconsent(euconsent)
                    checkGdprApplies(gdprApplies)
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

        wr(backup = { clickOnRefreshBtnActivity() })  { tapAcceptOnWebView() }
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

        wr {
            scenario.onActivity { activity ->
                val sp = PreferenceManager.getDefaultSharedPreferences(activity)
                sp.getString("IABUSPrivacy_String", null).assertEquals("1YNN")
            }
        }
    }

    @Test
    fun GIVEN_a_ccpa_campaign_CHECK_the_different_status() = runBlocking<Unit> {

        val spClient = SpClientMock()

        loadKoinModules(
            mockModule(
                spConfig = spConfCcpa,
                gdprPmId = "1",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        wr(backup = { clickOnRefreshBtnActivity() })  { tapAcceptOnWebView() }

        // check consentedAll
        wr { clickOnCcpaReviewConsent() }
        wr(backup = { clickOnCcpaReviewConsent() }) { checkAllCcpaConsentsOn() }
        wr { spClient.consentList.last().ccpa!!.consent.status.assertEquals(CcpaStatus.consentedAll) }

        // check consentedAll
        wr { clickOnCcpaReviewConsent() }
        wr(backup = { clickOnCcpaReviewConsent() }) { tapRejectAllWebView() }
        wr { spClient.consentList.last().ccpa!!.consent.status.assertEquals(CcpaStatus.rejectedAll) }

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

        wr(backup = { clickOnRefreshBtnActivity() })  { tapRejectOnWebView() }
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

        wr(backup = { clickOnRefreshBtnActivity() }) { tapAcceptOnWebView() }
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
    fun GIVEN_a_campaignList_ACCEPT_all_legislation_and_verify_that_the_popup_apper_1_time() = runBlocking<Unit> {

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

        wr(backup = { clickOnRefreshBtnActivity() }) { tapAcceptOnWebView() }
        wr { tapAcceptCcpaOnWebView() }
        clickOnRefreshBtnActivity()
        clickOnRefreshBtnActivity()
        clickOnRefreshBtnActivity()

        verify(exactly = 0) { spClient.onError(any()) }
        wr { verify(exactly = 4) { spClient.onSpFinished(any()) } }
        wr { verify(exactly = 5) { spClient.onConsentReady(any()) } }
        wr { verify(atLeast = 2) { spClient.onUIReady(any()) } }
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

        wr(backup = { clickOnRefreshBtnActivity() })  { tapOptionWebView() }
        wr { tapAcceptAllOnWebView() }
        wr { tapOptionWebView() }
        wr { tapAcceptAllOnWebView() }

        verify(exactly = 0) { spClient.onError(any()) }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
        wr { verify(exactly = 2) { spClient.onConsentReady(any()) } }
        wr { verify(atLeast = 4) { spClient.onUIReady(any()) } }
        wr { verify(exactly = 4) { spClient.onAction(any(), any()) } }
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
    fun GIVEN_a_camapignList_ACCEPT_all_legislation() = runBlocking<Unit> {

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

        wr(backup = { clickOnRefreshBtnActivity() }) { tapAcceptAllOnWebView() }
        wr { tapAcceptAllOnWebView() }

        verify(exactly = 0) { spClient.onError(any()) }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
        wr { verify(exactly = 2) { spClient.onConsentReady(any()) } }
        wr { verify(atLeast = 2) { spClient.onUIReady(any()) } }
        wr { verify(exactly = 2) { spClient.onAction(any(), any()) } }
        verify(exactly = 1) { spClient.onUIFinished(any()) }

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

        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr,
                gdprPmId = "488393",
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        wr(backup = { clickOnRefreshBtnActivity() })  { tapAcceptOnWebView() }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { tapAcceptAllOnWebView() }

        verify(exactly = 0) { spClient.onError(any()) }

        verify {
            spClient.run {
                onUIReady(any())
                onUIFinished(any())
                onAction(any(), any())
                onConsentReady(any())
                onSpFinished(any())
            }
        }
    }

    @Test
    fun GIVEN_a_gdpr_consent_ACCEPT_ALL() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr,
                gdprPmId = "488393",
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        wr(backup = { clickOnRefreshBtnActivity() })  { tapRejectOnWebView() }
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

        wr(backup = { clickOnRefreshBtnActivity() })  { tapRejectOnWebView() }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { tapNetworkOnWebView() }
        wr { checkDeepLinkDisplayed() }
    }

    @Test
    fun SAVE_AND_EXIT_action() = runBlocking<Unit> {

        loadKoinModules(mockModule(spConfig = spConfGdpr, gdprPmId = "488393"))

        scenario = launchActivity()

        wr(backup = { clickOnRefreshBtnActivity() })  { tapAcceptOnWebView() }
        wr {
            scenario.onActivity { activity ->
                PreferenceManager.getDefaultSharedPreferences(activity).contains("sp.gdpr.consentUUID").assertTrue()
            }
        }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { tapToDisableAllConsent() }
        wr { tapSaveAndExitWebView() }
        delay(300)
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { checkAllConsentsOff() }
    }

    @Test
    fun customConsentAction() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr,
                gdprPmId = "488393",
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        wr(backup = { clickOnRefreshBtnActivity() })  { tapRejectOnWebView() }
        wr { clickOnCustomConsent() }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { checkCustomCategoriesData() }
        wr { tapSiteVendorsWebView() }
        wr { checkCustomVendorDataList() }

        verify(exactly = 0) { spClient.onError(any()) }
        verify {
            spClient.run {
                onConsentReady(withArg {
                    it  .gdpr?.consent?.grants!!["5e7ced57b8e05c485246cce0"]!!.purposeGrants.values.first().assertTrue()
                })
            }
        }

    }

    @Test
    fun deleteCustomConsentAction() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr,
                gdprPmId = "488393",
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        wr(backup = { clickOnRefreshBtnActivity() })  { tapRejectOnWebView() }

        wr { clickOnCustomConsent() } // set a random custom consent
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { checkCustomCategoriesData() }
        wr { tapCancelOnWebView() }

        wr { clickOnDeleteCustomConsent() } // delete the previous custom consent
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { checkDeletedCustomCategoriesData() }
        wr { tapSiteVendorsWebView() }
        wr { checkDeletedCustomVendorDataList() }
        verify(exactly = 0) { spClient.onError(any()) }
        verify {
            spClient.run {
                onConsentReady(withArg {
                    it.gdpr?.consent?.grants!!.flatMap { i -> i.value.purposeGrants.values }.all { a -> a == false }.assertTrue()
                })
            }
        }
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

        wr(backup = { clickOnRefreshBtnActivity() })  { tapOptionWebView() }
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

        wr(backup = { clickOnRefreshBtnActivity() })  { tapCancelOnWebView() }

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
        wr(backup = { clickOnRefreshBtnActivity() })  { tapPartnersOnWebView() }
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

        wr(backup = { clickOnRefreshBtnActivity() })  { tapAcceptOnWebView() }

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

    @Test
    fun test_GracefulDegradation_gdpr_consent_present() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr.copy(messageTimeout = 3),
                gdprPmId = "488393",
                spClientObserver = listOf(spClient),
                pStoreStateGdpr = true
            )
        )

        scenario = launchActivity()


        wr { verify(exactly = 0) { spClient.onError(any()) } }
        wr { verify(exactly = 1) { spClient.onConsentReady(any()) } }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }

    }

    @Test
    fun test_GracefulDegradation_gdpr_and_ccpa_consent_present() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr.copy(messageTimeout = 3),
                gdprPmId = "488393",
                spClientObserver = listOf(spClient),
                pStoreStateGdpr = true,
                pStoreStateCcpa = true
            )
        )

        scenario = launchActivity()


        wr { verify(exactly = 0) { spClient.onError(any()) } }
        wr { verify(exactly = 1) { spClient.onConsentReady(any()) } }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }

    }

    @Test
    fun test_GracefulDegradation_ccpa_consent_present() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr.copy(messageTimeout = 3),
                gdprPmId = "488393",
                spClientObserver = listOf(spClient),
                pStoreStateCcpa = true
            )
        )

        scenario = launchActivity()


        wr { verify(exactly = 0) { spClient.onError(any()) } }
        wr { verify(exactly = 1) { spClient.onConsentReady(any()) } }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }

    }

    @Test
    fun test_GracefulDegradation_consent_absent() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr.copy(messageTimeout = 3),
                gdprPmId = "488393",
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()


        wr { verify(exactly = 1) { spClient.onError(any()) } }
        wr { verify(exactly = 0) { spClient.onConsentReady(any()) } }
        wr { verify(exactly = 0) { spClient.onSpFinished(any()) } }

    }

    @Test
    fun GracefulDegradation_GIVEN_a_backend_error_EXECUTE_the_onError() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr.copy(propertyName = "invalid.property"),
                gdprPmId = "488393",
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()


        wr { verify(exactly = 1) { spClient.onError(any()) } }
        wr { verify(exactly = 0) { spClient.onConsentReady(any()) } }
        wr { verify(exactly = 0) { spClient.onSpFinished(any()) } }

    }

    @Test
    fun GracefulDegradation_GIVEN_a__backend_error_and_a_saved_consent_EXECUTE_the_onConsentReady() =
        runBlocking<Unit> {

            val spClient = mockk<SpClient>(relaxed = true)

            loadKoinModules(
                mockModule(
                    spConfig = spConfGdpr.copy(propertyName = "invalid.property"),
                    gdprPmId = "488393",
                    spClientObserver = listOf(spClient),
                    pStoreStateCcpa = true
                )
            )

            scenario = launchActivity()


            wr { verify(exactly = 0) { spClient.onError(any()) } }
            wr { verify(exactly = 1) { spClient.onConsentReady(any()) } }
            wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }

        }

    @Test
    fun GIVEN_an_old_CCPA_GDPR_v6LocalState_VERIFY_that_the_migration_is_performed() = runBlocking<Unit> {

        val v6LocalState = JSONObject(TestData.storedConsentGdprCcap)

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

        scenario.onActivity { activity ->
            /**
             * Store an old v6 localState
             */
            val sp = PreferenceManager.getDefaultSharedPreferences(activity)
            val spEditor = sp.edit()
            v6LocalState.keys().forEach {
                check { v6LocalState.getString(it) }?.let { v -> spEditor.putString(it, v) }
                check { v6LocalState.getBoolean(it) }?.let { v -> spEditor.putBoolean(it, v) }
                check { v6LocalState.getInt(it) }?.let { v -> spEditor.putInt(it, v) }
            }
            spEditor.apply()
            // verify that before the migration the local state is present
            sp.contains("sp.key.local.state").assertTrue()
        }

        wr { verify(exactly = 1) { spClient.onConsentReady(any()) } }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
        wr { verify(exactly = 0) { spClient.onUIReady(any()) } }
        wr { verify(exactly = 0) { spClient.onUIFinished(any()) } }
        wr { verify(exactly = 0) { spClient.onAction(any(), any()) } }

        wr {
            scenario.onActivity { activity ->
                val sp = PreferenceManager.getDefaultSharedPreferences(activity)
                // verify that after the migration the local state is cancelled
                sp.contains("sp.key.local.state").assertFalse()
                // testr test
            }
        }
    }

    @Test
    fun GIVEN_an_old_GDPR_v6LocalState_VERIFY_that_the_migration_is_performed() = runBlocking<Unit> {

        val v6LocalState = JSONObject(TestData.storedConsentGdpr)

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

        scenario.onActivity { activity ->
            /**
             * Store an old v6 localState
             */
            val sp = PreferenceManager.getDefaultSharedPreferences(activity)
            val spEditor = sp.edit()
            v6LocalState.keys().forEach {
                check { v6LocalState.getString(it) }?.let { v -> spEditor.putString(it, v) }
                check { v6LocalState.getBoolean(it) }?.let { v -> spEditor.putBoolean(it, v) }
                check { v6LocalState.getInt(it) }?.let { v -> spEditor.putInt(it, v) }
            }
            spEditor.apply()
            // verify that before the migration the local state is present
            sp.contains("sp.key.local.state").assertTrue()
        }

        wr { verify(exactly = 1) { spClient.onConsentReady(any()) } }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
        wr { verify(exactly = 0) { spClient.onUIReady(any()) } }
        wr { verify(exactly = 0) { spClient.onUIFinished(any()) } }
        wr { verify(exactly = 0) { spClient.onAction(any(), any()) } }

        wr {
            scenario.onActivity { activity ->
                val sp = PreferenceManager.getDefaultSharedPreferences(activity)
                sp.contains("sp.key.local.state").assertFalse()
            }
        }
    }

    @Test
    fun GIVEN_an_old_CCPAv6LocalState_VERIFY_that_the_migration_is_performed() = runBlocking<Unit> {

        val v6LocalState = JSONObject(TestData.storedConsentCcap)

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

        scenario.onActivity { activity ->
            /**
             * Store an old v6 localState
             */
            val sp = PreferenceManager.getDefaultSharedPreferences(activity)
            val spEditor = sp.edit()
            v6LocalState.keys().forEach {
                check { v6LocalState.getString(it) }?.let { v -> spEditor.putString(it, v) }
                check { v6LocalState.getBoolean(it) }?.let { v -> spEditor.putBoolean(it, v) }
                check { v6LocalState.getInt(it) }?.let { v -> spEditor.putInt(it, v) }
            }
            spEditor.apply()
            // verify that before the migration the local state is present
            sp.contains("sp.key.local.state").assertTrue()
        }

        wr { verify(exactly = 1) { spClient.onConsentReady(any()) } }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
        wr { verify(exactly = 0) { spClient.onUIReady(any()) } }
        wr { verify(exactly = 0) { spClient.onUIFinished(any()) } }
        wr { verify(exactly = 0) { spClient.onAction(any(), any()) } }

        wr {
            scenario.onActivity { activity ->
                val sp = PreferenceManager.getDefaultSharedPreferences(activity)
                // verify that after the migration the local state is cancelled
                sp.contains("sp.key.local.state").assertFalse()
            }
        }
    }

    private fun <E> check(block: () -> E): E? {
        return try {
            block.invoke()
        } catch (e: Exception) {
            null
        }
    }

}
package com.sourcepoint.app.v6

import android.preference.PreferenceManager
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.example.uitestutil.*
import com.sourcepoint.app.v6.TestUseCase.Companion.checkAllCcpaConsentsOn
import com.sourcepoint.app.v6.TestUseCase.Companion.checkSomeConsentsOff
import com.sourcepoint.app.v6.TestUseCase.Companion.checkAllGdprConsentsOn
import com.sourcepoint.app.v6.TestUseCase.Companion.checkAllTogglesOFF
import com.sourcepoint.app.v6.TestUseCase.Companion.checkAllVendorsOff
import com.sourcepoint.app.v6.TestUseCase.Companion.checkCustomCategoriesData
import com.sourcepoint.app.v6.TestUseCase.Companion.checkCustomVendorDataList
import com.sourcepoint.app.v6.TestUseCase.Companion.checkDeepLinkDisplayed
import com.sourcepoint.app.v6.TestUseCase.Companion.checkDeletedCustomCategoriesData
import com.sourcepoint.app.v6.TestUseCase.Companion.checkEuconsent
import com.sourcepoint.app.v6.TestUseCase.Companion.checkFeaturesTab
import com.sourcepoint.app.v6.TestUseCase.Companion.checkGdprApplies
import com.sourcepoint.app.v6.TestUseCase.Companion.checkPurposesTab
import com.sourcepoint.app.v6.TestUseCase.Companion.checkWebViewDisplayedGDPRFirstLayerMessage
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnCcpaReviewConsent
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
import com.sourcepoint.app.v6.TestUseCase.Companion.tapToDisableSomeConsent
import com.sourcepoint.app.v6.TestUseCase.Companion.tapToEnableSomeOption
import com.sourcepoint.app.v6.TestUseCase.Companion.tapZustimmenAllOnWebView
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.creation.ConfigOption
import com.sourcepoint.cmplibrary.creation.config
import com.sourcepoint.cmplibrary.creation.to
import com.sourcepoint.cmplibrary.data.network.model.optimized.GCMStatus
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.exposed.CcpaStatus
import com.sourcepoint.cmplibrary.model.exposed.SpCampaign
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonPrimitive
import org.json.JSONObject
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import java.util.UUID

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityKotlinTest {

    lateinit var scenario: ActivityScenario<MainActivityKotlin>

    private val device: UiDevice by lazy { UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()) }
    private val appContext by lazy { InstrumentationRegistry.getInstrumentation().targetContext }

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

    private val spConfUsnat = config {
        accountId = 22
        propertyId = 16893
        propertyName = "mobile.multicampaign.demo"
        messLanguage = MessageLanguage.ENGLISH
        +(CampaignType.USNAT to setOf(ConfigOption.SUPPORT_LEGACY_USPSTRING))
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
        +SpCampaign(campaignType = CampaignType.GDPR, groupPmId = "613056")
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

    @Test
    fun GIVEN_a_USNAT_campaign_SHOW_message_and_ACCEPT_ALL() = runBlocking<Unit> {
        val spClient = mockk<SpClient>(relaxed = true)
        loadKoinModules(
            mockModule(
                spConfig = spConfUsnat,
                usnatPmId = "509688",
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        wr(backup = { clickOnRefreshBtnActivity() }) { tapAcceptOnWebView() }

        verify(exactly = 0) { spClient.onError(any()) }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
        verify { spClient.onAction(any(), withArg { it.pubData["pb_key"].assertEquals("pb_value") }) }
        wr { verify(exactly = 1) { spClient.onSpFinished( withArg {
            it.usNat!!.consent.run {
                (gppData["IABUSPrivacy_String"] as JsonPrimitive).content.assertEquals("1YYN")
                applies.assertTrue()
                statuses.consentedToAll!!.assertTrue()
                uuid.assertNotNull()
            }
        }) } }
    }

    @Test
    fun GIVEN_a_gdpr_campaign_SHOW_message_and_ACCEPT_ALL() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)
        val grantsTester = listOf(
            "5ff4d000a228633ac048be41",
            "5f1b2fbeb8e05c306f2a1eb9",
            "5e7ced57b8e05c485246cce0",
            "5e7e1298b8e05c4854221be9",
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

        wr(backup = { clickOnRefreshBtnActivity() }) { tapAcceptOnWebView() }

        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { checkAllGdprConsentsOn() }

        verify(exactly = 0) { spClient.onError(any()) }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
        verify { spClient.onAction(any(), withArg { it.pubData["pb_key"].assertEquals("pb_value") }) }

        verify {
            spClient.run {
                onUIReady(any())
                onAction(any(), withArg { it.pubData["pb_key"].assertEquals("pb_value") })
                onUIFinished(any())
                onAction(any(), any())
                onConsentReady(withArg {
                    it.gdpr!!.consent.grants.map { k -> k.key }.sorted().assertEquals(grantsTester)
                    it.gdpr!!.consent.uuid.assertNotNull()
                })
                onSpFinished(withArg {
                    it.gdpr!!.consent.applies.assertTrue()
                    it.gdpr!!.consent.uuid.assertNotNull()
                    it.gdpr!!.consent.consentStatus!!.consentedAll.assertNotNull()
                    it.gdpr!!.consent.googleConsentMode!!.adStorage.assertEquals(GCMStatus.GRANTED)
                    it.gdpr!!.consent.googleConsentMode!!.adUserData.assertEquals(GCMStatus.GRANTED)
                    it.gdpr!!.consent.googleConsentMode!!.adPersonalization.assertEquals(GCMStatus.GRANTED)
                    it.gdpr!!.consent.googleConsentMode!!.analyticsStorage.assertEquals(GCMStatus.GRANTED)
                })
            }
        }

        scenario.onActivity { activity ->
            PreferenceManager.getDefaultSharedPreferences(activity).run {
                getInt("IABTCF_EnableAdvertiserConsentMode", -1).assertEquals(1)

                getInt("IABTCF_PurposeOneTreatment", -1).assertNotEquals(-1)
                getInt("IABTCF_CmpSdkVersion", -1).assertNotEquals(-1)
                getInt("IABTCF_CmpSdkID", -1).assertNotEquals(-1)
                getInt("IABTCF_PolicyVersion", -1).assertNotEquals(-1)
                getInt("IABTCF_UseNonStandardTexts", -1).assertNotEquals(-1)
                getInt("IABTCF_gdprApplies", -1).assertNotEquals(-1)

                getString("IABTCF_TCString", null).assertNotNull()
                getString("IABTCF_PublisherLegitimateInterests", null).assertEquals("0000000000")
                getString("IABTCF_AddtlConsent", null).assertEquals("1~899")
                getString("IABTCF_PurposeConsents", null).assertNotNull()
                getString("IABTCF_VendorLegitimateInterests", null).assertNotNull()
                getString("IABTCF_TCString", null).assertNotNull()
                getString("IABTCF_PublisherRestrictions10", null).assertNotNull()
                getString("IABTCF_SpecialFeaturesOptIns", null).assertNotNull()
                getString("IABTCF_PublisherCC", null).assertNotNull()
                getString("IABTCF_VendorConsents", null).assertNotNull()
                getString("IABTCF_PublisherCustomPurposesLegitimateInterests", null).assertNotNull()
                getString("IABTCF_PurposeLegitimateInterests", null).assertNotNull()
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

        wr(backup = { clickOnRefreshBtnActivity() }) { tapAcceptOnWebView() }
        wr {
            verify {
                spClient.onSpFinished(withArg {
                    val euconsent = it.gdpr!!.consent.euconsent.assertNotNull()
                    val gdprApplies = it.gdpr!!.consent.applies.toString().assertNotNull()
                    clickOnConsentActivity()
                    checkEuconsent(euconsent)
                    checkGdprApplies(gdprApplies)
                    it.gdpr!!.consent.uuid.assertNotNull()
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

        wr(backup = { clickOnRefreshBtnActivity() }) { tapAcceptOnWebView() }
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
                onSpFinished(withArg {
                    it.ccpa!!.consent.applies.assertTrue()
                    it.ccpa!!.consent.uuid.assertNotNull()
                })
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

        wr(backup = { clickOnRefreshBtnActivity() }) { tapAcceptOnWebView() }

        // check consentedAll
        wr { clickOnCcpaReviewConsent() }
        wr(backup = { clickOnCcpaReviewConsent() }) { checkAllCcpaConsentsOn() }
        wr { spClient.consentList.last().ccpa!!.consent.status.assertEquals(CcpaStatus.consentedAll) }
        wr { spClient.consentList.last().ccpa!!.consent.applies.assertTrue() }

        // check consentedAll
        wr { clickOnCcpaReviewConsent() }
        wr(backup = { clickOnCcpaReviewConsent() }) { tapRejectAllWebView() }
        wr { spClient.consentList.last().ccpa!!.consent.status.assertEquals(CcpaStatus.rejectedAll) }
        wr { spClient.consentList.last().ccpa!!.consent.applies.assertTrue() }

        wr {
            scenario.onActivity { activity ->
                val sp = PreferenceManager.getDefaultSharedPreferences(activity)
                sp.getString("IABUSPrivacy_String", null).assertEquals("1YYN")
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

        wr(backup = { clickOnRefreshBtnActivity() }) { tapRejectOnWebView() }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { checkAllTogglesOFF() }

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
                    it.gdpr?.consent?.uuid.assertNotNull()
                })
                onSpFinished(withArg {
                    it.gdpr!!.consent.applies.assertTrue()
                    it.gdpr!!.consent.consentStatus!!.consentedAll.assertNotNull()
                    it.gdpr!!.consent.uuid.assertNotNull()
                    it.gdpr!!.consent.googleConsentMode!!.adStorage.assertEquals(GCMStatus.DENIED)
                    it.gdpr!!.consent.googleConsentMode!!.adUserData.assertEquals(GCMStatus.DENIED)
                    it.gdpr!!.consent.googleConsentMode!!.adPersonalization.assertEquals(GCMStatus.DENIED)
                    it.gdpr!!.consent.googleConsentMode!!.analyticsStorage.assertEquals(GCMStatus.DENIED)
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
        wr {
            verify(exactly = 1) {
                spClient.onSpFinished(withArg {
                    it.gdpr!!.consent.consentStatus!!.consentedAll.assertNotNull()
                })
            }
        }
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
                onSpFinished(withArg {
                    it.ccpa!!.consent.applies.assertTrue()
                    it.gdpr!!.consent.applies.assertTrue()
                    it.gdpr!!.consent.consentStatus!!.consentedAll.assertNotNull()
                    it.ccpa!!.consent.uuid.assertNotNull()
                    it.gdpr!!.consent.uuid.assertNotNull()
                })
            }
        }
    }

    @Test
    fun WITHOUT_a_stored_consent_GIVEN_no_internet_connection_exception_VERIFY_the_called_callbacks() =
        runBlocking<Unit> {

            val spClient = mockk<SpClient>(relaxed = true)

            loadKoinModules(
                mockModule(
                    spConfig = spConf,
                    gdprPmId = "488393",
                    ccpaPmId = "509688",
                    spClientObserver = listOf(spClient),
                    diagnostic = mutableListOf(Pair("connectionTest", false))
                )
            )

            scenario = launchActivity()

            wr { verify(exactly = 1) { spClient.onError(any()) } }
            wr { verify(exactly = 0) { spClient.onConsentReady(any()) } }
            wr { verify(exactly = 0) { spClient.onUIReady(any()) } }
            wr { verify(exactly = 0) { spClient.onUIFinished(any()) } }
            // TODO We have to change the behaviour of the graceful degradation, onSpFinished must be always called
            wr { verify(exactly = 0) { spClient.onSpFinished(any()) } }
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
        wr { verify(atLeast = 3) { spClient.onSpFinished(any()) } }
        wr { verify(atLeast = 4) { spClient.onConsentReady(any()) } }
        wr { verify(atLeast = 2) { spClient.onUIReady(any()) } }
        verify {
            spClient.run {
                onSpFinished(withArg {
                    it.ccpa!!.consent.applies.assertTrue()
                    it.gdpr!!.consent.applies.assertTrue()
                    it.gdpr!!.consent.consentStatus!!.consentedAll.assertNotNull()
                    it.ccpa!!.consent.uuid.assertNotNull()
                    it.gdpr!!.consent.uuid.assertNotNull()
                })
            }
        }
    }

    @Test
    fun GIVEN_a_campaign_without_pupup_to_show_VERIFY_that_the_tddata_gets_saved() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfGdprNoMessage,
                gdprPmId = "111111",
                ccpaPmId = "222222",
                spClientObserver = listOf(spClient),
            )
        )

        scenario = launchActivity()

        verify(exactly = 0) { spClient.onError(any()) }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
        wr { verify(exactly = 1) { spClient.onConsentReady(any()) } }
        wr { verify(exactly = 0) { spClient.onUIReady(any()) } }
        verify {
            spClient.run {
                onSpFinished(withArg {
                    it.gdpr!!.consent.applies.assertTrue()
                    it.gdpr!!.consent.consentStatus!!.consentedAll.assertNotNull()
                })
            }
        }

        scenario.onActivity { activity ->
            PreferenceManager.getDefaultSharedPreferences(activity).run {
                getString("IABTCF_AddtlConsent", null).assertEquals("1~")
                getString("IABTCF_PublisherLegitimateInterests", null).assertEquals("0000000000")
                getString("IABTCF_TCString", null).assertNotNull()
                getInt("IABTCF_CmpSdkVersion", -1).assertNotEquals(-1)
                getInt("IABTCF_CmpSdkID", -1).assertNotEquals(-1)
                getInt("IABTCF_PolicyVersion", -1).assertNotEquals(-1)
                // TODO
//                getInt("IABTCF_UseNonStandardStacks", -1).assertNotEquals(-1)
                getInt("IABTCF_gdprApplies", -1).assertNotEquals(-1)
                getInt("IABTCF_PurposeOneTreatment", -1).assertNotEquals(-1)
                getString("IABTCF_PurposeConsents", null).assertNotNull()
                getString("IABTCF_TCString", null).assertNotNull()
                getString("IABTCF_SpecialFeaturesOptIns", null).assertNotNull()
                getString("IABTCF_PublisherCC", null).assertNotNull()
                getString("IABTCF_PublisherCustomPurposesLegitimateInterests", null).assertNotNull()
                getString("IABTCF_PurposeLegitimateInterests", null).assertNotNull()
                getString("IABTCF_PublisherCustomPurposesConsents", null).assertNotNull()
                getString("IABTCF_PublisherConsent", null).assertNotNull()
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

        wr(backup = { clickOnRefreshBtnActivity() }) { tapOptionWebView() }
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
                onUIReady(any())
                onAction(any(), any())
                onUIReady(any())
                onConsentReady(any())
                onSpFinished(withArg {
                    it.ccpa!!.consent.applies.assertTrue()
                    it.gdpr!!.consent.applies.assertTrue()
                    it.gdpr!!.consent.consentStatus!!.consentedAll.assertNotNull()
                    it.ccpa!!.consent.uuid.assertNotNull()
                    it.gdpr!!.consent.uuid.assertNotNull()
                })
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
                onSpFinished(withArg {
                    it.ccpa!!.consent.applies.assertTrue()
                    it.gdpr!!.consent.applies.assertTrue()
                    it.gdpr!!.consent.consentStatus!!.consentedAll.assertNotNull()
                    it.ccpa!!.consent.uuid.assertNotNull()
                    it.gdpr!!.consent.uuid.assertNotNull()
                })
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

        wr(backup = { clickOnRefreshBtnActivity() }) { tapAcceptOnWebView() }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { tapAcceptAllOnWebView() }

        verify(exactly = 0) { spClient.onError(any()) }

        verify {
            spClient.run {
                onUIReady(any())
                onSpFinished(withArg {
                    it.gdpr!!.consent.applies.assertTrue()
                    it.gdpr!!.consent.consentStatus!!.consentedAll.assertNotNull()
                    it.gdpr!!.consent.uuid.assertNotNull()
                })
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

        wr(backup = { clickOnRefreshBtnActivity() }) { tapRejectOnWebView() }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { tapAcceptAllOnWebView() }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { checkAllGdprConsentsOn() }

        verify(exactly = 0) { spClient.onError(any()) }
        wr { verify(exactly = 2) { spClient.onSpFinished(any()) } }

        verify {
            spClient.run {
                onUIReady(any())
                onUIFinished(any())
                onAction(any(), any())
                onConsentReady(withArg {
                    it.gdpr?.consent?.grants?.values?.forEach { el -> el.granted.assertTrue() }
                    it.gdpr?.consent?.uuid.assertNotNull()
                })
            }
        }
    }

    @Test
    fun GIVEN_a_deeplink_OPEN_an_activity() = runBlocking<Unit> {

        loadKoinModules(mockModule(spConfig = spConfGdpr, gdprPmId = "488393"))

        scenario = launchActivity()

        wr(backup = { clickOnRefreshBtnActivity() }) { tapRejectOnWebView() }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { tapNetworkOnWebView() }
        wr { checkDeepLinkDisplayed() }
    }

    @Test
    fun SAVE_AND_EXIT_action() = runBlocking<Unit> {

        loadKoinModules(mockModule(spConfig = spConfGdpr, gdprPmId = "488393"))

        scenario = launchActivity()

        wr(backup = { clickOnRefreshBtnActivity() }) { tapAcceptOnWebView() }
//        wr {
//            scenario.onActivity { activity ->
//                PreferenceManager.getDefaultSharedPreferences(activity).contains("sp.gdpr.consentUUID").assertTrue()
//            }
//        }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { tapToDisableSomeConsent() }
        wr { tapSaveAndExitWebView() }
        delay(300)
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { checkSomeConsentsOff() }

    }


    @Test
    fun Appplies_usng_SAVE_AND_EXIT_action() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr,
                gdprPmId = "488393",
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        wr(backup = { clickOnRefreshBtnActivity() }) { tapOptionWebView() }
        wr { tapToEnableSomeOption() }
        wr { tapSaveAndExitWebView() }
        wr {
            verify{
                spClient.onConsentReady(withArg {
                    it.gdpr!!.consent.applies.assertTrue()
                    it.gdpr!!.consent.consentStatus!!.consentedAll.assertNotNull()
                    it.gdpr!!.consent.uuid.assertNotNull()
                })
            }
        }
        wr {
            verify(exactly = 1) {
                spClient.onSpFinished(withArg {
                    it.gdpr!!.consent.applies.assertTrue()
                    it.gdpr!!.consent.consentStatus!!.consentedAll.assertNotNull()
                    it.gdpr!!.consent.uuid.assertNotNull()
                })
            }
        }
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

        wr(backup = { clickOnRefreshBtnActivity() }) { tapRejectOnWebView() }
        wr { verify(exactly = 1) { spClient.onConsentReady(any()) } }
        wr { clickOnCustomConsent() }
        wr { verify(exactly = 2) { spClient.onConsentReady(any()) } }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { checkCustomCategoriesData() }
        wr { tapSiteVendorsWebView() }
        wr { checkCustomVendorDataList() }

        verify(exactly = 0) { spClient.onError(any()) }
        verify {
            spClient.run {
                onConsentReady(withArg {
                    it.gdpr?.consent?.grants!!["5e7ced57b8e05c485246cce0"]!!.purposeGrants.values.first().assertTrue()
                    it.gdpr?.consent?.uuid.assertNotNull()
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

        wr(backup = { clickOnRefreshBtnActivity() }) { tapAcceptAllOnWebView() }
        wr { verify(exactly = 1) { spClient.onConsentReady(any()) } }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { checkAllGdprConsentsOn() }
        wr { tapCancelOnWebView() }
        wr { clickOnDeleteCustomConsent() } // delete the previous custom consent
        wr { verify(exactly = 2) { spClient.onConsentReady(any()) } }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { checkDeletedCustomCategoriesData() }
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

        wr(backup = { clickOnRefreshBtnActivity() }) { tapOptionWebView() }
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

        wr(backup = { clickOnRefreshBtnActivity() }) { tapCancelOnWebView() }

        verify(exactly = 0) { spClient.onConsentReady(any()) }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
    }

    @Test
    fun GIVEN_a_groupId_VERIFY_that_the_right_pm_is_displayed() = runBlocking<Unit> {
        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfGdprGroupId,
                gdprPmId = "613058", // it is the wrong pmId because the right pmId should be selected automatically
                ccpaPmId = "-",
                useGdprGroupPmIfAvailable = true,
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        wr(backup = { clickOnRefreshBtnActivity() }) { tapZustimmenAllOnWebView() }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { checkTextInParagraph("Privacy Notice Prop 1") }

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
        wr(backup = { clickOnRefreshBtnActivity() }) { tapPartnersOnWebView() }
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
    fun GIVEN_a_ccpa_if_applies_FALSE_VERIFY_USPSTRING() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        val v7CCPALocalState = JSONObject(TestData.storedConsentCCPA_V7)

        loadKoinModules(
            mockModule(
                spConfig = spConfCcpa,
                gdprPmId = "123",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient),
                diagnostic = listOf("metadata_resp_applies_false" to false) + v7CCPALocalState.toList()
            )
        )

        scenario = launchActivity()

        wr {
            verify {
                spClient.run {
                    onSpFinished(withArg {
                        it.ccpa!!.consent.applies.assertFalse()
                        it.ccpa!!.consent.uuid.assertNotNull()
                    })
                }
            }
        }
    }

    @Test
    fun GIVEN_a_ccpa_if_applies_TRUE_VERIFY_USPSTRING() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        val v7CCPALocalState = JSONObject(TestData.storedConsentCCPA_applies_false_V7)

        loadKoinModules(
            mockModule(
                spConfig = spConfCcpa,
                gdprPmId = "123",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient),
                diagnostic = listOf("metadata_resp_applies_true" to true) + v7CCPALocalState.toList()
            )
        )

        scenario = launchActivity()

        wr {
            scenario.onActivity { activity ->
                val sp = PreferenceManager.getDefaultSharedPreferences(activity)
                sp.getString("IABUSPrivacy_String", null).assertNotEquals("1---")
            }
        }
        wr {
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

    @Test
    fun GIVEN_a_ccpa_if_rejectedAll_from_PM_VERIFY_USPSTRING() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        val v7CCPALocalState = JSONObject(TestData.storedConsentCCPA_V7)

        loadKoinModules(
            mockModule(
                spConfig = spConfCcpa,
                gdprPmId = "123",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient),
                diagnostic = v7CCPALocalState.toList()
            )
        )

        scenario = launchActivity()

        wr { clickOnCcpaReviewConsent() }
        wr(backup = { clickOnCcpaReviewConsent() }) { tapRejectAllWebView() }

        wr {
            scenario.onActivity { activity ->
                val sp = PreferenceManager.getDefaultSharedPreferences(activity)
                sp.getString("IABUSPrivacy_String", null).assertEquals("1YYN")
            }
        }

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
    fun GIVEN_the_user_has_consent_and_the_auth_id_changes_THEN_should_flush_data() = runBlocking<Unit> {

        val storedConsent = JSONObject(TestData.storedConsentWithAuthIdAndPropertyIdV741)
        val spClient = mockk<SpClient>(relaxed = true)
        val newAuthId = UUID.randomUUID().toString()

        loadKoinModules(
            mockModule(
                spConfig = spConf,
                gdprPmId = "488393",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient),
                diagnostic = storedConsent.toList(),
                pAuthId = newAuthId
            )
        )

        scenario = launchActivity()

        wr(backup = { clickOnRefreshBtnActivity() }) { tapAcceptAllOnWebView() }
        wr { tapAcceptAllOnWebView() }

        wr { verify(exactly = 0) { spClient.onError(any()) } }
        wr { verify(exactly = 2) { spClient.onUIReady(any()) } }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }

        scenario.onActivity { activity ->
            PreferenceManager.getDefaultSharedPreferences(activity).run {
                getString("sp.gdpr.authId", null).assertEquals(newAuthId)
            }
        }
    }

    @Test
    fun GIVEN_the_user_has_consent_and_the_property_id_changes_THEN_should_flush_data() = runBlocking<Unit> {

        val storedConsent = JSONObject(TestData.storedConsentWithAuthIdAndPropertyIdV741)
        val spClient = mockk<SpClient>(relaxed = true)
        val storedPropertyId = 31226
        val newPropertyId = 16893

        loadKoinModules(
            mockModule(
                spConfig = spConf,
                gdprPmId = "488393",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient),
                diagnostic = storedConsent.toList() + listOf(
                    Pair("sp.key.config.propertyId", storedPropertyId)
                ),
            )
        )

        scenario = launchActivity()

        wr(backup = { clickOnRefreshBtnActivity() }) { tapAcceptAllOnWebView() }
        wr { tapAcceptAllOnWebView() }

        wr { verify(exactly = 0) { spClient.onError(any()) } }
        wr { verify(exactly = 2) { spClient.onUIReady(any()) } }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }

        scenario.onActivity { activity ->
            PreferenceManager.getDefaultSharedPreferences(activity).run {
                getInt("sp.key.config.propertyId", 0).assertEquals(newPropertyId)
            }
        }
    }

    @Test
    fun GIVEN_a_no_internet_ex_during_AcceptAll_VERIFY_that_onConsenReady_onSpFinished_are_called() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr,
                gdprPmId = "488393",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient),
                diagnostic = listOf("gdpr_choice_exception" to true)
            )
        )

        scenario = launchActivity()

        wr(backup = { clickOnRefreshBtnActivity() }) { tapAcceptOnWebView() }

        verify(exactly = 0) { spClient.onError(any()) }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
        verify { spClient.onAction(any(), withArg { it.pubData["pb_key"].assertEquals("pb_value") }) }

        verify {
            spClient.run {
                onUIReady(any())
                onAction(any(), withArg { it.pubData["pb_key"].assertEquals("pb_value") })
                onUIFinished(any())
                onAction(any(), any())
                onConsentReady(withArg {
                    it.gdpr!!.consent.applies.assertTrue()
                    it.gdpr!!.consent.uuid.assertNotNull()
                    it.gdpr!!.consent.consentStatus!!.consentedAll!!.assertFalse()
                    it.gdpr!!.consent.consentStatus!!.hasConsentData!!.assertFalse()
                })
                onSpFinished(withArg {
                    it.gdpr!!.consent.applies.assertTrue()
                    it.gdpr!!.consent.uuid.assertNotNull()
                    it.gdpr!!.consent.consentStatus!!.consentedAll!!.assertFalse()
                    it.gdpr!!.consent.consentStatus!!.hasConsentData!!.assertFalse()
                })
            }
        }

    }

    @Test
    fun GIVEN_a_no_internet_ex_during_RejectAll_VERIFY_that_onConsenReady_onSpFinished_are_called() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr,
                gdprPmId = "488393",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient),
                diagnostic = listOf("gdpr_choice_exception" to true)
            )
        )

        scenario = launchActivity()

        wr(backup = { clickOnRefreshBtnActivity() }) { tapRejectOnWebView() }

        verify(exactly = 0) { spClient.onError(any()) }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
        verify { spClient.onAction(any(), withArg { it.pubData["pb_key"].assertEquals("pb_value") }) }

        verify {
            spClient.run {
                onUIReady(any())
                onAction(any(), withArg { it.pubData["pb_key"].assertEquals("pb_value") })
                onUIFinished(any())
                onAction(any(), any())
                onConsentReady(withArg {
                    it.gdpr!!.consent.applies.assertTrue()
                    it.gdpr!!.consent.uuid.assertNotNull()
                    it.gdpr!!.consent.consentStatus!!.consentedAll!!.assertFalse()
                    it.gdpr!!.consent.consentStatus!!.hasConsentData!!.assertFalse()
                })
                onSpFinished(withArg {
                    it.gdpr!!.consent.applies.assertTrue()
                    it.gdpr!!.consent.uuid.assertNotNull()
                    it.gdpr!!.consent.consentStatus!!.consentedAll!!.assertFalse()
                    it.gdpr!!.consent.consentStatus!!.hasConsentData!!.assertFalse()
                })
            }
        }
    }

    @Test
    fun GIVEN_a_no_internet_ex_during_AcceptAll_with_multicampaign_VERIFY_that_onConsenReady_onSpFinished_are_called() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConf,
                gdprPmId = "488393",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient),
                diagnostic = listOf("gdpr_choice_exception" to true)
            )
        )

        scenario = launchActivity()

        wr(backup = { clickOnRefreshBtnActivity() }) { checkWebViewDisplayedGDPRFirstLayerMessage() }
        clickAirplaneMode(device, appContext)
        wr { tapRejectOnWebView() }
        clickAirplaneMode(device, appContext)

        verify(exactly = 0) { spClient.onError(any()) }

        verify {
            spClient.run {
                onUIReady(any())
                onAction(any(), withArg { it.pubData["pb_key"].assertEquals("pb_value") })
                onUIFinished(any())
                onAction(any(), any())
                onConsentReady(withArg {
                    it.gdpr!!.consent.applies.assertTrue()
                    it.gdpr!!.consent.uuid.assertNotNull()
                    it.gdpr!!.consent.consentStatus!!.consentedAll!!.assertFalse()
                    it.gdpr!!.consent.consentStatus!!.hasConsentData!!.assertFalse()

                    it.ccpa!!.consent.applies.assertTrue()
                    it.ccpa!!.consent.uuid.assertNotNull()
                    it.ccpa!!.consent.status.assertEquals(CcpaStatus.rejectedNone)
                })
                onSpFinished(withArg {
                    it.gdpr!!.consent.applies.assertTrue()
                    it.gdpr!!.consent.uuid.assertNotNull()
                    it.gdpr!!.consent.consentStatus!!.consentedAll!!.assertFalse()
                    it.gdpr!!.consent.consentStatus!!.hasConsentData!!.assertFalse()

                    it.ccpa!!.consent.applies.assertTrue()
                    it.ccpa!!.consent.uuid.assertNotNull()
                    it.ccpa!!.consent.status.assertEquals(CcpaStatus.rejectedNone)
                })
            }
        }
    }
}
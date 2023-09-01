@file:Suppress("DEPRECATION")

package com.sourcepoint.app.v6

import android.preference.PreferenceManager
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.core.app.launchActivity
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.uitestutil.*
import com.sourcepoint.app.v6.TestUseCase.Companion.checkAllCcpaConsentsOn
import com.sourcepoint.app.v6.TestUseCase.Companion.checkAllConsentsOff
import com.sourcepoint.app.v6.TestUseCase.Companion.checkAllConsentsOn
import com.sourcepoint.app.v6.TestUseCase.Companion.checkAllGdprConsentsOn
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
import com.sourcepoint.app.v6.TestUseCase.Companion.tapToDisableAllConsent
import com.sourcepoint.app.v6.TestUseCase.Companion.tapZustimmenAllOnWebView
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.creation.config
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.exposed.CcpaStatus
import com.sourcepoint.cmplibrary.model.exposed.SpCampaign
import com.sourcepoint.cmplibrary.util.clearAllData
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runners.model.Statement
import org.koin.core.context.loadKoinModules


class Retry(private val retryCount: Int, val onRetry: (() -> Unit)? = null) : TestRule {
    override fun apply(base: Statement, description: Description): Statement {
        return statement(base, description)
    }

    private fun statement(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                var caughtThrowable: Throwable? = null

                // implement retry logic here
                for (i in 0 until retryCount) {
                    onRetry?.invoke()
                    try {
                        base.evaluate()
                        return
                    } catch (t: Throwable) {
                        caughtThrowable = t
                        System.err.println(description.displayName + ": run " + (i + 1) + " failed")
                    }
                }
                System.err.println(description.displayName + ": giving up after " + retryCount + " failures")
                throw caughtThrowable!!
            }
        }
    }
}

@RunWith(AndroidJUnit4ClassRunner::class)
@LargeTest
class MainActivityKotlinTest {
    @JvmField
    @Rule
    var retry = Retry(3, onRetry = {
        cleanLocalStorage()
        setupMocks()
    })

    private lateinit var scenario: ActivityScenario<MainActivityKotlin>

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

    private val spConfNative = config {
        accountId = 22
        propertyId = 18958
        propertyName = "mobile.multicampaign.native.demo" // gdprPmId = 545258
        messLanguage = MessageLanguage.ENGLISH
        messageTimeout = 5000
        +(CampaignType.GDPR)
    }

    @MockK
    lateinit var spClient: SpClient

    private var sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())

    private fun storeStateFrom(oldState: String) {
        val oldStateJson = JSONObject(oldState)
        val spEditor = sharedPrefs.edit()
        oldStateJson.keys().forEach {
            check { oldStateJson.getString(it) }?.let { v -> spEditor.putString(it, v) }
            check { oldStateJson.getBoolean(it) }?.let { v -> spEditor.putBoolean(it, v) }
            check { oldStateJson.getInt(it) }?.let { v -> spEditor.putInt(it, v) }
        }
        spEditor.apply()
        // verify that before the migration the local state is present
        sharedPrefs.contains("sp.key.local.state").assertTrue()
    }

    private var appIdlingResource: IdlingResource? = null

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

    @Before
    fun cleanLocalStorage() {
        clearAllData(getApplicationContext())
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

    @Test
    fun GIVEN_a_gdpr_campaign_SHOW_message_and_ACCEPT_ALL():Unit = runBlocking {
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

        launchApp()

        tapAcceptOnWebView()
        clickOnGdprReviewConsent()
        checkAllConsentsOn()

        verify(exactly = 0) { spClient.onError(any()) }

        verify {
            spClient.run {
                onUIReady(any())
                onAction(any(), withArg { it.pubData["pb_key"].assertEquals("pb_value") })
                onUIFinished(any())
                onConsentReady(withArg { spConsents ->
                    spConsents.gdpr?.consent?.uuid.assertNotNull()
                    spConsents.gdpr?.consent?.grants?.map { k -> k.key }?.sorted().assertEquals(grantsTester)
                })
                onSpFinished(any())
            }
        }

        sharedPrefs.run {
            getString("IABTCF_AddtlConsent", null).assertEquals("1~899")
            getString("IABTCF_PublisherLegitimateInterests", null).assertEquals("0000000000")
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

    @Test
    fun GIVEN_a_gdpr_campaign_CHECK_the_consent_from_a_second_activity():Unit = runBlocking {
        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr,
                gdprPmId = "488393",
                spClientObserver = listOf(spClient)
            )
        )

        launchApp()

        tapAcceptOnWebView()

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
    fun GIVEN_a_ccpa_campaign_SHOW_message_and_ACCEPT_ALL():Unit = runBlocking {
        loadKoinModules(
            mockModule(
                spConfig = spConfCcpa,
                gdprPmId = "488393",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient)
            )
        )

        launchApp()

        tapAcceptOnWebView()
        clickOnCcpaReviewConsent()
        checkAllCcpaConsentsOn()

        wr { verify(exactly = 0) { spClient.onError(any()) } }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
        wr { verify { spClient.onAction(any(), withArg { it.pubData["pb_key"].assertEquals("pb_value") }) } }

        wr {
            verify {
                spClient.run {
                    onUIReady(any())
                    onConsentReady(any())
                }
            }
        }

        wr { sharedPrefs.getString("IABUSPrivacy_String", null).assertEquals("1YNN") }
    }

    @Test
    fun GIVEN_a_ccpa_campaign_CHECK_the_different_status():Unit = runBlocking {
        loadKoinModules(
            mockModule(
                spConfig = spConfCcpa,
                gdprPmId = "1",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient)
            )
        )

        launchApp()

        tapAcceptOnWebView()

        // check consentedAll
        clickOnCcpaReviewConsent()
        checkAllCcpaConsentsOn()
        wr { verify { spClient.onConsentReady(withArg { consents ->
            consents.ccpa?.consent?.status.assertEquals(CcpaStatus.consentedAll)
        })}}
        tapRejectAllWebView()
        wr { verify { spClient.onConsentReady(withArg { consents ->
            consents.ccpa?.consent?.status.assertEquals(CcpaStatus.rejectedAll)
        })}}
    }

    @Test
    fun GIVEN_a_GDPR_campaign_SHOW_message_and_REJECT_ALL():Unit = runBlocking {
        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr,
                gdprPmId = "488393",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient)
            )
        )

        launchApp()

        tapRejectOnWebView()
        clickOnGdprReviewConsent()
        checkAllConsentsOff()

        wr { verify(exactly = 0) { spClient.onError(any()) } }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
        wr { verify { spClient.onAction(any(), withArg { it.pubData["pb_key"].assertEquals("pb_value") }) } }

        wr {
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
    }

    @Test
    fun GIVEN_a_campaignList_ACCEPT_all_legislation():Unit = runBlocking {
        loadKoinModules(
            mockModule(
                spConfig = spConf,
                gdprPmId = "488393",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient),
            )
        )

        launchApp()

        tapAcceptOnWebView()
        tapAcceptCcpaOnWebView()

        wr { verify(exactly = 0) { spClient.onError(any()) } }
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
    }

    @Test
    fun GIVEN_a_campaignList_ACCEPT_all_legislation_and_verify_that_the_popup_appears_1_time():Unit = runBlocking {
        loadKoinModules(
            mockModule(
                spConfig = spConf,
                gdprPmId = "488393",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient),
            )
        )

        launchApp()

        tapAcceptOnWebView()
        tapAcceptCcpaOnWebView()
        clickOnRefreshBtnActivity()
        clickOnRefreshBtnActivity()
        clickOnRefreshBtnActivity()

        verify(exactly = 0) { spClient.onError(any()) }
        wr { verify(exactly = 4) { spClient.onSpFinished(any()) } }
        wr { verify(exactly = 5) { spClient.onConsentReady(any()) } }
        wr { verify(exactly = 2) { spClient.onUIReady(any()) } }
    }

    @Test
    fun GIVEN_a_campaign_without_message_to_show_VERIFY_that_the_tddata_gets_saved():Unit = runBlocking {
        loadKoinModules(
            mockModule(
                spConfig = spConfGdprNoMessage,
                gdprPmId = "111111",
                ccpaPmId = "222222",
                spClientObserver = listOf(spClient),
            )
        )

        launchApp()

        wr { verify(exactly = 0) { spClient.onError(any()) } }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
        wr { verify(exactly = 1) { spClient.onConsentReady(any()) } }
        wr { verify(exactly = 0) { spClient.onUIReady(any()) } }

        sharedPrefs.run {
            getString("IABTCF_AddtlConsent", null).assertEquals("1~")
            getString("IABTCF_PublisherLegitimateInterests", null).assertEquals("0000000000")
            getString("IABTCF_TCString", null).assertNotNull()
            getInt("IABTCF_CmpSdkVersion", -1).assertNotEquals(-1)
            getInt("IABTCF_CmpSdkID", -1).assertNotEquals(-1)
            getInt("IABTCF_PolicyVersion", -1).assertNotEquals(-1)
            getInt("IABTCF_UseNonStandardStacks", -1).assertNotEquals(-1)
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

    @Test
    fun GIVEN_a_camapignList_ACCEPT_all_legislation_from_option_button():Unit = runBlocking {
        loadKoinModules(
            mockModule(
                spConfig = spConf,
                gdprPmId = "488393",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient)
            )
        )

        launchApp()

        tapOptionWebView()
        tapAcceptAllOnWebView()
        tapOptionWebView()
        tapAcceptAllOnWebView()

        wr { verify(exactly = 0) { spClient.onError(any()) } }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
        wr { verify(exactly = 2) { spClient.onConsentReady(any()) } }
        wr { verify(atLeast = 4) { spClient.onUIReady(any()) } }
        wr { verify(exactly = 4) { spClient.onAction(any(), any()) } }
    }


    @Test
    fun GIVEN_a_deeplink_OPEN_an_activity():Unit = runBlocking {
        loadKoinModules(mockModule(spConfig = spConfGdpr, gdprPmId = "488393"))

        launchApp()

        tapRejectOnWebView()
        clickOnGdprReviewConsent()
        tapNetworkOnWebView()
        checkDeepLinkDisplayed()
    }

    @Test
    fun SAVE_AND_EXIT_action():Unit = runBlocking {
        loadKoinModules(mockModule(spConfig = spConfGdpr, gdprPmId = "488393"))

        launchApp()

        tapAcceptOnWebView()
        clickOnGdprReviewConsent()
        tapToDisableAllConsent()
        tapSaveAndExitWebView()
        clickOnGdprReviewConsent()
        checkAllConsentsOff()
    }

    @Test
    fun customConsentAction():Unit = runBlocking {
        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr,
                gdprPmId = "488393",
                spClientObserver = listOf(spClient)
            )
        )

        launchApp()

        tapRejectOnWebView()
        wr { verify(exactly = 1) { spClient.onConsentReady(any()) } }

        clickOnCustomConsent()
        wr { verify(exactly = 2) { spClient.onConsentReady(any()) } }

        clickOnGdprReviewConsent()
        checkCustomCategoriesData()
        tapSiteVendorsWebView()
        checkCustomVendorDataList()

        verify(exactly = 0) { spClient.onError(any()) }
        verify {
            spClient.run {
                onConsentReady(withArg {
                    it.gdpr?.consent?.grants!!["5e7ced57b8e05c485246cce0"]!!.purposeGrants.values.first().assertTrue()
                })
            }
        }
    }

    @Test
    fun deleteCustomConsentAction():Unit = runBlocking {
        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr,
                gdprPmId = "488393",
                spClientObserver = listOf(spClient)
            )
        )

        launchApp()

        tapAcceptAllOnWebView()
        clickOnGdprReviewConsent()
        checkAllGdprConsentsOn()
        tapCancelOnWebView()
        clickOnDeleteCustomConsent() // delete the previous custom consent
        wr { verify { spClient.onConsentReady(any()) } }
        clickOnGdprReviewConsent()
        checkDeletedCustomCategoriesData()
    }

    @Test
    fun GIVEN_a_camapignList_VERIFY_back_btn():Unit = runBlocking {
        loadKoinModules(
            mockModule(
                spConfig = spConf,
                gdprPmId = "488393",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient)
            )
        )

        launchApp()

        tapOptionWebView()
        tapCancelOnWebView()
        checkWebViewDisplayedGDPRFirstLayerMessage()
        tapAcceptAllOnWebView()
        tapOptionWebView()
        tapCancelOnWebView()
        tapAcceptAllOnWebView()

        wr { verify(atLeast = 4) { spClient.onAction(any(), any()) } }
    }

    @Test
    fun GIVEN_a_camapignList_PRESS_cancel_VERIFY_onConsentReady_NOT_called():Unit = runBlocking {
        loadKoinModules(
            mockModule(
                spConfig = spConfGdprGroupId,
                gdprPmId = "613057",
                ccpaPmId = "-",
                spClientObserver = listOf(spClient)
            )
        )

        launchApp()

        tapCancelOnWebView()

        wr { verify(exactly = 0) { spClient.onConsentReady(any()) } }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
    }

    @Test
    fun GIVEN_a_groupId_VERIFY_that_the_right_pm_is_displayed():Unit = runBlocking {
        loadKoinModules(
            mockModule(
                spConfig = spConfGdprGroupId,
                gdprPmId = "613058", // it is the wrong pmId because the right pmId should be selected automatically
                ccpaPmId = "-",
                useGdprGroupPmIfAvailable = true,
                spClientObserver = listOf(spClient)
            )
        )

        launchApp()

        tapZustimmenAllOnWebView()
        clickOnGdprReviewConsent()
        checkTextInParagraph("Privacy Notice Prop 1")
    }

    @Test
    fun TAPPING_on_aVENDORS_link_SHOW_the_PM_VENDORS_tab():Unit = runBlocking {
        loadKoinModules(
            mockModule(
                spConfig = spConfNative,
                gdprPmId = "545258",
                spClientObserver = listOf(spClient)
            )
        )

        launchApp()

        // Vendors
        tapPartnersOnWebView()
        checkAllVendorsOff()
        tapCancelOnWebView()

        // Features
        tapFeaturesOnWebView()
        checkFeaturesTab()
        tapCancelOnWebView()

        // Purposes
        tapPurposesOnWebView()
        checkPurposesTab()
    }

//    TODO: move the following test out of UI tests
//    @Test
//    fun GIVEN_a_saved_consent_CLEAR_all_SDK_variables():Unit = runBlocking {
//        loadKoinModules(
//            mockModule(
//                spConfig = spConfGdpr,
//                gdprPmId = "488393",
//                spClientObserver = listOf(spClient)
//            )
//        )
//
//        launchApp()
//
//        tapAcceptOnWebView()
//        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
//        clickOnClearConsent()
//        wr {
//            sharedPrefs.all.size.assertEquals(1)
//            sharedPrefs.getString(CLIENT_PREF_KEY, "").assertEquals(CLIENT_PREF_VAL)
//        }
//    }

    @Test
    fun test_GracefulDegradation_gdpr_consent_present():Unit = runBlocking {
        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr.copy(messageTimeout = 1),
                gdprPmId = "488393",
                spClientObserver = listOf(spClient),
                pStoreStateGdpr = true
            )
        )

        launchApp()

        wr { verify(exactly = 0) { spClient.onError(any()) } }
        wr { verify(exactly = 1) { spClient.onConsentReady(any()) } }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
    }

    @Test
    fun test_GracefulDegradation_gdpr_and_ccpa_consent_present():Unit = runBlocking {
        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr.copy(messageTimeout = 1),
                gdprPmId = "488393",
                spClientObserver = listOf(spClient),
                pStoreStateGdpr = true,
                pStoreStateCcpa = true
            )
        )

        launchApp()

        wr { verify(exactly = 0) { spClient.onError(any()) } }
        wr { verify(exactly = 1) { spClient.onConsentReady(any()) } }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
    }

    @Test
    fun test_GracefulDegradation_ccpa_consent_present():Unit = runBlocking {
        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr.copy(messageTimeout = 1),
                gdprPmId = "488393",
                spClientObserver = listOf(spClient),
                pStoreStateCcpa = true
            )
        )

        launchApp()

        wr { verify(exactly = 0) { spClient.onError(any()) } }
        wr { verify(exactly = 1) { spClient.onConsentReady(any()) } }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
    }

    @Test
    fun test_GracefulDegradation_consent_absent():Unit = runBlocking {
        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr.copy(messageTimeout = 1),
                gdprPmId = "488393",
                spClientObserver = listOf(spClient)
            )
        )

        launchApp()

        wr { verify(exactly = 1) { spClient.onError(any()) } }
        wr { verify(exactly = 0) { spClient.onConsentReady(any()) } }
        wr { verify(exactly = 0) { spClient.onSpFinished(any()) } }
    }

    @Test
    fun GIVEN_an_old_CCPA_GDPR_v6LocalState_VERIFY_that_the_migration_is_performed():Unit = runBlocking {
        loadKoinModules(
            mockModule(
                spConfig = spConf,
                gdprPmId = "488393",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient)
            )
        )

        storeStateFrom(TestData.storedConsentGdprCcap)

        launchApp()

        wr { verify(exactly = 0) { spClient.onUIReady(any()) } }

        wr { sharedPrefs.contains("sp.key.local.state").assertFalse() }
    }

    private fun <E> check(block: () -> E): E? {
        return try {
            block.invoke()
        } catch (e: Exception) {
            null
        }
    }
}

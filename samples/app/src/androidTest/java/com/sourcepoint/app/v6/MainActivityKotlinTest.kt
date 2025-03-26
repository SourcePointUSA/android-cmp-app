package com.sourcepoint.app.v6

import android.app.Activity
import android.preference.PreferenceManager
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import com.example.uitestutil.assertEquals
import com.example.uitestutil.assertFalse
import com.example.uitestutil.assertNotEquals
import com.example.uitestutil.assertNotNull
import com.example.uitestutil.assertTrue
import com.example.uitestutil.checkTextInParagraph
import com.example.uitestutil.wr
import com.sourcepoint.app.v6.TestUseCase.Companion.checkAllCcpaConsentsOn
import com.sourcepoint.app.v6.TestUseCase.Companion.checkAllTogglesOFF
import com.sourcepoint.app.v6.TestUseCase.Companion.checkAllVendorsOff
import com.sourcepoint.app.v6.TestUseCase.Companion.checkCustomCategoriesData
import com.sourcepoint.app.v6.TestUseCase.Companion.checkCustomVendorDataList
import com.sourcepoint.app.v6.TestUseCase.Companion.checkDeepLinkDisplayed
import com.sourcepoint.app.v6.TestUseCase.Companion.checkDeletedCustomCategoriesData
import com.sourcepoint.app.v6.TestUseCase.Companion.checkFeaturesTab
import com.sourcepoint.app.v6.TestUseCase.Companion.checkPurposesTab
import com.sourcepoint.app.v6.TestUseCase.Companion.checkSomeConsentsOff
import com.sourcepoint.app.v6.TestUseCase.Companion.checkWebViewDisplayedGDPRFirstLayerMessage
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnCcpaReviewConsent
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnCustomConsent
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnDeleteCustomConsent
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnGdprReviewConsent
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnRefreshBtnActivity
import com.sourcepoint.app.v6.TestUseCase.Companion.mockModule
import com.sourcepoint.app.v6.TestUseCase.Companion.progRejectAll
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
import com.sourcepoint.cmplibrary.data.network.util.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.exposed.CcpaStatus
import com.sourcepoint.cmplibrary.model.exposed.SpCampaign
import io.mockk.clearMocks
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.koin.core.context.loadKoinModules
import kotlin.test.assertEquals

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
        propertyName = "mobile.multicampaign.native.demo"
        messLanguage = MessageLanguage.ENGLISH
        messageTimeout = 5000
        +(CampaignType.GDPR)
    }

    private fun getSharedPrefs(activity: Activity) = PreferenceManager.getDefaultSharedPreferences(activity)

    @Test
    fun programmatically_reject_all_calls_callbacks_and_rejects_all() = runBlocking {
        val spClient = mockk<SpClient>(relaxed = true)
        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr,
                gdprPmId = "488393",
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        wr { tapAcceptOnWebView() }
        wr { progRejectAll() }
        wr {
            verify(exactly = 2) {
                spClient.onConsentReady(any())
            }
        }
        wr { clickOnGdprReviewConsent() }
        wr { checkAllTogglesOFF() }
    }

    @Test
    fun given_a_USNAT_campaign_SHOW_message_and_ACCEPT_ALL() = runBlocking {
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
        wr { verify { spClient.onAction(any(), withArg { it.pubData["pb_key"].assertEquals("pb_value") }) } }
        wr { verify(exactly = 1) { spClient.onSpFinished( withArg {
            it.usNat!!.consent.run {
                scenario.onActivity { activity ->
                    getSharedPrefs(activity).getString("IABUSPrivacy_String", null).assertEquals("1YNN")
                }
                applies.assertTrue()
                statuses.consentedToAll!!.assertTrue()
                uuid.assertNotNull()
            }
        }) } }
    }

    @Test
    fun given_a_gdpr_campaign_SHOW_message_and_ACCEPT_ALL() = runBlocking<Unit> {
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

        wr { tapAcceptOnWebView() }

        verify(exactly = 0) { spClient.onError(any()) }

        wr { verify {
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
                    it.gdpr!!.consent.tcData.assertNotEquals(emptyMap())
                })
            }
        } }

        scenario.onActivity { activity ->
            getSharedPrefs(activity).run {
                getInt("IABTCF_EnableAdvertiserConsentMode", -1).assertEquals(1)

                getInt("IABTCF_PurposeOneTreatment", -1).assertNotEquals(-1)
                getInt("IABTCF_CmpSdkVersion", -1).assertNotEquals(-1)
                getInt("IABTCF_CmpSdkID", -1).assertNotEquals(-1)
                getInt("IABTCF_PolicyVersion", -1).assertNotEquals(-1)
                getInt("IABTCF_UseNonStandardTexts", -1).assertNotEquals(-1)
                getInt("IABTCF_gdprApplies", -1).assertNotEquals(-1)

                getString("IABTCF_TCString", null).assertNotNull()
                getString("IABTCF_PublisherLegitimateInterests", null).assertEquals("00000000000")
                getString("IABTCF_AddtlConsent", null).assertNotNull()
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
    fun given_a_ccpa_campaign_SHOW_message_and_ACCEPT_ALL() = runBlocking {
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

        wr { tapAcceptOnWebView() }
        wr { clickOnCcpaReviewConsent() }
        wr { checkAllCcpaConsentsOn() }

        verify(exactly = 0) { spClient.onError(any()) }
        wr { verify {
            spClient.run {
                onUIReady(any())
                onUIFinished(any())
                spClient.onAction(any(), withArg { it.pubData["pb_key"].assertEquals("pb_value") })
                onConsentReady(any())
                onSpFinished(withArg {
                    it.ccpa!!.consent.applies.assertTrue()
                    it.ccpa!!.consent.uuid.assertNotNull()
                })
            }
        }}

        wr {
            scenario.onActivity {
                getSharedPrefs(it).getString("IABUSPrivacy_String", null).assertEquals("1YNN")
            }
        }
    }

    @Test
    fun given_a_ccpa_campaign_CHECK_the_different_status() = runBlocking {
        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfCcpa,
                gdprPmId = "1",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        wr { tapAcceptOnWebView() }
        wr {
            verify {
                spClient.onSpFinished(withArg {
                    it.ccpa!!.consent.applies.assertTrue()
                    assertEquals(CcpaStatus.consentedAll, it.ccpa!!.consent.status)
                })
            }
        }

        wr { clickOnCcpaReviewConsent() }
        wr { checkAllCcpaConsentsOn() }
        wr { tapRejectAllWebView() }
        wr {
            verify {
                spClient.onSpFinished(withArg {
                    it.ccpa!!.consent.applies.assertTrue()
                    assertEquals(CcpaStatus.rejectedAll, it.ccpa!!.consent.status)
                })
            }
        }
        wr {
            scenario.onActivity {
                getSharedPrefs(it).getString("IABUSPrivacy_String", null).assertEquals("1YYN")
            }
        }
    }

    @Test
    fun given_a_gdpr_campaign_SHOW_message_and_REJECT_ALL() = runBlocking {

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

        wr { tapRejectOnWebView() }
        wr { clickOnGdprReviewConsent() }
        wr { checkAllTogglesOFF() }

        wr { verify(exactly = 0) { spClient.onError(any()) } }
        wr {
            verify {
                spClient.run {
                    onUIReady(any())
                    onUIFinished(any())
                    spClient.onAction(
                        any(),
                        withArg { it.pubData["pb_key"].assertEquals("pb_value") })
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
                        it.gdpr!!.consent.googleConsentMode!!.adPersonalization.assertEquals(
                            GCMStatus.DENIED
                        )
                        it.gdpr!!.consent.googleConsentMode!!.analyticsStorage.assertEquals(
                            GCMStatus.DENIED
                        )
                    })
                }
            }
        }
    }

    @Test
    fun given_a_campaignList_ACCEPT_all_legislation() = runBlocking {

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
        wr { verify(exactly = 2) { spClient.onUIFinished(any()) } }

        wr {
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
    }

    @Test
    fun acceptAll_works_even_if_legislation_applies_false() = runBlocking {
        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = config {
                    accountId = 22
                    propertyId = 36345
                    propertyName = "android.applies.false.in.pm"
                    messageTimeout = 5000
                    +(CampaignType.GDPR)
                },
                gdprPmId = "1144201",
                ccpaPmId = "",
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        wr {
            verify { spClient.onSpFinished(any()) }
            clearMocks(spClient)
        }
        wr { clickOnGdprReviewConsent() }
        wr { tapAcceptAllOnWebView() }

        wr { verify {
            spClient.onSpFinished(withArg { consents ->
                consents.gdpr!!.consent.applies.assertFalse()
                consents.gdpr!!.consent.euconsent.assertNotEquals("")
                consents.gdpr!!.consent.euconsent.assertNotNull()
                consents.gdpr!!.consent.uuid.assertNotNull()
            })
        }}
    }

    @Test
    fun given_a_campaignList_ACCEPT_all_legislation_and_verify_that_the_popup_appear_1_time() = runBlocking {
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

        wr { tapAcceptOnWebView() }
        wr { tapAcceptCcpaOnWebView() }
        wr { clickOnRefreshBtnActivity() }

        verify(exactly = 0) { spClient.onError(any()) }
        wr { verify(exactly = 2) { spClient.onSpFinished(any()) } }
        wr { verify(exactly = 3) { spClient.onConsentReady(any()) } }
        wr { verify(exactly = 2) { spClient.onUIReady(any()) } }
        wr {
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
    }

    @Test
    fun given_a_campaign_without_popup_to_show_VERIFY_that_the_tcdata_gets_saved() = runBlocking<Unit> {

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
        wr { verify(exactly = 0) { spClient.onUIReady(any()) } }
        wr {
            verify {
                spClient.run {
                    onSpFinished(withArg {
                        it.gdpr!!.consent.applies.assertTrue()
                        it.gdpr!!.consent.consentStatus!!.consentedAll.assertNotNull()
                    })
                }
            }
        }

        scenario.onActivity {
            getSharedPrefs(it).run {
                getString("IABTCF_AddtlConsent", null).assertNotNull()
                getString("IABTCF_PublisherLegitimateInterests", null).assertEquals("00000000000")
                getString("IABTCF_TCString", null).assertNotNull()
                getInt("IABTCF_CmpSdkVersion", -1).assertNotEquals(-1)
                getInt("IABTCF_CmpSdkID", -1).assertNotEquals(-1)
                getInt("IABTCF_PolicyVersion", -1).assertNotEquals(-1)
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
    fun given_a_campaignList_ACCEPT_all_legislation_from_option_button() = runBlocking {

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

        wr { tapOptionWebView() }
        wr { tapAcceptAllOnWebView() }
        wr { tapOptionWebView() }
        wr(delay = 500) { tapAcceptAllOnWebView() }

        verify(exactly = 0) { spClient.onError(any()) }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
        wr { verify(exactly = 2) { spClient.onConsentReady(any()) } }
        wr { verify(exactly = 2) { spClient.onUIReady(any()) } }
        wr { verify(exactly = 4) { spClient.onAction(any(), any()) } }
        wr { verify(exactly = 2) { spClient.onUIFinished(any()) } }

        wr {
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
    }

    @Test
    fun given_consent_USING_gdpr_pm() = runBlocking {

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

        wr { verify(exactly = 0) { spClient.onError(any()) } }

        wr {
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
    }

    @Test
    fun given_a_gdpr_consent_ACCEPT_ALL() = runBlocking {

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr,
                gdprPmId = "488393",
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        wr { tapAcceptAllOnWebView() }

        verify(exactly = 0) { spClient.onError(any()) }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }

        wr {
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
    }

    @Test
    fun given_a_deeplink_OPEN_an_activity() = runBlocking {

        loadKoinModules(mockModule(spConfig = spConfGdpr, gdprPmId = "488393"))

        scenario = launchActivity()

        wr(backup = { clickOnRefreshBtnActivity() }) { tapRejectOnWebView() }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { tapNetworkOnWebView() }
        wr { checkDeepLinkDisplayed() }
    }

    @Test
    fun save_and_exit_action_persists_consent() = runBlocking {

        loadKoinModules(mockModule(spConfig = spConfGdpr, gdprPmId = "488393"))

        scenario = launchActivity()

        wr(backup = { clickOnRefreshBtnActivity() }) { tapAcceptOnWebView() }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { tapToDisableSomeConsent() }
        wr { tapSaveAndExitWebView() }
        delay(300)
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { checkSomeConsentsOff() }
    }

    @Test
    fun applies_usnat_SAVE_AND_EXIT_action() = runBlocking {
        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr,
                gdprPmId = "488393",
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        wr { tapOptionWebView() }
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
    fun customConsentAction() = runBlocking {
        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr,
                gdprPmId = "488393",
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        wr { tapRejectOnWebView() }
        wr {
            verify {
                spClient.run {
                    onConsentReady(withArg {
                        it.gdpr?.consent?.grants?.all { vendorGrant -> vendorGrant.value.purposeGrants.all { purposeGrant -> !purposeGrant.value } }?.assertTrue()
                        it.gdpr?.consent?.uuid.assertNotNull()
                    })
                }
            }
        }
        clearMocks(spClient)
        wr { clickOnCustomConsent() }
        wr { clickOnGdprReviewConsent() }
        wr { checkCustomCategoriesData() }
        wr { tapSiteVendorsWebView() }
        wr { checkCustomVendorDataList() }
        wr { tapCancelOnWebView() }
        wr { clickOnRefreshBtnActivity() }
        wr {
            verify {
                spClient.run {
                    onConsentReady(withArg {
                        it.gdpr?.consent?.grants!!["5ff4d000a228633ac048be41"]!!.granted.assertTrue()
                        it.gdpr?.consent?.uuid.assertNotNull()
                    })
                }
            }
        }

        verify(exactly = 0) { spClient.onError(any()) }
    }

    @Test
    fun deleteCustomConsentAction() = runBlocking {
        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr,
                gdprPmId = "488393",
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        wr { tapAcceptAllOnWebView() }
        wr { clickOnDeleteCustomConsent() }
        wr { clickOnGdprReviewConsent() }
        wr { checkDeletedCustomCategoriesData() }
    }

    @Test
    fun given_a_campaignList_VERIFY_back_btn() = runBlocking {
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
    fun given_a_campaignList_PRESS_cancel_VERIFY_onConsentReady_NOT_called() = runBlocking {
        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr,
                gdprPmId = "488393",
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        wr { tapAcceptAllOnWebView() }
        wr { clickOnGdprReviewConsent() }
        wr { tapCancelOnWebView() }

        wr { verify(exactly = 1) { spClient.onConsentReady(any()) } }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
    }

    @Test
    fun given_a_groupId_VERIFY_that_the_right_pm_is_displayed() = runBlocking {
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

        wr { tapZustimmenAllOnWebView() }
        wr { clickOnGdprReviewConsent() }
        wr { checkTextInParagraph("Privacy Notice Prop 1") }
    }

    @Test
    fun tapping_on_aVENDORS_link_SHOW_the_PM_VENDORS_tab() = runBlocking {
        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfNative,
                gdprPmId = "545258",
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()

        wr { tapPartnersOnWebView() }
        wr { checkAllVendorsOff() }
        wr { tapCancelOnWebView() }

        // Features
        wr { tapFeaturesOnWebView() }
        wr { checkFeaturesTab() }
        wr { tapCancelOnWebView() }

        wr { tapPurposesOnWebView() }
        wr { checkPurposesTab() }
    }

    @Test
    fun given_a_ccpa_if_rejectedAll_from_PM_VERIFY_USPSTRING() = runBlocking {
        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfCcpa,
                gdprPmId = "123",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient),
            )
        )

        scenario = launchActivity()

        wr { tapOptionWebView() }
        wr(delay = 500) { tapRejectAllWebView() }

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
        wr {
            scenario.onActivity {
                getSharedPrefs(it).getString("IABUSPrivacy_String", null).assertEquals("1YYN")
            }
        }
    }
}

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
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnClearConsent
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnConsentActivity
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnCustomConsent
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnDeleteCustomConsent
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnGdprReviewConsent
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnRefreshBtnActivity
import com.sourcepoint.app.v6.TestUseCase.Companion.mockModule
import com.sourcepoint.app.v6.TestUseCase.Companion.tapAcceptAllOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapAcceptCcpaOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapAcceptOnOk
import com.sourcepoint.app.v6.TestUseCase.Companion.tapAcceptOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapAcceptOnWebViewDE
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
class MainActivityKotlinOldConsentTest {

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

    private val spConfGdprFinnish = config {
        accountId = 1047
        propertyId = 26891
        propertyName = "OikotieAndroid2022"
        messLanguage = MessageLanguage.FINNISH
        +(CampaignType.GDPR)
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
    fun GIVEN_an_old_CCPA_GDPR_v6LocalState_VERIFY_that_the_migration_is_performed() = runBlocking<Unit> {

        val v6LocalState = JSONObject(TestData.storedConsentGdprCcpa)

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConf,
                gdprPmId = "488393",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient),
                diagnostic = v6LocalState.toList()
            )
        )

        scenario = launchActivity()

        wr {
            verify(exactly = 1) {
                spClient.onSpFinished(withArg {
                    it.ccpa!!.consent.applies.assertTrue()
                    it.gdpr!!.consent.applies.assertTrue()
                })
            }
        }
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
                spClientObserver = listOf(spClient),
                diagnostic = v6LocalState.toList()
            )
        )

        scenario = launchActivity()

        wr {
            verify(exactly = 1) {
                spClient.onSpFinished(withArg { it.gdpr!!.consent.applies.assertTrue() })
            }
        }
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

        val v6LocalState = JSONObject(TestData.storedConsentCcpa)

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfCcpa,
                gdprPmId = "488393",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient),
                diagnostic = v6LocalState.toList()
            )
        )

        scenario = launchActivity()

        wr {
            verify(exactly = 1) {
                spClient.onSpFinished(withArg { it.ccpa!!.consent.applies.assertTrue() })
            }
        }
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

    @Test
    fun GIVEN_an_old_GDPR_FINNISH_v6LocalState_VERIFY_that_the_migration_is_performed() = runBlocking<Unit> {

        val v6LocalState = JSONObject(TestData.storedConsentGdprFinnish)

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            mockModule(
                spConfig = spConfGdprFinnish,
                gdprPmId = "662122",
                ccpaPmId = "-",
                spClientObserver = listOf(spClient),
                diagnostic = v6LocalState.toList()
            )
        )

        scenario = launchActivity()

        wr { verify(exactly = 0) { spClient.onUIReady(any()) } }
        wr { verify(exactly = 0) { spClient.onUIFinished(any()) } }
        wr {
            verify(exactly = 1) {
                spClient.onSpFinished(withArg { it.gdpr!!.consent.applies.assertTrue() })
            }
        }
        wr { verify(exactly = 0) { spClient.onAction(any(), any()) } }

        wr {
            scenario.onActivity { activity ->
                val sp = PreferenceManager.getDefaultSharedPreferences(activity)
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
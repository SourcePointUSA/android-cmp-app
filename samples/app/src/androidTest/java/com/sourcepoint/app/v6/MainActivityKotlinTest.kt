package com.sourcepoint.app.v6

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.uitestutil.wr
import com.sourcepoint.app.v6.TestUseCase.Companion.checkAllConsentsOff
import com.sourcepoint.app.v6.TestUseCase.Companion.checkAllConsentsOn
import com.sourcepoint.app.v6.TestUseCase.Companion.checkCustomCategoriesData
import com.sourcepoint.app.v6.TestUseCase.Companion.checkCustomVendorDataList
import com.sourcepoint.app.v6.TestUseCase.Companion.checkDeepLinkDisplayed
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnCustomConsent
import com.sourcepoint.app.v6.TestUseCase.Companion.clickOnGdprReviewConsent
import com.sourcepoint.app.v6.TestUseCase.Companion.tapAcceptAllOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapAcceptCcpaOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapAcceptOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapNetworkOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapOptionWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapRejectOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapSaveAndExitWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapSiteVendorsWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapToDisableAllConsent
import com.sourcepoint.app.v6.core.DataProvider
import com.sourcepoint.app.v6.di.customCategoriesDataProd
import com.sourcepoint.app.v6.di.customVendorDataListProd
import com.sourcepoint.cmplibrary.creation.config
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityKotlinTest {

    lateinit var scenario: ActivityScenario<MainActivityKotlin>

    @After
    fun cleanup() {
        if (this::scenario.isLateinit) scenario.close()
    }

    private val spConfGdpr = config {
        accountId = 22
        propertyName = "mobile.multicampaign.demo"
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

    @Test
    fun GIVEN_a_gdpr_campaign_SHOW_message_and_ACCEPT_ALL() = runBlocking<Unit> {

        loadKoinModules(mockModule(spConfig = spConfGdpr, gdprPmId = "488393", ccpaPmId = "509688"))

        scenario = launchActivity()

        wr { tapAcceptOnWebView() }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { checkAllConsentsOn() }
    }

    @Test
    fun GIVEN_a_ccpa_campaign_SHOW_message_and_REJECT_ALL() = runBlocking<Unit> {

        loadKoinModules(mockModule(spConfig = spConfGdpr, gdprPmId = "488393", ccpaPmId = "509688"))

        scenario = launchActivity()

        wr { tapRejectOnWebView() }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { checkAllConsentsOff() }
    }

    @Test
    fun GIVEN_a_campaignList_ACCEPT_all_legislation() = runBlocking<Unit> {

        loadKoinModules(mockModule(spConfig = spConf, gdprPmId = "488393", ccpaPmId = "509688"))

        scenario = launchActivity()

        wr { tapAcceptOnWebView() }
        wr { tapAcceptCcpaOnWebView() }
    }

    @Test
    fun GIVEN_a_camapignList_ACCEPT_all_legislation_from_option_button() = runBlocking<Unit> {

        loadKoinModules(mockModule(spConfig = spConf, gdprPmId = "488393", ccpaPmId = "509688"))

        scenario = launchActivity()

        wr { tapOptionWebView() }
        wr { tapAcceptAllOnWebView() }
        wr { tapOptionWebView() }
        wr { tapAcceptAllOnWebView() }
    }

    @Test
    fun GIVEN_consent_USING_gdpr_pm() = runBlocking<Unit> {

        loadKoinModules(mockModule(spConfig = spConfGdpr, gdprPmId = "488393"))

        scenario = launchActivity()

        wr { tapAcceptOnWebView() }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { tapAcceptAllOnWebView() }
    }

    @Test
    fun GIVEN_a_gdpr_consent_ACCEPT_ALL() = runBlocking<Unit> {

        loadKoinModules(mockModule(spConfig = spConfGdpr, gdprPmId = "488393"))

        scenario = launchActivity()

        wr { tapRejectOnWebView() }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { tapAcceptAllOnWebView() }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { checkAllConsentsOn() }
    }

//    @Test
    fun GIVEN_a_deeplink_OPEN_an_activity() = runBlocking<Unit> {

        loadKoinModules(mockModule(spConfig = spConfGdpr, gdprPmId = "488393"))

        scenario = launchActivity()

        wr { tapRejectOnWebView() }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { tapNetworkOnWebView() }
        wr { checkDeepLinkDisplayed() }
    }

    @Test
    fun SAVE_AND_EXIT_action() = runBlocking<Unit> {

        loadKoinModules(mockModule(spConfig = spConfGdpr, gdprPmId = "488393"))

        scenario = launchActivity()

        wr { tapAcceptOnWebView() }
//        wr { tapAcceptCcpaOnWebView() }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { tapToDisableAllConsent() }
        wr { tapSaveAndExitWebView() }
        wr { clickOnGdprReviewConsent() }
        wr { checkAllConsentsOff() }
    }

    @Test
    fun customConsentAction() = runBlocking<Unit> {

        loadKoinModules(mockModule(spConfig = spConfGdpr, gdprPmId = "488393"))

        scenario = launchActivity()

        wr { tapRejectOnWebView() }
        wr { clickOnCustomConsent() }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { checkCustomCategoriesData() }
        wr { tapSiteVendorsWebView() }
        wr { checkCustomVendorDataList() }
    }

    private fun mockModule(
        spConfig: SpConfig,
        gdprPmId: String,
        ccpaPmId: String = "",
        uuid: String? = null,
        url: String = "",
    ): Module {
        return module(override = true) {
            single<DataProvider> {
                object : DataProvider {
                    override val authId = uuid
                    override val resetAll = true
                    override val url = url
                    override val spConfig: SpConfig = spConfig
                    override val gdprPmId: String = gdprPmId
                    override val ccpaPmId: String = ccpaPmId
                    override val customVendorList: List<String> = customVendorDataListProd.map { it.first }
                    override val customCategories: List<String> = customCategoriesDataProd.map { it.first }
                }
            }
        }
    }

}
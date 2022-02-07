package com.sourcepointmeta.metaapp

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.periodicWr
import com.example.uitestutil.recreateAndResume
import com.example.uitestutil.wr
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.addProperty
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.addTestProperty
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.checkAllVendorsOff
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.checkDeepLinkDisplayed
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.checkFeaturesTab
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.checkNumberOfNullMessage
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.checkOnConsentReady
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.checkOnSpFinish
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.checkPurposesTab
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.checkWebViewDisplayedGDPRFirstLayerMessage
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.clickOnGdprReviewConsent
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.runDemo
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.saveProperty
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.swipeLeftPager
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.tapAcceptAllOnWebView
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.tapCancelOnWebView
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.tapFab
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.tapFeaturesOnWebView
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.tapMetaDeepLinkOnWebView
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.tapOptionWebView
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.tapPartnersOnWebView
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.tapPurposesOnWebView
import com.sourcepointmeta.metaapp.data.localdatasource.createDb
import com.sourcepointmeta.metaapp.db.MetaAppDB
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.qualifier.named
import org.koin.dsl.module

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityTest {

    lateinit var scenario: ActivityScenario<MainActivity>
    private val appContext by lazy { InstrumentationRegistry.getInstrumentation().targetContext }
    private val db by lazy<MetaAppDB> { createDb(appContext) }

    @After
    fun cleanup() {
        if (this::scenario.isLateinit) scenario.close()
    }

    @Before
    fun setup() {
        db.campaignQueries.run {
            deleteAllProperties()
            deleteStatusCampaign()
        }
    }

    @Test
    fun INSERT_a_property_and_VERIFY_the_data() = runBlocking<Unit> {
        scenario = launchActivity()

        tapFab()
        addTestProperty()
        saveProperty()
    }

    @Test
    fun GIVEN_an_authId_VERIFY_no_first_layer_mess_gets_called() = runBlocking<Unit> {
        val spClient = mockk<SpClient>(relaxed = true)
        loadKoinModules(
            module(override = true) {
                single<List<SpClient>> { listOf(spClient) }
                single(qualifier = named("ui_test_running")) { true }
            }
        )
        scenario = launchActivity()

        db.addTestProperty(autId = "test")

        periodicWr(period = 2000, times = 2, backup = { scenario.recreateAndResume() }) { runDemo() }
        wr { checkNumberOfNullMessage(position = 2) }
        wr { checkOnConsentReady(position = 1) }
        wr { checkOnSpFinish(position = 0) }

        verify(exactly = 1) { spClient.onSpFinished(any()) }
        verify(exactly = 1) { spClient.onConsentReady(any()) }
        verify(exactly = 0) { spClient.onUIReady(any()) }
        verify(exactly = 0) { spClient.onError(any()) }
        verify(exactly = 0) { spClient.onUIFinished(any()) }
    }

    @Test
    fun GIVEN_a_camapignList_VERIFY_back_btn() = runBlocking<Unit> {
        val spClient = mockk<SpClient>(relaxed = true)
        loadKoinModules(
            module(override = true) {
                single<List<SpClient>> { listOf(spClient) }
                single(qualifier = named("ui_test_running")) { true }
            }
        )
        scenario = launchActivity()

        db.addTestProperty(gdprEnabled = true, ccpaEnabled = true)

        periodicWr(period = 2000, times = 2, backup = { scenario.recreateAndResume() }) { runDemo() }
        wr { tapOptionWebView() }
        wr { tapCancelOnWebView() }
        wr { checkWebViewDisplayedGDPRFirstLayerMessage() }
        wr { tapAcceptAllOnWebView() }
        wr { tapOptionWebView() }
        wr { tapCancelOnWebView() }
        wr { tapAcceptAllOnWebView() }

        verify(exactly = 4) { spClient.onAction(any(), any()) }
    }

    @Test
    fun GIVEN_an_deepLink_SHOW_the_deep_link_activity() = runBlocking<Unit> {
        val spClient = mockk<SpClient>(relaxed = true)
        loadKoinModules(
            module(override = true) {
                single<List<SpClient>> { listOf(spClient) }
                single(qualifier = named("ui_test_running")) { true }
            }
        )
        scenario = launchActivity()

        db.addTestProperty(autId = "test")

        periodicWr(period = 2000, times = 2, backup = { scenario.recreateAndResume() }) { runDemo() }
        wr { checkOnConsentReady(position = 1) }
        wr { checkOnSpFinish(position = 0) }
        wr(delay = 200) { swipeLeftPager() }
        wr { clickOnGdprReviewConsent() }
        wr(backup = { clickOnGdprReviewConsent() }) { tapMetaDeepLinkOnWebView() }
        wr { checkDeepLinkDisplayed() }

        verify(exactly = 1) { spClient.onSpFinished(any()) }
        verify(exactly = 1) { spClient.onConsentReady(any()) }
        verify(atLeast = 1) { spClient.onUIReady(any()) }
    }

    @Test
    fun TAPPING_on_aVENDORS_link_SHOW_the_PM_VENDORS_tab() = runBlocking<Unit> {
        val spClient = mockk<SpClient>(relaxed = true)
        loadKoinModules(
            module(override = true) {
                single<List<SpClient>> { listOf(spClient) }
                single(qualifier = named("ui_test_running")) { true }
            }
        )
        scenario = launchActivity()

        db.addProperty(propertyName = "mobile.multicampaign.native.demo", gdprPmId = 545258)

        periodicWr(period = 2000, times = 2, backup = { scenario.recreateAndResume() }) { runDemo() }

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
}

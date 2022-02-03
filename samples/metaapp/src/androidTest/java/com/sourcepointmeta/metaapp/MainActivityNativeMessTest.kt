package com.sourcepointmeta.metaapp

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.wr
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.addNativeTestProperty
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.addProperty
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.checkAllGdprConsentsOn
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.checkCcpaNativeTitle
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.checkGdprNativeTitle
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.runDemo
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.swipeLeftPager
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.tapNmAcceptAll
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.tapNmDismiss
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.tapShowPmBtn
import com.sourcepointmeta.metaapp.data.localdatasource.createDb
import com.sourcepointmeta.metaapp.db.MetaAppDB
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.qualifier.named
import org.koin.dsl.module

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityNativeMessTest {

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
    fun GIVEN_a_native_message_DISMISS_all_messages() = runBlocking<Unit> {
        val spClient = mockk<SpClient>(relaxed = true)
        loadKoinModules(
            module(override = true) {
                single<List<SpClient>> { listOf(spClient) }
                single(qualifier = named("ui_test_running")) { true }
            }
        )
        scenario = launchActivity()

        db.addNativeTestProperty(gdprEnabled = true, ccpaEnabled = true)

        runDemo()

        wr { checkGdprNativeTitle() }
        wr { tapNmDismiss() }
        wr { checkCcpaNativeTitle() }
        wr { tapNmDismiss() }

        verify(exactly = 2) { spClient.onNativeMessageReady(any(), any()) }
        verify(exactly = 1) { spClient.onSpFinished(any()) }
        verify(exactly = 0) { spClient.onConsentReady(any()) }
        verify(exactly = 0) { spClient.onUIReady(any()) }
        verify(exactly = 0) { spClient.onError(any()) }
        verify(exactly = 0) { spClient.onUIFinished(any()) }
        verify(exactly = 0) { spClient.onNoIntentActivitiesFound(any()) }
        verify(exactly = 0) { spClient.onAction(any(), any()) }
    }

    @Test
    fun GIVEN_a_gdpr_native_message_ACCEPT_ALL_and_verify() = runBlocking<Unit> {
        val spClient = mockk<SpClient>(relaxed = true)
        loadKoinModules(
            module(override = true) {
                single<List<SpClient>> { listOf(spClient) }
                single(qualifier = named("ui_test_running")) { true }
            }
        )
        scenario = launchActivity()

        db.addProperty(gdprEnabled = true, ccpaEnabled = false)

        runDemo()

        wr { checkGdprNativeTitle() }
        wr { tapNmAcceptAll() }
        wr { swipeLeftPager() }
        wr { tapShowPmBtn() }
        wr(backup = { tapShowPmBtn() }) { checkAllGdprConsentsOn() }

        verify(atLeast = 1) { spClient.onNativeMessageReady(any(), any()) }
        verify(atLeast = 1) { spClient.onSpFinished(any()) }
        verify(atLeast = 1) { spClient.onConsentReady(any()) }
        verify(atLeast = 1) { spClient.onUIReady(any()) }
        verify(exactly = 0) { spClient.onError(any()) }
        verify(exactly = 0) { spClient.onUIFinished(any()) }
        verify(exactly = 0) { spClient.onNoIntentActivitiesFound(any()) }
        verify(exactly = 0) { spClient.onAction(any(), any()) }
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

        db.addNativeTestProperty(autId = "test")

        runDemo()

        wr { TestUseCaseMeta.checkNumberOfNullMessage(position = 2) }
        wr { TestUseCaseMeta.checkOnConsentReady(position = 1) }
        wr { TestUseCaseMeta.checkOnSpFinish(position = 0) }

        verify(exactly = 1) { spClient.onSpFinished(any()) }
        verify(exactly = 1) { spClient.onConsentReady(any()) }
        verify(exactly = 0) { spClient.onUIReady(any()) }
        verify(exactly = 0) { spClient.onError(any()) }
        verify(exactly = 0) { spClient.onUIFinished(any()) }
    }
}

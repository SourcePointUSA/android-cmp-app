package com.sourcepointmeta.metaapp

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.wr
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.addNativeTestProperty
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.checkCcpaNativeTitle
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.checkGdprNativeTitle
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.runDemo
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.tapDismiss
import com.sourcepointmeta.metaapp.data.localdatasource.createDb
import com.sourcepointmeta.metaapp.db.MetaAppDB
import io.mockk.mockk
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
        wr { tapDismiss() }
        wr { checkCcpaNativeTitle() }
        wr { tapDismiss() }
    }
}

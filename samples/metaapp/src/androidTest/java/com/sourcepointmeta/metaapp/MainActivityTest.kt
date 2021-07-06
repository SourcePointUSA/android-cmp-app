package com.sourcepointmeta.metaapp

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.addTestProperty
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.clickFirstItem
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.runDemo
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.saveProperty
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.tapFab
import com.sourcepointmeta.metaapp.data.localdatasource.createDb
import com.sourcepointmeta.metaapp.db.MetaAppDB
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
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
    fun setup(){
        db.campaignQueries.run {
            deleteAllProperties()
            deleteStatusCampaign()
        }
    }

    @Test
    fun AAA_set_test_property() = runBlocking<Unit> {
        loadKoinModules(
            module(override = true) {
                single(qualifier = named("clear_db")) { true }
            }
        )
        scenario = launchActivity()

        tapFab()
        addTestProperty()
        saveProperty()
        println("sort: 1")
    }

    @Test
    fun GIVEN_an_authId_VERIFY_no_first_layer_mess_gets_called() = runBlocking<Unit> {
        scenario = launchActivity()
        db.addTestProperty()
//        clickFirstItem()
        runDemo()


    }
}

package com.sourcepointmeta.metaapp

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.addTestProperty
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.saveProperty
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.tapFab
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.qualifier.named
import org.koin.dsl.module

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityTest {

    lateinit var scenario: ActivityScenario<MainActivity>

    @After
    fun cleanup() {
        if (this::scenario.isLateinit) scenario.close()
    }

    @Test
    fun test1() = runBlocking<Unit> {

        loadKoinModules(
            module(override = true) {
                single(qualifier = named("clear_db")) { true }
            }
        )

        scenario = launchActivity()

        tapFab()
        addTestProperty()
        saveProperty()
    }
}

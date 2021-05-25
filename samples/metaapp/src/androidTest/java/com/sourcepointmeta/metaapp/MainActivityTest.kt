package com.sourcepointmeta.metaapp

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.checkMessageDisplayed
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityTest {

    lateinit var scenario: ActivityScenario<MainActivity>

    @After
    fun cleanup() {
        if (this::scenario.isLateinit) scenario.close()
    }

    @Test
    fun test1() = runBlocking<Unit> {

        scenario = launchActivity()

        checkMessageDisplayed()

    }

}
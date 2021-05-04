package com.sourcepoint.app.v6

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class ATest {

    lateinit var scenario: ActivityScenario<MainActivityJava>

    @After
    fun cleanup() {
        if(this::scenario.isLateinit) scenario.close()
    }

    private val d = 1000L

    @Test
    fun GIVEN_a_camapignList_ACCEPT_all_legislation() = runBlocking<Unit> {

        scenario = launchActivity()

        delay(5000)
    }
}
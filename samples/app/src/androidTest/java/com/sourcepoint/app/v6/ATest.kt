package com.sourcepoint.app.v6

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.uitestutil.wr
import com.sourcepoint.app.v6.TestUseCase.Companion.tapAcceptAllOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapAcceptCcpaOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapAcceptOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapOptionWebView
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class ATest {

    lateinit var scenario: ActivityScenario<MainActivityV6>

    @After
    fun cleanup() {
        if(this::scenario.isLateinit) scenario.close()
    }

    private val d = 1000L

    @Test
    fun GIVEN_a_camapignList_ACCEPT_all_legislation() = runBlocking<Unit> {

        scenario = launchActivity()

    }
}
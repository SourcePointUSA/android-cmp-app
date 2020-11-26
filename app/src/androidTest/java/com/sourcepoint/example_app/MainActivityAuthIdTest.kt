package com.sourcepoint.example_app

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.sourcepoint.example_app.ExampleAppTestsRobot.Companion.checkAuthIdIsDisplayed
import com.sourcepoint.example_app.ExampleAppTestsRobot.Companion.checkAuthIdIsNotDisplayed
import com.sourcepoint.example_app.ExampleAppTestsRobot.Companion.openAuthIdActivity
import com.sourcepoint.example_app.ExampleAppTestsRobot.Companion.tapAcceptOnWebView
import com.sourcepoint.example_app.ExampleAppTestsRobot.Companion.tapDismissWebView
import com.sourcepoint.example_app.ExampleAppTestsRobot.Companion.tapRejectOnWebView
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.test.KoinTest
import java.util.*

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityAuthIdTest : KoinTest {

    lateinit var scenario: ActivityScenario<MainActivity>

    @After
    fun cleanup() {
        if (this::scenario.isLateinit) {
            scenario.close()
        }
    }

    @Test
    fun accept_all_authId_is_displayed() = runBlocking {

        val uuid = UUID.randomUUID().toString()

        loadKoinModules(mockModule(uuid))

        scenario = launchActivity()

        wr { tapAcceptOnWebView() }
        wr { openAuthIdActivity() }
        wr(600) { checkAuthIdIsDisplayed(uuid) }
    }

    @Test
    fun reject_all_authId_is_displayed() = runBlocking {

        val uuid = UUID.randomUUID().toString()

        loadKoinModules(mockModule(uuid))

        scenario = launchActivity()

        wr { tapRejectOnWebView() }
        wr { openAuthIdActivity() }
        wr(600) { checkAuthIdIsDisplayed(uuid) }
    }

    @Test
    fun dismiss_privacy_note_authId_is_not_displayed() = runBlocking {

        val uuid = UUID.randomUUID().toString()

        loadKoinModules(mockModule(uuid))

        scenario = launchActivity()

        wr { tapDismissWebView() }
        wr { openAuthIdActivity() }
        wr { checkAuthIdIsNotDisplayed() }
    }
}
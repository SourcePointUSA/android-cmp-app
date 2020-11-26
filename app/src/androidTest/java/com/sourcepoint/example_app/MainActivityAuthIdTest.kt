package com.sourcepoint.example_app

import android.webkit.CookieManager
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.sourcepoint.example_app.ExampleAppTestsRobot.Companion.openAuthIdActivity
import com.sourcepoint.example_app.ExampleAppTestsRobot.Companion.tapAcceptOnWebView
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
    private val urlTest = "https://carmelo-iriti.github.io/authid.github.io"

    @After
    fun cleanup() {
        if (this::scenario.isLateinit) {
            scenario.close()
        }
    }

    @Test
    fun accept_all_authId_is_displayed_and_cookie_set() = runBlocking {

        val uuid = UUID.randomUUID().toString()

        loadKoinModules(mockModule(uuid, urlTest))

        scenario = launchActivity()

        wr { tapAcceptOnWebView() }
        wr { openAuthIdActivity() }

        CookieManager.getInstance()
            .getCookie("https://carmelo-iriti.github.io")
            .assertNotNull()
            .contains(uuid)
            .assertTrue()
    }

    @Test
    fun reject_all_authId_is_displayed_and_cookie_set() = runBlocking {

        val uuid = UUID.randomUUID().toString()

        loadKoinModules(mockModule(uuid, urlTest))

        scenario = launchActivity()

        wr { tapRejectOnWebView() }
        wr { openAuthIdActivity() }

        CookieManager.getInstance()
            .getCookie("https://carmelo-iriti.github.io")
            .assertNotNull()
            .contains(uuid)
            .assertTrue()
    }

    @Test
    fun dismiss_privacy_note_authId_is_not_displayed() = runBlocking {

        loadKoinModules(mockModule(null, urlTest))

        scenario = launchActivity()

        wr { tapRejectOnWebView() }
        wr { openAuthIdActivity() }

        CookieManager.getInstance()
            .getCookie("https://carmelo-iriti.github.io")
            .assertNull()
    }
}
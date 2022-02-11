package com.sourcepoint.app.v6

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.uitestutil.wr
import com.sourcepoint.app.v6.TestUseCase.Companion.checkCookieExist
import com.sourcepoint.app.v6.TestUseCase.Companion.checkCookieNotExist
import com.sourcepoint.app.v6.TestUseCase.Companion.mockModule
import com.sourcepoint.app.v6.TestUseCase.Companion.openAuthIdActivity
import com.sourcepoint.app.v6.TestUseCase.Companion.tapAcceptOnWebView
import com.sourcepoint.app.v6.TestUseCase.Companion.tapRejectOnWebView
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.creation.config
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import java.util.*

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityAuthIdTest {

    lateinit var scenario: ActivityScenario<MainActivityKotlin>
    private val urlTest = "https://carmelo-iriti.github.io/authid.github.io"

    @After
    fun cleanup() {
        if (this::scenario.isLateinit) {
            scenario.close()
        }
    }

    private val spConfGdpr = config {
        accountId = 22
        propertyName = "mobile.multicampaign.demo"
        messLanguage = MessageLanguage.ENGLISH
        messageTimeout = 3000
        +(CampaignType.GDPR)
    }

    @Test
    fun accept_all_authId_is_displayed_and_cookie_set() = runBlocking {

        val spClient = mockk<SpClient>(relaxed = true)
        val uuid = UUID.randomUUID().toString()

        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr,
                gdprPmId = "488393",
                authId = uuid,
                spClientObserver = listOf(spClient),
                authIdUrl = urlTest
            )
        )

        scenario = launchActivity()

        wr { tapAcceptOnWebView() }
        wr { openAuthIdActivity() }
        wr { checkCookieExist(urlTest, uuid) }
    }

    @Test
    fun reject_all_authId_is_displayed_and_cookie_set() = runBlocking {

        val spClient = mockk<SpClient>(relaxed = true)
        val uuid = UUID.randomUUID().toString()

        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr,
                gdprPmId = "488393",
                authId = uuid,
                spClientObserver = listOf(spClient),
                authIdUrl = urlTest
            )
        )

        scenario = launchActivity()

        wr { tapRejectOnWebView() }
        wr { openAuthIdActivity() }
        wr { checkCookieExist(urlTest, uuid) }
    }

    @Test
    fun dismiss_privacy_note_authId_is_not_displayed() = runBlocking {

        val spClient = mockk<SpClient>(relaxed = true)
        val uuid = UUID.randomUUID().toString()

        loadKoinModules(
            mockModule(
                spConfig = spConfGdpr,
                gdprPmId = "488393",
                authId = uuid,
                spClientObserver = listOf(spClient),
                authIdUrl = urlTest
            )
        )

        scenario = launchActivity()

        wr { tapRejectOnWebView() }
        wr { openAuthIdActivity() }
        wr { checkCookieNotExist(urlTest) }
    }
}
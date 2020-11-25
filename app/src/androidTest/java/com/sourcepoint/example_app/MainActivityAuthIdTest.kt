package com.sourcepoint.example_app

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.sourcepoint.example_app.ExampleAppTestsRobot.Companion.clickOnAuthIdBtn
import com.sourcepoint.example_app.ExampleAppTestsRobot.Companion.tapAcceptAllOnWebView
import com.sourcepoint.example_app.core.DataProvider
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.test.KoinTest

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityAuthIdTest : KoinTest {

    lateinit var scenario: ActivityScenario<MainActivity>

//    @MockK
//    lateinit var dataProvider: DataProvider

    @After
    fun cleanup() {
        if(this::scenario.isLateinit) scenario.close()
    }

    private val mockModule = module(override = true) { single<DataProvider> {
        object : DataProvider{
            override val authId: String
                get() = "aaaa-bbbb-cccc-dddd"
        }
    } }

    @Test
    fun acceptAll_is_checked_authId_is_displayed() = runBlocking {

        loadKoinModules(mockModule)

        scenario = launchActivity()

        wr { tapAcceptAllOnWebView() }
        wr { clickOnAuthIdBtn() }
    }

}
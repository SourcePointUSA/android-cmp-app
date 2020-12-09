package com.sourcepointmeta.metaapp

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.uitestutil.wr
import com.sourcepointmeta.metaapp.ui.SplashScreenActivity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MessageLanguageTests {

    lateinit var scenario: ActivityScenario<SplashScreenActivity>

    @After
    fun cleanup() {
        scenario.close()
    }
    @Test
    fun acceptAllActionOnFrenchLanguage() = runBlocking {

        scenario = launchActivity()

        wr { MetaAppTestCases.tapOnAddProperty() }
        wr { MetaAppTestCases.addMessageLanguagePropertyDetails() }
        wr { MetaAppTestCases.tapOnSave() }
        wr { MetaAppTestCases.checkWebViewDisplayedForMessage() }
        wr { MetaAppTestCases.tapAcceptAllFrenchOnWebView() }
    }

    @Test
    fun rejectAllActionOnFrenchLanguage() = runBlocking {

        scenario = launchActivity()

        wr { MetaAppTestCases.tapOnAddProperty() }
        wr { MetaAppTestCases.addMessageLanguagePropertyDetails() }
        wr { MetaAppTestCases.tapOnSave() }
        wr { MetaAppTestCases.checkWebViewDisplayedForMessage() }
        wr { MetaAppTestCases.tapRejectAllFrenchOnWebView() }
    }

    @Test
    fun showOptionsActionOnFrenchLanguage() = runBlocking {

        scenario = launchActivity()

        wr { MetaAppTestCases.tapOnAddProperty() }
        wr { MetaAppTestCases.addMessageLanguagePropertyDetails() }
        wr { MetaAppTestCases.tapOnSave() }
        wr { MetaAppTestCases.checkWebViewDisplayedForMessage() }
        wr { MetaAppTestCases.tapShowOptionOnWebView() }
        wr { MetaAppTestCases.checkWebViewDisplayedForPrivacyManager() }
    }

}
package com.sourcepointmeta.metaapp

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.uitestutil.checkPMTabSelected
import com.example.uitestutil.wr
import com.sourcepointmeta.metaapp.TestData.*
import com.sourcepointmeta.metaapp.ui.SplashScreenActivity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class PMTabFeatureTests {

    lateinit var scenario: ActivityScenario<SplashScreenActivity>

    @After
    fun cleanup() {
        scenario.close()
    }

    @Test
    fun testPmTabVendors() = runBlocking {
        scenario = launchActivity()

        wr { MetaAppTestCases.tapOnAddProperty() }
        wr { MetaAppTestCases.addPMTabFeaturesPropertyDetails(VENDORS.toUpperCase()) }
        wr { MetaAppTestCases.tapOnSave() }
        wr { MetaAppTestCases.tapAcceptOnWebView() }
        wr { MetaAppTestCases.loadPrivacyManagerDirect() }
        wr { checkPMTabSelected(SITE_VENDORS) }
    }
}
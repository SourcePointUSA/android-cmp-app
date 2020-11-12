package com.sourcepointmeta.metaapp

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.sourcepointmeta.metaapp.ui.SplashScreenActivity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MetaAppTestsK {

    lateinit var scenario: ActivityScenario<SplashScreenActivity>

    @After
    fun cleanup() {
        scenario.close()
    }

    @Test
    fun checkNativeMessageRejectAllFromPMViaMessage() = runBlocking<Unit> {

        scenario = launchActivity()

        MetaAppTestsKRobot()
            .tapOnAddProperty()
            .addNativeMessagePropertyDetails()
            .tapOnSave()
            .checkNativeMessageDisplayed()
            .tapShowOption()
            .isPrivacyManagerVisible()
            .rejectAll()
            .isPropertyInfoScreenDisplayed()
            .navigateBackToListView()
            .isAddPropertyDisplayed()
            .tapOnProperty()
            .checkNativeMessageDisplayed()
            .isPropertyInfoScreenDisplayed()
    }

}
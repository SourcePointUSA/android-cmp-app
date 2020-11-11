package com.sourcepointmeta.metaapp

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.*
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import com.sourcepointmeta.metaapp.ui.SplashScreenActivity
import org.junit.After
import org.junit.Assert
import org.junit.Rule
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
    fun myTest3() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), SplashScreenActivity::class.java)
                .putExtra("title", "Testing rules!")
        scenario = launchActivity(intent)
        Assert.assertEquals(State.DESTROYED, scenario.state)
        // Your test code goes here.
    }

    @Test
    fun myTestWithDifferentExtra() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), SplashScreenActivity::class.java)
                .putExtra("title", "Something different")
        scenario = launchActivity(intent)
        Assert.assertEquals(State.DESTROYED, scenario.state)
        // Your test code goes here.
    }

}
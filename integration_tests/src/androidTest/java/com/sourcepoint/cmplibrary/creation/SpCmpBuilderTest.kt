package com.sourcepoint.cmplibrary.creation

import android.app.Activity
import android.app.Application
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.assertNotNull
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.creation.delegate.spConsentLibLazy
import com.sourcepoint.cmplibrary.integrations.didomi.DidomiConfig
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class SpCmpBuilderTest {
    private val application = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application
    private val activityMock = mockk<Activity>(relaxed = true).also { every { it.application } returns application }
    private val spClientMock = mockk<SpClient>(relaxed = true)
    private val testDidomiConfig = DidomiConfig(apiKey = "test-api-key")

    @Test(expected = RuntimeException::class)
    fun throwsExceptionIfConfigHasNoDidomiConfig() {
        val sut by spConsentLibLazy {
            activity = activityMock
            spClient = spClientMock
            config { }
        }
        sut.assertNotNull()
    }

    @Test
    fun makeSureConfigIsCreated() {
        val sut by spConsentLibLazy {
            activity = activityMock
            spClient = spClientMock
            config {
                addDidomiConfig(testDidomiConfig)
            }
        }
        sut.assertNotNull()
    }

    @Test(expected = RuntimeException::class)
    fun throwsExceptionIfConfigIsMissing() {
        val sut by spConsentLibLazy {
            activity = activityMock
            spClient = spClientMock
        }
        sut.assertNotNull()
    }

    @Test(expected = RuntimeException::class)
    fun throwsExceptionIfActivityIsMissing() {
        val sut by spConsentLibLazy {
            spClient = spClientMock
            config {
                addDidomiConfig(testDidomiConfig)
            }
        }
        sut.assertNotNull()
    }

    @Test(expected = RuntimeException::class)
    fun throwsExceptionIfSpClientIsMissing() {
        val sut by spConsentLibLazy {
            activity = activityMock
            config {
                addDidomiConfig(testDidomiConfig)
            }
        }
        sut.assertNotNull()
    }
}

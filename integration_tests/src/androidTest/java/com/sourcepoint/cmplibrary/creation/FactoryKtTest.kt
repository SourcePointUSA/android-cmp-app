package com.sourcepoint.cmplibrary.creation

import android.app.Activity
import android.app.Application
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.assertNotNull
import com.sourcepoint.cmplibrary.integrations.didomi.DidomiConfig
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class FactoryKtTest {
    private val application = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application
    private val activity = mockk<Activity>(relaxed = true).also { every { it.application } returns application }

    private fun spConfig(didomiConfig: DidomiConfig?) = SpConfig(
        messageLanguage = MessageLanguage.ENGLISH,
        messageTimeout = 3000,
        didomiConfig = didomiConfig,
    )

    @Test
    fun makeConsentLibWorks() {
        makeConsentLib(
            spConfig = spConfig(DidomiConfig(apiKey = "test-api-key")),
            activity = activity,
            spClient = mockk(relaxed = true)
        ).assertNotNull()
    }

    @Test(expected = IllegalArgumentException::class)
    fun makeConsentLibFailsWithoutDidomiConfig() {
        makeConsentLib(
            spConfig = spConfig(didomiConfig = null),
            activity = activity,
            spClient = mockk(relaxed = true)
        )
    }
}

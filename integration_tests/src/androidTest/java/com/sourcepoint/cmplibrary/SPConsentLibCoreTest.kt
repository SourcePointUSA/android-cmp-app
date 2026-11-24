package com.sourcepoint.cmplibrary

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.wr
import com.sourcepoint.cmplibrary.exception.RenderingAppException
import com.sourcepoint.cmplibrary.integrations.didomi.DidomiConfig
import com.sourcepoint.cmplibrary.integrations.didomi.DidomiIntegration
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

// Consent collection and UI are handled by Didomi, so what's covered here is what SpConsentLibMobileCore does on its own.
// Flows that need Didomi's UI belong in the sample app's UI tests.
@RunWith(AndroidJUnit4::class)
class SPConsentLibCoreTest {
    private val spClient = mockk<SpClient>(relaxed = true)
    private val context = InstrumentationRegistry.getInstrumentation().context

    private fun getConsentLib(spClient: SpClient = this.spClient): SpConsentLib = SpConsentLibMobileCore(
        activity = null,
        context = context,
        spClient = spClient,
        didomiIntegration = DidomiIntegration(DidomiConfig(apiKey = "test-api-key"), spClient),
    )

    @Test
    fun loadMessageCallsOnErrorIfThereIsNoActivity() = runBlocking {
        val client = spClient
        getConsentLib(spClient = client).loadMessage()
        wr {
            verify(exactly = 0) { client.onUIReady(any()) }
            verify(exactly = 1) { client.onError(ofType<RenderingAppException>()) }
        }
    }

    @Test
    fun loadPrivacyManagerCallsOnErrorIfThereIsNoActivity() = runBlocking {
        val client = spClient
        getConsentLib(spClient = client).loadPrivacyManager(pmId = "123")
        wr {
            verify(exactly = 0) { client.onUIReady(any()) }
            verify(exactly = 1) { client.onError(ofType<RenderingAppException>()) }
        }
    }
}

package com.sourcepoint.app.v6

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.uitestutil.toList
import com.example.uitestutil.wr
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.creation.config
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityKotlinGracefulDegradationTest {

    lateinit var scenario: ActivityScenario<MainActivityKotlin>

    @After
    fun cleanup() {
        if (this::scenario.isLateinit) scenario.close()
    }

    private val spConf = config {
        accountId = 22
        propertyId = 16893
        propertyName = "mobile.multicampaign.demo"
        messLanguage = MessageLanguage.ENGLISH
        messageTimeout = 5000
        +(CampaignType.GDPR)
        +(CampaignType.CCPA)
    }

    private val spConfGdpr = config {
        accountId = 22
        propertyId = 16893
        propertyName = "mobile.multicampaign.demo"
        messLanguage = MessageLanguage.ENGLISH
        +(CampaignType.GDPR)
    }

    @Test
    fun test_GracefulDegradation_gdpr_consent_present() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        val storedConsent = JSONObject(TestData.storedConsentGDPR_V7)

        loadKoinModules(
            TestUseCase.mockModule(
                spConfig = spConfGdpr.copy(messageTimeout = 3),
                gdprPmId = "488393",
                spClientObserver = listOf(spClient),
                diagnostic = storedConsent.toList()
            )
        )

        scenario = launchActivity()


        wr { verify(exactly = 0) { spClient.onError(any()) } }
        wr { verify(exactly = 1) { spClient.onConsentReady(any()) } }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }

    }

    @Test
    fun test_GracefulDegradation_gdpr_and_ccpa_consent_present() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        val storedConsent = JSONObject(TestData.storedConsentV741)

        loadKoinModules(
            TestUseCase.mockModule(
                spConfig = spConfGdpr.copy(messageTimeout = 3),
                gdprPmId = "488393",
                spClientObserver = listOf(spClient),
                diagnostic = storedConsent.toList()
            )
        )

        scenario = launchActivity()


        wr { verify(exactly = 0) { spClient.onError(any()) } }
        wr { verify(exactly = 1) { spClient.onConsentReady(any()) } }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }

    }

    @Test
    fun test_GracefulDegradation_ccpa_consent_present() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        val storedConsent = JSONObject(TestData.storedConsentCCPA_V7)

        loadKoinModules(
            TestUseCase.mockModule(
                spConfig = spConfGdpr.copy(messageTimeout = 3),
                gdprPmId = "488393",
                spClientObserver = listOf(spClient),
                diagnostic = storedConsent.toList()
            )
        )

        scenario = launchActivity()

        wr { verify(exactly = 0) { spClient.onError(any()) } }
        wr { verify(exactly = 1) { spClient.onConsentReady(any()) } }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }

    }

    @Test
    fun test_GracefulDegradation_consent_absent() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            TestUseCase.mockModule(
                spConfig = spConfGdpr.copy(messageTimeout = 3),
                gdprPmId = "488393",
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()


        wr { verify(exactly = 1) { spClient.onError(any()) } }
        wr { verify(exactly = 0) { spClient.onConsentReady(any()) } }
        wr { verify(exactly = 0) { spClient.onSpFinished(any()) } }

    }

    @Test
    fun GracefulDegradation_GIVEN_a_backend_error_EXECUTE_the_onError() = runBlocking<Unit> {

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            TestUseCase.mockModule(
                spConfig = spConfGdpr.copy(propertyName = "invalid.property"),
                gdprPmId = "488393",
                spClientObserver = listOf(spClient)
            )
        )

        scenario = launchActivity()


        wr { verify(exactly = 1) { spClient.onError(any()) } }
        wr { verify(exactly = 0) { spClient.onConsentReady(any()) } }
        wr { verify(exactly = 0) { spClient.onSpFinished(any()) } }

    }

    @Test
    fun GracefulDegradation_GIVEN_a__backend_error_and_a_saved_consent_EXECUTE_the_onConsentReady() = runBlocking<Unit> {

            val spClient = mockk<SpClient>(relaxed = true)

            val storedConsent = JSONObject(TestData.storedConsentV741)

            loadKoinModules(
                TestUseCase.mockModule(
                    spConfig = spConfGdpr.copy(propertyName = "invalid.property"),
                    gdprPmId = "488393",
                    spClientObserver = listOf(spClient),
                    diagnostic = storedConsent.toList()
                )
            )

            scenario = launchActivity()


            wr { verify(exactly = 0) { spClient.onError(any()) } }
            wr { verify(exactly = 1) { spClient.onConsentReady(any()) } }
            wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }

        }


    @Test
    fun WITHOUT_a_stored_consent_GIVEN_no_internet_connection_exception_VERIFY_the_called_callbacks() = runBlocking<Unit> {

            val spClient = mockk<SpClient>(relaxed = true)

            loadKoinModules(
                TestUseCase.mockModule(
                    spConfig = spConf,
                    gdprPmId = "488393",
                    ccpaPmId = "509688",
                    spClientObserver = listOf(spClient),
                    diagnostic = mutableListOf(Pair("connectionTest", false))
                )
            )

            scenario = launchActivity()

            wr { verify(exactly = 1) { spClient.onError(any()) } }
            wr { verify(exactly = 0) { spClient.onConsentReady(any()) } }
            wr { verify(exactly = 0) { spClient.onUIReady(any()) } }
            wr { verify(exactly = 0) { spClient.onUIFinished(any()) } }
            // TODO We have to change the behaviour of the graceful degradation, onSpFinished must be always called
            wr { verify(exactly = 0) { spClient.onSpFinished(any()) } }
        }

    @Test
    fun WITH_a_stored_consent_GIVEN_no_internet_connection_exception_VERIFY_the_called_callbacks() = runBlocking<Unit> {

        val v7Consent = JSONObject(TestData.storedConsentV741)

        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            TestUseCase.mockModule(
                spConfig = spConf,
                gdprPmId = "488393",
                ccpaPmId = "509688",
                spClientObserver = listOf(spClient),
                diagnostic = v7Consent.toList() + listOf(Pair("connectionTest", false))
            )
        )

        scenario = launchActivity()

        wr { verify(exactly = 1) { spClient.onConsentReady(any()) } }
        wr { verify(exactly = 0) { spClient.onError(any()) } }
        wr { verify(exactly = 0) { spClient.onUIReady(any()) } }
        wr { verify(exactly = 0) { spClient.onUIFinished(any()) } }
        wr { verify(exactly = 1) { spClient.onSpFinished(any()) } }
    }

    private fun <E> check(block: () -> E): E? {
        return try {
            block.invoke()
        } catch (e: Exception) {
            null
        }
    }
}
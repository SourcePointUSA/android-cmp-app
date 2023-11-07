package com.sourcepoint.app.v6

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.assertEquals
import com.example.uitestutil.toList
import com.example.uitestutil.wr
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.creation.config
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.util.userConsents
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules

@RunWith(AndroidJUnit4ClassRunner::class)
class UserConsentsRegressionTest {

    private val appContext by lazy { InstrumentationRegistry.getInstrumentation().targetContext }

    private val spConf = config {
        accountId = 22
        propertyId = 31226
        propertyName = "mobile.bohdan.test.demo"
        messLanguage = MessageLanguage.ENGLISH
        +(CampaignType.GDPR)
        +(CampaignType.CCPA)
    }
    private val gdprPmId = "815371"
    private val ccpaPmId = "807279"

    /**
     * Test case that tests when the user has both campaigns applies value as TRUE then should return
     * sane from the userConsents() method
     */
    @Test
    fun given_stored_consent_with_save_and_exit_choices_and_applies_true_then_user_consents_should_return_true_for_both_applies() = runBlocking {

        val v7Consent = JSONObject(TestData.storedConsentWithSaveAndExitChoicesAndAppliesTrueV741)
        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            TestUseCase.mockModule(
                spConfig = spConf,
                gdprPmId = gdprPmId,
                ccpaPmId = ccpaPmId,
                spClientObserver = listOf(spClient),
                diagnostic = v7Consent.toList()
            )
        )

        wr {
            val gdprAppliesFromUserConsents = userConsents(appContext).gdpr?.consent?.applies
            gdprAppliesFromUserConsents.assertEquals(true)
            val ccpaAppliesFromUserConsents = userConsents(appContext).ccpa?.consent?.applies
            ccpaAppliesFromUserConsents.assertEquals(true)
        }
    }

    /**
     * Test case that tests when the user has both campaigns applies value as FALSE then should return
     * sane from the userConsents() method
     */
    @Test
    fun given_stored_consent_with_save_and_exit_choices_and_applies_false_then_user_consents_should_return_false_for_both_applies() = runBlocking {

        val v7Consent = JSONObject(TestData.storedConsentWithSaveAndExitChoicesAndAppliesFalseV741)
        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            TestUseCase.mockModule(
                spConfig = spConf,
                gdprPmId = gdprPmId,
                ccpaPmId = ccpaPmId,
                spClientObserver = listOf(spClient),
                diagnostic = v7Consent.toList()
            )
        )

        wr {
            val gdprAppliesFromUserConsents = userConsents(appContext).gdpr?.consent?.applies
            gdprAppliesFromUserConsents.assertEquals(false)
            val ccpaAppliesFromUserConsents = userConsents(appContext).ccpa?.consent?.applies
            ccpaAppliesFromUserConsents.assertEquals(false)
        }
    }
}
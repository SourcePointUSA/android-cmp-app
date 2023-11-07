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

    private val spConfGdpr = config {
        accountId = 22
        propertyId = 16893
        propertyName = "mobile.multicampaign.demo"
        messLanguage = MessageLanguage.ENGLISH
        +(CampaignType.GDPR)
    }

    @Test
    fun given_the_user_provides_partial_consent_for_gdpr_then_user_consents_should_return_proper_consent() = runBlocking<Unit> {

        val v7Consent = JSONObject(TestData.storedConsentV741)
        val spClient = mockk<SpClient>(relaxed = true)

        loadKoinModules(
            TestUseCase.mockModule(
                spConfig = spConfGdpr,
                gdprPmId = "488393",
                spClientObserver = listOf(spClient),
                diagnostic = v7Consent.toList()
            )
        )

        wr {
            val gdprAppliesFromUserConsents = userConsents(appContext).gdpr?.consent?.applies
            gdprAppliesFromUserConsents.assertEquals(true)
        }
    }
}
package com.sourcepoint.cmplibrary.creation.delegate

import android.view.View
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.uitestutil.assertEquals
import com.example.uitestutil.assertNotNull
import com.sourcepoint.cmplibrary.MyDebugActivity
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class SpConsentLibDelegateTest {

    @Test
    fun `VERIFY_that_the_delegate_with_a_tab_is_not_null`() {

        val scenario = launchActivity<MyDebugActivity>()
        scenario.onActivity { act ->
            val delegate by spConsentLibLazy {
                activity = act
                spClient = MockClient()
                config {
                    accountId = 22
                    propertyName = "mobile.multicampaign.demo"
                    messLanguage = MessageLanguage.ENGLISH
                    +(CampaignType.CCPA to listOf(("location" to "US")))
                    +(CampaignType.GDPR to listOf(("location" to "EU")))
                }
            }
            val sut1 = delegate
            val sut2 = delegate

            sut1.assertNotNull()
            sut2.assertNotNull()
            sut2.assertEquals(sut1)
        }
    }

    @Test
    fun `VERIFY_that_the_delegate_with_a_tab_is_null`() {

        val scenario = launchActivity<MyDebugActivity>()
        scenario.onActivity { act ->
            val delegate by spConsentLibLazy {
                activity = act
                spClient = MockClient()
                config {
                    accountId = 22
                    propertyName = "mobile.multicampaign.demo"
                    messLanguage = MessageLanguage.ENGLISH
                    +(CampaignType.CCPA to listOf(("location" to "US")))
                    +(CampaignType.GDPR to listOf(("location" to "EU")))
                }
            }
            val sut1 = delegate
            val sut2 = delegate

            sut1.assertNotNull()
            sut2.assertNotNull()
            sut2.assertEquals(sut1)
        }
    }

    class MockClient : SpClient {
        override fun onUIReady(view: View) {
        }

        override fun onMessageReady(message: JSONObject) {
        }

        override fun onAction(view: View, actionType: ActionType) {
        }

        override fun onUIFinished(view: View) {
        }

        override fun onConsentReady(consent: SPConsents) {
        }

        override fun onError(error: Throwable) {
        }
    }
}

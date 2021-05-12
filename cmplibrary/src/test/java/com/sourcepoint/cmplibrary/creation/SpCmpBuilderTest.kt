package com.sourcepoint.cmplibrary.creation

import android.app.Activity
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.creation.delegate.spConsentLibLazy
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test

class SpCmpBuilderTest {

    @MockK
    private lateinit var mockContext: Activity
    @MockK
    private lateinit var mockClient: SpClient

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
    }

    @Test
    fun `GIVEN a DLS config VERIFY the spConfig object created`() {
        val sut by spConsentLibLazy {
            activity = mockContext
            spClient = mockClient
            config {
                accountId = 22
                propertyName = "mobile.multicampaign.demo"
                messLanguage = MessageLanguage.ENGLISH
                +(CampaignType.GDPR)
                +(CampaignType.CCPA to listOf(("location" to "US")))
            }
        }
        sut.assertNotNull()
    }

    @Test(expected = RuntimeException::class)
    fun `GIVEN an obj with a missing config THROWS an exception`() {
        val sut by spConsentLibLazy {
            activity = mockContext
            spClient = mockClient
        }
        sut.assertNotNull()
    }

    @Test(expected = RuntimeException::class)
    fun `GIVEN an obj with a missing activity THROWS an exception`() {
        val sut by spConsentLibLazy {
            spClient = mockClient
            config {
                accountId = 22
                propertyName = "mobile.multicampaign.demo"
                messLanguage = MessageLanguage.ENGLISH
                +(CampaignType.GDPR)
                +(CampaignType.CCPA to listOf(("location" to "US")))
            }
        }
        sut.assertNotNull()
    }

    @Test(expected = RuntimeException::class)
    fun `GIVEN an obj with a missing spClient THROWS an exception`() {
        val sut by spConsentLibLazy {
            activity = mockContext
            config {
                accountId = 22
                propertyName = "mobile.multicampaign.demo"
                messLanguage = MessageLanguage.ENGLISH
                +(CampaignType.GDPR)
                +(CampaignType.CCPA to listOf(("location" to "US")))
            }
        }
        sut.assertNotNull()
    }
}

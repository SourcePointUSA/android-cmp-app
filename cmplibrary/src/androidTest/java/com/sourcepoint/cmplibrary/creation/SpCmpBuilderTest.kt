package com.sourcepoint.cmplibrary.creation

import android.app.Activity
import com.example.uitestutil.assertNotNull
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.creation.delegate.spConsentLibLazy
import com.sourcepoint.cmplibrary.data.network.util.CampaignType
import io.mockk.mockk
import org.junit.Test

class SpCmpBuilderTest {
    private val activityMock = mockk<Activity>(relaxed = true)
    private val spClientMock = mockk<SpClient>(relaxed = true)

    @Test(expected = RuntimeException::class)
    fun throwsExceptionIfConfigHasNoPropertyId() {
        val sut by spConsentLibLazy {
            activity = activityMock
            spClient = spClientMock
            config {
                accountId = 22
                propertyName = "mobile.multicampaign.demo"
                +(CampaignType.GDPR)
            }
        }
        sut.assertNotNull()
    }

    @Test
    fun makeSureConfigIsCreated() {
        val sut by spConsentLibLazy {
            activity = activityMock
            spClient = spClientMock
            config {
                accountId = 22
                propertyName = "mobile.multicampaign.demo"
                propertyId = 16893
                +(CampaignType.GDPR)
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
                accountId = 22
                propertyName = "mobile.multicampaign.demo"
                propertyId = 16893
                +(CampaignType.GDPR)
            }
        }
        sut.assertNotNull()
    }

    @Test(expected = RuntimeException::class)
    fun throwsExceptionIfSpClientIsMissing() {
        val sut by spConsentLibLazy {
            activity = activityMock
            config {
                accountId = 22
                propertyName = "mobile.multicampaign.demo"
                propertyId = 16893
                +(CampaignType.GDPR)
            }
        }
        sut.assertNotNull()
    }
}

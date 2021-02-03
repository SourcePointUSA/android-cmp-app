package com.sourcepoint.cmplibrary

import android.app.Activity
import com.sourcepoint.cmplibrary.creation.Builder
import com.sourcepoint.gdpr_cmplibrary.PrivacyManagerTab
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test

class BuilderTest {

    @MockK
    private lateinit var context: Activity

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
    }

    @Test(expected = RuntimeException::class)
    fun `A context object is MISSING an exception is THROWN`() {
        Builder()
            .setAccountId(1)
            .setPmId("1")
            // .setContext(context)
            .setPropertyId(1)
            .setPropertyName("a")
            .setPrivacyManagerTab(PrivacyManagerTab.FEATURES)
            .build()
    }

    @Test(expected = RuntimeException::class)
    fun `A pmId is MISSING an exception is THROWN`() {
        Builder()
            .setAccountId(1)
            // .setPmId("1")
            .setContext(context)
            .setPropertyId(1)
            .setPropertyName("a")
            .setPrivacyManagerTab(PrivacyManagerTab.FEATURES)
            .build()
    }

    @Test(expected = RuntimeException::class)
    fun `An accountId is MISSING an exception is THROWN`() {
        Builder()
            // .setAccountId(1)
            .setPmId("1")
            .setContext(context)
            .setPropertyId(1)
            .setPropertyName("a")
            .setPrivacyManagerTab(PrivacyManagerTab.FEATURES)
            .build()
    }

    @Test(expected = RuntimeException::class)
    fun `A propertyId is MISSING an exception is THROWN`() {
        Builder()
            .setAccountId(1)
            .setPmId("1")
            .setContext(context)
            // .setPropertyId(1)
            .setPropertyName("a")
            .setPrivacyManagerTab(PrivacyManagerTab.FEATURES)
            .build()
    }

    @Test(expected = RuntimeException::class)
    fun `A propertyName is MISSING an exception is THROWN`() {
        Builder()
            .setAccountId(1)
            .setPmId("1")
            .setContext(context)
            .setPropertyId(1)
            // .setPropertyName("a")
            .setPrivacyManagerTab(PrivacyManagerTab.FEATURES)
            .build()
    }

    @Test
    fun `A privacyManagerTab is MISSING NOTHING happened`() {
        Builder()
            .setAccountId(1)
            .setPmId("1")
            .setContext(context)
            .setPropertyId(1)
            .setPropertyName("a")
            // .setPrivacyManagerTab(PrivacyManagerTab.FEATURES)
            .build()
    }
}

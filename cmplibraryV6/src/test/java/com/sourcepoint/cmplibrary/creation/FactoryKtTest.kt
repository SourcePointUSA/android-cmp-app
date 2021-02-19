package com.sourcepoint.cmplibrary.creation

import android.app.Activity
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.model.PrivacyManagerTabK
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class FactoryKtTest {

    @MockK
    private lateinit var context: Activity

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
    }

    @Test
    fun `CREATE a new instance of ConsentLib`() {
        makeConsentLib(
            gdpr = null,
            ccpa = null,
            privacyManagerTab = PrivacyManagerTabK.FEATURES,
            context = context,
        ).assertNotNull()
        makeConsentLib(
            gdpr = mockk(),
            ccpa = mockk(),
            privacyManagerTab = PrivacyManagerTabK.FEATURES,
            context = context,
        ).assertNotNull()
        makeConsentLib(
            gdpr = mockk(),
            ccpa = null,
            privacyManagerTab = PrivacyManagerTabK.FEATURES,
            context = context,
        ).assertNotNull()
        makeConsentLib(
            gdpr = null,
            ccpa = mockk(),
            privacyManagerTab = PrivacyManagerTabK.FEATURES,
            context = context,
        ).assertNotNull()
    }
}

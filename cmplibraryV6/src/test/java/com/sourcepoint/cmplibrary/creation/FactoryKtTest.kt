package com.sourcepoint.cmplibrary.creation

import android.app.Activity
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.gdpr_cmplibrary.PrivacyManagerTab
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
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
        val sut = makeConsentLib(
            accountId = 1,
            propertyId = 1,
            privacyManagerTab = PrivacyManagerTab.FEATURES,
            pmId = "1",
            context = context,
            propertyName = "test"
        )
        sut.assertNotNull()
    }

}
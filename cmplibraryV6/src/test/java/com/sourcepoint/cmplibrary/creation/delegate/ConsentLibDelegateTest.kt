package com.sourcepoint.cmplibrary.creation.delegate

import android.app.Activity
import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.model.Campaign
import com.sourcepoint.gdpr_cmplibrary.PrivacyManagerTab
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class ConsentLibDelegateTest {

    private val campaign = Campaign(
        22,
        7639,
        "tcfv2.mobile.webview",
        "122058"
    )

    @MockK
    private lateinit var context: Activity

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
    }

    @Test
    fun `VERIFY that the delegate with a tab is not null`() {

        val delegate = ConsentLibDelegate(PrivacyManagerTab.FEATURES, campaign)
        val sut1 = delegate.getValue(context, mockk())
        val sut2 = delegate.getValue(context, mockk())

        sut1.assertNotNull()
        sut2.assertNotNull()
        sut2.assertEquals(sut1)
    }

    @Test
    fun `VERIFY that the delegate with a tab is null`() {

        val delegate = ConsentLibDelegate(campaign = campaign)
        val sut1 = delegate.getValue(context, mockk())
        val sut2 = delegate.getValue(context, mockk())

        sut1.assertNotNull()
        sut2.assertNotNull()
        sut2.assertEquals(sut1)
    }
}

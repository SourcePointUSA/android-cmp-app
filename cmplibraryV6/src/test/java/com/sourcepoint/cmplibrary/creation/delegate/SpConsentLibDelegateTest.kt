package com.sourcepoint.cmplibrary.creation.delegate

import android.app.Activity
import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.model.PrivacyManagerTabK
import com.sourcepoint.cmplibrary.model.SpProperty
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class SpConsentLibDelegateTest {

    private val campaign = SpProperty(
        22,
        "tcfv2.mobile.webview",
        Env.STAGE,
        "122058",
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

        val delegate = ConsentLibDelegate(campaign, PrivacyManagerTabK.FEATURES)
        val sut1 = delegate.getValue(context, mockk())
        val sut2 = delegate.getValue(context, mockk())

        sut1.assertNotNull()
        sut2.assertNotNull()
        sut2.assertEquals(sut1)
    }

    @Test
    fun `VERIFY that the delegate with a tab is null`() {

        val delegate = ConsentLibDelegate(campaign)
        val sut1 = delegate.getValue(context, mockk())
        val sut2 = delegate.getValue(context, mockk())

        sut1.assertNotNull()
        sut2.assertNotNull()
        sut2.assertEquals(sut1)
    }
}

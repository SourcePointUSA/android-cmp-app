package com.sourcepoint.cmplibrary.creation.delegate

import android.app.Activity
import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class SpConsentLibDelegateTest {

    private val campaign = SpConfig(
        22,
        "tcfv2.mobile.webview",
        emptyArray(),

    )

    @MockK
    private lateinit var context: Activity

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
    }

    @Test
    fun `VERIFY that the delegate with a tab is not null`() {

        val delegate = ConsentLibDelegate(campaign, PMTab.FEATURES)
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

package com.sourcepoint.cmplibrary.creation

import android.app.Activity
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.model.SpConfig
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
        makeConsentLib(
            spConfig = SpConfig(22, "asfa", emptyArray(), ),
            context = context,
        ).assertNotNull()
    }
}

package com.sourcepoint.cmplibrary.creation

import android.app.Activity
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
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
            spConfig = SpConfig(
                22,
                "asfa",
                emptyList(),
                MessageLanguage.ENGLISH,
                propertyId = 1234,
                messageTimeout = 3000,
                clientSideOnly = false
            ),
            activity = context,
            spClient = mockk()
        ).assertNotNull()
    }
}

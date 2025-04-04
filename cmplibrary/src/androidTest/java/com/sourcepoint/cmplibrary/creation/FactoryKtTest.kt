package com.sourcepoint.cmplibrary.creation

import com.example.uitestutil.assertNotNull
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import io.mockk.mockk
import org.junit.Test

class FactoryKtTest {
    @Test
    fun makeConsentLibWorks() {
        makeConsentLib(
            spConfig = SpConfig(
                accountId = 22,
                propertyName = "foo",
                propertyId = 1234,
                campaigns = emptyList(),
                messageLanguage = MessageLanguage.ENGLISH,
                messageTimeout = 3000,
            ),
            activity = mockk(relaxed = true),
            spClient = mockk(relaxed = true)
        ).assertNotNull()
    }
}

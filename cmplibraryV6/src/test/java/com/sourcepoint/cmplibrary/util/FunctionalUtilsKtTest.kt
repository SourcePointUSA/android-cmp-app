package com.sourcepoint.cmplibrary.util

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.gdpr_cmplibrary.exception.ConsentLibExceptionK
import com.sourcepoint.gdpr_cmplibrary.exception.GenericSDKException
import com.sourcepoint.gdpr_cmplibrary.exception.MissingClientException
import org.junit.Test
import java.io.IOException

class FunctionalUtilsKtTest {

    @Test
    fun `GIVEN a MissingClientException RETURN the same exception`() {
        val sut = check { throw MissingClientException(description = "test", isConsumed = true) }
        (sut as Either.Left).t.run {
            (this as ConsentLibExceptionK)
            message.assertEquals("test")
            description.assertEquals("test")
            isConsumed.assertEquals(true)
        }
    }

    @Test
    fun `GIVEN a RuntimeException RETURN a GenericSDKException`() {
        val sut = check { throw IOException("test") }
        (sut as Either.Left).t.run {
            (this as GenericSDKException)
            message.assertEquals("test")
            description.assertEquals("test")
            isConsumed.assertEquals(false)
        }
    }
}

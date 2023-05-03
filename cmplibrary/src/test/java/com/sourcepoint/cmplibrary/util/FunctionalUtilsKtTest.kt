package com.sourcepoint.cmplibrary.util

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.exception.* // ktlint-disable
import org.junit.Test
import java.io.IOException
import java.io.InterruptedIOException

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

    @Test
    fun `GIVEN a InterruptedIOException with meta-data message RETURN a ConnectionTimeoutException`() {
        val sut = check(NetworkCallErrorsCode.META_DATA) { throw InterruptedIOException() }
        (sut as Either.Left).t.run {
            (this as ConnectionTimeoutException)
            message.assertEquals(TIMEOUT_MESSAGE)
            description.assertEquals(TIMEOUT_MESSAGE)
            code.errorCode.assertEquals(CodeList.CONNECTION_TIMEOUT.errorCode + NetworkCallErrorsCode.META_DATA.code)
            isConsumed.assertEquals(false)
        }
    }
}

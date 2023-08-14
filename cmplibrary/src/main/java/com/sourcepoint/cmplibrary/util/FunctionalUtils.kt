package com.sourcepoint.cmplibrary.util

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.exception.ApiRequestPostfix
import com.sourcepoint.cmplibrary.exception.ConnectionTimeoutException
import com.sourcepoint.cmplibrary.exception.ConsentLibExceptionK
import com.sourcepoint.cmplibrary.exception.GenericSDKException
import com.sourcepoint.cmplibrary.exception.UnableToParseResponseException
import kotlinx.serialization.SerializationException
import java.io.InterruptedIOException

/**
 * This method execute the `block` closure and
 * based on the result return an obj success or a failure
 *
 * @param block closure to execute
 * @return an either object containing the result
 */
internal fun <E> check(block: () -> E): Either<E> {
    return try {
        val res = block.invoke()
        Either.Right(res)
    } catch (e: Exception) {
        Either.Left(e.toConsentLibException())
    }
}

internal fun <E> check(
    requestPostfix: ApiRequestPostfix? = null,
    block: () -> E,
): Either<E> {
    return try {
        val res = block.invoke()
        Either.Right(res)
    } catch (e: Exception) {
        Either.Left(e.toConsentLibException(requestPostfix = requestPostfix))
    }
}

internal fun Throwable.toConsentLibException(
    requestPostfix: ApiRequestPostfix? = null
): ConsentLibExceptionK = when (this) {
    is ConsentLibExceptionK -> this
    is SerializationException -> UnableToParseResponseException(
        cause = this,
        description = this.message ?: "${this::class.java}",
        apiRequestPostfix = requestPostfix?.apiPostfix ?: "",
    )
    is InterruptedIOException -> ConnectionTimeoutException(
        cause = this,
        networkCode = requestPostfix?.apiPostfix ?: ""
    )
    else -> GenericSDKException(
        cause = this,
        description = this.message ?: "${this::class.java}"
    )
}

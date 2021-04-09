package com.sourcepoint.cmplibrary.util

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.exception.ConsentLibExceptionK
import com.sourcepoint.cmplibrary.exception.GenericSDKException

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

fun Throwable.toConsentLibException(): ConsentLibExceptionK {
    return when (this) {
        is ConsentLibExceptionK -> this
        else -> GenericSDKException(cause = this, description = this.message ?: "${this::class.java}")
    }
}

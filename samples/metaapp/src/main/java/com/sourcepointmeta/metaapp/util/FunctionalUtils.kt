package com.sourcepointmeta.metaapp.util

import com.sourcepointmeta.metaapp.core.Either
import com.sourcepointmeta.metaapp.core.MetaException

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
    } catch (e: Throwable) {
        e as? MetaException
        Either.Left(e)
    }
}

package com.sourcepointmeta.metaapp.core

import com.sourcepointmeta.metaapp.core.Either.Left
import com.sourcepointmeta.metaapp.core.Either.Right

/**
 * Either pattern implementation
 */
internal sealed class Either<out R> {
    data class Right<R>(val r: R) : Either<R>()
    data class Left(val t: Throwable) : Either<Nothing>()
}

/**
 * This extension is an implementation of the `flatMap` pattern
 *
 * @param f closure to execute which receive in input the content
 * of the either obj and has to give an Either object as a computational result
 *
 * @return an either object containing the transformation
 */
internal inline fun <B, C> Either<B>.flatMap(f: (B) -> Either<C>): Either<C> =
    this.let {
        when (it) {
            is Right -> f(it.r)
            is Left -> it
        }
    }

/**
 * This extension is an implementation of the `map` pattern
 *
 * @param f closure to execute which receive in input the content
 * of the either obj and has to give generic object as a computational result
 *
 * @return an either object containing the transformation
 */
internal inline fun <B, C> Either<B>.map(f: (B) -> C): Either<C> =
    flatMap { Right(f(it)) }

/**
 * This extension execute the closure if invoked on a [Either.Left] object
 */
internal inline fun <B> Either<B>.executeOnLeft(block: (Throwable) -> Unit): Either<B> = apply {
    when (this) {
        is Right -> { /** do nothing */ }
        is Left -> block(this.t)
    }
}

/**
 * This extension execute the closure if invoked on a [Either.Right] object
 */
internal inline fun <B> Either<B>.executeOnRight(block: (B) -> Unit): Either<B> = apply {
    when (this) {
        is Right -> block(this.r)
        is Left -> { /** do nothing */ }
    }
}

/**
 * Applies `ifLeft` if this is a [Left] or `ifRight` if this is a [Right].
 *
 * @param ifLeft the function to apply if this is a [Left]
 * @param ifRight the function to apply if this is a [Right]
 * @return the results of applying the function
 */
internal inline fun <B, C> Either<B>.fold(ifLeft: (Throwable) -> C, ifRight: (B) -> C): C = when (this) {
    is Right -> ifRight(r)
    is Left -> ifLeft(t)
}

/**
 * @return the results if applied on a [Right] instance or null if applied on a [Left]
 */
internal inline fun <B> Either<B>.getOrNull(): B? = when (this) {
    is Right -> r
    is Left -> null
}

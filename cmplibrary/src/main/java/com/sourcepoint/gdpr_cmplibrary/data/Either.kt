package com.sourcepoint.gdpr_cmplibrary.data

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
            is Either.Right -> f(it.r)
            is Either.Left -> it
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
    flatMap { Either.Right(f(it)) }

/**
 * This extension execute the closure if invoked on a [com.sourcepoint.gdpr_cmplibrary.data.Either.Left] object
 */
internal inline fun <B> Either<B>.executeOnLeft(block: (Throwable) -> Unit): Either<B> = apply {
    when(this){
        is Either.Right -> { /** do nothing */ }
        is Either.Left -> block(this.t)
    }
}
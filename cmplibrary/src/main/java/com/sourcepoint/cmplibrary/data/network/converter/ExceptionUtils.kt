package com.sourcepoint.cmplibrary.data.network.converter

//import com.sourcepoint.cmplibrary.exception.InvalidResponseWebMessageException
import java.lang.RuntimeException

/**
 * Util method to throws a [ConsentLibExceptionK] with a custom message
 * @param param name of the null object
 */
internal fun failParam(param: String): Nothing {
    throw Exception()
}

internal fun fail(message: String): Nothing {
    throw Exception()
}

internal fun fail(message: String, throwable: Throwable): Nothing {
    throw Exception()
}

internal fun genericFail(message: String): Nothing {
    throw RuntimeException(message)
}

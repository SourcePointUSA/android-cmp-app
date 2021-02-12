package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.exception.InvalidResponseWebMessageException

/**
 * Util method to throws a [ConsentLibExceptionK] with a custom message
 * @param param name of the null object
 */
internal fun failParam(param: String): Nothing {
    throw InvalidResponseWebMessageException(description = "$param object is null")
}

internal fun fail(message: String): Nothing {
    throw InvalidResponseWebMessageException(description = message)
}

package com.sourcepoint.cmplibrary.data.network.util

import com.sourcepoint.cmplibrary.exception.ConsentLibExceptionK
import com.sourcepoint.cmplibrary.exception.InvalidResponseWebMessageException
import okhttp3.Response

internal fun Response.toException(): ConsentLibExceptionK {
    // TODO add the logic to map the
    return InvalidResponseWebMessageException(null, "")
}

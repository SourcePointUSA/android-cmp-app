package com.sourcepoint.gdpr_cmplibrary.data.network

import com.sourcepoint.gdpr_cmplibrary.exception.ConsentLibExceptionK
import com.sourcepoint.gdpr_cmplibrary.exception.InvalidResponseWebMessageException
import okhttp3.Response

internal fun Response.toException() : ConsentLibExceptionK {
    // TODO add the logic to map the
    return InvalidResponseWebMessageException(null, "")
}
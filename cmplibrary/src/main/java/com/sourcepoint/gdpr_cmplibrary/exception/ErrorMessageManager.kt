package com.sourcepoint.gdpr_cmplibrary.exception

internal interface ErrorMessageManager {
    fun build(exception: ConsentLibExceptionK): String
}
package com.sourcepoint.gdpr_cmplibrary

import com.sourcepoint.gdpr_cmplibrary.exception.ConsentLibExceptionK
import com.sourcepoint.gdpr_cmplibrary.exception.Logger

internal class MockLogger : Logger {
    override fun error(e: ConsentLibExceptionK) {}
}
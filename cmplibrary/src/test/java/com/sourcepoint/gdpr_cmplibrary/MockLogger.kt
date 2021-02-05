package com.sourcepoint.gdpr_cmplibrary

import com.sourcepoint.gdpr_cmplibrary.exception.ConsentLibExceptionK
import com.sourcepoint.gdpr_cmplibrary.exception.Logger

internal class MockLogger : Logger {
    override fun error(e: ConsentLibExceptionK) {}
    override fun i(tag: String, msg: String) { }
    override fun d(tag: String, msg: String) { }
    override fun v(tag: String, msg: String) { }
}
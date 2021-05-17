package com.sourcepoint.cmplibrary.consent

interface CustomConsentClient {
    fun transferCustomConsentToUnitySide(spCustomConsentsJSON: String)
}

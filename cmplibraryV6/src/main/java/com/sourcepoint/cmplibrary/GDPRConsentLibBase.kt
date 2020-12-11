package com.sourcepoint.cmplibrary

import com.sourcepoint.gdpr_cmplibrary.NativeMessage

interface GDPRConsentLibBase {
    fun loadMessage()
    fun loadMessage(nativeMessage : NativeMessage)
    fun loadPrivacyManager()
}
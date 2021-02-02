package com.sourcepoint.gdpr_cmplibrary

interface NativeMessageClient {
    fun onClickAcceptAll(ca: ConsentAction)
    fun onClickRejectAll(ca: ConsentAction)
    fun onPmDismiss(ca: ConsentAction)
    fun onClickShowOptions(ca: ConsentAction)
    fun onClickCancel(ca: ConsentAction)
    fun onDefaultAction(ca: ConsentAction)
}
package com.sourcepointmeta.metaapp.core

class MetaException(val uiCode: UIErrorCode, val errorMessage: String) : Throwable(errorMessage)

sealed class UIErrorCode {
    object PropertyName : UIErrorCode()
    object AccountId : UIErrorCode()
    object MessageType : UIErrorCode()
    object MessageLanguage : UIErrorCode()
    object GdprPmId : UIErrorCode()
    object CcpaPmId : UIErrorCode()
    object AuthId : UIErrorCode()
    object PmTab : UIErrorCode()
    object NoUiError : UIErrorCode()
}

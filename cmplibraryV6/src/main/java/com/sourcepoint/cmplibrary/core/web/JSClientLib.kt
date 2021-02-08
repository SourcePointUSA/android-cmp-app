package com.sourcepoint.cmplibrary.core.web

import android.webkit.WebView

internal interface JSClientLib : JSReceiver {
    fun onConsentUIReady(isFromPM: Boolean, wv: WebView)
    companion object
}

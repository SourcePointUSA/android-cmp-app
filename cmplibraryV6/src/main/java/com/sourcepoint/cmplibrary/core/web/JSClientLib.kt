package com.sourcepoint.cmplibrary.core.web

import android.view.View

internal interface JSClientLib : JSReceiver {
    fun onConsentUIReady(isFromPM: Boolean, wv : View)
    companion object
}

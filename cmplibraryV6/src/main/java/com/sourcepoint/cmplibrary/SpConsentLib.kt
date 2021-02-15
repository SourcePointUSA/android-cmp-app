package com.sourcepoint.cmplibrary

import android.view.View
import com.sourcepoint.cmplibrary.core.layout.NativeMessage
import com.sourcepoint.cmplibrary.core.layout.NativeMessageK

interface SpConsentLib {

    var spClient: SpClient?

    fun loadMessage()
    fun loadMessage(authId: String)
    fun loadMessage(nativeMessage: NativeMessage)
    fun loadMessage(nativeMessage: NativeMessageK)

    fun loadGDPRPrivacyManager()
    fun loadCCPAPrivacyManager()

    fun showView(view: View)
    fun removeView(view: View)

    fun dispose()
}

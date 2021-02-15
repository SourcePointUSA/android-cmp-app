package com.sourcepoint.cmplibrary

import android.view.View
import com.sourcepoint.cmplibrary.core.layout.nat.NativeMessageAbstract

interface SpConsentLib {

    var spClient: SpClient?

    fun loadMessage()
    fun loadMessage(authId: String)
    fun loadMessage(nativeMessage: NativeMessageAbstract)

    fun loadGDPRPrivacyManager()
    fun loadCCPAPrivacyManager()

    fun showView(view: View)
    fun removeView(view: View)

    fun dispose()
}

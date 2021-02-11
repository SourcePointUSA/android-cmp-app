package com.sourcepoint.cmplibrary

import android.view.View
import com.sourcepoint.cmplibrary.core.layout.NativeMessage

interface ConsentLib {

    var spClient: SpClient?

    fun loadMessage()
    fun loadMessage(authId: String)
    fun loadMessage(nativeMessage: NativeMessage)

    fun loadGDPRPrivacyManager()
    fun loadCCPAPrivacyManager()

    fun showView(view: View)
    fun removeView(view: View)

    fun dispose()
}

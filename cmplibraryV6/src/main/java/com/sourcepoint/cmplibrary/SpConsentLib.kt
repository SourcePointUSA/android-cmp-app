package com.sourcepoint.cmplibrary

import android.view.View
import com.sourcepoint.cmplibrary.core.layout.nat.NativeMessage
import com.sourcepoint.cmplibrary.model.PMTab

interface SpConsentLib {

    var spClient: SpClient?

    fun loadMessage()
    fun loadMessage(authId: String)
    fun loadMessage(nativeMessage: NativeMessage)

    fun loadGDPRPrivacyManager(pmId: String, pmTab: PMTab)
    fun loadCCPAPrivacyManager(pmId: String, pmTab: PMTab)

    fun showView(view: View)
    fun removeView(view: View)

    fun dispose()
}

package com.sourcepoint.cmplibrary.legislation.ccpa

import android.view.View
import com.sourcepoint.gdpr_cmplibrary.NativeMessage

internal class CCPAConsentLibImpl : CCPAConsentLib {

    override var spCcpaClient: SpCCPAClient? = null
    override fun loadMessage() { }
    override fun loadMessage(authId: String) { }
    override fun loadMessage(nativeMessage: NativeMessage) { }
    override fun loadMessage(authId: String, nativeMessage: NativeMessage) { }
    override fun loadPrivacyManager() { }
    override fun loadPrivacyManager(authId: String) { }
    override fun showView(view: View) { }
    override fun removeView(view: View) { }
}

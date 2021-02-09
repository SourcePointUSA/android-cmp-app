package com.sourcepoint.cmplibrary

import android.view.View
import com.sourcepoint.cmplibrary.core.layout.NativeMessage

interface ConsentLib {

    var spClient: SpClient?

    fun loadMessage()
    fun loadMessage(authId: String)
    fun loadMessage(nativeMessage: NativeMessage)
    fun loadMessage(authId: String, nativeMessage: NativeMessage)
    fun loadPrivacyManager()
    fun loadPrivacyManager(authId: String)

    fun showView(view: View)
    fun removeView(view: View)

    fun dispose()
}

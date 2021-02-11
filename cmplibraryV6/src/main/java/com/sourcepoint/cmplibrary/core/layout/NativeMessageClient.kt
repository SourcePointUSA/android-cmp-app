package com.sourcepoint.cmplibrary.core.layout

import android.view.View
import com.sourcepoint.cmplibrary.data.network.model.ConsentAction

interface NativeMessageClient {

    /**
     * onclick listener connected to the acceptAll button in the NativeMessage View
     */
    fun onClickAcceptAll(view: View, ca: ConsentAction)
    /**
     * onclick listener connected to the RejectAll button in the NativeMessage View
     */
    fun onClickRejectAll(view: View, ca: ConsentAction)

    fun onPmDismiss(view: View, ca: ConsentAction)
    /**
     * onclick listener connected to the ShowOptions button in the NativeMessage View
     */
    fun onClickShowOptions(view: View, ca: ConsentAction)
    /**
     * onclick listener connected to the Cancel button in the NativeMessage View
     */
    fun onClickCancel(view: View, ca: ConsentAction)

    fun onDefaultAction(view: View, ca: ConsentAction)
}

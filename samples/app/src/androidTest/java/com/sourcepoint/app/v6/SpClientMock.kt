package com.sourcepoint.app.v6

import android.view.View
import com.sourcepoint.cmplibrary.NativeMessageController
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.core.nativemessage.MessageStructure
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import org.json.JSONObject

class SpClientMock : SpClient {

    val consentList = mutableListOf<SPConsents>()

    override fun onUIReady(view: View) { }

    override fun onNativeMessageReady(message: MessageStructure, messageController: NativeMessageController) { }

    override fun onMessageReady(message: JSONObject) {  }

    override fun onAction(view: View, consentAction: ConsentAction): ConsentAction = consentAction

    override fun onUIFinished(view: View) { }

    override fun onConsentReady(consent: SPConsents) { }

    override fun onError(error: Throwable) { }

    override fun onSpFinished(sPConsents: SPConsents) { consentList.add(sPConsents) }

    override fun onNoIntentActivitiesFound(url: String) { }
}
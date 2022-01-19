package com.sourcepoint.app.v6

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sourcepoint.cmplibrary.NativeMessageController
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.core.nativemessage.MessageStructure
import com.sourcepoint.cmplibrary.creation.delegate.spConsentLibLazy
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.util.clearAllData
import kotlinx.android.synthetic.main.only_gdpr.*
import org.json.JSONObject

class OnlyGdprKotlin : AppCompatActivity() {

    companion object{
        const val propName = "mobile.multicampaign.demo"
        const val pmId = "488393"
        const val accId = 22
    }

    private val spConsentLib by spConsentLibLazy {
        activity = this@OnlyGdprKotlin
        spClient = LocalClient()
        config {
            accountId = accId
            propertyName = propName
            messLanguage = MessageLanguage.ENGLISH
            messageTimeout = 3000
            +(CampaignType.GDPR)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.only_gdpr)

        review_consents_gdpr.setOnClickListener {
            spConsentLib.loadPrivacyManager(
                pmId,
                PMTab.PURPOSES,
                CampaignType.GDPR
            )
        }

        clear_all.setOnClickListener { clearAllData(this) }

        propname_tv.text = propName
        pmid_tv.text = pmId

    }

    override fun onResume() {
        super.onResume()
        spConsentLib.loadMessage()
    }

    override fun onDestroy() {
        super.onDestroy()
        spConsentLib.dispose()
    }

    internal inner class LocalClient : SpClient {

        override fun onNoIntentActivitiesFound(url: String) {
            Log.i(this::class.java.name, "onNoIntentActivitiesFound")
        }

        override fun onUIReady(view: View) {
            spConsentLib.showView(view)
            Log.i(this::class.java.name, "onUIReady")
        }

        override fun onAction(view: View, consentAction: ConsentAction): ConsentAction {
            Log.i(this::class.java.name, "ActionType: ${consentAction.actionType}")
            return consentAction
        }

        override fun onNativeMessageReady(message: MessageStructure, messageController: NativeMessageController) {

        }

        override fun onUIFinished(view: View) {
            spConsentLib.removeView(view)
            Log.i(this::class.java.name, "onUIFinished")
        }

        override fun onError(error: Throwable) {
            error.printStackTrace()
        }

        override fun onSpFinished(sPConsents: SPConsents) {
            Log.i(this::class.java.name, "onSpFinish: $sPConsents")
            Log.i(this::class.java.name, "==================== onSpFinish ==================")
        }

        override fun onConsentReady(consent: SPConsents) {
            Log.i(this::class.java.name, "onConsentReady: $consent")
        }

        override fun onMessageReady(message: JSONObject) {}
    }
}
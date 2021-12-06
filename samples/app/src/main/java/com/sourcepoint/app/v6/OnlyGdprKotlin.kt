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

    private val spConsentLib by spConsentLibLazy {
        activity = this@OnlyGdprKotlin
        spClient = LocalClient()
        config {
            accountId = 22
            propertyName = "mobile.multicampaign.demo"
            messLanguage = MessageLanguage.ENGLISH
            +(CampaignType.GDPR)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.only_gdpr)

        review_consents_gdpr.setOnClickListener {
            spConsentLib.loadPrivacyManager(
                "488393",
                PMTab.PURPOSES,
                CampaignType.GDPR
            )
        }

        clear_all.setOnClickListener { clearAllData(this) }

        custom_consent.setOnClickListener {
            spConsentLib.customConsentGDPR(
                vendors = listOf("5ff4d000a228633ac048be41"),
                categories = listOf("608bad95d08d3112188e0e29", "608bad95d08d3112188e0e2f"),
                legIntCategories = emptyList(),
                success = { spCustomConsents -> println("custom consent: [$spCustomConsents]") }
            )
        }
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

        override fun onUIReady(view: View) {
            spConsentLib.showView(view)
        }

        override fun onMessageReady(message: JSONObject) {
        }

        override fun onNativeMessageReady(message: MessageStructure, messageController: NativeMessageController) {
        }

        override fun onUIFinished(view: View) {
            spConsentLib.removeView(view)
        }

        override fun onError(error: Throwable) {
            error.printStackTrace()
        }

        override fun onConsentReady(consent: SPConsents) {
            Log.i(this::class.java.name, "onConsentReady: $consent")
        }

        override fun onSpFinished(sPConsents: SPConsents) {
        }

        override fun onAction(view: View, consentAction: ConsentAction): ConsentAction {
            Log.i(this::class.java.name, "ActionType: $consentAction")
            return consentAction
        }

        override fun onNoIntentActivitiesFound(url: String) {
            Log.i(this::class.java.name, "No activities found for url: $url")
        }
    }
}
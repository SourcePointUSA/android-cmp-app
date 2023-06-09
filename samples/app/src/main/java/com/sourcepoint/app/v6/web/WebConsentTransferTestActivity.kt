package com.sourcepoint.app.v6.web

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.sourcepoint.app.v6.R
import com.sourcepoint.cmplibrary.NativeMessageController
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.core.nativemessage.MessageStructure
import com.sourcepoint.cmplibrary.creation.delegate.spConsentLibLazy
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.util.extensions.preloadConsent
import kotlinx.android.synthetic.main.activity_web_consent_transfer_test.*
import org.json.JSONObject

class WebConsentTransferTestActivity : AppCompatActivity() {

    private val consentWebViewClient = object : WebViewClient() {
        // TODO implement error handling
    }

    private val consentWebChromeClient = object : WebChromeClient() {
        // TODO implement error handling
    }

    private val spConsentLib by spConsentLibLazy {
        activity = this@WebConsentTransferTestActivity
        spClient = LocalClient()
        config {
            accountId = 22
            propertyId = 16893
            propertyName = "mobile.multicampaign.demo"
            messLanguage = MessageLanguage.ENGLISH
            messageTimeout = 3000
            +(CampaignType.GDPR)
            +(CampaignType.CCPA)
        }
    }

    private var sourcePointConsent: SPConsents? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_consent_transfer_test)
        initConsentWebView()
        to_web_view_consent_action.setOnClickListener { transferConsent() }
    }

    override fun onResume() {
        super.onResume()
        spConsentLib.loadMessage()
    }

    override fun onDestroy() {
        super.onDestroy()
        spConsentLib.dispose()
    }

    private fun initConsentWebView() {
        consent_transfer_web_view.apply {
            webViewClient = consentWebViewClient
            webChromeClient = consentWebChromeClient
            settings.javaScriptEnabled = true
            loadUrl(CONSENT_TEST_URL)
        }
    }

    private fun transferConsent() {
        sourcePointConsent?.let { consent_transfer_web_view.preloadConsent(it) }
    }

    private fun processConsentResponse(sPConsents: SPConsents) {
        sourcePointConsent = sPConsents
        runOnUiThread {
            ccpa_uuid_value_text_view.text = sPConsents.ccpa?.consent?.uuid ?: "NULL"
            gdpr_uuid_value_text_view.text = sPConsents.gdpr?.consent?.uuid ?: "NULL"
            to_web_view_consent_action.isEnabled = true
        }
    }

    /**
     * Local client class
     */
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
            processConsentResponse(sPConsents)
            Log.i(this::class.java.name, "onSpFinish: $sPConsents")
            Log.i(this::class.java.name, "==================== onSpFinish ==================")
        }

        override fun onConsentReady(consent: SPConsents) {
            Log.i(this::class.java.name, "onConsentReady: $consent")
        }

        override fun onMessageReady(message: JSONObject) {}
    }

    companion object {
        private const val CONSENT_TEST_URL = "https://sourcepointusa.github.io/sdks-auth-consent-test-page/?_sp_pass_consent=true"
    }
}
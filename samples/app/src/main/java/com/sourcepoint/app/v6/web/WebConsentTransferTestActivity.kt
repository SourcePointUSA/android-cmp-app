package com.sourcepoint.app.v6.web

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.sourcepoint.app.v6.databinding.ActivityWebConsentTransferTestBinding
import com.sourcepoint.cmplibrary.NativeMessageController
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.core.nativemessage.MessageStructure
import com.sourcepoint.cmplibrary.creation.delegate.spConsentLibLazy
import com.sourcepoint.cmplibrary.data.network.util.CampaignType
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.util.clearAllData
import com.sourcepoint.cmplibrary.util.extensions.preloadConsent
import org.json.JSONObject

class WebConsentTransferTestActivity : AppCompatActivity() {

    private val consentWebViewClient = object : WebViewClient() { }
    private val consentWebChromeClient = object : WebChromeClient() { }

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

    private lateinit var binding: ActivityWebConsentTransferTestBinding
    private var sourcePointConsent: SPConsents? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebConsentTransferTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initConsentWebView()
        initOnClickListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        spConsentLib.dispose()
    }

    private fun initOnClickListeners() {
        binding.webConsentClearDataButton.setOnClickListener {
            clearAllData(this)
            binding.ccpaUuidValueTextView.text = ""
            binding.gdprUuidValueTextView.text = ""
            binding.toWebViewConsentAction.isEnabled = false
        }
        binding.webConsentRefreshButton.setOnClickListener { spConsentLib.loadMessage() }
        binding.toWebViewConsentAction.setOnClickListener { transferConsent() }
    }

    private fun initConsentWebView() {
        binding.consentTransferWebView.apply {
            webViewClient = consentWebViewClient
            webChromeClient = consentWebChromeClient
            settings.javaScriptEnabled = true
            loadUrl(CONSENT_TEST_URL)
        }
    }

    private fun transferConsent() {
        sourcePointConsent?.let { binding.consentTransferWebView.preloadConsent(it) }
    }

    private fun processConsentResponse(sPConsents: SPConsents) {
        sourcePointConsent = sPConsents
        runOnUiThread {
            binding.ccpaUuidValueTextView.text = sPConsents.ccpa?.consent?.uuid ?: "NULL"
            binding.gdprUuidValueTextView.text = sPConsents.gdpr?.consent?.uuid ?: "NULL"
            binding.toWebViewConsentAction.isEnabled = true
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

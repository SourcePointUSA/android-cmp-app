package com.sourcepoint.app.v6

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import com.sourcepoint.cmplibrary.NativeMessageController
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.core.nativemessage.MessageStructure
import com.sourcepoint.cmplibrary.creation.delegate.spConsentLibLazy
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.util.clearAllData
import com.sourcepoint.cmplibrary.util.extensions.preloadConsent
import org.json.JSONObject

class ConsentTransferActivity : AppCompatActivity() {

    private val consentWebViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            sourcePointConsent?.let { wvTransferPage.preloadConsent(it) }
        }
    }
    private val consentWebChromeClient = object : WebChromeClient() { }

    private lateinit var tvCcpaUuid: AppCompatTextView
    private lateinit var tvGdprUuid: AppCompatTextView
    private lateinit var btnClearData: AppCompatButton
    private lateinit var btnRefresh: AppCompatButton
    private lateinit var wvTransferPage: WebView

    private val spConsentLib by spConsentLibLazy {
        activity = this@ConsentTransferActivity
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
        setContentView(R.layout.activity_consent_transfer)
        initViews()
    }

    override fun onResume() {
        super.onResume()
        spConsentLib.loadMessage()
    }

    override fun onDestroy() {
        super.onDestroy()
        spConsentLib.dispose()
    }

    /**
     * Method to initialize views on the activity (should be modified when ViewBinding is integrated into the project)
     */
    private fun initViews() {

        // initialize text views
        tvCcpaUuid = findViewById(R.id.tv_ccpa_uuid_value)
        tvGdprUuid = findViewById(R.id.tv_gdpr_uuid_value)

        // initialize buttons
        btnRefresh = findViewById(R.id.btn_refresh)
        btnRefresh.setOnClickListener { spConsentLib.loadMessage() }
        btnClearData = findViewById(R.id.btn_clear_data)
        btnClearData.setOnClickListener {
            clearAllData(this)
            tvCcpaUuid.text = ""
            tvGdprUuid.text = ""
        }

        // initialize web view
        wvTransferPage = findViewById(R.id.wv_transfer_page)
        wvTransferPage.apply {
            webViewClient = consentWebViewClient
            webChromeClient = consentWebChromeClient
            settings.javaScriptEnabled = true
            loadUrl(CONSENT_TEST_URL)
        }
    }

    private fun processConsentResponse(sPConsents: SPConsents) {
        sourcePointConsent = sPConsents
        runOnUiThread {
            tvCcpaUuid.text = sPConsents.ccpa?.consent?.uuid ?: "NULL"
            tvGdprUuid.text = sPConsents.gdpr?.consent?.uuid ?: "NULL"
            wvTransferPage.preloadConsent(sPConsents)
        }
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
//        private const val CONSENT_TEST_URL = "https://www.cgmaurer.com/android/webview.html?_sp_pass_consent=true&accountId=155&propHref=www.cgmaurer.com&gdprCamp=true&ccpaCamp=false&usnatCamp=false&usnatCCPACamp=true"
    }
}
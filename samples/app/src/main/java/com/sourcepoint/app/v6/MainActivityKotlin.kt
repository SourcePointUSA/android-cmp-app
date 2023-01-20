package com.sourcepoint.app.v6

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sourcepoint.app.v6.core.DataProvider
import com.sourcepoint.cmplibrary.NativeMessageController
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.core.nativemessage.MessageStructure
import com.sourcepoint.cmplibrary.creation.delegate.spConsentLibLazy
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.exposed.OttPmType
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.util.clearAllData
import org.json.JSONObject
import org.koin.android.ext.android.inject

class MainActivityKotlin : AppCompatActivity() {

    companion object {
        private const val TAG = "**MainActivity"
        const val CLIENT_PREF_KEY = "client_pref_key"
        const val CLIENT_PREF_VAL = "client_pref_val"
    }

    private val dataProvider by inject<DataProvider>()
    private val spClientObserver: List<SpClient> by inject()

    private val spConsentLib by spConsentLibLazy {
        activity = this@MainActivityKotlin
        spClient = LocalClient()
        spConfig = dataProvider.spConfig
//        config {
//            accountId = 22
//            propertyName = "mobile.multicampaign.native.demo"
//            messLanguage = MessageLanguage.ENGLISH
//            +(CampaignType.GDPR)
//            +(CampaignType.CCPA to listOf(("location" to "US")))
//        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (dataProvider.resetAll) {
            clearAllData(this)
        }

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        sp.edit().putString(CLIENT_PREF_KEY, CLIENT_PREF_VAL).apply()

        gracefulDegradationTest(sp, dataProvider) // 4 testing

        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.review_consents_gdpr).setOnClickListener { _v: View? ->
            spConsentLib.loadPrivacyManager(
                dataProvider.gdprPmId,
                PMTab.PURPOSES,
                CampaignType.GDPR
            )
        }
        findViewById<View>(R.id.review_consents_ccpa).setOnClickListener { _v: View? ->
            spConsentLib.loadPrivacyManager(
                dataProvider.ccpaPmId,
                PMTab.PURPOSES,
                CampaignType.CCPA
            )
        }
        findViewById<View>(R.id.review_consents_ccpa_ott).setOnClickListener { _v: View? ->
            spConsentLib.loadPrivacyManager(
                dataProvider.ccpaPmId,
                CampaignType.CCPA,
                OttPmType.LEGACY_OTT
            )
        }
        findViewById<View>(R.id.clear_all).setOnClickListener { _v: View? -> clearAllData(this) }
        findViewById<View>(R.id.auth_id_activity).setOnClickListener { _v: View? ->
            startActivity(Intent(this, MainActivityAuthId::class.java))
        }
        findViewById<View>(R.id.custom_consent).setOnClickListener { _v: View? ->
            spConsentLib.customConsentGDPR(
                vendors = dataProvider.customVendorList,
                categories = dataProvider.customCategories,
                legIntCategories = emptyList(),
                success = { spCustomConsents -> println("custom consent: [$spCustomConsents]") }
            )
        }
        findViewById<View>(R.id.delete_custom_consent).setOnClickListener { _v: View? ->
            spConsentLib.deleteCustomConsentTo(
                vendors = dataProvider.customVendorList,
                categories = dataProvider.customCategories,
                legIntCategories = emptyList(),
                success = { spCustomConsents -> println("custom consent: [$spCustomConsents]") }
            )
        }
        findViewById<View>(R.id.consent_btn).setOnClickListener {
            spConsentLib.dispose()
            finish()
            startActivity(Intent(this, MainActivityViewConsent::class.java))
        }
    }

    private fun gracefulDegradationTest(sp: SharedPreferences, dataProvider: DataProvider) {
        if(dataProvider.storeStateGdpr){
            sp.edit().putString("sp.gdpr.consent.resp", "fake state").apply()
            sp.edit().putBoolean("sp.key.saved.consent", true).apply()
        }
        if(dataProvider.storeStateCcpa){
            sp.edit().putString("sp.ccpa.consent.resp", "fake state").apply()
            sp.edit().putBoolean("sp.key.saved.consent", true).apply()
        }
    }

    override fun onResume() {
        super.onResume()
        spConsentLib.loadMessage()
    }

    override fun onDestroy() {
        super.onDestroy()
        spConsentLib.dispose()
        Log.i(TAG, "onDestroy: disposed")
    }

    internal inner class LocalClient : SpClient {

        override fun onNativeMessageReady(
            message: MessageStructure,
            messageController: NativeMessageController
        ) {

        }

        override fun onMessageReady(message: JSONObject) {
        }

        override fun onError(error: Throwable) {
            spClientObserver.forEach { it.onError(error) }
            error.printStackTrace()
            Log.i(TAG, "onError: $error")
        }

        override fun onConsentReady(consent: SPConsents) {
            val grants = consent.gdpr?.consent?.grants
            grants?.forEach { grant ->
                val granted = grants[grant.key]?.granted
                val purposes = grants[grant.key]?.purposeGrants
                println("vendor: ${grant.key} - granted: $granted - purposes: $purposes")
            }
            spClientObserver.forEach { it.onConsentReady(consent) }
            Log.i(TAG, "onConsentReady: $consent")
        }

        override fun onUIFinished(view: View) {
            spClientObserver.forEach { it.onUIFinished(view) }
            spConsentLib.removeView(view)
            Log.i(TAG, "onUIFinished")
        }

        override fun onUIReady(view: View) {
            spClientObserver.forEach { it.onUIReady(view) }
            spConsentLib.showView(view)
            Log.i(TAG, "onUIReady")
        }

        override fun onAction(view: View, consentAction: ConsentAction): ConsentAction {
            spClientObserver.forEach { it.onAction(view, consentAction) }
            Log.i(TAG, "onAction ActionType: $consentAction")
            consentAction.pubData.put("pb_key", "pb_value")
            return consentAction
        }

        override fun onSpFinished(sPConsents: SPConsents) {
            spClientObserver.forEach { it.onSpFinished(sPConsents) }
            Log.i(TAG, "onSpFinish: $sPConsents")
            Log.i(TAG, "==================== onSpFinish ==================")
        }

        override fun onNoIntentActivitiesFound(url: String) {
            Log.i(TAG, "onNoIntentActivitiesFound: $url")
            spClientObserver.forEach { it.onNoIntentActivitiesFound(url) }
        }
    }
}

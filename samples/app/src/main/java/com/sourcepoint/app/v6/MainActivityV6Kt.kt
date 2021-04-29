package com.sourcepoint.app.v6

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sourcepoint.app.v6.core.DataProvider
import com.sourcepoint.cmplibrary.UnitySpClient
import com.sourcepoint.cmplibrary.creation.delegate.spConsentLibLazy
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.util.clearAllData
import org.json.JSONObject
import org.koin.android.ext.android.inject

class MainActivityV6Kt : AppCompatActivity() {

    private val dataProvider by inject<DataProvider>()

    private val spConsentLib2 by spConsentLibLazy {
        activity = this@MainActivityV6Kt
        spClient = LocalClient()
        privacyManagerTab = PMTab.FEATURES
        config {
            accountId = 22
            propertyName = "mobile.multicampaign.demo"
            +(CampaignType.CCPA to listOf(("location" to "US")))
            +(CampaignType.GDPR to listOf(("location" to "EU")))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.review_consents_gdpr).setOnClickListener { _v: View? ->
            spConsentLib2.loadPrivacyManager(
                "13111",
                PMTab.PURPOSES,
                CampaignType.GDPR
            )
        }
        findViewById<View>(R.id.review_consents_ccpa).setOnClickListener { _v: View? ->
            spConsentLib2.loadPrivacyManager(
                "14967",
                PMTab.PURPOSES,
                CampaignType.CCPA
            )
        }
        findViewById<View>(R.id.clear_all).setOnClickListener { _v: View? -> clearAllData(this) }
        findViewById<View>(R.id.auth_id_activity).setOnClickListener { _v: View? ->
//            startActivity(Intent(this, MainActivityAuthId::class.java))
            spConsentLib2.customConsentGDPR(
                consentUUID = "4ce617bd-adce-4355-8840-556544eae34b",
                propertyId = 4245,
                vendors = emptyList(),//listOf("5fbe6f090d88c7d28d765e1e"),
                categories = emptyList(),//listOf("60657acc9c97c400122f21f3"),
                legIntCategories = emptyList(),
                success = { spCustomConsents -> println("custom consent: [$spCustomConsents]") }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (!dataProvider.onlyPm) {
            spConsentLib2.loadMessage()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        spConsentLib2.dispose()
    }

    internal inner class LocalClient : UnitySpClient {
        override fun onMessageReady(message: JSONObject) {}
        override fun onError(error: Throwable) {
            error.printStackTrace()
        }

        override fun onConsentReady(consent: SPConsents) {
            Log.i(TAG, "onConsentReady: $consent")
        }

        override fun onConsentReady(consent: String) {
            Log.i(TAG, "onConsentReady: $consent")
        }

        override fun onUIFinished(view: View) {
            spConsentLib2.removeView(view)
        }

        override fun onUIReady(view: View) {
            spConsentLib2.showView(view)
        }

        override fun onAction(view: View, actionType: ActionType) {
            Log.i(TAG, "ActionType: $actionType")
        }
    }

    companion object {
        private const val TAG = "**MainActivity"
    }
}
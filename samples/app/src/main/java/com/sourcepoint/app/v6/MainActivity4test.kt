package com.sourcepoint.app.v6

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sourcepoint.app.v6.core.DataProvider
import com.sourcepoint.cmplibrary.UnitySpClient
import com.sourcepoint.cmplibrary.creation.makeConsentLib
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.util.clearAllData
import org.json.JSONObject
import org.koin.android.ext.android.inject

class MainActivity4test : AppCompatActivity() {

    private val dataProvider by inject<DataProvider>()

    private val spConsentLib2 by lazy {
        makeConsentLib(
            spConfig = dataProvider.spConfig,
            activity = this@MainActivity4test,
            messageLanguage = MessageLanguage.ENGLISH,
            spClient = LocalClient()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.review_consents_gdpr).setOnClickListener { _v: View? ->
            spConsentLib2.loadPrivacyManager(
                dataProvider.gdprPmId,
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
            startActivity(Intent(this, MainActivityAuthId::class.java))
        }
        findViewById<View>(R.id.custom_consent).setOnClickListener { _v: View? ->
            spConsentLib2.customConsentGDPR(
                vendors = emptyList(),
                categories = emptyList(),
                legIntCategories = emptyList(),//listOf("5fbe6f090d88c7d28d765e1e"),
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
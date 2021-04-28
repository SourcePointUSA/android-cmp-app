package com.sourcepoint.app.v6

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sourcepoint.app.v6.core.DataProvider
import com.sourcepoint.cmplibrary.UnitySpClient
import com.sourcepoint.cmplibrary.creation.SpConfigDataBuilder
import com.sourcepoint.cmplibrary.creation.spConsentLib
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.exposed.*
import com.sourcepoint.cmplibrary.util.clearAllData
import org.json.JSONObject
import org.koin.android.ext.android.inject

class MainActivityV6Kt : AppCompatActivity() {

    private val gdprCampaign = SpCampaign(
        CampaignType.GDPR,
        listOf(TargetingParam("location", "EU"))
    )
    private val ccpaCamapign = SpCampaign(
        CampaignType.CCPA,
        listOf(TargetingParam("location", "EU"))
    )
    private val spConfig = SpConfigDataBuilder()
        .addAccountId(22)
        .addPropertyName("mobile.multicampaign.demo")
        .addCampaign(CampaignType.GDPR)
        .addCampaign(CampaignType.CCPA)
        .build()

    //    private val spConsentLib by ConsentLibDelegate(spConfig, MessageLanguage.ENGLISH)

    private val dataProvider by inject<DataProvider>()

    private val spConsentLib2 by lazy {
        spConsentLib {
            activity = this@MainActivityV6Kt
            spClient = LocalClient()
            privacyManagerTab = PMTab.FEATURES
            config {
                accountId = 22
                propertyName = "mobile.multicampaign.demo"
                + (CampaignType.CCPA to listOf(("location" to "US")))
                + (CampaignType.GDPR to listOf(("location" to "EU")))
            }
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
        findViewById<View>(R.id.auth_id_activity).setOnClickListener { _v: View? -> startActivity(Intent(this, MainActivityAuthId::class.java)) }
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
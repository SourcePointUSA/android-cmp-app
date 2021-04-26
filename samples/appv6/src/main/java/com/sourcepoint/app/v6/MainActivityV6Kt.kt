package com.sourcepoint.app.v6

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sourcepoint.app.v6.core.DataProvider
import com.sourcepoint.cmplibrary.SpConsentLib
import com.sourcepoint.cmplibrary.UnitySpClient
import com.sourcepoint.cmplibrary.creation.SpConfigDataBuilder
import com.sourcepoint.cmplibrary.creation.makeConsentLib
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.model.exposed.SpCampaign
import com.sourcepoint.cmplibrary.model.exposed.TargetingParam
import com.sourcepoint.cmplibrary.util.clearAllData
import org.json.JSONObject
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent

class MainActivityV6Kt : AppCompatActivity() {

    private val gdprCampaign = SpCampaign(
        Legislation.GDPR,
        listOf(TargetingParam("location", "EU"))
    )
    private val ccpaCamapign = SpCampaign(
        Legislation.CCPA,
        listOf(TargetingParam("location", "EU"))
    )
    private val spConfig2 = SpConfigDataBuilder()
        .addAccountId(22)
        .addPropertyName("mobile.multicampaign.demo")
        .addCampaign(gdprCampaign)
        .addCampaign(ccpaCamapign)
        .build()
    private lateinit var spConsentLib: SpConsentLib

    private val dataProvider by inject<DataProvider>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        spConsentLib = makeConsentLib(spConfig2, this, MessageLanguage.ENGLISH)
        spConsentLib.spClient = LocalClient()
        findViewById<View>(R.id.review_consents_gdpr).setOnClickListener { _v: View? ->
            spConsentLib.loadPrivacyManager(
                "13111",
                PMTab.PURPOSES,
                Legislation.GDPR
            )
        }
        findViewById<View>(R.id.review_consents_ccpa).setOnClickListener { _v: View? ->
            spConsentLib.loadPrivacyManager(
                "14967",
                PMTab.PURPOSES,
                Legislation.CCPA
            )
        }
        findViewById<View>(R.id.clear_all).setOnClickListener { _v: View? -> clearAllData(this) }
        findViewById<View>(R.id.auth_id_activity).setOnClickListener { _v: View? -> startActivity(Intent(this, MainActivityAuthId::class.java)) }
    }

    override fun onResume() {
        super.onResume()
        if (!dataProvider.onlyPm) {
            dataProvider.authId?.let {
                spConsentLib.loadMessage(it)
            } ?: spConsentLib.loadMessage()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        spConsentLib.dispose()
    }

    internal inner class LocalClient : UnitySpClient {
        override fun onMessageReady(message: JSONObject) {}
        override fun onError(error: Throwable) {
            error.printStackTrace()
        }

        override fun onConsentReady(consent: SPConsents) {
            println("onConsentReady: $consent")
        }

        override fun onConsentReady(consent: String) {
            println("onConsentReady String: $consent")
        }

        override fun onUIFinished(view: View) {
            spConsentLib.removeView(view)
        }

        override fun onUIReady(view: View) {
            spConsentLib.showView(view)
        }

        override fun onAction(view: View, actionType: ActionType) {
            Log.i(TAG, "ActionType: $actionType")
        }
    }

    companion object {
        private const val TAG = "**MainActivity"
    }
}
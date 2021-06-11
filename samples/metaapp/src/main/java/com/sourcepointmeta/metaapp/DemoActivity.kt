package com.sourcepointmeta.metaapp

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.creation.delegate.spConsentLibLazy
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepoint.cmplibrary.util.clearAllData
import com.sourcepointmeta.metaapp.core.getOrNull
import com.sourcepointmeta.metaapp.data.localdatasource.LocalDataSource
import kotlinx.android.synthetic.main.activity_demo.*
import org.json.JSONObject
import org.koin.android.ext.android.inject

class DemoActivity : AppCompatActivity() {

    private val dataSource by inject<LocalDataSource>()

    private val config: SpConfig by lazy {
        intent.extras
            ?.getString("property_name")
            ?.let { dataSource.getSPConfig(it).getOrNull() }
            ?: throw RuntimeException("extra property_name param is null!!!")
    }

    private val property by lazy {
        val propName = intent.extras
            ?.getString("property_name") ?: ""
        dataSource.fetchPropertyByNameSync(propName)
    }

    private val gdprPmId by lazy { property.gdprPmId }
    private val ccpaPmId by lazy { property.ccpaPmId }
    private val authId by lazy { property.authId }

    private val spConsentLib by spConsentLibLazy {
        activity = this@DemoActivity
        spClient = LocalClient()
        spConfig = config
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clearAllData(this)
        setContentView(R.layout.activity_demo)

        campaign_name.text = config.propertyName

        review_consents_gdpr.setOnClickListener { _v: View? ->
            spConsentLib.loadPrivacyManager(
                gdprPmId?.toString() ?: "",
                PMTab.PURPOSES,
                CampaignType.GDPR
            )
        }

        config.campaigns.find { it.campaignType == CampaignType.CCPA }
            ?.let { review_consents_ccpa.visibility = View.VISIBLE } ?: kotlin.run {
            review_consents_ccpa.visibility = View.GONE
        }
        review_consents_ccpa.setOnClickListener { _v: View? ->
            spConsentLib.loadPrivacyManager(
                ccpaPmId?.toString() ?: "",
                PMTab.PURPOSES,
                CampaignType.CCPA
            )
        }
    }

    override fun onResume() {
        super.onResume()
        authId
            ?.let { spConsentLib.loadMessage(authId = it) }
            ?: run { spConsentLib.loadMessage() }

    }

    override fun onDestroy() {
        super.onDestroy()
        spConsentLib.dispose()
    }

    internal inner class LocalClient : SpClient {
        override fun onMessageReady(message: JSONObject) {}
        override fun onError(error: Throwable) {
            error.printStackTrace()
        }

        override fun onConsentReady(consent: SPConsents) {
            Log.i(this::class.java.name, "onConsentReady: $consent")
        }

        override fun onUIFinished(view: View) {
            spConsentLib.removeView(view)
        }

        override fun onUIReady(view: View) {
            spConsentLib.showView(view)
        }

        override fun onAction(view: View, actionType: ActionType) {
            Log.i(this::class.java.name, "ActionType: $actionType")
        }
    }
}
package com.sourcepoint.app.v6

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.sourcepoint.app.v6.core.DataProvider
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.util.campaignApplies
import com.sourcepoint.cmplibrary.util.userConsents
import kotlinx.android.synthetic.main.activity_main_consent.*
import org.koin.android.ext.android.inject

class MainActivityViewConsent : AppCompatActivity() {

    private val TAG = "MainActivityViewConsent"
    private val dataProvider by inject<DataProvider>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_consent)
        val consent = userConsents(this.applicationContext)
        val gdprApplies = campaignApplies(this, CampaignType.GDPR)
        val ccpaApplies = campaignApplies(this, CampaignType.CCPA)
        val euconsent = consent.gdpr?.consent?.euconsent ?: "NO DATA"
        val uuid = consent.gdpr?.consent?.uuid ?: "NO DATA"
        consent_uuid.text = euconsent
        gdpr_applies.text = gdprApplies.toString()
        Log.i(TAG, "uuid: $uuid")
        Log.i(TAG, "euconsent: $euconsent")
        Log.i(TAG, "gdpr applies: $gdprApplies")
    }
}
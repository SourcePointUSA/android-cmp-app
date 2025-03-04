package com.sourcepoint.app.v6

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.sourcepoint.app.v6.core.DataProvider
import com.sourcepoint.app.v6.databinding.ActivityMainConsentBinding
import com.sourcepoint.cmplibrary.data.network.util.CampaignType
import com.sourcepoint.cmplibrary.util.campaignApplies
import com.sourcepoint.cmplibrary.util.userConsents
import org.koin.android.ext.android.inject

class MainActivityViewConsent : AppCompatActivity() {

    private val TAG = "MainActivityViewConsent"
    private val dataProvider by inject<DataProvider>()

    private lateinit var binding: ActivityMainConsentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainConsentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val consent = userConsents(this.applicationContext)
        val gdprApplies = campaignApplies(this, CampaignType.GDPR)
        val ccpaApplies = campaignApplies(this, CampaignType.CCPA)
        val euconsent = consent.gdpr?.consent?.euconsent ?: "NO DATA"
        val uuid = consent.gdpr?.consent?.uuid ?: "NO DATA"

        binding.consentUuid.text = euconsent
        binding.gdprApplies.text = gdprApplies.toString()

        Log.i(TAG, "uuid: $uuid")
        Log.i(TAG, "euconsent: $euconsent")
        Log.i(TAG, "gdpr applies: $gdprApplies")
    }
}

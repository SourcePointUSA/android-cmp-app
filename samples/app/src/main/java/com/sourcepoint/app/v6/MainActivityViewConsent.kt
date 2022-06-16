package com.sourcepoint.app.v6

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.sourcepoint.app.v6.core.DataProvider
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
        val uuid = consent.gdpr?.consent?.uuid ?: "NO DATA"
        consent_uuid.text = uuid
        Log.i(TAG, "uuid: $uuid")
    }
}
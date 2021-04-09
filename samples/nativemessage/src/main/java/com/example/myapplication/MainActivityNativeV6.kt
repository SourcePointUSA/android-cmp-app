package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.sourcepoint.cmplibrary.SPMessage
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.SpConsentLib
import com.sourcepoint.cmplibrary.core.layout.createCustomNativeMessage
import com.sourcepoint.cmplibrary.core.layout.createDefaultNativeMessage
import com.sourcepoint.cmplibrary.core.layout.nat.NativeMessage
import com.sourcepoint.cmplibrary.model.ActionType
import com.sourcepoint.cmplibrary.model.SPConsents

class MainActivityNativeV6 : AppCompatActivity() {

    private val TAG = "**MainActivity"

//    private val nativeCampaign = Campaign(
//        accountId = 22,
//        propertyId = 7094,
//        propertyName = "tcfv2.mobile.demo",
//        pmId = "179657"
//    )
//
//    private val gdpr = GDPRCampaign(
//        accountId = 22,
//        propertyId = 10589,
//        propertyName = "https://unified.mobile.demo",
//        pmId = "404472"
//    )
//
//    private val ccpa = CCPACampaign(
//        accountId = 22,
//        propertyId = 10589,
//        propertyName = "https://unified.mobile.demo",
//        pmId = "404472"
//    )
//
//    private val consentLib by ConsentLibDelegate(
//        gdpr = gdpr,
//        ccpa = ccpa,
//        privacyManagerTab = PrivacyManagerTabK.FEATURES
//    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

//        consentLib.spClient = LocalClient(consentLib)

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

//        consent.setOnClickListener {
//            consentLib.loadGDPRPrivacyManager()
//        }
//
//        findViewById<View>(R.id.consent).setOnClickListener { consentLib.loadGDPRPrivacyManager() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "init");
        val nm = buildNativeMessageV6()
//        val nm = buildNativeMessageV6Local()
//        consentLib.loadMessage(nm)
//        consentLib.loadMessage()
    }

    override fun onDestroy() {
        super.onDestroy()
//        consentLib.dispose()
    }

    private fun buildNativeMessageV6Local(): NativeMessage {
        return createCustomNativeMessage(
            activity = this,
            layout = R.layout.custom_layout_cl,
            accept = R.id.accept_all_cl,
            reject = R.id.reject_all_cl,
            pBody = R.id.body_cl,
            pTitle = R.id.title_cl,
            cancel = R.id.cancel_cl,
            show = R.id.show_options_cl
        )
    }

    private fun buildNativeMessageV6(): NativeMessage {
        return createDefaultNativeMessage(this)
    }

    inner class LocalClient(private val gdpr: SpConsentLib) : SpClient {

        override fun onMessageReady(message: SPMessage) {
        }

        override fun onConsentReady(consent: SPConsents) {
        }

        override fun onUIFinished(view: View) {
            gdpr.removeView(view)
        }

        override fun onUIReady(view: View) {
            gdpr.showView(view)
        }

        override fun onError(error: Throwable) {
            error.printStackTrace()
        }

        override fun onAction(view: View, actionType: ActionType) {
            Toast.makeText(this@MainActivityNativeV6, "Action[${actionType.name}]", Toast.LENGTH_SHORT).show()
        }
    }

}
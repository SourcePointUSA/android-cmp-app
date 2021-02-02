package com.example.myapplication

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.sourcepoint.cmplibrary.Campaign
import com.sourcepoint.cmplibrary.creation.Builder
import com.sourcepoint.cmplibrary.creation.GDPRConsentLibDelegate
import com.sourcepoint.cmplibrary.legislation.gdpr.SpGDPRClient
import com.sourcepoint.gdpr_cmplibrary.*
import kotlinx.android.synthetic.main.content_main.*
import com.sourcepoint.cmplibrary.legislation.gdpr.GDPRConsentLib as GDPRConsentLibV6

class MainActivityV6 : AppCompatActivity() {

    private val TAG = "**MainActivity"

    private val nativeCampaign = Campaign(
        accountId = 22,
        propertyId = 7094,
        propertyName = "tcfv2.mobile.demo",
        pmId = "179657"
    )

    private val gdprConsent by GDPRConsentLibDelegate(
        campaign = nativeCampaign,
        privacyManagerTab = PrivacyManagerTab.FEATURES
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        gdprConsent.spGdprClient = Client(gdprConsent)

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        consent.setOnClickListener {
            gdprConsent.loadPrivacyManager()
        }

        findViewById<View>(R.id.consent).setOnClickListener { gdprConsent.loadPrivacyManager() }
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
//        gdprConsent.loadMessage(buildNativeMessage())
    }

    private fun buildNativeMessage(): NativeMessage {
        return NativeMessage(this)
    }

    private fun buildNativeMessageConstraintLayout(): NativeMessage {
        return object : NativeMessage(this) {
            override fun init() {
                // set your layout
                View.inflate(context, R.layout.custom_layout_cl, this)
                setAcceptAll(findViewById(R.id.accept_all_cl))
                setRejectAll(findViewById(R.id.reject_all_cl))
                setShowOptions(findViewById(R.id.show_options_cl))
                setCancel(findViewById(R.id.cancel_cl))
                setTitle(findViewById(R.id.title_cl))
                setBody(findViewById(R.id.body_cl))
            }

            override fun setAttributes(attrs: NativeMessageAttrs?) {
                // This will ensure all attributes are correctly set.
                super.setAttributes(attrs)

                // Overwrite any layout after calling super.setAttributes
                getAcceptAll().button.setBackgroundColor(Color.RED)
                getRejectAll().button.setBackgroundColor(Color.YELLOW)
                getTitle().text = "custom title"
            }
        }
    }

    private fun buildNativeMessageRelativeLayout(): NativeMessage {
        return object : NativeMessage(this) {
            override fun init() {
                // set your layout
                View.inflate(context, R.layout.custom_layout, this)
                setAcceptAll(findViewById(R.id.accept_all))
                setRejectAll(findViewById(R.id.reject_all))
                setShowOptions(findViewById(R.id.show_options))
                setCancel(findViewById(R.id.cancel))
                setTitle(findViewById(R.id.title))
                setBody(findViewById(R.id.body))
            }
        }
    }

    inner class Client(private val gdpr : GDPRConsentLibV6) : SpGDPRClient{
        override fun onConsentReady(c: GDPRUserConsent?) {

        }

        override fun onConsentUIFinished(v: View) {
           gdpr.removeView(v)
        }

        override fun onConsentUIReady(v: View) {
            gdpr.showView(v)
        }

        override fun onError(error: ConsentLibException?) {

        }

        override fun onAction(actionTypes: ActionTypes) {
            Toast.makeText(this@MainActivityV6, "Action[${actionTypes.name}]", Toast.LENGTH_SHORT).show()
        }
    }

}
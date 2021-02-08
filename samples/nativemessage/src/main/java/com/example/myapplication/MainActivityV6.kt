package com.example.myapplication

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.sourcepoint.cmplibrary.model.Campaign
import com.sourcepoint.cmplibrary.ConsentLib
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.creation.delegate.ConsentLibDelegate
import com.sourcepoint.cmplibrary.model.CCPAUserConsent
import com.sourcepoint.gdpr_cmplibrary.* // ktint-disable
import com.sourcepoint.gdpr_cmplibrary.exception.ConsentLibExceptionK
import kotlinx.android.synthetic.main.content_main.* // ktint-disable

class MainActivityV6 : AppCompatActivity() {

    private val TAG = "**MainActivity"

    private val nativeCampaign = Campaign(
        accountId = 22,
        propertyId = 7094,
        propertyName = "tcfv2.mobile.demo",
        pmId = "179657"
    )

    private val consentLib by ConsentLibDelegate(
        campaign = nativeCampaign,
        privacyManagerTab = PrivacyManagerTab.FEATURES
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        consentLib.spClient = LocalClient(consentLib)

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        consent.setOnClickListener {
            consentLib.loadPrivacyManager()
        }

        findViewById<View>(R.id.consent).setOnClickListener { consentLib.loadPrivacyManager() }
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
        consentLib.loadMessage()
//        consentLib.loadMessage(buildNativeMessage())
    }

    private fun buildNativeMessage(): NativeMessage {
        return NativeMessage(this)
    }

    var view : View? = null
    override fun onBackPressed() {
        consentLib.removeView(view, )
        view?.let {
            view = null
        }?: kotlin.run {
            super.onBackPressed()
        }

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

    inner class LocalClient(private val gdpr : ConsentLib) : SpClient {

        override fun onConsentReadyCallback(c: CCPAUserConsent) {}

        override fun onConsentReady(c: GDPRUserConsent) {}

        override fun onConsentUIFinished(v: View) {
           gdpr.removeView(v, )
        }

        override fun onConsentUIReady(v: View) {
            view = v
            gdpr.showView(v, )
        }

        override fun onError(error: ConsentLibExceptionK) {
            throw error
        }

        override fun onAction(actionTypes: ActionTypes) {
            Toast.makeText(this@MainActivityV6, "Action[${actionTypes.name}]", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        consentLib.dispose()
    }

}
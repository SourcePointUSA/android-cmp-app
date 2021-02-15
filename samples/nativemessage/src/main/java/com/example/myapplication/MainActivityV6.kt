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
import com.sourcepoint.cmplibrary.SpConsentLib
import com.sourcepoint.cmplibrary.SPMessage
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.core.layout.nat.NativeMessage
import com.sourcepoint.cmplibrary.core.layout.createCustomNativeMessage
import com.sourcepoint.cmplibrary.core.layout.createDefaultNativeMessage
import com.sourcepoint.cmplibrary.creation.delegate.ConsentLibDelegate
import com.sourcepoint.cmplibrary.data.network.model.SPCCPAConsents
import com.sourcepoint.cmplibrary.data.network.model.SPGDPRConsent
import com.sourcepoint.gdpr_cmplibrary.* // ktint-disable
import com.sourcepoint.cmplibrary.exception.ConsentLibExceptionK
import com.sourcepoint.cmplibrary.model.ActionType
import com.sourcepoint.cmplibrary.model.PrivacyManagerTabK
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
        privacyManagerTab = PrivacyManagerTabK.FEATURES
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
            consentLib.loadGDPRPrivacyManager()
        }

        findViewById<View>(R.id.consent).setOnClickListener { consentLib.loadGDPRPrivacyManager() }
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
//        consentLib.loadMessage()
        consentLib.loadMessage(buildNativeMessageV6())
//        consentLib.loadMessage(buildNativeMessageV6Local())

    }

    private fun buildNativeMessageV6(): NativeMessage {
        return createDefaultNativeMessage(this)
    }

    private fun buildNativeMessage(): com.sourcepoint.gdpr_cmplibrary.NativeMessage {
        return com.sourcepoint.gdpr_cmplibrary.NativeMessage(this)
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

    var localView : View? = null
    override fun onBackPressed() {

        localView?.let {
            consentLib.removeView(it)
            localView = null
        }?: kotlin.run {
            super.onBackPressed()
        }

    }

    private fun buildNativeMessageConstraintLayout(): com.sourcepoint.gdpr_cmplibrary.NativeMessage {
        return object : com.sourcepoint.gdpr_cmplibrary.NativeMessage(this) {
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

    private fun buildNativeMessageRelativeLayout(): com.sourcepoint.gdpr_cmplibrary.NativeMessage {
        return object : com.sourcepoint.gdpr_cmplibrary.NativeMessage(this) {
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

    inner class LocalClient(private val gdpr : SpConsentLib) : SpClient {

        override fun onMessageReady(message: SPMessage) {
        }

        override fun onConsentReady(consent: SPCCPAConsents) {
        }

        override fun onConsentReady(consent: SPGDPRConsent) {}

        override fun onUIFinished(view: View) {
           gdpr.removeView(view)
        }

        override fun onUIReady(view: View) {
            localView = view
            gdpr.showView(view)
        }

        override fun onError(error: ConsentLibExceptionK) {
             error.printStackTrace()
        }

        override fun onAction(view: View, actionType: ActionType) {
            Toast.makeText(this@MainActivityV6, "Action[${actionType.name}]", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        consentLib.dispose()
    }

}
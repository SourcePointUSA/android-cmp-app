package com.example.myapplication

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.sourcepoint.gdpr_cmplibrary.GDPRConsentLib
import com.sourcepoint.gdpr_cmplibrary.NativeMessage
import com.sourcepoint.gdpr_cmplibrary.NativeMessageAttrs
import kotlinx.android.synthetic.main.content_main.*

class NativeMessageActivity : AppCompatActivity() {

    private val mainViewGroup by lazy<ViewGroup> { findViewById(android.R.id.content) }

    private val TAG = "**MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        consent.setOnClickListener {
            buildGDPRConsentLib().showPm()
        }

        findViewById<View>(R.id.consent).setOnClickListener { buildGDPRConsentLib().showPm() }
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
        buildGDPRConsentLib().run(buildNativeMessageConstraintLayout());


    }

    private fun buildGDPRConsentLib(): GDPRConsentLib {
        return GDPRConsentLib
            .newBuilder(
                Accounts.nativeAccount.accountId,
                Accounts.nativeAccount.propertyName,
                Accounts.nativeAccount.propertyId,
                Accounts.nativeAccount.pmId,
                applicationContext
            )
            .setOnConsentUIReady { view ->
                showView(view);
                Log.i(TAG, "onConsentUIReady");
            }
            .setOnConsentUIFinished { view ->
                removeView(view);
                Log.i(TAG, "onConsentUIFinished");
            }
            .setOnConsentReady { consent ->
                Log.i(TAG, "onConsentReady")
                Log.i(TAG, "consentString: " + consent.consentString)
                Log.i(TAG, consent.TCData.toString())
                for (vendorId in consent.acceptedVendors) {
                    Log.i(TAG, "The vendor $vendorId was accepted.")
                }
                for (purposeId in consent.acceptedCategories) {
                    Log.i(TAG, "The category $purposeId was accepted.")
                }
                for (purposeId in consent.legIntCategories) {
                    Log.i(TAG, "The legIntCategory $purposeId was accepted.")
                }
                for (specialFeatureId in consent.specialFeatures) {
                    Log.i(
                        TAG,
                        "The specialFeature $specialFeatureId was accepted."
                    )
                }
            }
            .setOnError { error ->
                Log.e(TAG, "Something went wrong: ", error)
                Log.i(TAG, "ConsentLibErrorMessage: " + error.consentLibErrorMessage)
            }
            .build()
    }

    private fun removeView(view: View) {
        if (view.parent != null) mainViewGroup.removeView(view)
    }

    private fun showView(view: View) {
        if (view.parent == null) {
            view.layoutParams = ViewGroup.LayoutParams(0, 0)
            view.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            view.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            view.bringToFront()
            view.requestLayout()
            mainViewGroup.addView(view)
        }
    }

    private fun buildNativeMessage(): NativeMessage? {
        return NativeMessage(this)
    }

    private fun buildNativeMessageConstraintLayout(): NativeMessage? {
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

    private fun buildNativeMessageRelativeLayout(): NativeMessage? {
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

}
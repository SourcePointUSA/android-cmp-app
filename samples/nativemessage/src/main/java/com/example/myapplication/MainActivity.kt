package com.example.myapplication

import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.sourcepoint.gdpr_cmplibrary.GDPRConsentLib
import com.sourcepoint.gdpr_cmplibrary.NativeMessage
import com.sourcepoint.gdpr_cmplibrary.NativeMessageAttrs
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

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
        buildGDPRConsentLib().run(buildNativeMessage());
    }

    fun buildGDPRConsentLib(): GDPRConsentLib {
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
        return layoutInflater.inflate(R.layout.custom_layout, null) as? NativeMessage
    }
}
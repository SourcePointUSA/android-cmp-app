package com.sourcepoint.cmplibrary

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout.LayoutParams
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.sourcepoint.cmplibrary.legislation.gdpr.GDPRConsentLib
import com.sourcepoint.cmplibrary.legislation.gdpr.SpGDPRClient
import com.sourcepoint.gdpr_cmplibrary.ActionTypes
import com.sourcepoint.gdpr_cmplibrary.ConsentLibException
import com.sourcepoint.gdpr_cmplibrary.GDPRUserConsent

class MainActivity : Activity() {

    companion object {
        val TAG = "**MainActivity"
    }

    private val rlp: LayoutParams by lazy {
        LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
    }

    private val campaign = Campaign(
        22,
        7639,
        "tcfv2.mobile.webview",
        "122058"
    )

    val gdprConsent: GDPRConsentLib by lazy {
        Builder()
            .setAccountId(campaign.accountId)
            .setPropertyName(campaign.propertyName)
            .setPropertyId(campaign.propertyId)
            .setPmId(campaign.pmId)
            .setContext(this)
            .setAuthId("authId")
            .build(GDPRConsentLib::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val components: Pair<ConstraintLayout, Button> = getComponents()
        setContentView(components.first, rlp)
        val button = components.second
        button.setOnClickListener { Toast.makeText(this, "test", Toast.LENGTH_SHORT).show() }
        gdprConsent.spGdprClient = GdprClient()
    }

    override fun onResume() {
        super.onResume()
    }

    inner class GdprClient : SpGDPRClient {
        override fun onConsentReady(consent: GDPRUserConsent?) {
            for (line in consent.toString().split("\n").toTypedArray()) Log.i(TAG, line)
        }

        override fun onConsentUIFinished(v: View) {
            gdprConsent.removeView(v)
        }

        override fun onConsentUIReady(v: View) {
            gdprConsent.showView(v)
        }

        override fun onError(error: ConsentLibException?) {
            Log.e(TAG, "Something went wrong")
        }

        override fun onAction(actionTypes: ActionTypes?) {
            Log.i(TAG, "ActionType: " + actionTypes.toString())
        }
    }

    fun Activity.getComponents(): Pair<ConstraintLayout, Button> {
        val cl = ConstraintLayout(this)
        cl.id = View.generateViewId()
        setContentView(cl, rlp)

        val button = Button(this)
        button.id = View.generateViewId()
        button.text = "pm"
        cl.addView(button)
        val set = ConstraintSet()
        set.clone(cl)
        set.connect(
            button.id, // the ID of the widget to be constrained
            ConstraintSet.START, // the side of the widget to constrain
            cl.id, // the id of the widget to constrain to
            ConstraintSet.START // the side of widget to constrain to
        )
        set.connect(button.id, ConstraintSet.END, cl.id, ConstraintSet.END)
        set.connect(button.id, ConstraintSet.TOP, cl.id, ConstraintSet.TOP)
        set.connect(button.id, ConstraintSet.BOTTOM, cl.id, ConstraintSet.BOTTOM)
        set.applyTo(cl)
        return Pair(cl, button)
    }
}

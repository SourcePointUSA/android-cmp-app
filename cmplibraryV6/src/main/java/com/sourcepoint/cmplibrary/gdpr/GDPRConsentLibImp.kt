package com.sourcepoint.cmplibrary.gdpr

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.sourcepoint.gdpr_cmplibrary.*
import com.sourcepoint.gdpr_cmplibrary.GDPRConsentLibTest.Companion.newBuilder_

internal class GDPRConsentLibImpl(
    private val accountId: Int,
    private val propertyName: String,
    private val propertyId: Int,
    private val pmId: String,
    private val authId: String?,
    private val privacyManagerTab: PrivacyManagerTab?,
    private val context: Context,
    pClientInteraction: ClientInteraction
) : GDPRConsentLibClient {

    override val clientInteraction: ClientInteraction = pClientInteraction

    private val gdprConsentLib: GDPRConsentLibTest by lazy {
        newBuilder_(accountId, propertyName, propertyId, pmId, context)
            .setOnConsentUIReady { view: View? -> view?.let { clientInteraction.onConsentUIReadyCallback(it) } }
            .setOnAction { actionType: ActionTypes -> Log.i("builder v6", "ActionType: $actionType") }
            .setOnConsentUIFinished { view: View? -> view?.let { clientInteraction.onConsentUIFinishedCallback(it) } }
            .setOnConsentReady { consent: GDPRUserConsent ->
                // at this point it's safe to initialise vendors
                for (line in consent.toString().split("\n").toTypedArray()) Log.i("builder v6", line)
            }
            .setAuthId(authId)
            .setOnError { error: ConsentLibException? -> Log.e("builder v6", "Something went wrong") }
            .build() as GDPRConsentLibTest
    }

    override fun loadMessage(authId: String?) {
        gdprConsentLib.setAuthId(authId)
        gdprConsentLib.run()
    }

    override fun loadMessage() {
        gdprConsentLib.run()
    }

    override fun loadMessage(nativeMessage: NativeMessage) {
        gdprConsentLib.run(nativeMessage)
    }

    override fun loadMessage(authId: String, nativeMessage: NativeMessage) {
        TODO("Not yet implemented")
    }

    override fun    loadPrivacyManager() {
        // if privacyManagerTab has not been set, use null value.

        // if privacyManagerTab has not been set, use null value.
        privacyManagerTab?.let {
            val selectedTab: String? = if (TextUtils.isEmpty(it.name)) null else it.name
            gdprConsentLib.showPm(pmId, selectedTab)
        }
        gdprConsentLib.showPm(pmId, null)
    }

    override fun loadPrivacyManager(authId: String) {
        gdprConsentLib.showPm(pmId, null)
    }
}
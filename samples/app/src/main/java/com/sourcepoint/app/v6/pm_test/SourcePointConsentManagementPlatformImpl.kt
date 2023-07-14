package com.sourcepoint.app.v6.pm_test

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.os.ConfigurationCompat
import com.jakewharton.rxrelay2.BehaviorRelay
import com.sourcepoint.cmplibrary.NativeMessageController
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.core.nativemessage.MessageStructure
import com.sourcepoint.cmplibrary.creation.config
import com.sourcepoint.cmplibrary.creation.delegate.spConsentLibLazy
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import io.reactivex.Observable
import org.json.JSONObject

class SourcePointConsentManagementPlatformImpl(
    private val sourcePointConfig: SourcePointConfig.Loaded,
    private val activity: Activity,
) : ConsentManagementPlatform {

    private var consentCallback: (() -> Unit)? = null

    private val handler = Handler(Looper.getMainLooper())

    private val eventsRelay = BehaviorRelay.create<ConsentManagementPlatform.Event>()

    override val events: Observable<ConsentManagementPlatform.Event> = eventsRelay

    private val cmpConfig: SpConfig = config {
        accountId = sourcePointConfig.accountId
        propertyName = sourcePointConfig.propertyName
        messLanguage = getMessageLanguage()
        messageTimeout = MESSAGE_TIMEOUT_MS
        propertyId = sourcePointConfig.propertyId
        +CampaignType.GDPR
        +CampaignType.CCPA
    }

    private val spConsentLib by spConsentLibLazy {
        activity = this@SourcePointConsentManagementPlatformImpl.activity
        spClient = LocalClient()
        spConfig = cmpConfig
    }

    override fun openFirstLayer(consentGivenCallback: () -> Unit) {
        consentCallback = consentGivenCallback
        spConsentLib.loadMessage()
    }

    override fun dispose() {
        spConsentLib.dispose()
    }

    // this 100% of the cases will be called after we initialized the platform by
    // spConsentLib.loadMessage()
    override fun openPrivacyManager(consentGivenCallback: () -> Unit) {
        consentCallback = consentGivenCallback
        return when (getConsentType()) {
            ConsentType.GDPR -> launchGDPRSettings()
            ConsentType.CCPA -> launchCCPASettings()
            else -> consentCallback = null
        }
    }

    private fun getMessageLanguage(): MessageLanguage =
        ConfigurationCompat.getLocales(activity.resources.configuration).let { localeList ->
            if (!localeList.isEmpty) {
                localeList[0]
            } else null
        }.asMessageLanguage()

    private fun getConsentType() = getCmpConsentType(activity.applicationContext)

    private fun launchGDPRSettings() {
        spConsentLib.loadPrivacyManager(
            pmId = sourcePointConfig.gdprId,
            pmTab = PMTab.PURPOSES,
            campaignType = CampaignType.GDPR
        )
    }

    private fun launchCCPASettings() {
        spConsentLib.loadPrivacyManager(
            pmId = sourcePointConfig.ccpaId,
            pmTab = PMTab.PURPOSES,
            campaignType = CampaignType.CCPA
        )
    }

    inner class LocalClient : SpClient {
        override fun onAction(view: View, consentAction: ConsentAction): ConsentAction {
            return consentAction
        }

        override fun onConsentReady(consent: SPConsents) {
        }

        override fun onError(error: Throwable) {
            Log.e("DIA-1716", "=== onError ===")
            Log.e("DIA-1716", "error = ${error.message}")
            handler.post {
                consentCallback?.invoke()
                eventsRelay.accept(ConsentManagementPlatform.Event.Finished)
            }
        }

        override fun onMessageReady(message: JSONObject) {
            Log.d("DIA-1716", "=== onMessageReady ===")
        }

        override fun onNativeMessageReady(
            message: MessageStructure,
            messageController: NativeMessageController
        ) {
            Log.d("DIA-1716", "=== onNativeMessageReady ===")
        }

        override fun onNoIntentActivitiesFound(url: String) {
        }

        override fun onSpFinished(sPConsents: SPConsents) {
            handler.post {
                consentCallback?.invoke()
                eventsRelay.accept(ConsentManagementPlatform.Event.Finished)
            }
            val consentType = getConsentType()
            ConsentManagementPlatform.consentReadyRelay.accept(consentType)
        }

        override fun onUIFinished(view: View) {
            Log.d("DIA-1716", "=== onUIFinished ===")
            eventsRelay.accept(ConsentManagementPlatform.Event.UiEvent.ViewFinished(view))
        }

        override fun onUIReady(view: View) {
            eventsRelay.accept(ConsentManagementPlatform.Event.UiEvent.ViewReady(view))
        }
    }

    override fun onScreenShown() {
    }

    companion object {
        private const val MESSAGE_TIMEOUT_MS = 10_000L
    }
}

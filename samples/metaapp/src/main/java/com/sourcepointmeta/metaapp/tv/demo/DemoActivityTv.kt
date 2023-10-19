package com.sourcepointmeta.metaapp.tv.demo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.sourcepoint.cmplibrary.NativeMessageController
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.core.nativemessage.MessageStructure
import com.sourcepoint.cmplibrary.creation.delegate.spConsentLibLazy
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepoint.cmplibrary.util.OttDelegate
import com.sourcepoint.cmplibrary.util.clearAllData
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.core.getOrNull
import com.sourcepointmeta.metaapp.core.replaceFragment
import com.sourcepointmeta.metaapp.data.localdatasource.LocalDataSource
import com.sourcepointmeta.metaapp.logger.LoggerImpl
import com.sourcepointmeta.metaapp.tv.viewer.JsonViewerActivityTv
import com.sourcepointmeta.metaapp.tv.viewer.JsonViewerFragmentTv
import org.json.JSONObject
import org.koin.android.ext.android.inject
import java.util.* //ktlint-disable

class DemoActivityTv : FragmentActivity() {

    private val dataSource by inject<LocalDataSource>()
    private val spClientObserver: List<SpClient> by inject()

    private val config: SpConfig by lazy {
        intent.extras
            ?.getString("property_name")
            ?.let { dataSource.getSPConfig(it).getOrNull() }
            ?: throw RuntimeException("extra property_name param is null!!!")
    }

    private val property by lazy {
        val propName = intent.extras
            ?.getString("property_name") ?: ""
        dataSource.fetchPropertyByNameSync(propName)
    }

    val fragment by lazy { DemoEventFragmentTv.instance(propertyName = property.propertyName) }

    private val gdprPmId by lazy { property.gdprPmId }
    private val ccpaPmId by lazy { property.ccpaPmId }

    private val logger by lazy {
        LoggerImpl(
            propertyName = config.propertyName,
            ds = dataSource,
            session = "${property.propertyName}-${Date().time}"
        )
    }

    private val spConsentLib by spConsentLibLazy {
        activity = this@DemoActivityTv
        spClient = LocalClient()
        spConfig = config.copy(logger = logger)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clearAllData(this)
        setContentView(R.layout.main_activity)
        savedInstanceState ?: replaceFragment(R.id.container, fragment)
        fragment.flmListener = { spConsentLib.loadMessage() }
        fragment.pmListener = {
            when (it) {
                CampaignType.GDPR -> spConsentLib.loadPrivacyManager(gdprPmId.toString(), CampaignType.GDPR)
                CampaignType.CCPA -> spConsentLib.loadPrivacyManager(ccpaPmId.toString(), CampaignType.CCPA)
            }
        }
        fragment.logClickListener = {
            intent.putExtra("run_demo", false)
            val intent = Intent(baseContext, JsonViewerActivityTv::class.java)
            intent.putExtra(JsonViewerFragmentTv.LOG_ID, it.id ?: -1L)
            intent.putExtra(JsonViewerFragmentTv.TITLE, "${it.type} - ${it.tag}")
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        val callSuperOnBackPressed = { super.onBackPressed() }

        spConsentLib.verifyHome(object : OttDelegate {
            override fun onHomePage() {
                callSuperOnBackPressed()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        spConsentLib.loadMessage()
    }

    override fun onDestroy() {
        super.onDestroy()
        spConsentLib.dispose()
    }

    internal inner class LocalClient : SpClient {

        override fun onNativeMessageReady(message: MessageStructure, messageController: NativeMessageController) {
            spClientObserver.forEach { it.onNativeMessageReady(message, messageController) }
        }

        override fun onError(error: Throwable) {
            spClientObserver.forEach { it.onError(error) }
            error.printStackTrace()
        }

        override fun onMessageReady(message: JSONObject) {
        }

        override fun onConsentReady(consent: SPConsents) {
            spClientObserver.forEach { it.onConsentReady(consent) }
        }

        override fun onUIFinished(view: View) {
            spClientObserver.forEach { it.onUIFinished(view) }
            spConsentLib.removeView(view)
        }

        override fun onUIReady(view: View) {
            spClientObserver.forEach { it.onUIReady(view) }
            spConsentLib.showView(view)
        }

        override fun onAction(view: View, consentAction: ConsentAction): ConsentAction {
            spClientObserver.forEach { it.onAction(view, consentAction) }
            Log.i(this::class.java.name, "ActionType: $consentAction")
            return consentAction
        }

        override fun onSpFinished(sPConsents: SPConsents) {
            spClientObserver.forEach { it.onSpFinished(sPConsents) }
        }

        override fun onNoIntentActivitiesFound(url: String) {
            spClientObserver.forEach { it.onNoIntentActivitiesFound(url) }
        }
    }
}

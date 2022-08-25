package com.sourcepointmeta.metaapp.tv.demo

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
import com.sourcepoint.cmplibrary.util.clearAllData
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.core.getOrNull
import com.sourcepointmeta.metaapp.core.replaceFragment
import com.sourcepointmeta.metaapp.data.localdatasource.LocalDataSource
import com.sourcepointmeta.metaapp.data.localdatasource.RemoteDataSource
import com.sourcepointmeta.metaapp.logger.LoggerImpl
import kotlinx.coroutines.MainScope
import org.json.JSONObject
import org.koin.android.ext.android.inject
import java.util.* //ktlint-disable

class DemoActivityTv : FragmentActivity() {

    companion object {
        const val DEMO_REFRESH_ACTION = "com.metaapp.broadcast.demo.REFRESH"
    }

    private val dataSource by inject<LocalDataSource>()
    private val spClientObserver: List<SpClient> by inject()
    private val remoteDataSource by inject<RemoteDataSource>()
    private val scope by lazy { MainScope() }

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
    private val useGroupPmIfAvailable by lazy { property.useGdprGroupPmIfAvailable }
    private val ccpaPmId by lazy { property.ccpaPmId }
    private val authId by lazy { property.authId }

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
        fragment.pmListener = {
            when (it) {
                CampaignType.GDPR -> spConsentLib.loadPrivacyManager(gdprPmId.toString(), CampaignType.GDPR)
                CampaignType.CCPA -> {}
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
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

//    inner class DemoBr : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            fragment.refreshData()
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        unregisterReceiver(br)
//    }
}

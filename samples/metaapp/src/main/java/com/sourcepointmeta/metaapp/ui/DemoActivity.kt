package com.sourcepointmeta.metaapp.ui

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.creation.delegate.spConsentLibLazy
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepoint.cmplibrary.util.clearAllData
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.core.getOrNull
import com.sourcepointmeta.metaapp.data.localdatasource.LocalDataSource
import com.sourcepointmeta.metaapp.logger.LoggerImpl
import kotlinx.android.synthetic.main.activity_demo.* // ktlint-disable
import org.json.JSONObject
import org.koin.android.ext.android.inject
import java.util.* // ktlint-disable

class DemoActivity : FragmentActivity() {

    private val dataSource by inject<LocalDataSource>()

    private val config: SpConfig by lazy {
        intent.extras
            ?.getString("property_name")
            ?.let { dataSource.getSPConfig(it).getOrNull() }
            ?: throw RuntimeException("extra property_name param is null!!!")
    }

    private val logger by lazy {
        LoggerImpl(
            propertyName = config.propertyName,
            ds = dataSource,
            session = "${property.propertyName}-${Date().time}"
        )
    }

    private val property by lazy {
        val propName = intent.extras
            ?.getString("property_name") ?: ""
        dataSource.fetchPropertyByNameSync(propName)
    }

    private val gdprPmId by lazy { property.gdprPmId }
    private val ccpaPmId by lazy { property.ccpaPmId }
    private val authId by lazy { property.authId }

    private val spConsentLib by spConsentLibLazy {
        activity = this@DemoActivity
        spClient = LocalClient()
        spConfig = config.copy(logger = logger)
    }

    private val demoFr by lazy { DemoFragment.instance(config.propertyName) }
    private val logFr by lazy { LogFragment.instance(config.propertyName) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clearAllData(this)
        setContentView(R.layout.activity_demo)

        tool_bar.run {
            title = config.propertyName
            setNavigationOnClickListener { onBackPressed() }
        }

        val pagerAdapter = ScreenSlidePagerAdapter(this)
        pager.adapter = pagerAdapter

        demoFr.demoListener = { action ->
            pager.currentItem = 0
            when (action) {
                DemoFragment.DemoAction.GDPR_PM -> {
                    gdprPmId?.toString()
                        ?.let {
                            spConsentLib.loadPrivacyManager(
                                it,
                                PMTab.PURPOSES,
                                CampaignType.GDPR
                            )
                        }
                        ?: pmNotValid()
                }
                DemoFragment.DemoAction.CCPA_PM -> {
                    ccpaPmId?.toString()
                        ?.let {
                            spConsentLib.loadPrivacyManager(
                                it,
                                PMTab.PURPOSES,
                                CampaignType.CCPA
                            )
                        }
                        ?: pmNotValid()
                }
                DemoFragment.DemoAction.LOG -> {
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed(
            {
                authId
                    ?.let { spConsentLib.loadMessage(authId = it) }
                    ?: run { spConsentLib.loadMessage() }
            },
            400
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        spConsentLib.dispose()
    }

    internal inner class LocalClient : SpClient {
        override fun onMessageReady(message: JSONObject) {}
        override fun onError(error: Throwable) {
            error.printStackTrace()
        }

        override fun onConsentReady(consent: SPConsents) {
        }

        override fun onUIFinished(view: View) {
            spConsentLib.removeView(view)
        }

        override fun onUIReady(view: View) {
            spConsentLib.showView(view)
        }

        override fun onAction(view: View, actionType: ActionType) {
            Log.i(this::class.java.name, "ActionType: $actionType")
        }
    }

    private fun pmNotValid() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Privacy Manager Id is not valid")
            .setPositiveButton("OK", null)
            .show()
    }

    inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> logFr
                else -> demoFr
            }
        }
    }

    override fun onBackPressed() {
        if (pager.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            pager.currentItem = pager.currentItem - 1
        }
    }
}

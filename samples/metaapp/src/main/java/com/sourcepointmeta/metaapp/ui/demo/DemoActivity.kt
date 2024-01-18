package com.sourcepointmeta.metaapp.ui.demo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sourcepoint.cmplibrary.NativeMessageController
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.core.nativemessage.MessageStructure
import com.sourcepoint.cmplibrary.core.nativemessage.NativeAction
import com.sourcepoint.cmplibrary.core.nativemessage.NativeComponent
import com.sourcepoint.cmplibrary.creation.delegate.spConsentLibLazy
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.exposed.NativeMessageActionType
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepoint.cmplibrary.util.clearAllData
import com.sourcepointmeta.metaapp.BuildConfig
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.core.getOrNull
import com.sourcepointmeta.metaapp.data.localdatasource.LocalDataSource
import com.sourcepointmeta.metaapp.data.localdatasource.RemoteDataSource
import com.sourcepointmeta.metaapp.logger.LoggerImpl
import com.sourcepointmeta.metaapp.ui.eventlogs.LogFragment
import com.sourcepointmeta.metaapp.ui.propertylist.PropertyListFragment
import com.sourcepointmeta.metaapp.ui.sp.SpFragment
import com.sourcepointmeta.metaapp.ui.viewer.JsonViewer4LogFragment.Companion.LOG_ID
import com.sourcepointmeta.metaapp.ui.viewer.JsonViewer4LogFragment.Companion.TITLE
import com.sourcepointmeta.metaapp.ui.viewer.JsonViewer4SharedPrefFragment.Companion.SP_KEY
import com.sourcepointmeta.metaapp.ui.viewer.JsonViewer4SharedPrefFragment.Companion.SP_VALUE
import com.sourcepointmeta.metaapp.ui.viewer.JsonViewerActivity
import io.github.g00fy2.versioncompare.Version
import kotlinx.android.synthetic.main.activity_demo.* // ktlint-disable
import kotlinx.android.synthetic.main.native_message.view.* // ktlint-disable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named
import java.util.* // ktlint-disable

class DemoActivity : FragmentActivity() {

    private val dataSource by inject<LocalDataSource>()
    private val spClientObserver: List<SpClient> by inject()
    private val remoteDataSource by inject<RemoteDataSource>()
    private val scope by lazy { MainScope() }
    private val errorColor: Int by lazy {
        TypedValue().apply { theme.resolveAttribute(R.attr.colorErrorResponse, this, true) }
            .data
    }
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

    private val pTab by lazy {
        property.pmTab
            ?.let { pt -> PMTab.values().find { it.name == pt } }
            ?: PMTab.DEFAULT
    }

    private val pubData: JSONObject = JSONObject().apply {
        put("timeStamp", 1628620031363)
        put("key_1", "value_1")
        put("key_2", true)
        put("key_3", JSONObject())
    }

    private val isUITestRunning by inject<Boolean>(qualifier = named("ui_test_running"))

    private val gdprPmId by lazy { property.gdprPmId }
    private val useGroupPmIfAvailable by lazy { property.useGdprGroupPmIfAvailable }
    private val ccpaPmId by lazy { property.ccpaPmId }
    private val usnatPmId by lazy { property.usnatPmId }
    private val authId by lazy { property.authId }

    private val spConsentLib by spConsentLibLazy {
        activity = this@DemoActivity
        spClient = LocalClient()
        spConfig = config.copy(logger = logger)
    }

    private val demoFr by lazy { DemoFragment.instance(config.propertyName) }
    private val logFr by lazy { LogFragment.instance(config.propertyName) }
    private val spFr by lazy { SpFragment.instance() }

    private val sp by lazy { PreferenceManager.getDefaultSharedPreferences(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!sp.contains(PropertyListFragment.OLD_V6_CONSENT) &&
            !sp.contains(PropertyListFragment.V7_CONSENT)
        ) {
            clearAllData(this)
        }
        setContentView(R.layout.activity_demo)

        tool_bar.run {
            title = "${BuildConfig.VERSION_NAME} - ${config.propertyName}"
            setNavigationOnClickListener { onBackPressed() }
        }

        val pagerAdapter = ScreenSlidePagerAdapter(this)
        pager.adapter = pagerAdapter

        demoFr.demoListener = { action ->

            // don't go to the first fragment during UI tests
            if (!isUITestRunning) {
                pager.currentItem = 0
            }

            when (action) {
                DemoFragment.DemoAction.GDPR_PM -> {
                    gdprPmId?.toString()
                        ?.let {
                            spConsentLib.loadPrivacyManager(
                                pmId = it,
                                pmTab = pTab,
                                campaignType = CampaignType.GDPR,
                                useGroupPmIfAvailable = useGroupPmIfAvailable,
                                messageType = property.messageType,
                            )
                        }
                        ?: pmNotValid()
                }
                DemoFragment.DemoAction.CCPA_PM -> {
                    ccpaPmId?.toString()
                        ?.let {
                            spConsentLib.loadPrivacyManager(
                                pmId = it,
                                pmTab = pTab,
                                campaignType = CampaignType.CCPA,
                                useGroupPmIfAvailable = useGroupPmIfAvailable,
                                messageType = property.messageType,
                            )
                        }
                        ?: pmNotValid()
                }
                DemoFragment.DemoAction.USNAT_PM -> {
                    usnatPmId?.toString()
                        ?.let {
                            spConsentLib.loadPrivacyManager(
                                pmId = it,
                                pmTab = pTab,
                                campaignType = CampaignType.USNAT,
                                useGroupPmIfAvailable = useGroupPmIfAvailable,
                                messageType = property.messageType,
                            )
                        }
                        ?: pmNotValid()
                }
                DemoFragment.DemoAction.LOG -> {
                }
            }
        }

        logFr.logClickListener = {
            intent.putExtra("run_demo", false)
            val intent = Intent(baseContext, JsonViewerActivity::class.java)
            intent.putExtra(LOG_ID, it.id ?: -1L)
            intent.putExtra(TITLE, "${it.type} - ${it.tag}")
            startActivity(intent)
        }

        spFr.spItemClickListener = { key, value ->
            intent.putExtra("run_demo", false)
            val intent = Intent(baseContext, JsonViewerActivity::class.java)
            intent.putExtra(SP_KEY, key)
            intent.putExtra(SP_VALUE, value)
            startActivity(intent)
        }

        tool_bar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_share -> logFr.shareLogs()
                R.id.action_share_sp -> logFr.sendEmail(getAllPref())
                R.id.action_clear_log -> logFr.clearLog()
                R.id.action_clear_sp -> logFr.clearSp()
                R.id.action_refresh -> triggerLib()
            }
            true
        }
    }

    private fun getAllPref(): String {
        return JSONObject(PreferenceManager.getDefaultSharedPreferences(this).all.toMap()).toString()
    }

    override fun onResume() {
        super.onResume()
        checkVersion()
        triggerLib()
    }

    private fun triggerLib() {
        if (intent.getBooleanExtra("run_demo", true)) {
            Handler().postDelayed(
                {
                    authId
                        ?.let { spConsentLib.loadMessage(authId = it, pubData = pubData, cmpViewId = null) }
                        ?: run { spConsentLib.loadMessage(pubData = pubData) }
                },
                400
            )
        }
        intent.putExtra("run_demo", true)
    }

    override fun onDestroy() {
        super.onDestroy()
        spConsentLib.dispose()
    }

    internal inner class LocalClient : SpClient {

        override fun onNativeMessageReady(message: MessageStructure, messageController: NativeMessageController) {
            spClientObserver.forEach { it.onNativeMessageReady(message, messageController) }
            setNativeMessage(message, messageController)
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
            spFr.update()
        }

        override fun onNoIntentActivitiesFound(url: String) {
            spClientObserver.forEach { it.onNoIntentActivitiesFound(url) }
        }
    }

    private fun pmNotValid() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Privacy Manager Id is not valid")
            .setPositiveButton("OK", null)
            .show()
    }

    inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> logFr
                1 -> demoFr
                else -> spFr
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

    private fun checkVersion() {
        scope.launch {
            withContext(Dispatchers.IO) { remoteDataSource.fetchLatestVersion() }
                .getOrNull()
                ?.let {
                    if (!Version(BuildConfig.VERSION_NAME).isEqual(it)) {
                        tool_bar.setTitleTextColor(errorColor)
                    }
                }
        }
    }

    fun setNativeMessage(message: MessageStructure, messageController: NativeMessageController) {
        val customLayout = View.inflate(this, R.layout.native_message, null)
        customLayout.run {
            message.messageComponents?.let {
                setTitle(customLayout, it.title ?: throw RuntimeException())
                setBody(customLayout, it.body ?: throw RuntimeException())
                setAgreeBtn(customLayout, it.body ?: throw RuntimeException())
                it.actions.forEach { a ->
                    when (a.choiceType) {
                        NativeMessageActionType.REJECT_ALL -> setRejectAllBtn(customLayout, a)
                        NativeMessageActionType.ACCEPT_ALL -> setAcceptAllBtn(customLayout, a)
                        NativeMessageActionType.MSG_CANCEL -> setCancelBtn(customLayout, a)
                        NativeMessageActionType.SHOW_OPTIONS -> setOptionBtn(customLayout, a)
                        else -> {}
                    }
                }
            }
            accept_all.setOnClickListener {
                messageController.run {
                    removeNativeView(customLayout)
                    sendConsent(NativeMessageActionType.ACCEPT_ALL, message.campaignType)
                }
            }
            cancel.setOnClickListener {
                messageController.run {
                    removeNativeView(customLayout)
                    sendConsent(NativeMessageActionType.MSG_CANCEL, message.campaignType)
                }
            }
            reject_all.setOnClickListener {
                messageController.run {
                    removeNativeView(customLayout)
                    sendConsent(NativeMessageActionType.REJECT_ALL, message.campaignType)
                }
            }
            show_options_btn.setOnClickListener {
                messageController.run {
                    removeNativeView(customLayout)
                    when (message.campaignType) {
                        CampaignType.GDPR -> gdprPmId
                        CampaignType.CCPA -> ccpaPmId
                        CampaignType.USNAT -> throw RuntimeException()
                    }?.let { pmId ->
                        messageController.showOptionNativeMessage(message.campaignType, pmId.toString())
                    }
                }
            }
        }
        messageController.showNativeView(customLayout)
    }

    fun setTitle(view: View, t: NativeComponent) {
        view.title_nm.run {
            text = t.text ?: ""
            setBackgroundColor(t.style?.backgroundColor?.toColorInt() ?: throw RuntimeException())
            setTextColor(t.style?.color?.toColorInt() ?: throw RuntimeException())
            textSize = t.style?.fontSize ?: 10F
        }
    }

    fun setBody(view: View, t: NativeComponent) {
        view.body_nm.run {
            text = t.text ?: ""
            setBackgroundColor(t.style?.backgroundColor?.toColorInt() ?: throw RuntimeException())
            setTextColor(t.style?.color?.toColorInt() ?: throw RuntimeException())
            textSize = t.style?.fontSize ?: 10F
            movementMethod = ScrollingMovementMethod()
        }
    }

    fun setAgreeBtn(view: View, t: NativeComponent) {
        view.body_nm.run {
            text = t.text ?: ""
            setBackgroundColor(t.style?.backgroundColor?.toColorInt() ?: throw RuntimeException())
            setTextColor(t.style?.color?.toColorInt() ?: throw RuntimeException())
        }
    }

    fun setCancelBtn(view: View, na: NativeAction) {
        view.cancel.run {
            text = na.text
            setBackgroundColor(na.style.backgroundColor.toColorInt() ?: throw RuntimeException())
            setTextColor(na.style.color?.toColorInt() ?: throw RuntimeException())
            textSize = na.style.fontSize ?: 10F
        }
    }

    fun setOptionBtn(view: View, na: NativeAction) {
        view.show_options_btn.run {
            text = na.text
            setBackgroundColor(na.style.backgroundColor.toColorInt() ?: throw RuntimeException())
            setTextColor(na.style.color?.toColorInt() ?: throw RuntimeException())
            textSize = na.style.fontSize ?: 10F
        }
    }

    fun setRejectAllBtn(view: View, na: NativeAction) {
        view.reject_all.run {
            text = na.text
            setBackgroundColor(na.style.backgroundColor.toColorInt() ?: throw RuntimeException())
            setTextColor(na.style.color?.toColorInt() ?: throw RuntimeException())
            textSize = na.style.fontSize ?: 10F
        }
    }

    fun setAcceptAllBtn(view: View, na: NativeAction) {
        view.accept_all.run {
            text = na.text
            setBackgroundColor(na.style.backgroundColor.toColorInt() ?: throw RuntimeException())
            setTextColor(na.style.color?.toColorInt() ?: throw RuntimeException())
            textSize = na.style.fontSize ?: 10F
        }
    }
}

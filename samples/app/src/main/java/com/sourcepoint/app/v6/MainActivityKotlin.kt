package com.sourcepoint.app.v6

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import com.sourcepoint.app.v6.core.DataProvider
import com.sourcepoint.app.v6.web.WebConsentTransferTestActivity
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
import com.sourcepoint.cmplibrary.util.clearAllData
import kotlinx.android.synthetic.main.activity_main.auth_id_activity
import kotlinx.android.synthetic.main.activity_main.clear_all
import kotlinx.android.synthetic.main.activity_main.consent_btn
import kotlinx.android.synthetic.main.activity_main.custom_consent
import kotlinx.android.synthetic.main.activity_main.delete_custom_consent
import kotlinx.android.synthetic.main.activity_main.review_consents_ccpa
import kotlinx.android.synthetic.main.activity_main.review_consents_gdpr
import kotlinx.android.synthetic.main.activity_main_v7.*
import kotlinx.android.synthetic.main.native_message.view.*
import org.json.JSONObject
import org.koin.android.ext.android.inject

class MainActivityKotlin : AppCompatActivity() {

    companion object {
        private const val TAG = "**MainActivity"
        const val CLIENT_PREF_KEY = "client_pref_key"
        const val CLIENT_PREF_VAL = "client_pref_val"
    }

    private val dataProvider by inject<DataProvider>()
    private val spClientObserver: List<SpClient> by inject()

    private val spConsentLib by spConsentLibLazy {
        activity = this@MainActivityKotlin
        spClient = LocalClient()
        spConfig = dataProvider.spConfig
//        config {
//            accountId = 22
//            propertyName = "sca-ott-newwebpm"
//            messLanguage = MessageLanguage.ENGLISH
//            propertyId = 27927
//            +(CampaignType.GDPR)
//            +(CampaignType.CCPA to listOf(("location" to "US")))
//        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (dataProvider.resetAll) {
            clearAllData(this)
            PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply()
        }

        storeDiagnosticObj(dataProvider.diagnostic)

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        sp.edit().putString(CLIENT_PREF_KEY, CLIENT_PREF_VAL).apply()

        setContentView(R.layout.activity_main_v7)
        review_consents_gdpr.setOnClickListener { _v: View? -> selectGDPRPM(dataProvider) }
        review_consents_ccpa.setOnClickListener { _v: View? -> selectCCPAPM(dataProvider) }
        clear_all.setOnClickListener { _v: View? ->
            clearAllData(this)
            PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply()
        }
        auth_id_activity.setOnClickListener { _v: View? ->
            startActivity(Intent(this, MainActivityAuthId::class.java))
        }
        custom_consent.setOnClickListener { _v: View? ->
            spConsentLib.customConsentGDPR(
                vendors = dataProvider.customVendorList,
                categories = dataProvider.customCategories,
                legIntCategories = emptyList(),
                success = { spCustomConsents -> spClientObserver.forEach { it.onConsentReady(spCustomConsents!!) } }
            )
        }
        delete_custom_consent.setOnClickListener { _v: View? ->
            spConsentLib.deleteCustomConsentTo(
                vendors = dataProvider.customVendorList,
                categories = dataProvider.customCategories,
                legIntCategories = emptyList(),
                success = { spCustomConsents -> spClientObserver.forEach { it.onConsentReady(spCustomConsents!!) } }
            )
        }
        consent_btn.setOnClickListener {
            spConsentLib.dispose()
            finish()
            startActivity(Intent(this, MainActivityViewConsent::class.java))
        }
        refresh_btn.setOnClickListener { executeCmpLib() }
        add_old_consent.setOnClickListener { addOldV6Consent() }
        transfer_consent_to_web_view.setOnClickListener { openTransferConsentActivity() }
    }

    private fun openTransferConsentActivity() {
        startActivity(Intent(this, WebConsentTransferTestActivity::class.java))
    }

    override fun onResume() {
        super.onResume()
        executeCmpLib()
    }

    private fun executeCmpLib() {
        spConsentLib.loadMessage(authId = dataProvider.authId)
    }

    override fun onDestroy() {
        super.onDestroy()
        spConsentLib.dispose()
        Log.i(TAG, "onDestroy: disposed")
    }

    internal inner class LocalClient : SpClient {

        override fun onNativeMessageReady(
            message: MessageStructure,
            messageController: NativeMessageController
        ) {
            setNativeMessage(message, messageController)
        }

        override fun onMessageReady(message: JSONObject) {
        }

        override fun onError(error: Throwable) {
            spClientObserver.forEach { it.onError(error) }
            error.printStackTrace()
            Log.i(TAG, "onError: $error")
        }

        override fun onConsentReady(consent: SPConsents) {
            val grants = consent.gdpr?.consent?.grants
            grants?.forEach { grant ->
                val granted = grants[grant.key]?.granted
                val purposes = grants[grant.key]?.purposeGrants
                println("vendor: ${grant.key} - granted: $granted - purposes: $purposes")
            }
            spClientObserver.forEach { it.onConsentReady(consent) }
            Log.i(TAG, "onConsentReady: $consent")
        }

        override fun onUIFinished(view: View) {
            spClientObserver.forEach { it.onUIFinished(view) }
            spConsentLib.removeView(view)
            Log.i(TAG, "onUIFinished")
        }

        override fun onUIReady(view: View) {
            spClientObserver.forEach { it.onUIReady(view) }
            spConsentLib.showView(view)
            Log.i(TAG, "onUIReady")
        }

        override fun onAction(view: View, consentAction: ConsentAction): ConsentAction {
            spClientObserver.forEach { it.onAction(view, consentAction) }
            Log.i(TAG, "onAction ActionType: $consentAction")
            consentAction.pubData.put("pb_key", "pb_value")
            return consentAction
        }

        override fun onSpFinished(sPConsents: SPConsents) {
            spClientObserver.forEach { it.onSpFinished(sPConsents) }
            Log.i(TAG, "onSpFinish: $sPConsents")
            Log.i(TAG, "==================== onSpFinish ==================")
        }

        override fun onNoIntentActivitiesFound(url: String) {
            Log.i(TAG, "onNoIntentActivitiesFound: $url")
            spClientObserver.forEach { it.onNoIntentActivitiesFound(url) }
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
                        CampaignType.GDPR -> dataProvider.gdprPmId
                        CampaignType.CCPA -> dataProvider.ccpaPmId
                        CampaignType.USNAT -> throw RuntimeException()
                    }.let { pmId ->
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

    fun addOldV6Consent() {
        val sp = PreferenceManager.getDefaultSharedPreferences(this@MainActivityKotlin)
        val v6LocalState = JSONObject(consent)
        val spEditor = sp.edit()
        v6LocalState.keys().forEach {
            check { v6LocalState.getString(it) }?.let { v -> spEditor.putString(it, v) }
            check { v6LocalState.getBoolean(it) }?.let { v -> spEditor.putBoolean(it, v) }
            check { v6LocalState.getInt(it) }?.let { v -> spEditor.putInt(it, v) }
        }
        spEditor.apply()
    }

    fun prefToJsonObject() {
        val sp = PreferenceManager.getDefaultSharedPreferences(this@MainActivityKotlin)
        val obj = JSONObject(consent)
        sp.all.forEach {
            obj.put(it.key, it.value)
        }
    }

    private fun <E> check(block: () -> E): E? {
        return try {
            block.invoke()
        } catch (e: Exception) {
            null
        }
    }

    private fun storeDiagnosticObj(list: List<Pair<String, Any?>>) {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val spEditor = sp.edit()
        list.forEach {
            check { it.second as? String }?.let { v -> spEditor.putString(it.first, v) }
            check { it.second as? Boolean }?.let { v -> spEditor.putBoolean(it.first, v) }
            check { it.second as? Int }?.let { v -> spEditor.putInt(it.first, v) }
        }
        spEditor.apply()
    }

    private fun selectGDPRPM(dataProvider: DataProvider){
        dataProvider.messageType
            ?.let {
                spConsentLib.loadPrivacyManager(
                    pmId = dataProvider.gdprPmId,
                    pmTab = PMTab.PURPOSES,
                    campaignType = CampaignType.GDPR,
                    useGroupPmIfAvailable = dataProvider.useGdprGroupPmIfAvailable,
                    messageType = it
                )
            }
            ?: run {
                spConsentLib.loadPrivacyManager(
                    pmId = dataProvider.gdprPmId,
                    pmTab = PMTab.PURPOSES,
                    campaignType = CampaignType.GDPR,
                    useGroupPmIfAvailable = dataProvider.useGdprGroupPmIfAvailable
                )
            }
    }

    private fun selectCCPAPM(dataProvider: DataProvider){
        dataProvider.messageType
            ?.let {
                spConsentLib.loadPrivacyManager(
                    pmId = dataProvider.ccpaPmId,
                    pmTab = PMTab.PURPOSES,
                    campaignType = CampaignType.CCPA,
                    messageType = it
                )
            }
            ?: run {
                spConsentLib.loadPrivacyManager(
                    pmId = dataProvider.ccpaPmId,
                    pmTab = PMTab.PURPOSES,
                    campaignType = CampaignType.CCPA
                )
            }
    }
}

val consent = """
{
  "sp.ccpa.key.date.created": "2022-12-08T09:24:13.542Z",
  "sp.gdpr.key.date.created": "2022-12-08T13:56:00.766Z",
  "sp.ccpa.key.consent.status": "{\n  \"consentedAll\": true,\n  \"cookies\": [\n    {\n      \"key\": \"ccpaUUID\",\n      \"maxAge\": 31536000,\n      \"shareRootDomain\": true,\n      \"value\": \"08dcd5d5-738f-4e0d-8bd8-7ee9f24ac053\",\n      \"setPath\": true\n    },\n    {\n      \"key\": \"dnsDisplayed\",\n      \"maxAge\": 31536000,\n      \"value\": \"false\",\n      \"setPath\": true\n    },\n    {\n      \"key\": \"ccpaApplies\",\n      \"maxAge\": 31536000,\n      \"value\": \"true\",\n      \"setPath\": true\n    },\n    {\n      \"key\": \"signedLspa\",\n      \"maxAge\": 31536000,\n      \"value\": \"false\",\n      \"setPath\": true\n    }\n  ],\n  \"ccpaApplies\": true,\n  \"dateCreated\": \"2022-12-08T09:24:13.542Z\",\n  \"newUser\": false,\n  \"rejectedAll\": false,\n  \"rejectedCategories\": [\n  ],\n  \"rejectedVendors\": [\n  ],\n  \"signedLspa\": false,\n  \"status\": \"consentedAll\",\n  \"uspstring\": \"1YNN\",\n  \"uuid\": \"08dcd5d5-738f-4e0d-8bd8-7ee9f24ac053\"\n}",
  "IABTCF_PurposeOneTreatment": 0,
  "IABTCF_PublisherConsent": "0000000000",
  "IABTCF_gdprApplies": 1,
  "sp.ccpa.key.applies": true,
  "sp.key.local.state": "{\"ccpa\":{\"dnsDisplayed\":true,\"expiration\":\"2023-12-08T14:11:48.006Z\",\"messageId\":616558,\"mmsCookies\":[\"_sp_v1_uid=1:780:3955bcb6-4f6a-41af-b0c7-ba264fa13458\",\"_sp_v1_data=2:458337:1670508707:0:1:0:1:0:0:_:-1\",\"_sp_v1_ss=1:H4sIAAAAAAAAAItWqo5RKimOUbKKBjLyQAyD2lidGKVUEDOvNCcHyC4BK6iurVWKBQAW54XRMAAAAA%3D%3D\",\"_sp_v1_opt=1:\",\"_sp_v1_consent=1!0:-1:-1:-1:-1:-1\",\"_sp_v1_stage=\",\"_sp_v1_csv=null\",\"_sp_v1_lt=1:\"],\"propertyId\":16893,\"status\":\"rejectedSome\",\"uuid\":\"175dbca3-a884-433c-98d5-df4b679a9f62\"},\"gdpr\":{\"expiration\":\"2023-12-08T14:11:47.912Z\",\"messageId\":525881,\"mmsCookies\":[\"_sp_v1_uid=1:162:299c5d95-d2f1-41c8-ac0e-dc69b0b5bb97\",\"_sp_v1_data=2:372393:1670508707:0:1:0:1:0:0:_:-1\",\"_sp_v1_ss=1:H4sIAAAAAAAAAItWqo5RKimOUbKKBjLyQAyD2lidGKVUEDOvNCcHyC4BK6iurVWKBQAW54XRMAAAAA%3D%3D\",\"_sp_v1_opt=1:\",\"_sp_v1_consent=1!-1:-1:-1:-1:-1:-1\",\"_sp_v1_stage=\",\"_sp_v1_csv=null\",\"_sp_v1_lt=1:\"],\"propertyId\":16893,\"uuid\":\"74b71f2c-451b-4162-bd1b-552e694f265a_14\"}}",
  "sp.key.messages.v7.local.state": "{\n  \"gdpr\": {\n    \"mmsCookies\": [\n      \"_sp_v1_ss=1:H4sIAAAAAAAAAItWqo5RKimOUbLKK83J0YlRSkVil4AlqmtrlWIBBrfP6SgAAAA%3D\"\n    ],\n    \"propertyId\": 16893,\n    \"messageId\": 0\n  },\n  \"ccpa\": {\n    \"mmsCookies\": [\n      \"_sp_v1_ss=1:H4sIAAAAAAAAAItWqo5RKimOUbLKK83J0YlRSkVil4AlqmtrlWIBBrfP6SgAAAA%3D\"\n    ],\n    \"propertyId\": 16893,\n    \"messageId\": 0\n  }\n}",
  "IABTCF_PublisherRestrictions2": "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
  "IABTCF_VendorLegitimateInterests": "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
  "IABTCF_UseNonStandardStacks": 0,
  "IABTCF_PublisherCustomPurposesConsents": "0000000000",
  "IABTCF_PolicyVersion": 2,
  "IABTCF_AddtlConsent": "1~899",
  "IABTCF_PurposeLegitimateInterests": "0100000000",
  "sp.key.saved.consent": true,
  "sp.key.consent.status": "{\n  \"consentedAll\": true,\n  \"consentedToAny\": true,\n  \"granularStatus\": {\n    \"defaultConsent\": false,\n    \"previousOptInAll\": false,\n    \"purposeConsent\": \"ALL\",\n    \"purposeLegInt\": \"ALL\",\n    \"vendorConsent\": \"ALL\",\n    \"vendorLegInt\": \"ALL\"\n  },\n  \"hasConsentData\": true,\n  \"rejectedAny\": false,\n  \"rejectedLI\": false\n}",
  "IABTCF_CmpSdkID": 6,
  "IABTCF_CmpSdkVersion": 2,
  "IABTCF_PublisherCustomPurposesLegitimateInterests": "0000000000",
  "sp.key.meta.data": "{\n  \"ccpa\": {\n    \"applies\": true\n  },\n  \"gdpr\": {\n    \"additionsChangeDate\": \"2021-07-09T09:55:20.433Z\",\n    \"applies\": true,\n    \"getMessageAlways\": false,\n    \"_id\": \"608badf1a22863112f750a18\",\n    \"legalBasisChangeDate\": \"2021-06-30T15:52:45.117Z\",\n    \"version\": 27\n  }\n}",
  "IABUSPrivacy_String": "1YNN",
  "IABTCF_VendorConsents": "000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
  "IABTCF_PublisherCC": "DE",
  "IABTCF_SpecialFeaturesOptIns": "00",
  "client_pref_key": "client_pref_val",
  "sp.ccpa.consent.resp": "{\"uspstring\":\"1YYN\",\"status\":\"rejectedSome\",\"rejectedVendors\":[],\"rejectedCategories\":[\"608bae685461ff11a2c28653\"],\"signedLspa\":false,\"rejectedAll\":false}",
  "IABTCF_PublisherLegitimateInterests": "0000000000",
  "sp.key.gdpr": "{\"applies\":true,\"message\":{\"categories\":[{\"_id\":\"608bad95d08d3112188e0e29\",\"description\":\"Cookies, device identifiers, or other information can be stored or accessed on your device for the purposes presented to you.\",\"name\":\"Store and\\/or access information on a device\",\"type\":\"IAB_PURPOSE\"},{\"_id\":\"608bad95d08d3112188e0e2f\",\"description\":\"Ads can be shown to you based on the content you’re viewing, the app you’re using, your approximate location, or your device type.\",\"name\":\"Select basic ads\",\"type\":\"IAB_PURPOSE\"},{\"_id\":\"608bad95d08d3112188e0e36\",\"description\":\"A profile can be built about you and your interests to show you personalised ads that are relevant to you.\",\"name\":\"Create a personalised ads profile\",\"type\":\"IAB_PURPOSE\"},{\"_id\":\"608bad95d08d3112188e0e3d\",\"description\":\"Personalised ads can be shown to you based on a profile about you.\",\"name\":\"Select personalised ads\",\"type\":\"IAB_PURPOSE\"},{\"_id\":\"608bad96d08d3112188e0e4d\",\"description\":\"The performance and effectiveness of ads that you see or interact with can be measured.\",\"name\":\"Measure ad performance\",\"type\":\"IAB_PURPOSE\"},{\"_id\":\"608bad96d08d3112188e0e59\",\"description\":\"Market research can be used to learn more about the audiences who visit sites\\/apps and view ads.\",\"name\":\"Apply market research to generate audience insights\",\"type\":\"IAB_PURPOSE\"},{\"_id\":\"608bad96d08d3112188e0e5f\",\"description\":\"Your data can be used to improve existing systems and software, and to develop new products\",\"name\":\"Develop and improve products\",\"type\":\"IAB_PURPOSE\"},{\"_id\":\"60b65857619abe242bed971e\",\"description\":\"<p>It's a custom purpose\\/category for demo.<\\/p>\",\"hasConsent\":true,\"hasLegInt\":false,\"name\":\"Our Custom Purpose\",\"type\":\"CUSTOM\"},{\"_id\":\"5e37fc3d973acf1e955b895d\",\"description\":\"Your data can be used to monitor for and prevent fraudulent activity, and ensure systems and processes work properly and securely.\",\"name\":\"Ensure security, prevent fraud, and debug\"},{\"_id\":\"5e37fc3d973acf1e955b895e\",\"description\":\"Your device can receive and send information that allows you to see and interact with ads and content.\",\"name\":\"Technically deliver ads or content\"},{\"_id\":\"5e37fc3e973acf1e955b8965\",\"description\":\"Data from offline data sources can be combined with your online activity in support of one or more purposes\",\"name\":\"Match and combine offline data sources\"},{\"_id\":\"5e37fc3e973acf1e955b8964\",\"description\":\"Different devices can be determined as belonging to you or your household in support of one or more of purposes.\",\"name\":\"Link different devices\"},{\"_id\":\"5e37fc3e973acf1e955b8963\",\"description\":\"Your device might be distinguished from other devices based on information it automatically sends, such as IP address or browser type.\",\"name\":\"Receive and use automatically-sent device characteristics for identification\"}],\"language\":\"EN\",\"message_choice\":[{\"button_text\":\"Dismiss\",\"choice_id\":4534385,\"iframe_url\":null,\"type\":15},{\"button_text\":\"1619766996522\",\"choice_id\":4534386,\"iframe_url\":\"https:\\/\\/notice.sp-prod.net\\/privacy-manager\\/index.html?message_id=488393\",\"type\":12},{\"button_text\":\"1619767038123\",\"choice_id\":4534387,\"iframe_url\":null,\"type\":13},{\"button_text\":\"1619767045875\",\"choice_id\":4534388,\"iframe_url\":null,\"type\":11}],\"message_json\":{\"message_json_string\":\"{\\\"type\\\":\\\"Notice\\\",\\\"name\\\":\\\"GDPR Message\\\",\\\"settings\\\":{\\\"selected_privacy_manager\\\":{\\\"type\\\":12,\\\"data\\\":{\\\"button_text\\\":\\\"1619766959808\\\",\\\"privacy_manager_iframe_url\\\":\\\"https:\\/\\/notice.sp-prod.net\\/privacy-manager\\/index.html?message_id=488393\\\",\\\"consent_origin\\\":\\\"https:\\/\\/sourcepoint.mgr.consensu.org\\/tcfv2\\\"}},\\\"languages\\\":{\\\"EN\\\":{\\\"iframeTitle\\\":\\\"<p>SP Consent Message<\\/p>\\\"}},\\\"iframeTitle\\\":\\\"<p>SP Consent Message<\\/p>\\\"},\\\"children\\\":[{\\\"type\\\":\\\"Text\\\",\\\"name\\\":\\\"Text\\\",\\\"settings\\\":{\\\"languages\\\":{\\\"EN\\\":{\\\"text\\\":\\\"<p>GDPR Message<\\/p>\\\"}},\\\"text\\\":\\\"<p>GDPR Message<\\/p>\\\",\\\"font\\\":{\\\"fontSize\\\":24,\\\"fontWeight\\\":\\\"400\\\",\\\"color\\\":\\\"#000000\\\",\\\"fontFamily\\\":\\\"arial, helvetica, sans-serif\\\"}},\\\"children\\\":[]},{\\\"type\\\":\\\"Button\\\",\\\"name\\\":\\\"Button\\\",\\\"settings\\\":{\\\"languages\\\":{\\\"EN\\\":{\\\"text\\\":\\\"Show Options\\\"}},\\\"text\\\":\\\"Show Options\\\",\\\"choice_option\\\":{\\\"type\\\":12,\\\"data\\\":{\\\"button_text\\\":\\\"1619766996522\\\",\\\"privacy_manager_iframe_url\\\":\\\"https:\\/\\/notice.sp-prod.net\\/privacy-manager\\/index.html?message_id=488393\\\",\\\"consent_origin\\\":\\\"https:\\/\\/sourcepoint.mgr.consensu.org\\/tcfv2\\\"}},\\\"font\\\":{\\\"fontSize\\\":14,\\\"fontWeight\\\":\\\"400\\\",\\\"color\\\":\\\"#1890ff\\\",\\\"fontFamily\\\":\\\"arial, helvetica, sans-serif\\\"},\\\"background\\\":\\\"#ffffff\\\"},\\\"children\\\":[]},{\\\"type\\\":\\\"Button\\\",\\\"name\\\":\\\"Button\\\",\\\"settings\\\":{\\\"languages\\\":{\\\"EN\\\":{\\\"text\\\":\\\"Reject All\\\"}},\\\"text\\\":\\\"Reject All\\\",\\\"choice_option\\\":{\\\"type\\\":13,\\\"data\\\":{\\\"button_text\\\":\\\"1619767038123\\\",\\\"consent_origin\\\":\\\"https:\\/\\/sourcepoint.mgr.consensu.org\\/tcfv2\\\",\\\"consent_language\\\":\\\"EN\\\"}},\\\"background\\\":\\\"#ed719e\\\"},\\\"children\\\":[]},{\\\"type\\\":\\\"Button\\\",\\\"name\\\":\\\"Button\\\",\\\"settings\\\":{\\\"languages\\\":{\\\"EN\\\":{\\\"text\\\":\\\"Accept All\\\"}},\\\"text\\\":\\\"Accept All\\\",\\\"choice_option\\\":{\\\"type\\\":11,\\\"data\\\":{\\\"button_text\\\":\\\"1619767045875\\\",\\\"consent_origin\\\":\\\"https:\\/\\/sourcepoint.mgr.consensu.org\\/tcfv2\\\",\\\"consent_language\\\":\\\"EN\\\"}}},\\\"children\\\":[]},{\\\"type\\\":\\\"Row\\\",\\\"name\\\":\\\"Row\\\",\\\"settings\\\":{},\\\"children\\\":[{\\\"type\\\":\\\"Text\\\",\\\"name\\\":\\\"Text\\\",\\\"settings\\\":{\\\"languages\\\":{\\\"EN\\\":{\\\"text\\\":\\\"<p><a href=\\\\\\\"exampleapp:\\/\\/network\\\\\\\" target=\\\\\\\"_blank\\\\\\\" aria-label=\\\\\\\"message-link\\\\\\\">metalink<\\/a><\\/p>\\\"}},\\\"text\\\":\\\"<p><a href=\\\\\\\"exampleapp:\\/\\/network\\\\\\\" target=\\\\\\\"_blank\\\\\\\" aria-label=\\\\\\\"message-link\\\\\\\">metalink<\\/a><\\/p>\\\"},\\\"children\\\":[]}]}],\\\"compliance_status\\\":true,\\\"compliance_list\\\":[{\\\"1\\\":true},{\\\"2\\\":true},{\\\"3\\\":true},{\\\"4\\\":true},{\\\"5\\\":true},{\\\"6\\\":true},{\\\"7\\\":true},{\\\"8\\\":true},{\\\"9\\\":true},{\\\"10\\\":true},{\\\"11\\\":true}]}\",\"name\":\"GDPR Message\",\"settings\":{\"iframeTitle\":\"<p>SP Consent Message<\\/p>\",\"languages\":{\"EN\":{\"iframeTitle\":\"<p>SP Consent Message<\\/p>\"}},\"selected_privacy_manager\":{\"data\":{\"button_text\":\"1619766959808\",\"consent_origin\":\"https:\\/\\/sourcepoint.mgr.consensu.org\\/tcfv2\",\"privacy_manager_iframe_url\":\"https:\\/\\/notice.sp-prod.net\\/privacy-manager\\/index.html?message_id=488393\"},\"type\":12}},\"signature\":[235,149,96,143,109,145,136,28,93,136,106,192,186,17,83,82,236,190,29,212,201,97,195,230,253,36,122,248,19,72,51,61,140,211,43,199,109,222,125,223,60,236,23,170,247,52,120,5,254,161,44,29,116,130,90,134,136,211,161,75,198,132,166,9],\"type\":\"Notice\"},\"site_id\":16893},\"messageMetaData\":{\"bucket\":162,\"categoryId\":1,\"messageId\":525881,\"msgDescription\":\"\",\"prtnUUID\":\"454933ba-c2a5-4b4f-8277-86b4fcacbc89\",\"subCategoryId\":5},\"type\":\"GDPR\",\"url\":\"https:\\/\\/cdn.privacy-mgmt.com\\/index.html?consentUUID=74b71f2c-451b-4162-bd1b-552e694f265a&message_id=525881&consentLanguage=EN&preload_message=true&version=v1\",\"userConsent\":{\"TCData\":{\"IABTCF_AddtlConsent\":\"1~\",\"IABTCF_CmpSdkID\":6,\"IABTCF_CmpSdkVersion\":2,\"IABTCF_PolicyVersion\":2,\"IABTCF_PublisherCC\":\"DE\",\"IABTCF_PublisherConsent\":\"0000000000\",\"IABTCF_PublisherCustomPurposesConsents\":\"0000000000\",\"IABTCF_PublisherCustomPurposesLegitimateInterests\":\"0000000000\",\"IABTCF_PublisherLegitimateInterests\":\"0000000000\",\"IABTCF_PurposeConsents\":\"0000000000\",\"IABTCF_PurposeLegitimateInterests\":\"0000000000\",\"IABTCF_PurposeOneTreatment\":0,\"IABTCF_SpecialFeaturesOptIns\":\"00\",\"IABTCF_TCString\":\"CPjq5oAPjq5oAAGABCENCtCgAAAAAAAAAAYgAAAAAAAA.YAAAAAAAAAAA\",\"IABTCF_UseNonStandardStacks\":0,\"IABTCF_VendorConsents\":\"000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\"IABTCF_VendorLegitimateInterests\":\"000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\"IABTCF_gdprApplies\":1},\"addtlConsent\":\"1~\",\"childPmId\":null,\"consentedToAll\":null,\"customVendorsResponse\":{\"consentedPurposes\":[],\"consentedVendors\":[{\"_id\":\"5f1b2fbeb8e05c306f2a1eb9\",\"name\":\"QuarticON\",\"vendorType\":\"CUSTOM\"}],\"legIntPurposes\":[{\"_id\":\"608bad95d08d3112188e0e2f\",\"name\":\"Select basic ads\"}]},\"dateCreated\":\"2022-12-08T14:11:47.904Z\",\"euconsent\":\"CPjq5oAPjq5oAAGABCENCtCgAAAAAAAAAAYgAAAAAAAA.YAAAAAAAAAAA\",\"grants\":{\"5e7ced57b8e05c485246cce0\":{\"purposeGrants\":{\"608bad95d08d3112188e0e29\":false,\"608bad95d08d3112188e0e36\":false,\"608bad96d08d3112188e0e59\":false,\"60b65857619abe242bed971e\":false},\"vendorGrant\":false},\"5f1b2fbeb8e05c306f2a1eb9\":{\"purposeGrants\":{\"608bad95d08d3112188e0e29\":false,\"608bad95d08d3112188e0e2f\":true},\"vendorGrant\":false},\"5ff4d000a228633ac048be41\":{\"purposeGrants\":{\"608bad95d08d3112188e0e2f\":false,\"608bad95d08d3112188e0e36\":false},\"vendorGrant\":false}},\"hasConsentData\":false,\"rejectedAny\":null}}",
  "sp.key.gdpr.message.subcategory": 5,
  "sp.gdpr.key.applies": true,
  "sp.gdpr.consent.resp": "{\"euconsent\":\"CPjq5oAPjq5oAAGABBENCtCgAIAAAEAAAAYgASAAAAAAQAAAAhAAIAJA.YAAAAAAAAAAA\",\"addtlConsent\":\"1~899\",\"specialFeatures\":[],\"legIntCategories\":[\"608bad95d08d3112188e0e2f\"],\"grants\":{\"5e7ced57b8e05c485246cce0\":{\"vendorGrant\":false,\"purposeGrants\":{\"608bad95d08d3112188e0e29\":true,\"608bad95d08d3112188e0e36\":false,\"608bad96d08d3112188e0e59\":false,\"60b65857619abe242bed971e\":false}},\"5f1b2fbeb8e05c306f2a1eb9\":{\"vendorGrant\":true,\"purposeGrants\":{\"608bad95d08d3112188e0e29\":true,\"608bad95d08d3112188e0e2f\":true}},\"5ff4d000a228633ac048be41\":{\"vendorGrant\":false,\"purposeGrants\":{\"608bad95d08d3112188e0e2f\":false,\"608bad95d08d3112188e0e36\":false}}},\"acceptedVendors\":[\"5f1b2fbeb8e05c306f2a1eb9\",\"5e7ced57b8e05c485246cce0\"],\"acceptedCategories\":[\"608bad95d08d3112188e0e29\"],\"consentedToAll\":false,\"rejectedAny\":true,\"dateCreated\":\"2022-12-08T14:11:58.091Z\",\"customVendorsResponse\":{\"consentedVendors\":[{\"_id\":\"5f1b2fbeb8e05c306f2a1eb9\",\"name\":\"QuarticON\",\"vendorType\":\"CUSTOM\"}],\"consentedPurposes\":[{\"_id\":\"608bad95d08d3112188e0e29\",\"name\":\"Store and\\/or access information on a device\"}],\"legIntPurposes\":[{\"_id\":\"608bad95d08d3112188e0e2f\",\"name\":\"Select basic ads\"}]},\"TCData\":{\"IABTCF_AddtlConsent\":\"1~899\",\"IABTCF_CmpSdkID\":6,\"IABTCF_CmpSdkVersion\":2,\"IABTCF_PolicyVersion\":2,\"IABTCF_PublisherCC\":\"DE\",\"IABTCF_PurposeOneTreatment\":0,\"IABTCF_UseNonStandardStacks\":0,\"IABTCF_TCString\":\"CPjq5oAPjq5oAAGABBENCtCgAIAAAEAAAAYgASAAAAAAQAAAAhAAIAJA.YAAAAAAAAAAA\",\"IABTCF_VendorConsents\":\"000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\"IABTCF_VendorLegitimateInterests\":\"000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\"IABTCF_PurposeConsents\":\"1000000000\",\"IABTCF_PurposeLegitimateInterests\":\"0100000000\",\"IABTCF_SpecialFeaturesOptIns\":\"00\",\"IABTCF_PublisherConsent\":\"0000000000\",\"IABTCF_PublisherLegitimateInterests\":\"0000000000\",\"IABTCF_PublisherCustomPurposesConsents\":\"0000000000\",\"IABTCF_PublisherCustomPurposesLegitimateInterests\":\"0000000000\",\"IABTCF_gdprApplies\":1,\"IABTCF_PublisherRestrictions2\":\"000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\"}}",
  "IABTCF_TCString": "CPjq5oAPjq5oAAGABBENCtCgAIAAAEAAAAYgASAAAAAAQAAAAhAAIAJA.YAAAAAAAAAAA",
  "sp.key.pv.data": "{\n  \"gdpr\": {\n    \"cookies\": [\n      {\n        \"key\": \"consentUUID\",\n        \"maxAge\": 31536000,\n        \"session\": false,\n        \"shareRootDomain\": false,\n        \"value\": \"af60a9f6-e3e3-442d-86a6-b6e9b5ca5b45\"\n      }\n    ],\n    \"uuid\": \"af60a9f6-e3e3-442d-86a6-b6e9b5ca5b45\"\n  }\n}",
  "sp.gdpr.key.message.metadata": "{\n  \"bucket\": 92,\n  \"categoryId\": 1,\n  \"messageId\": 525881,\n  \"msgDescription\": \"\",\n  \"prtnUUID\": \"454933ba-c2a5-4b4f-8277-86b4fcacbc89\",\n  \"subCategoryId\": 5\n}",
  "IABTCF_PurposeConsents": "1000000000",
  "sp.gdpr.key.consent.status": "{\n  \"addtlConsent\": \"1~899\",\n  \"consentStatus\": {\n    \"consentedAll\": true,\n    \"consentedToAny\": true,\n    \"granularStatus\": {\n      \"defaultConsent\": false,\n      \"previousOptInAll\": false,\n      \"purposeConsent\": \"ALL\",\n      \"purposeLegInt\": \"ALL\",\n      \"vendorConsent\": \"ALL\",\n      \"vendorLegInt\": \"ALL\"\n    },\n    \"hasConsentData\": true,\n    \"rejectedAny\": false,\n    \"rejectedLI\": false\n  },\n  \"consentUUID\": \"d82edf09-464c-4d2b-9344-5509ceb4ad06_14\",\n  \"cookieExpirationDays\": 365,\n  \"customVendorsResponse\": {\n    \"consentedPurposes\": [\n      {\n        \"_id\": \"608bad95d08d3112188e0e29\",\n        \"name\": \"Store and/or access information on a device\"\n      },\n      {\n        \"_id\": \"608bad95d08d3112188e0e2f\",\n        \"name\": \"Select basic ads\"\n      },\n      {\n        \"_id\": \"608bad95d08d3112188e0e36\",\n        \"name\": \"Create a personalised ads profile\"\n      },\n      {\n        \"_id\": \"608bad96d08d3112188e0e59\",\n        \"name\": \"Apply market research to generate audience insights\"\n      },\n      {\n        \"_id\": \"60b65857619abe242bed971e\",\n        \"name\": \"Our Custom Purpose\"\n      }\n    ],\n    \"consentedVendors\": [\n      {\n        \"_id\": \"5f1b2fbeb8e05c306f2a1eb9\",\n        \"name\": \"QuarticON\",\n        \"vendorType\": \"CUSTOM\"\n      },\n      {\n        \"_id\": \"5ff4d000a228633ac048be41\",\n        \"name\": \"Game Accounts\",\n        \"vendorType\": \"CUSTOM\"\n      }\n    ],\n    \"legIntPurposes\": [\n      {\n        \"_id\": \"608bad95d08d3112188e0e2f\",\n        \"name\": \"Select basic ads\"\n      }\n    ]\n  },\n  \"dateCreated\": \"2022-12-08T09:24:13.037Z\",\n  \"euconsent\": \"CPjq5oAPjq5oAAGABCENCtCsAOCAAEAAAAYgASAAAAAAQAAACBAAIAJBAAEAEg4ACACQoABABIAA.YAAAAAAAAAAA\",\n  \"gdprApplies\": true,\n  \"grants\": {\n    \"5e7ced57b8e05c485246cce0\": {\n      \"vendorGrant\": true,\n      \"purposeGrants\": {\n        \"608bad95d08d3112188e0e29\": true,\n        \"608bad95d08d3112188e0e36\": true,\n        \"608bad96d08d3112188e0e59\": true,\n        \"60b65857619abe242bed971e\": true\n      }\n    },\n    \"5f1b2fbeb8e05c306f2a1eb9\": {\n      \"vendorGrant\": true,\n      \"purposeGrants\": {\n        \"608bad95d08d3112188e0e29\": true,\n        \"608bad95d08d3112188e0e2f\": true\n      }\n    },\n    \"5ff4d000a228633ac048be41\": {\n      \"vendorGrant\": true,\n      \"purposeGrants\": {\n        \"608bad95d08d3112188e0e2f\": true,\n        \"608bad95d08d3112188e0e36\": true\n      }\n    }\n  },\n  \"TCData\": {\n    \"IABTCF_AddtlConsent\": \"1~899\",\n    \"IABTCF_CmpSdkID\": \"6\",\n    \"IABTCF_CmpSdkVersion\": \"2\",\n    \"IABTCF_PolicyVersion\": \"2\",\n    \"IABTCF_PublisherCC\": \"DE\",\n    \"IABTCF_PurposeOneTreatment\": \"0\",\n    \"IABTCF_UseNonStandardStacks\": \"0\",\n    \"IABTCF_TCString\": \"CPjq5oAPjq5oAAGABCENCtCsAOCAAEAAAAYgASAAAAAAQAAACBAAIAJBAAEAEg4ACACQoABABIAA.YAAAAAAAAAAA\",\n    \"IABTCF_VendorConsents\": \"000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n    \"IABTCF_VendorLegitimateInterests\": \"000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n    \"IABTCF_PurposeConsents\": \"1110000010\",\n    \"IABTCF_PurposeLegitimateInterests\": \"0100000000\",\n    \"IABTCF_SpecialFeaturesOptIns\": \"11\",\n    \"IABTCF_PublisherConsent\": \"0000000000\",\n    \"IABTCF_PublisherLegitimateInterests\": \"0000000000\",\n    \"IABTCF_PublisherCustomPurposesConsents\": \"0000000000\",\n    \"IABTCF_PublisherCustomPurposesLegitimateInterests\": \"0000000000\",\n    \"IABTCF_gdprApplies\": \"1\",\n    \"IABTCF_PublisherRestrictions2\": \"000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n    \"IABTCF_PublisherRestrictions4\": \"000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n    \"IABTCF_PublisherRestrictions7\": \"000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n    \"IABTCF_PublisherRestrictions10\": \"000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\"\n  },\n  \"localDataCurrent\": false,\n  \"uuid\": \"d82edf09-464c-4d2b-9344-5509ceb4ad06_14\",\n  \"vendorListId\": \"608badf1a22863112f750a18\"\n}",
  "sp.key.ccpa": "{\"applies\":true,\"message\":{\"message_choice\":[{\"button_text\":\"Dismiss\",\"choice_id\":5681956,\"iframe_url\":null,\"type\":15},{\"button_text\":\"1623758277257\",\"choice_id\":5681957,\"iframe_url\":\"https:\\/\\/ccpa-notice.sp-prod.net\\/ccpa_pm\\/index.html?message_id=509688\",\"type\":12},{\"button_text\":\"1623758165801\",\"choice_id\":5681958,\"iframe_url\":null,\"type\":13},{\"button_text\":\"1623758176582\",\"choice_id\":5681959,\"iframe_url\":null,\"type\":11}],\"message_json\":{\"message_json_string\":\"{\\\"type\\\":\\\"Notice\\\",\\\"name\\\":\\\"CCPA Message\\\",\\\"settings\\\":{},\\\"children\\\":[{\\\"type\\\":\\\"Text\\\",\\\"name\\\":\\\"Text\\\",\\\"settings\\\":{\\\"languages\\\":{\\\"EN\\\":{\\\"text\\\":\\\"<p>CCPA Message<\\/p>\\\"}},\\\"text\\\":\\\"<p>CCPA Message<\\/p>\\\"},\\\"children\\\":[]},{\\\"type\\\":\\\"Button\\\",\\\"name\\\":\\\"Button\\\",\\\"settings\\\":{\\\"languages\\\":{\\\"EN\\\":{\\\"text\\\":\\\"Show Options\\\"}},\\\"text\\\":\\\"Show Options\\\",\\\"choice_option\\\":{\\\"type\\\":12,\\\"data\\\":{\\\"button_text\\\":\\\"1623758277257\\\",\\\"privacy_manager_iframe_url\\\":\\\"https:\\/\\/ccpa-notice.sp-prod.net\\/ccpa_pm\\/index.html?message_id=509688\\\",\\\"consent_origin\\\":\\\"https:\\/\\/ccpa-service.sp-prod.net\\\"}}},\\\"children\\\":[]},{\\\"type\\\":\\\"Button\\\",\\\"name\\\":\\\"Button\\\",\\\"settings\\\":{\\\"languages\\\":{\\\"EN\\\":{\\\"text\\\":\\\"Reject All\\\"}},\\\"text\\\":\\\"Reject All\\\",\\\"choice_option\\\":{\\\"type\\\":13,\\\"data\\\":{\\\"button_text\\\":\\\"1623758165801\\\",\\\"consent_origin\\\":\\\"https:\\/\\/ccpa-service.sp-prod.net\\\",\\\"consent_language\\\":\\\"EN\\\"}},\\\"background\\\":\\\"#9a244f\\\"},\\\"children\\\":[]},{\\\"type\\\":\\\"Button\\\",\\\"name\\\":\\\"Button\\\",\\\"settings\\\":{\\\"languages\\\":{\\\"EN\\\":{\\\"text\\\":\\\"Accept All\\\"}},\\\"text\\\":\\\"Accept All\\\",\\\"choice_option\\\":{\\\"type\\\":11,\\\"data\\\":{\\\"button_text\\\":\\\"1623758176582\\\",\\\"consent_origin\\\":\\\"https:\\/\\/ccpa-service.sp-prod.net\\\",\\\"consent_language\\\":\\\"EN\\\"}},\\\"background\\\":\\\"#4f7a28\\\"},\\\"children\\\":[]}],\\\"compliance_status\\\":false,\\\"compliance_list\\\":[]}\",\"name\":\"CCPA Message\",\"settings\":{},\"signature\":[180,139,208,159,150,52,33,251,87,207,64,248,255,84,201,7,141,197,107,32,181,3,205,144,199,255,101,82,87,35,123,41,52,2,44,110,7,114,9,78,242,57,177,23,70,241,39,206,92,37,120,165,67,42,243,33,96,114,75,181,13,206,191,7],\"type\":\"Notice\"},\"site_id\":16893},\"messageMetaData\":{\"bucket\":780,\"categoryId\":2,\"messageId\":616558,\"msgDescription\":\"\",\"prtnUUID\":\"5cfc830e-3d3a-4b18-979d-c543dd065516\",\"subCategoryId\":1},\"type\":\"CCPA\",\"url\":\"https:\\/\\/cdn.privacy-mgmt.com\\/index.html?ccpaUUID=175dbca3-a884-433c-98d5-df4b679a9f62&message_id=616558&consentLanguage=EN&preload_message=true&version=v1\",\"userConsent\":{\"dateCreated\":\"2022-12-08T14:11:47.959Z\",\"newUser\":true,\"rejectedAll\":false,\"rejectedCategories\":[],\"rejectedVendors\":[],\"signedLspa\":false,\"status\":\"rejectedNone\",\"uspstring\":\"1YNN\"}}",
  "sp.key.gdpr.applies": true
}
""".trimIndent()
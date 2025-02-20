package com.sourcepoint.cmplibrary

import android.R.id.content
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import com.sourcepoint.cmplibrary.consent.CustomConsentClient
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.mobile_core.SPConsentWebView
import com.sourcepoint.cmplibrary.mobile_core.SPMessageUI
import com.sourcepoint.cmplibrary.mobile_core.SPMessageUIClient
import com.sourcepoint.cmplibrary.mobile_core.appendQueryParameterIfPresent
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.ConsentActionImplOptimized
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.MessageType
import com.sourcepoint.cmplibrary.model.exposed.MessageType.LEGACY_OTT
import com.sourcepoint.cmplibrary.model.exposed.MessageType.MOBILE
import com.sourcepoint.cmplibrary.model.exposed.MessageType.OTT
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.util.SpBackPressOttDelegate
import com.sourcepoint.cmplibrary.util.extensions.toJsonObject
import com.sourcepoint.mobile_core.Coordinator
import com.sourcepoint.mobile_core.models.MessageToDisplay
import com.sourcepoint.mobile_core.models.SPAction
import com.sourcepoint.mobile_core.models.SPActionType
import com.sourcepoint.mobile_core.models.consents.SPUserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.ref.WeakReference

fun launch(task: suspend () -> Unit) {
    CoroutineScope(Dispatchers.Default).launch { task() }
}

fun runOnMain(task: () -> Unit) {
    Handler(Looper.getMainLooper()).post(task)
}

// TODO: move the url methods below to their own util class
val basePmPaths = mapOf(
    CampaignType.GDPR to mapOf(
        LEGACY_OTT to "privacy-manager-ott/index.html",
        OTT to "native-ott/index.html",
        MOBILE to "privacy-manager/index.html"
    ),
    CampaignType.CCPA to mapOf(
        LEGACY_OTT to "ccpa_ott/index.html",
        OTT to "native-ott/index.html",
        MOBILE to "ccpa_pm/index.html"
    ),
    CampaignType.USNAT to mapOf(
        LEGACY_OTT to "ccpa_ott/index.html",
        OTT to "native-ott/index.html",
        MOBILE to "us_pm/index.html"
    ),
)

// TODO: guard against not finding the correct path for the campaign/pm type?
fun basePmUrlFor(campaignType: CampaignType, pmType: MessageType) =
    "https://cdn.privacy-mgmt.com/" + (basePmPaths[campaignType]?.get(pmType) ?: "")

fun buildPMUrl(
    campaignType: CampaignType,
    pmId: String,
    propertyId: Int,
    pmType: MessageType = MOBILE,
    baseUrl: String? = basePmUrlFor(campaignType, pmType),
    userData: SPUserData,
    language: String?,
    pmTab: PMTab?,
): String {
    val uuidQueryParam = when(campaignType) {
        CampaignType.CCPA -> "ccpaUUID" to userData.ccpa?.consents?.uuid
        CampaignType.GDPR -> "consentUUID" to userData.gdpr?.consents?.uuid
        CampaignType.USNAT -> "consentUUID" to userData.usnat?.consents?.uuid
        else -> "consentUUID" to null
    }
    return baseUrl.let {
        Uri.parse(it).buildUpon()
            .appendQueryParameterIfPresent("consentLanguage", language)
            .appendQueryParameterIfPresent(uuidQueryParam.first, uuidQueryParam.second)
            .appendQueryParameterIfPresent("pmTab", pmTab?.key)
            .appendQueryParameter("message_id", pmId)
            .appendQueryParameter("site_id", propertyId.toString())
            .appendQueryParameter("preload_consent", "true")
            .build()
            .toString()
    }
}

class SpConsentLibMobileCore(
    private val propertyId: Int,
    private val language: MessageLanguage,
    private val activity: WeakReference<Activity>,
    private val context: Context,
    private val coordinator: Coordinator,
    private val spClient: SpClient,
): SpConsentLib, SPMessageUIClient {
    private var messagesToDisplay: ArrayDeque<MessageToDisplay> = ArrayDeque(emptyList())

    private val messageUI: SPMessageUI by lazy {
        SPConsentWebView(context = context, messageUIClient = this, propertyId = propertyId)
    }

    private val mainView: ViewGroup? get() = activity.get()?.findViewById(content)

    private val userData: SPUserData get() = coordinator.userData

    override fun loadMessage() {
        loadMessage(authId = null, pubData = null, cmpViewId = null)
    }

    override fun loadMessage(cmpViewId: Int) {
        loadMessage(authId = null, pubData = null, cmpViewId = cmpViewId)
    }

    override fun loadMessage(pubData: JSONObject?) {
        loadMessage(authId = null, pubData = pubData)
    }

    override fun loadMessage(authId: String?) {
        loadMessage(authId = authId, pubData = null)
    }

    override fun loadMessage(authId: String?, pubData: JSONObject?, cmpViewId: Int?) {
        launch {
            messagesToDisplay = ArrayDeque(coordinator.loadMessages(authId = authId, pubData = pubData?.toJsonObject()))
            renderNextMessageIfAny()
        }
    }

    private fun renderNextMessageIfAny() {
        if(messagesToDisplay.isEmpty()) {
            spClient.onSpFinished(SPConsents(coordinator.userData))
        } else {
            val messageToRender = messagesToDisplay.removeFirst()
            messageUI.load(
                message = messageToRender.message,
                url = messageToRender.url,
                campaignType = CampaignType.fromCore(messageToRender.type),
                userData = userData
            )
        }
    }

    override fun customConsentGDPR(
        vendors: List<String>,
        categories: List<String>,
        legIntCategories: List<String>,
        success: (SPConsents?) -> Unit
    ) {
        launch {
            coordinator.customConsentGDPR(
                vendors = vendors,
                categories = categories,
                legIntCategories = legIntCategories
            )
            success(SPConsents(coordinator.userData))
        }
    }

    override fun customConsentGDPR(
        vendors: Array<String>,
        categories: Array<String>,
        legIntCategories: Array<String>,
        successCallback: CustomConsentClient
    ) {
        customConsentGDPR(
            vendors = vendors.toList(),
            categories = categories.toList(),
            legIntCategories = legIntCategories.toList()
        ) {
            // TODO: make sure to encode it to json string
            successCallback.transferCustomConsentToUnity(it.toString())
        }
    }

    override fun deleteCustomConsentTo(
        vendors: List<String>,
        categories: List<String>,
        legIntCategories: List<String>,
        success: (SPConsents?) -> Unit
    ) {
        launch {
            coordinator.deleteCustomConsentGDPR(
                vendors = vendors,
                categories = categories,
                legIntCategories = legIntCategories
            )
            success(SPConsents(coordinator.userData))
        }
    }

    override fun deleteCustomConsentTo(
        vendors: Array<String>,
        categories: Array<String>,
        legIntCategories: Array<String>,
        successCallback: CustomConsentClient
    ) {
        deleteCustomConsentTo(
            vendors = vendors.toList(),
            categories = categories.toList(),
            legIntCategories = legIntCategories.toList()
        ) {
            // TODO: make sure to encode it to json string
            successCallback.transferCustomConsentToUnity(it.toString())
        }
    }

    override fun rejectAll(campaignType: CampaignType) {
        launch {
            coordinator.reportAction(SPAction(
                type = SPActionType.RejectAll,
                campaignType = campaignType.toCore()
            ))
        }
    }

    override fun loadPrivacyManager(pmId: String, campaignType: CampaignType) {
        internalLoadPM(pmId = pmId, campaignType = campaignType)
    }

    override fun loadPrivacyManager(pmId: String, pmTab: PMTab, campaignType: CampaignType) {
        internalLoadPM(pmId = pmId, campaignType = campaignType, pmTab = pmTab)
    }

    override fun loadPrivacyManager(
        pmId: String,
        pmTab: PMTab,
        campaignType: CampaignType,
        useGroupPmIfAvailable: Boolean // TODO: use group pm id
    ) {
        internalLoadPM(
            pmId = pmId,
            pmTab = pmTab,
            campaignType = campaignType,
            useGroupPmIfAvailable = useGroupPmIfAvailable
        )
    }

    override fun loadPrivacyManager(
        pmId: String,
        campaignType: CampaignType,
        messageType: MessageType
    ) {
        internalLoadPM(pmId = pmId, campaignType = campaignType, messageType = messageType)
    }

    override fun loadPrivacyManager(
        pmId: String,
        pmTab: PMTab,
        campaignType: CampaignType,
        messageType: MessageType
    ) {
        internalLoadPM(pmId = pmId, pmTab = pmTab, campaignType = campaignType, messageType = messageType)
    }

    override fun loadPrivacyManager(
        pmId: String,
        pmTab: PMTab,
        campaignType: CampaignType,
        useGroupPmIfAvailable: Boolean,
        messageType: MessageType
    ) {
        internalLoadPM(
            pmId = pmId,
            pmTab = pmTab,
            campaignType = campaignType,
            useGroupPmIfAvailable = useGroupPmIfAvailable,
            messageType = messageType
        )
    }

    private fun internalLoadPM(
        pmId: String,
        campaignType: CampaignType,
        pmTab: PMTab? = null,
        useGroupPmIfAvailable: Boolean? = null, // TODO: use group pm id
        messageType: MessageType = MOBILE
    ) {
        messageUI.load(
            url = buildPMUrl(
                campaignType = campaignType,
                pmId = pmId,
                propertyId = propertyId,
                userData = userData,
                language = language.value,
                pmTab = pmTab,
                pmType = messageType
            ),
            campaignType = campaignType,
            userData = userData
        )
    }

    override fun showView(view: View) {
        view.layoutParams = ViewGroup.LayoutParams(0, 0)
        view.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        view.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        view.bringToFront()
        view.requestLayout()
        view.requestFocus()
        mainView?.addView(view)
    }

    override fun removeView(view: View) {
        mainView?.removeView(view)
    }

    override fun dispose() {
        TODO("Not yet implemented")
    }

    override fun handleOnBackPress(
        isMessageDismissible: Boolean,
        ottDelegate: SpBackPressOttDelegate
    ) {
        TODO("Not yet implemented")
    }

    override fun handleOnBackPress(isMessageDismissible: Boolean, onHomePage: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun loaded(view: View) {
        spClient.onUIReady(view)
    }

    override fun onAction(view: View, action: ConsentAction) {
        launch {
            val userAction = spClient.onAction(view, action) as ConsentActionImplOptimized
            when(userAction.actionType) {
                ActionType.ACCEPT_ALL, ActionType.REJECT_ALL, ActionType.SAVE_AND_EXIT -> {
                    launch { coordinator.reportAction(userAction.toCore()) }
                }
                else -> {}
            }
        }
    }

    override fun onError() {
        spClient.onError(Exception())
    }

    override fun finished(view: View) {
        spClient.onUIFinished(view)
        renderNextMessageIfAny()
    }
}

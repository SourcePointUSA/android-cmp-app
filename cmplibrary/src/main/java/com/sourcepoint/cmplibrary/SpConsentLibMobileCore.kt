package com.sourcepoint.cmplibrary

import android.R.id.content
import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import com.sourcepoint.cmplibrary.consent.CustomConsentClient
import com.sourcepoint.cmplibrary.exception.ConsentLibExceptionK
import com.sourcepoint.cmplibrary.exception.RenderingAppException
import com.sourcepoint.cmplibrary.integrations.didomi.DidomiIntegration
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.exposed.MessageType
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.util.SpBackPressOttDelegate
import com.sourcepoint.mobile_core.network.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.util.WeakHashMap

fun launch(task: suspend () -> Unit) {
    CoroutineScope(Dispatchers.Default).launch { task() }
}

fun runOnMain(task: () -> Unit) {
    Handler(Looper.getMainLooper()).post(task)
}

class SpConsentLibMobileCore(
    private val activity: WeakReference<Activity>?,
    private val context: Context,
    private val spClient: SpClient,
    override val hideAppsViewsFromAccessibilityWhileMessageIsDisplayed: Boolean = false,
    override val dismissMessageOnBackPress: Boolean = true,
    override var cleanUserDataOnError: Boolean = false,
    private val didomiIntegration: DidomiIntegration,
) : SpConsentLib {
    private val mainView: ViewGroup? get() = activity?.get()?.findViewById(content)

    private val accessibilityManater by lazy {
        context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    }

    // used to store accessibility state of main view's children
    private var a11ySnapshot: WeakHashMap<View?, Int> = WeakHashMap()

    // authId, pubData and cmpViewId are not handled by Didomi yet
    override fun loadMessage() = loadMessage(authId = null, pubData = null, cmpViewId = null)

    override fun loadMessage(cmpViewId: Int) =
        loadMessage(authId = null, pubData = null, cmpViewId = cmpViewId)

    override fun loadMessage(pubData: JSONObject?) = loadMessage(authId = null, pubData = pubData)

    override fun loadMessage(authId: String?) = loadMessage(authId = authId, pubData = null)

    override fun loadMessage(authId: String?, pubData: JSONObject?, cmpViewId: Int?) {
        activity?.get()?.let { act ->
            didomiIntegration.loadMessage(act)
        } ?: onError(RenderingAppException(description = "Activity not available for Didomi integration"))
    }

    override fun customConsentGDPR(
        vendors: List<String>,
        categories: List<String>,
        legIntCategories: List<String>,
        success: (SPConsents?) -> Unit
    ) = didomiIntegration.customConsentGDPR(vendors, categories, legIntCategories, success)

    override fun customConsentGDPR(
        vendors: Array<String>,
        categories: Array<String>,
        legIntCategories: Array<String>,
        successCallback: CustomConsentClient
    ) = customConsentGDPR(
        vendors = vendors.toList(),
        categories = categories.toList(),
        legIntCategories = legIntCategories.toList()
    ) { consents ->
        consents?.let { successCallback.transferCustomConsentToUnity(json.encodeToString(it)) }
    }

    override fun deleteCustomConsentTo(
        vendors: List<String>,
        categories: List<String>,
        legIntCategories: List<String>,
        success: (SPConsents?) -> Unit
    ) = didomiIntegration.deleteCustomConsentTo(vendors, categories, legIntCategories, success)

    override fun deleteCustomConsentTo(
        vendors: Array<String>,
        categories: Array<String>,
        legIntCategories: Array<String>,
        successCallback: CustomConsentClient
    ) = deleteCustomConsentTo(
        vendors = vendors.toList(),
        categories = categories.toList(),
        legIntCategories = legIntCategories.toList()
    ) { consents ->
        consents?.let { successCallback.transferCustomConsentToUnity(json.encodeToString(it)) }
    }

    override fun rejectAll() = didomiIntegration.rejectAll()

    override fun loadPrivacyManager(pmId: String) = showPrivacyManager()

    override fun loadPrivacyManager(pmId: String, pmTab: PMTab) = showPrivacyManager()

    override fun loadPrivacyManager(
        pmId: String,
        pmTab: PMTab,
        useGroupPmIfAvailable: Boolean
    ) = showPrivacyManager()

    override fun loadPrivacyManager(
        pmId: String,
        messageType: MessageType
    ) = showPrivacyManager()

    override fun loadPrivacyManager(
        pmId: String,
        pmTab: PMTab,
        messageType: MessageType
    ) = showPrivacyManager()

    override fun loadPrivacyManager(
        pmId: String,
        pmTab: PMTab,
        useGroupPmIfAvailable: Boolean,
        messageType: MessageType
    ) = showPrivacyManager()

    // The privacy manager is Didomi's preferences: pmId, tab, campaign and message type don't apply to it
    private fun showPrivacyManager() {
        activity?.get()?.let { act ->
            didomiIntegration.showPrivacyManager(act)
        } ?: onError(RenderingAppException(description = "Activity not available for Didomi integration"))
    }

    // This is an aggressive way to "hide" ui elements from accessibility services in order to give
    // the message full attention. We store a weak reference to each child, so we can later restore
    // their original accessibility state.
    private fun hideMainViewChildrenFromAccessibility(parent: ViewGroup?) {
        val parent = parent ?: return
        if (!a11ySnapshot.isEmpty()) return

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            a11ySnapshot[child] = child.importantForAccessibility
            child.importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
        }
    }

    private fun restoreChildrenA11y() {
        for ((view, accessibilityState) in a11ySnapshot.entries.toList()) {
            view?.importantForAccessibility = accessibilityState
        }
        a11ySnapshot.clear()
    }

    override fun showView(view: View) {
        mainView?.let { parentView ->
            view.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            if (hideAppsViewsFromAccessibilityWhileMessageIsDisplayed) {
                hideMainViewChildrenFromAccessibility(parentView)
            }
            parentView.addView(view)
            view.bringToFront()
            view.requestLayout()
            view.requestFocus()
            if (accessibilityManater.isEnabled && accessibilityManater.isTouchExplorationEnabled) {
                view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
            }
        }
    }

    override fun removeView(view: View) {
        mainView?.let { parentView ->
            parentView.removeView(view)
            restoreChildrenA11y()
        }
    }

    override fun dismissMessage() = didomiIntegration.dismissMessage()

    override fun dispose() = didomiIntegration.dispose()

    @Deprecated(
        message = "This method is deprecated and has no effect.",
        replaceWith = ReplaceWith(
            """
            Set dismissMessageOnBackPress = false if you wish to prevent the user from dismissing the message
            when the back button is pressed.
        """"
        )
    )
    override fun handleOnBackPress(isMessageDismissible: Boolean, ottDelegate: SpBackPressOttDelegate) {}

    @Deprecated(
        message = "This method is deprecated and has no effect.",
        replaceWith = ReplaceWith(
            """
            Set dismissMessageOnBackPress = false if you wish to prevent the user from dismissing the message
            when the back button is pressed.
        """"
        )
    )
    override fun handleOnBackPress(isMessageDismissible: Boolean, onHomePage: () -> Unit) {}

    override fun clearLocalData() = didomiIntegration.clearLocalData()

    private fun onError(error: ConsentLibExceptionK) {
        restoreChildrenA11y()
        if (cleanUserDataOnError) {
            clearLocalData()
        }
        spClient.onError(error)
    }
}

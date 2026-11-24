package com.sourcepoint.cmplibrary.creation

import android.app.Activity
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.SpConsentLib
import com.sourcepoint.cmplibrary.SpConsentLibMobileCore
import com.sourcepoint.cmplibrary.data.network.connection.ConnectionManager
import com.sourcepoint.cmplibrary.data.network.connection.ConnectionManagerImpl
import com.sourcepoint.cmplibrary.integrations.didomi.DidomiIntegration
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import java.lang.ref.WeakReference

fun makeConsentLib(spConfig: SpConfig, activity: Activity, spClient: SpClient) = makeConsentLib(
    spConfig = spConfig,
    activity = activity,
    spClient = spClient,
    connectionManager = ConnectionManagerImpl(activity.applicationContext),
    dismissMessageOnBackPress = true
)

fun makeConsentLib(
    spConfig: SpConfig,
    activity: Activity,
    spClient: SpClient,
    dismissMessageOnBackPress: Boolean = true,
    hideAppsViewsFromAccessibilityWhileMessageIsDisplayed: Boolean = false,
    @Suppress("UNUSED_PARAMETER") // Sourcepoint's network is no longer used, kept for source compatibility
    connectionManager: ConnectionManager = ConnectionManagerImpl(activity.applicationContext),
): SpConsentLib {
    // Didomi is the engine behind consent collection, UI and storage, so its config is required
    val didomiConfig = requireNotNull(spConfig.didomiConfig) {
        "SpConfig.didomiConfig is required: add it with `addDidomiConfig(DidomiConfig(apiKey = ...))`"
    }
    val didomiIntegration = DidomiIntegration(didomiConfig, spClient, spConfig.messageTimeout).also { integration ->
        integration.initialize(activity.application)
        integration.disposeWith(activity)
    }

    return SpConsentLibMobileCore(
        spClient = spClient,
        context = activity.applicationContext,
        activity = WeakReference(activity),
        dismissMessageOnBackPress = dismissMessageOnBackPress,
        hideAppsViewsFromAccessibilityWhileMessageIsDisplayed = hideAppsViewsFromAccessibilityWhileMessageIsDisplayed,
        didomiIntegration = didomiIntegration
    )
}

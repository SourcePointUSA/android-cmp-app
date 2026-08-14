package com.sourcepoint.cmplibrary.integrations.didomi

import android.app.Application
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import io.didomi.sdk.Didomi
import io.didomi.sdk.DidomiInitializeParameters

/**
 * Main orchestrator for Didomi SDK integration with Sourcepoint.
 *
 * Responsibilities:
 * - Initialize Didomi SDK with manual UI handling mode
 * - Bridge Didomi events to Sourcepoint callbacks
 * - Manage Didomi consent UI lifecycle
 * - Map consent data between SDKs
 */
class DidomiIntegration(
    private val config: DidomiConfig,
    private val spClient: SpClient
) {

    private val didomi = Didomi.getInstance()
    private val consentMapper = DidomiConsentMapper()
    private val eventAdapter = DidomiEventAdapter(spClient, consentMapper)

    private var currentView: View? = null
    private var isReady = false
    private var pendingLoadMessage = false

    init {
        setupEventCallbacks()
    }

    /**
     * Initialize Didomi SDK.
     * Must be called from Application.onCreate() or Activity.onCreate().
     */
    fun initialize(application: Application) {
        val params = DidomiInitializeParameters(
            apiKey = config.apiKey,
            noticeId = config.noticeId,
            languageCode = config.languageCode,
            localConfigurationPath = config.localConfigurationPath,
            remoteConfigurationUrl = config.remoteConfigurationUrl,
            handleConsentUIAutomatically = false // Manual UI mode
        )

        didomi.initialize(application, params)

        didomi.onReady {
            isReady = true
            if (pendingLoadMessage) {
                pendingLoadMessage = false
                checkAndLoadMessage()
            }
        }

        // Register event listener
        didomi.addEventListener(eventAdapter.didomiEventListener)
    }

    /**
     * Load consent message if needed.
     * Should be called from Activity.onResume().
     */
    fun loadMessage(activity: FragmentActivity) {
        if (!isReady) {
            pendingLoadMessage = true
            return
        }

        checkAndLoadMessage()
    }

    /**
     * Show privacy manager.
     */
    fun showPrivacyManager(activity: FragmentActivity) {
        if (!isReady) {
            return
        }

        didomi.showPreferences(activity)
    }

    /**
     * Get current consent data.
     */
    fun getUserConsents(): SPConsents {
        return consentMapper.mapToSourcepoint()
    }

    /**
     * Check if consent should be collected.
     */
    fun shouldCollectConsent(): Boolean {
        return didomi.shouldUserStatusBeCollected()
    }

    /**
     * Cleanup resources.
     */
    fun dispose() {
        currentView = null
        // Didomi doesn't have explicit dispose, managed by lifecycle
    }

    private fun checkAndLoadMessage() {
        if (!didomi.shouldUserStatusBeCollected()) {
            // User already has consent, notify immediately
            val consents = consentMapper.mapToSourcepoint()
            spClient.onConsentReady(consents)
            spClient.onSpFinished(consents)
            return
        }

        // Show notice - will trigger ConsentUIReadyEvent
        // Note: Didomi's showNotice requires an activity parameter
        // This will be handled when the activity calls loadMessage(activity)
    }

    private fun setupEventCallbacks() {
        eventAdapter.onConsentUIReady = { view ->
            currentView = view
            spClient.onUIReady(view)
        }

        eventAdapter.onConsentUIFinished = { view ->
            spClient.onUIFinished(view)

            // Get final consent and notify completion
            val consents = consentMapper.mapToSourcepoint()
            spClient.onSpFinished(consents)

            currentView = null
        }
    }

    /**
     * Show the Didomi notice UI.
     * Called after onUIReady to actually display the UI.
     */
    fun showView(view: View) {
        // In Didomi's manual mode, the integrator is responsible for adding
        // the view to the view hierarchy. This is typically done in SpClient.onUIReady()
        // No action needed here - view management is delegated to the app
    }

    /**
     * Remove the Didomi notice UI.
     * Called after onUIFinished to remove the UI from view hierarchy.
     */
    fun removeView(view: View) {
        // In Didomi's manual mode, the integrator removes the view.
        // The actual cleanup is done in the app's SpClient.onUIFinished()
        (view as? io.didomi.sdk.webview.DidomiConsentWebView)?.cleanup()
    }
}

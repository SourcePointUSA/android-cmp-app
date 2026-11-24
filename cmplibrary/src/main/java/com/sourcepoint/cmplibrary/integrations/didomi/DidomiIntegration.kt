package com.sourcepoint.cmplibrary.integrations.didomi

import android.app.Activity
import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.exception.ConsentLibExceptionK
import com.sourcepoint.cmplibrary.exception.RenderingAppException
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import io.didomi.sdk.Didomi
import io.didomi.sdk.DidomiInitializeParameters
import io.didomi.sdk.events.ErrorType
import java.lang.ref.WeakReference

/**
 * Main orchestrator for Didomi SDK integration with Sourcepoint.
 *
 * Responsibilities:
 * - Initialize Didomi SDK with manual UI handling mode
 * - Bridge Didomi events to Sourcepoint callbacks
 * - Manage Didomi consent UI lifecycle
 * - Map consent data between SDKs
 *
 * All state is confined to the main thread. Public entry points can be called from any thread and hop to main.
 */
class DidomiIntegration(
    private val config: DidomiConfig,
    private val spClient: SpClient,
    private val messageTimeoutMillis: Long = DEFAULT_MESSAGE_TIMEOUT_MILLIS,
) {

    private val didomi = Didomi.getInstance()
    private val consentMapper = DidomiConsentMapper()
    private val eventAdapter = DidomiEventAdapter(spClient, consentMapper)
    private val mainHandler = Handler(Looper.getMainLooper())

    private var currentView: View? = null
    private var initialized = false
    private var disposed = false
    private var isReady = false
    private var initError: String? = null

    // Incremented by every load, so that late events (Didomi ready, timeout) of a superseded load are ignored
    private var loadId = 0
    private var pendingLoad: PendingLoad? = null
    private var isDecidingMessage = false
    private var timeoutRunnable: Runnable? = null

    private class PendingLoad(val loadId: Int, val activity: Activity, val showsPreferences: Boolean = false)

    init {
        setupEventCallbacks()
    }

    /**
     * Initialize Didomi SDK. Safe to call more than once, only the first call has an effect.
     * Must be called from Application.onCreate() or Activity.onCreate().
     */
    fun initialize(application: Application) = onMain {
        if (initialized || disposed) return@onMain
        initialized = true

        // Didomi is a process-wide singleton: only the latest integration may listen to its events
        active?.get()?.takeIf { it !== this }?.dispose()
        active = WeakReference(this)

        val params = DidomiInitializeParameters(
            apiKey = config.apiKey,
            noticeId = config.noticeId,
            languageCode = config.languageCode,
            localConfigurationPath = config.localConfigurationPath,
            remoteConfigurationUrl = config.remoteConfigurationUrl,
            handleConsentUIAutomatically = false // Manual UI mode
        )

        // Listener first, so that no event emitted during initialization is missed
        didomi.addEventListener(eventAdapter)

        // Initializing again would reset an already ready Didomi (e.g. when the Activity is recreated)
        if (!didomi.isReady) didomi.initialize(application, params)

        // Called right away when Didomi is already ready
        didomi.onReady { onMain { didomiReady() } }
    }

    /**
     * Dispose this integration when [activity] is destroyed, so that host apps don't have to.
     * Activities that are not lifecycle owners (i.e. not a ComponentActivity) are not supported by Didomi's UI anyway.
     */
    fun disposeWith(activity: Activity) = onMain {
        (activity as? LifecycleOwner)?.lifecycle?.doOnDestroy { dispose() }
    }

    /**
     * Load consent message if needed.
     * Should be called from Activity.onResume().
     */
    fun loadMessage(activity: Activity) = onMain {
        if (disposed) return@onMain
        // Typically called from onResume: don't restart the load (and its timeout) while the UI is on screen
        if (isConsentUIDisplayed()) return@onMain
        val load = startLoad(activity)
        initError?.let { return@onMain failLoad(load.loadId, it) }
        if (isReady) {
            checkAndLoadMessage(load)
        } else {
            pendingLoad = load
        }
    }

    /**
     * Show privacy manager (Didomi's preferences).
     * Which privacy manager, tab or campaign is shown is up to the Didomi notice configuration.
     */
    fun showPrivacyManager(activity: Activity) = onMain {
        if (disposed) return@onMain
        val load = startLoad(activity)
        initError?.let { return@onMain failLoad(load.loadId, it) }
        if (isReady) {
            showPreferences(load)
        } else {
            pendingLoad = load.copyForPreferences()
        }
    }

    /**
     * Dismiss whichever Didomi consent UI is currently displayed.
     */
    fun dismissMessage() = onMain {
        if (disposed || !isReady) return@onMain
        // The notice's WebView can be on screen before Didomi reports it as visible, so don't gate on the visibility flags
        runCatching { didomi.hidePreferences() }
            .onFailure { reportError("Unable to dismiss Didomi preferences: ${it.message}") }
        runCatching { didomi.hideNotice() }
            .onFailure { reportError("Unable to dismiss Didomi notice: ${it.message}") }
    }

    // TODO: The features below are stubs, to be implemented with Didomi one at a time.

    fun rejectAll() = notImplemented("rejectAll")

    fun customConsentGDPR(
        vendors: List<String>,
        categories: List<String>,
        legIntCategories: List<String>,
        success: (SPConsents?) -> Unit
    ) = notImplemented("customConsentGDPR")

    fun deleteCustomConsentTo(
        vendors: List<String>,
        categories: List<String>,
        legIntCategories: List<String>,
        success: (SPConsents?) -> Unit
    ) = notImplemented("deleteCustomConsentTo")

    fun clearLocalData() {
        Log.w(TAG, "clearLocalData is not implemented for Didomi yet")
    }

    private fun notImplemented(feature: String) = onMain {
        if (!disposed) reportError("$feature is not implemented for Didomi yet")
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
     * Clean up resources. The integration can't be used afterwards.
     */
    fun dispose() = onMain {
        if (disposed) return@onMain
        disposed = true
        cancelTimeout()
        loadId++
        pendingLoad = null
        isDecidingMessage = false
        currentView = null
        eventAdapter.onConsentUIReady = null
        eventAdapter.onConsentUIFinished = null
        eventAdapter.onConsentChanged = null
        eventAdapter.onError = null
        runCatching { didomi.removeEventListener(eventAdapter) }
    }

    private fun setupEventCallbacks() {
        eventAdapter.onConsentUIReady = { view ->
            onMain {
                if (!disposed) {
                    cancelTimeout()
                    currentView = view
                    spClient.onUIReady(view)
                }
            }
        }

        eventAdapter.onConsentUIFinished = { view ->
            onMain {
                if (!disposed) {
                    spClient.onUIFinished(view)
                    if (currentView === view) currentView = null
                    spClient.onSpFinished(getUserConsents())
                }
            }
        }

        eventAdapter.onConsentChanged = {
            // Changes that happen while deciding the message (e.g. sync or migration) are not user actions
            onMain {
                if (!disposed && isReady && !isDecidingMessage) {
                    spClient.onConsentReady(getUserConsents())
                }
            }
        }

        eventAdapter.onError = { type, message -> onMain { if (!disposed) didomiError(type, message) } }
    }

    private fun startLoad(activity: Activity): PendingLoad {
        loadId++
        isDecidingMessage = true
        pendingLoad = null
        startTimeout(loadId)
        return PendingLoad(loadId, activity)
    }

    private fun didomiReady() {
        if (disposed) return
        isReady = true
        initError = null
        val load = pendingLoad ?: return
        pendingLoad = null
        if (load.loadId != loadId) return
        if (load.showsPreferences) showPreferences(load) else checkAndLoadMessage(load)
    }

    private fun didomiError(type: ErrorType, message: String) {
        val failsInitialization = !isReady && type in INITIALIZATION_ERRORS
        if (!failsInitialization) return reportError(message)

        // Didomi will never be ready: fail the pending load (which reports the error), or just report it
        initError = message
        val load = pendingLoad ?: return reportError(message)
        failLoad(load.loadId, message)
    }

    private fun isConsentUIDisplayed(): Boolean =
        currentView != null || (isReady && runCatching { didomi.isNoticeVisible() || didomi.isPreferencesVisible() }.getOrDefault(false))

    private fun checkAndLoadMessage(load: PendingLoad) {
        if (load.loadId != loadId) return
        val shouldCollect = runCatching { shouldCollectConsent() }.getOrElse {
            return failLoad(load.loadId, "Didomi SDK is not ready: ${it.message}")
        }
        if (!shouldCollect) {
            // Consent already collected, no UI to show
            finishLoad()
            val consents = getUserConsents()
            spClient.onConsentReady(consents)
            spClient.onSpFinished(consents)
            return
        }

        val fragmentActivity = load.fragmentActivityOrFail() ?: return
        // From here on, consent changes come from the user interacting with the UI
        isDecidingMessage = false
        // Didomi emits ConsentUIReadyEvent once the notice is created (manual UI mode)
        runCatching { didomi.setupUI(fragmentActivity) }
            .onFailure { failLoad(load.loadId, "Unable to set up Didomi consent UI: ${it.message}") }
    }

    private fun showPreferences(load: PendingLoad) {
        if (load.loadId != loadId) return
        val fragmentActivity = load.fragmentActivityOrFail() ?: return
        // Preferences are created synchronously and do not emit ConsentUIReadyEvent, so there is nothing to wait for
        finishLoad()
        runCatching { didomi.showPreferences(fragmentActivity) }
            .onFailure { reportError("Unable to show Didomi preferences: ${it.message}") }
    }

    private fun PendingLoad.fragmentActivityOrFail(): FragmentActivity? =
        (activity as? FragmentActivity) ?: run {
            failLoad(loadId, "Didomi integration requires a FragmentActivity (e.g. AppCompatActivity)")
            null
        }

    private fun PendingLoad.copyForPreferences() = PendingLoad(loadId, activity, showsPreferences = true)

    private fun finishLoad() {
        cancelTimeout()
        isDecidingMessage = false
    }

    private fun failLoad(failedLoadId: Int, message: String) {
        if (failedLoadId != loadId) return
        // The load is over: anything that comes in for it later is ignored
        loadId++
        pendingLoad = null
        finishLoad()
        reportError(message)
    }

    private fun reportError(message: String) {
        val error: ConsentLibExceptionK = RenderingAppException(description = message)
        spClient.onError(error)
    }

    private fun startTimeout(forLoadId: Int) {
        cancelTimeout()
        if (messageTimeoutMillis <= 0) return
        val runnable = Runnable {
            timeoutRunnable = null
            failLoad(forLoadId, "Didomi consent UI was not ready within $messageTimeoutMillis ms")
        }
        timeoutRunnable = runnable
        mainHandler.postDelayed(runnable, messageTimeoutMillis)
    }

    private fun cancelTimeout() {
        timeoutRunnable?.let { mainHandler.removeCallbacks(it) }
        timeoutRunnable = null
    }

    private fun onMain(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) block() else mainHandler.post(block)
    }

    private companion object {
        const val DEFAULT_MESSAGE_TIMEOUT_MILLIS = 5000L
        const val TAG = "DidomiIntegration"
        val INITIALIZATION_ERRORS = setOf(
            ErrorType.NULL_PROPERTY,
            ErrorType.CONFIG_FILE_ERROR,
            ErrorType.INVALID_API_KEY,
            ErrorType.INITIALIZATION_ERROR,
        )

        // Main thread only
        var active: WeakReference<DidomiIntegration>? = null
    }
}

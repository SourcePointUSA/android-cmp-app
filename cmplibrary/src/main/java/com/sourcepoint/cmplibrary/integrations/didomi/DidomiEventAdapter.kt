package com.sourcepoint.cmplibrary.integrations.didomi

import android.view.View
import com.sourcepoint.cmplibrary.SpClient
import io.didomi.sdk.events.ConsentChangedEvent
import io.didomi.sdk.events.ConsentUIFinishedEvent
import io.didomi.sdk.events.ConsentUIReadyEvent
import io.didomi.sdk.events.ErrorEvent
import io.didomi.sdk.events.ErrorType
import io.didomi.sdk.events.EventListener
import io.didomi.sdk.events.NoticeClickAgreeEvent
import io.didomi.sdk.events.NoticeClickDisagreeEvent
import io.didomi.sdk.events.PreferencesClickAgreeToAllEvent
import io.didomi.sdk.events.PreferencesClickDisagreeToAllEvent
import io.didomi.sdk.events.PreferencesClickSaveChoicesEvent

/**
 * Adapter that wraps Didomi's EventListener and maps events to Sourcepoint's SpClient callbacks.
 */
internal class DidomiEventAdapter(
    private val spClient: SpClient,
    private val consentMapper: DidomiConsentMapper
) : EventListener() {

    var onConsentUIReady: ((View) -> Unit)? = null
    var onConsentUIFinished: ((View) -> Unit)? = null
    var onConsentChanged: (() -> Unit)? = null
    var onError: ((ErrorType, String) -> Unit)? = null

    override fun consentUIReady(event: ConsentUIReadyEvent) {
        event.uiEvent.view?.let { view ->
            onConsentUIReady?.invoke(view)
        }
    }

    override fun consentUIFinished(event: ConsentUIFinishedEvent) {
        event.uiEvent.view?.let { view ->
            onConsentUIFinished?.invoke(view)
        }
    }

    override fun consentChanged(event: ConsentChangedEvent) {
        onConsentChanged?.invoke()
    }

    override fun noticeClickAgree(event: NoticeClickAgreeEvent) {
        // Action callbacks handled via consent changed
    }

    override fun noticeClickDisagree(event: NoticeClickDisagreeEvent) {
        // Action callbacks handled via consent changed
    }

    override fun preferencesClickAgreeToAll(event: PreferencesClickAgreeToAllEvent) {
        // Action callbacks handled via consent changed
    }

    override fun preferencesClickDisagreeToAll(event: PreferencesClickDisagreeToAllEvent) {
        // Action callbacks handled via consent changed
    }

    override fun preferencesClickSaveChoices(event: PreferencesClickSaveChoicesEvent) {
        // Action callbacks handled via consent changed
    }

    override fun error(event: ErrorEvent) {
        onError?.invoke(event.errorType, event.errorMessage ?: "Didomi SDK error")
    }
}

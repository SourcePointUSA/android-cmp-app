package com.sourcepoint.cmplibrary.integrations.didomi

import android.view.View
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.exception.ConsentLibExceptionK
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import io.didomi.sdk.events.*

/**
 * Adapter that wraps Didomi's EventListener and maps events to Sourcepoint's SpClient callbacks.
 */
internal class DidomiEventAdapter(
    private val spClient: SpClient,
    private val consentMapper: DidomiConsentMapper
) {

    var onConsentUIReady: ((View) -> Unit)? = null
    var onConsentUIFinished: ((View) -> Unit)? = null

    val didomiEventListener = object : DidomiEventListener() {

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
            // Map Didomi consent to Sourcepoint format
            val spConsents = consentMapper.mapToSourcepoint()
            spClient.onConsentReady(spConsents)
        }

        override fun noticeClickAgree(event: NoticeClickAgreeEvent) {
            handleAction(ActionType.ACCEPT_ALL)
        }

        override fun noticeClickDisagree(event: NoticeClickDisagreeEvent) {
            handleAction(ActionType.REJECT_ALL)
        }

        override fun preferencesClickAgreeToAll(event: PreferencesClickAgreeToAllEvent) {
            handleAction(ActionType.ACCEPT_ALL)
        }

        override fun preferencesClickDisagreeToAll(event: PreferencesClickDisagreeToAllEvent) {
            handleAction(ActionType.REJECT_ALL)
        }

        override fun preferencesClickSaveChoices(event: PreferencesClickSaveChoicesEvent) {
            handleAction(ActionType.SAVE_AND_EXIT)
        }

        override fun hideNotice(event: HideNoticeEvent) {
            // Notice hidden - will trigger onConsentUIFinished
        }

        override fun hidePreferences(event: HidePreferencesEvent) {
            // Preferences hidden - will trigger onConsentUIFinished
        }

        override fun error(event: ErrorEvent) {
            val exception = ConsentLibExceptionK(
                description = event.errorMessage ?: "Didomi SDK error",
                code = event.errorType.name
            )
            spClient.onError(exception)
        }
    }

    private fun handleAction(actionType: ActionType) {
        val consentAction = ConsentAction(
            actionType = actionType,
            campaignType = null, // Will be populated by Sourcepoint
            pubData = mutableMapOf()
        )
        // Note: SpClient.onAction expects a View parameter, but we don't have it from Didomi events
        // The view will be provided via onConsentUIReady callback
    }
}

package com.sourcepoint.app.v6.pm_test

import android.view.View
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable

interface ConsentManagementPlatform {
    fun openFirstLayer(consentGivenCallback: () -> Unit)
    fun openPrivacyManager(consentGivenCallback: () -> Unit)
    fun dispose()

    fun onScreenShown()

    val events: Observable<Event>

    sealed class Event {

        object Finished : Event()

        sealed class UiEvent : Event() {

            class ViewReady(val view: View) : UiEvent()
            class ViewFinished(val view: View) : UiEvent()
        }
    }

    companion object CmpEvents {

        // TODO try make internal or protected after removing mock implementation
        val consentReadyRelay = BehaviorRelay.create<ConsentType>()

        fun consentReadyEvents(): Observable<ConsentType> = consentReadyRelay
    }
}

package com.sourcepoint.cmplibrary.integrations.didomi

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

/**
 * Runs [block] once, when this lifecycle is destroyed. Runs it right away if it already is.
 * Must be called from the main thread.
 */
internal fun Lifecycle.doOnDestroy(block: () -> Unit) {
    if (currentState == Lifecycle.State.DESTROYED) return block()
    addObserver(object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            owner.lifecycle.removeObserver(this)
            block()
        }
    })
}

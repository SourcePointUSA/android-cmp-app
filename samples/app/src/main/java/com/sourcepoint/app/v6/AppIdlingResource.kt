package com.sourcepoint.app.v6

import android.util.Log
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.IdlingResource.ResourceCallback
import java.util.concurrent.atomic.AtomicBoolean


class AppIdlingResource : IdlingResource {
    @Volatile
    private var mCallback: ResourceCallback? = null

    // Idleness is controlled with this boolean.
    private val mIsIdleNow = AtomicBoolean(true)

    override fun getName(): String {
        return this.javaClass.name
    }

    override fun registerIdleTransitionCallback(callback: ResourceCallback?) {
        mCallback = callback
    }

    override fun isIdleNow(): Boolean {
        return mIsIdleNow.get()
    }

    /**
     * Sets the new idle state, if isIdleNow is true, it pings the [ResourceCallback].
     * @param isIdleNow false if there are pending operations, true if idle.
     */
    fun setIdleState(isIdleNow: Boolean, whoSetIt: String) {
        Log.d("AppIdlingResource", "setIdleState($isIdleNow) called by $whoSetIt: ")
        mIsIdleNow.set(isIdleNow)
        if (isIdleNow) {
            mCallback?.onTransitionToIdle()
        }
    }
}
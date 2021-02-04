package com.sourcepoint.cmplibrary.util

import android.content.Context
import android.os.Handler

internal interface ExecutorManager {
    fun executeOnMain(block: () -> Unit)
    companion object
}

/**
 * Factory method to create an instance of a [ExecutorManager] using its implementation
 * @param context is the client application context
 * @return an instance of the [ExecutorManagerImpl] implementation
 */
internal fun ExecutorManager.Companion.create(context: Context): ExecutorManager = ExecutorManagerImpl(context)

private class ExecutorManagerImpl(val context: Context) : ExecutorManager {
    override fun executeOnMain(block: () -> Unit) {
        val mainLooper = context.mainLooper
        Handler(mainLooper).post(block)
    }
}

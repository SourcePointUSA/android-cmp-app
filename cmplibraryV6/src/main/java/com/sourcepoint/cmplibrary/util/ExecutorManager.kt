package com.sourcepoint.cmplibrary.util

import android.content.Context
import android.os.Handler

internal interface ExecutorManager {
    fun executeOnMain(block: () -> Unit)
    companion object
}

internal fun ExecutorManager.Companion.create(context: Context): ExecutorManager = ExecutorManagerImpl(context)

private class ExecutorManagerImpl(val context: Context) : ExecutorManager {
    override fun executeOnMain(block: () -> Unit) {
        val mainLooper = context.mainLooper
        Handler(mainLooper).post(block)
    }
}

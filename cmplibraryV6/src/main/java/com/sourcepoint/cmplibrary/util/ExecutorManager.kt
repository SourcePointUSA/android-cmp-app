package com.sourcepoint.cmplibrary.util

import android.content.Context
import android.os.Handler
import java.io.File
import java.io.FileFilter
import java.util.concurrent.Executors
import java.util.regex.Pattern

internal interface ExecutorManager {
    fun executeOnMain(block: () -> Unit)
    fun executeOnWorkerThread(block: () -> Unit)
    fun executeOnSingleThread(block: () -> Unit)
    fun dispose()
    companion object
}

/**
 * Factory method to create an instance of a [ExecutorManager] using its implementation
 * @param context is the client application context
 * @return an instance of the [ExecutorManagerImpl] implementation
 */
internal fun ExecutorManager.Companion.create(context: Context): ExecutorManager = ExecutorManagerImpl(context)

private class ExecutorManagerImpl(val context: Context) : ExecutorManager {

    private val executor = Executors.newFixedThreadPool(getNumCores())
    private val singleThreadExecutor = Executors.newSingleThreadExecutor()

    override fun executeOnMain(block: () -> Unit) {
        val mainLooper = context.mainLooper
        Handler(mainLooper).post(block)
    }

    override fun executeOnWorkerThread(block: () -> Unit) {
        executor.execute(block)
    }

    override fun dispose() {
        executor.shutdown()
        singleThreadExecutor.shutdown()
    }

    override fun executeOnSingleThread(block: () -> Unit) {
        singleThreadExecutor.execute(block)
    }

    private fun getNumCores(): Int {
        return try {
            // Get directory containing CPU info
            val dir = File("/sys/devices/system/cpu/")
            // Filter to only list the devices we care about
            val files: Array<File> = dir.listFiles(FileFilter { Pattern.matches("cpu[0-9]+", it.name) })
            // Return the number of cores (virtual CPU devices)
            files.size
        } catch (e: Exception) {
            // Default to return 1 core
            1
        }
    }
}

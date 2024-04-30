package com.sourcepoint.app.v6.utils

import android.util.Log
import androidx.test.runner.screenshot.Screenshot
import java.io.IOException

fun takeScreenshot(parentFolderPath: String = "", screenShotName: String) {
    Log.d(TAG, "Taking screenshot of '$screenShotName'")
    val screenCapture = Screenshot.capture()
    val processors = setOf(ScreenCaptureProcessor(parentFolderPath))
    try {
        screenCapture.apply {
            name = screenShotName
            process(processors)
        }
        Log.d(TAG, "Screenshot taken")
    } catch (ex: IOException) {
        Log.e(TAG, "Could not take a screenshot", ex)
    }
}

private const val TAG = "Screenshots"
package com.sourcepoint.app.v6

import android.content.Context
import android.content.Intent
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector

fun clickAirplaneMode(device: UiDevice, context: Context) {
    device.run {
        context.packageManager.getLaunchIntentForPackage("com.android.settings")?.let {intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
            findObject(UiSelector().textContains("Network")).clickAndWaitForNewWindow()
            findObject(UiSelector().textContains("Airplane")).click()
        }
        device.pressBack()
        device.pressBack()
    }
}
package com.sourcepoint.cmplibrary.creation

import android.content.Context
import android.preference.PreferenceManager
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.network.connection.ConnectionManager
import com.sourcepoint.cmplibrary.data.network.connection.create

internal fun getConnectionManager(appCtx: Context): ConnectionManager {
    val mockObject: ConnectionManager? = com.sourcepoint.cmplibrary.util.check {
        PreferenceManager.getDefaultSharedPreferences(appCtx).all
            .toList()
            .find { it.first == "connectionTest" }
            ?.let { it.second as? Boolean }
            ?.let {
                object : ConnectionManager {
                    override val isConnected: Boolean
                        get() {
                            // emulate the delay in a real device
                            Thread.sleep(400)
                            return it
                        }
                }
            }
    }.getOrNull()

    return mockObject ?: ConnectionManager.create(appCtx)
}

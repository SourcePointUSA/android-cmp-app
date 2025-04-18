package com.sourcepoint.cmplibrary.data.network.connection

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

/**
 * Factory method to create an instance of a [ConnectionManager] using its implementation
 * @param context is the client application context
 * @return an instance of the [ConnectionManagerImpl] implementation
 */
internal fun ConnectionManager.Companion.create(context: Context): ConnectionManager = ConnectionManagerImpl(context)

internal class ConnectionManagerImpl(
    private val context: Context
) : ConnectionManager {

    private val connectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override val isConnected: Boolean
        get() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val activeNetwork = connectivityManager.activeNetwork
                    ?: return false
                val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
                    ?: return false

                return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            } else {
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                return activeNetworkInfo != null && activeNetworkInfo.isConnected
            }
        }
}

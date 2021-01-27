package com.sourcepoint.cmplibrary.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

fun ConnectionManager.Companion.create(context: Context): ConnectionManager = ConnectionManagerImpl(context)

private class ConnectionManagerImpl(context: Context) : ConnectionManager {
    private val cm by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
    private val activeNetwork: NetworkInfo? by lazy { cm.activeNetworkInfo }
    override val isConnected: Boolean
        get() = activeNetwork?.isConnected ?: false
}

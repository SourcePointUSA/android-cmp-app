package com.sourcepoint.cmplibrary.creation

import android.content.Context
import com.sourcepoint.cmplibrary.data.network.NetworkClient
import com.sourcepoint.cmplibrary.data.network.connection.ConnectionManager
import com.sourcepoint.cmplibrary.data.network.connection.create
import com.sourcepoint.cmplibrary.data.network.createNetworkClient
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManagerSingleton
import com.sourcepoint.cmplibrary.data.network.util.ResponseManager
import com.sourcepoint.cmplibrary.exception.Logger
import okhttp3.OkHttpClient

internal fun getConnectionManager(appCtx: Context): ConnectionManager = ConnectionManager.create(appCtx)

internal fun networkClient(appCtx: Context, netClient: OkHttpClient, responseManage: ResponseManager, logger: Logger): NetworkClient {
    return createNetworkClient(
        httpClient = netClient,
        responseManager = responseManage,
        urlManager = HttpUrlManagerSingleton,
        logger = logger
    )
}

@file:JvmName("FactoryUtils")

package com.sourcepoint.cmplibrary.data.network

import okhttp3.HttpUrl
import okhttp3.OkHttpClient

// TODO delete it, it is for test
internal fun createNetClient(
    httpClient: OkHttpClient,
    url: HttpUrl,
    responseManager: ResponseManager
): NetworkClient = createNetworkClient(httpClient, url, responseManager)

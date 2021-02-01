package com.sourcepoint.cmplibrary.creation

import com.example.gdpr_cmplibrary.BuildConfig
import com.sourcepoint.cmplibrary.Campaign
import com.sourcepoint.cmplibrary.data.network.NetworkClient
import com.sourcepoint.cmplibrary.data.network.createNetworkClient
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManagerSingleton
import com.sourcepoint.cmplibrary.data.network.util.ResponseManager
import com.sourcepoint.gdpr_cmplibrary.exception.* // ktlint-disable
import okhttp3.OkHttpClient

internal fun createClientInfo(): ClientInfo {
    return ClientInfo(
        clientVersion = "5.X.X",
        deviceFamily = "android",
        osVersion = "30"
    )
}

internal fun errorMessageManager(a: Campaign, client: ClientInfo): ErrorMessageManager {
    return createErrorManager(
        accountId = a.accountId,
        propertyId = a.propertyId,
        propertyHref = "http://dev.local",
        clientInfo = client,
        legislation = Legislation.GDPR
    )
}

internal fun createLogger(errorMessageManager: ErrorMessageManager): Logger {
    return createLogger(
        networkClient = OkHttpClient(),
        errorMessageManager = errorMessageManager,
        url = BuildConfig.LOGGER_URL
    )
}

internal fun networkClient(netClient: OkHttpClient, responseManage: ResponseManager): NetworkClient {
    return createNetworkClient(
        httpClient = netClient,
        responseManager = responseManage,
        url = HttpUrlManagerSingleton.inAppUrlMessage
    )
}

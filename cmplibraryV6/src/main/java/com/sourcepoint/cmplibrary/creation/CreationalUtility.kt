package com.sourcepoint.cmplibrary.creation

import android.os.Build
import com.example.cmplibrary.BuildConfig
import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.data.network.NetworkClient
import com.sourcepoint.cmplibrary.data.network.createNetworkClient
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManagerSingleton
import com.sourcepoint.cmplibrary.data.network.util.ResponseManager
import com.sourcepoint.cmplibrary.exception.* // ktlint-disable
import okhttp3.OkHttpClient

internal fun createClientInfo(): ClientInfo {
    return ClientInfo(
        clientVersion = BuildConfig.VERSION_NAME,
        deviceFamily = "[${Build.MANUFACTURER}]-[${Build.MODEL}]-[${Build.DEVICE}]",
        osVersion = "${Build.VERSION.SDK_INT}"
    )
}

internal fun errorMessageManager(campaignManager: CampaignManager, client: ClientInfo): ErrorMessageManager {
    return createErrorManager(
        campaignManager = campaignManager,
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
        urlManager = HttpUrlManagerSingleton
    )
}

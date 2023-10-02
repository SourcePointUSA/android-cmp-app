package com.sourcepoint.cmplibrary.creation

import android.content.Context
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
        campaignType = CampaignType.GDPR
    )
}

internal fun createLogger(errorMessageManager: ErrorMessageManager): Logger {
    return createLogger(
        networkClient = OkHttpClient(),
        errorMessageManager = errorMessageManager,
        url = BuildConfig.LOGGER_URL
    )
}

internal fun networkClient(
        context: Context,
        netClient: OkHttpClient,
        responseManage: ResponseManager,
        logger: Logger
): NetworkClient = createNetworkClient(
        context = context,
        httpClient = netClient,
        responseManager = responseManage,
        urlManager = HttpUrlManagerSingleton,
        logger = logger
)

internal val validPattern = "^[a-zA-Z.:/0-9-]*$".toRegex()

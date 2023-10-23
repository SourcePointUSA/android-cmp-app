package com.sourcepoint.cmplibrary.creation

import android.os.Build
import com.example.cmplibrary.BuildConfig
import com.sourcepoint.cmplibrary.campaign.CampaignManager
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

internal val validPattern = "^[a-zA-Z.:/0-9-]*$".toRegex()

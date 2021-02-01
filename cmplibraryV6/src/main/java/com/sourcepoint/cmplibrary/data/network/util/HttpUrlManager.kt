package com.sourcepoint.cmplibrary.data.network.util

import okhttp3.HttpUrl

/**
 * Component responsible of building and providing the URLs
 */
internal interface HttpUrlManager {
    val inAppUrlMessage: HttpUrl
    val inAppUrlNativeMessage: HttpUrl
}

/**
 * Implementation of the [HttpUrlManager] interface
 */
internal object HttpUrlManagerSingleton : HttpUrlManager {

    private const val message = "wrapper/v1/unified/message"

    val inAppLocalUrlMessage: HttpUrl = HttpUrl.Builder()
        .scheme("http")
        .host("localhost")
        .port(3000)
        .addPathSegments(message)
        .addQueryParameter("env", "localProd")
        .addQueryParameter("inApp", "true")
        .build()

    override val inAppUrlMessage: HttpUrl = inAppLocalUrlMessage

    override val inAppUrlNativeMessage: HttpUrl
        get() = HttpUrl.Builder()
            .scheme("https")
            .host("cdn.privacy-mgmt.com")
            .addPathSegments("wrapper/tcfv2/v1/gdpr")
            .addPathSegments("native-message")
            .addQueryParameter("inApp", "true")
            .build()
}

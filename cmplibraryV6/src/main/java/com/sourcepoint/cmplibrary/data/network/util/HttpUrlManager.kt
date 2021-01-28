package com.sourcepoint.cmplibrary.data.network.util

import okhttp3.HttpUrl

/**
 * Component responsible of building and providing the URLs
 */
internal interface HttpUrlManager {
    val inAppUrlMessage: HttpUrl
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
}

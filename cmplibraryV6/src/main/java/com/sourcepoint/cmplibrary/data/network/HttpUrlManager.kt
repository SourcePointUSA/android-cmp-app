package com.sourcepoint.cmplibrary.data.network

import okhttp3.HttpUrl

internal interface HttpUrlManager{
    val inAppUrlMessage : HttpUrl
}

internal object HttpUrlManagerSingleton : HttpUrlManager{

    private const val message = "wrapper/v1/unified/message"

    val inAppLocalUrlMessage: HttpUrl = HttpUrl.Builder()
        .scheme("http")
        .host("localhost")
        .port(3000)
        .addPathSegments(message)
        .addQueryParameter("env","localProd")
        .addQueryParameter("inApp","true")
        .build()

    override val inAppUrlMessage: HttpUrl = inAppLocalUrlMessage
}
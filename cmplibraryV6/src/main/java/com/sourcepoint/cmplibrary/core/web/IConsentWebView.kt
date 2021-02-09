package com.sourcepoint.cmplibrary.core.web

import com.sourcepoint.cmplibrary.util.Either
import okhttp3.HttpUrl
import org.json.JSONObject

internal interface IConsentWebView {
    fun loadConsentUIFromUrl(url: HttpUrl, message: JSONObject): Either<Boolean>
    fun loadConsentUIFromUrl(url: HttpUrl): Either<Boolean>
}

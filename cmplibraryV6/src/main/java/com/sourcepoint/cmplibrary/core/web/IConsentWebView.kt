package com.sourcepoint.cmplibrary.core.web

import com.sourcepoint.cmplibrary.data.network.model.UnifiedMessageResp
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.util.Either
import okhttp3.HttpUrl
import org.json.JSONObject

internal interface IConsentWebView {
    fun loadConsentUIFromUrl(url: HttpUrl): Either<Boolean>
    fun loadConsentUI(messageResp: UnifiedMessageResp, url: HttpUrl): Either<Boolean>
    fun loadConsentUI(messageResp: JSONObject, url: HttpUrl, legislation: Legislation): Either<Boolean>
}

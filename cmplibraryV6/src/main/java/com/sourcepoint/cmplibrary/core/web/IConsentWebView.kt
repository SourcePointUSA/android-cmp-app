package com.sourcepoint.cmplibrary.core.web

import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.util.Either
import okhttp3.HttpUrl

internal interface IConsentWebView {
    fun loadConsentUIFromUrl(url: HttpUrl, legislation: Legislation): Either<Boolean>
    fun loadConsentUI(messageResp: CampaignModel, url: HttpUrl, legislation: Legislation): Either<Boolean>
}

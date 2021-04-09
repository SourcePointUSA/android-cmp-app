package com.sourcepoint.cmplibrary.core.web

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.exception.Legislation
import okhttp3.HttpUrl

internal interface IConsentWebView {
    fun loadConsentUIFromUrl(url: HttpUrl, legislation: Legislation): Either<Boolean>
    fun loadConsentUI(campaignModel: CampaignModel, url: HttpUrl, legislation: Legislation): Either<Boolean>
}

package com.sourcepoint.cmplibrary.core.web

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.exception.CampaignType
import okhttp3.HttpUrl

internal interface IConsentWebView {

    fun loadConsentUIFromUrlPreloadingOption(
        url: HttpUrl,
        campaignType: CampaignType,
        pmId: String?,
        consent: String?,
    ): Either<Boolean>

    fun loadConsentUI(
        campaignModel: CampaignModel,
        url: HttpUrl,
        campaignType: CampaignType
    ): Either<Boolean>
}

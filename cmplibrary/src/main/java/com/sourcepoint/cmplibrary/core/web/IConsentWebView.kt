package com.sourcepoint.cmplibrary.core.web

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.network.model.v7.CampaignMessage
import com.sourcepoint.cmplibrary.exception.CampaignType
import okhttp3.HttpUrl

internal interface IConsentWebView {
    fun loadConsentUIFromUrl(url: HttpUrl, campaignType: CampaignType): Either<Boolean>
    fun loadConsentUIFromUrl(url: HttpUrl, campaignType: CampaignType, pmId: String?, singleShot: Boolean): Either<Boolean>
    fun loadConsentUIFromUrl(url: HttpUrl, campaignType: CampaignType, pmId: String?, campaignModel: CampaignModel?): Either<Boolean>
    fun loadConsentUI(campaignModel: CampaignModel, url: HttpUrl, campaignType: CampaignType): Either<Boolean>
    fun loadConsentUIV7(campaignMessage: CampaignMessage, url: HttpUrl, campaignType: CampaignType): Either<Boolean>
}

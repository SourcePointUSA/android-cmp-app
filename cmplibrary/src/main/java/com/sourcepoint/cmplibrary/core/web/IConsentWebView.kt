package com.sourcepoint.cmplibrary.core.web

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.exception.CampaignType
import okhttp3.HttpUrl
import org.json.JSONObject

internal interface IConsentWebView {
    fun loadConsentUIFromUrl(url: HttpUrl, campaignType: CampaignType): Either<Boolean>
    fun loadConsentUIFromUrl(url: HttpUrl, campaignType: CampaignType, pmId: String?, singleShot: Boolean): Either<Boolean>
    fun loadConsentUIFromUrlPreloading(url: HttpUrl, campaignType: CampaignType, pmId: String?, singleShot: Boolean, consent: JSONObject): Either<Boolean>
    fun loadConsentUIFromUrl(url: HttpUrl, campaignType: CampaignType, pmId: String?, campaignModel: CampaignModel?): Either<Boolean>
    fun loadConsentUI(campaignModel: CampaignModel, url: HttpUrl, campaignType: CampaignType): Either<Boolean>
}

package com.sourcepoint.cmplibrary.core.web

import com.sourcepoint.cmplibrary.data.network.model.CampaignResp1203
import com.sourcepoint.cmplibrary.data.network.model.UnifiedMessageResp
import com.sourcepoint.cmplibrary.data.network.model.UnifiedMessageResp1203
import com.sourcepoint.cmplibrary.util.Either
import okhttp3.HttpUrl

internal interface IConsentWebView {
    fun loadConsentUIFromUrl(url: HttpUrl): Either<Boolean>
    fun loadConsentUI(messageResp: UnifiedMessageResp, url: HttpUrl): Either<Boolean>
    fun loadConsentUI(messageResp: CampaignResp1203, url: HttpUrl): Either<Boolean>
}

package com.sourcepoint.cmplibrary.core.web

import com.sourcepoint.cmplibrary.util.Either
import com.sourcepoint.gdpr_cmplibrary.ConsentLibException
import okhttp3.HttpUrl
import org.json.JSONObject

internal interface IConsentWebView {
    fun loadConsentUIFromUrl(url: HttpUrl, message: JSONObject): Either<Boolean>
    var onNoIntentActivitiesFoundFor: ((url: String) -> Unit)?
    var onError: ((ConsentLibException) -> Unit)?
}

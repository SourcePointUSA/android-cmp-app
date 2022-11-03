package com.sourcepoint.cmplibrary.data

import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.network.NetworkClient
import com.sourcepoint.cmplibrary.data.network.model.v7.PostChoiceResp
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.model.ConsentActionImpl
import com.sourcepoint.cmplibrary.model.ConsentResp
import com.sourcepoint.cmplibrary.model.CustomConsentReq
import com.sourcepoint.cmplibrary.model.exposed.SPConsents

/**
 * Encapsulates the logic to fetch the data from the server, using the [NetworkClient], and
 * storing fields from the Response like the one with prefix `IABTCF_`
 */
internal interface Service : NetworkClient, CampaignManager {

    fun sendConsent(
        localState: String,
        consentActionImpl: ConsentActionImpl,
        env: Env,
        pmId: String?
    ): Either<ConsentResp>

    fun sendConsentV7(
        consentActionImpl: ConsentActionImpl,
        env: Env,
        pmId: String?
    ): Either<PostChoiceResp>

    fun sendCustomConsentServ(
        customConsentReq: CustomConsentReq,
        env: Env
    ): Either<SPConsents?>

    fun deleteCustomConsentToServ(
        customConsentReq: CustomConsentReq,
        env: Env
    ): Either<SPConsents?>

    companion object
}

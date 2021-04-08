package com.sourcepoint.cmplibrary.data

import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.data.network.NetworkClient
import com.sourcepoint.cmplibrary.data.network.model.ConsentAction
import com.sourcepoint.cmplibrary.data.network.model.consent.ConsentResp
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.util.Either

/**
 * Encapsulates the logic to fetch the data from the server, using the [NetworkClient], and
 * storing fields from the Response like the one with prefix `IABTCF_`
 */
internal interface Service : NetworkClient, CampaignManager {

    fun sendConsent(
        consentAction: ConsentAction,
        success: (ConsentResp) -> Unit,
        error: (Throwable) -> Unit,
        env: Env
    )

    fun sendConsent(
        consentAction: ConsentAction,
        env: Env
    ): Either<ConsentResp>

    fun sendConsent(
        localState: String,
        consentAction: ConsentAction,
        env: Env
    ): Either<ConsentResp>

    companion object
}

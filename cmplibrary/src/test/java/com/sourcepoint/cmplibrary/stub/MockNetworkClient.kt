package com.sourcepoint.cmplibrary.stub

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.network.NetworkClient
import com.sourcepoint.cmplibrary.data.network.model.v7.ConsentStatusParamReq
import com.sourcepoint.cmplibrary.data.network.model.v7.ConsentStatusResp
import com.sourcepoint.cmplibrary.data.network.model.v7.MetaDataParamReq
import com.sourcepoint.cmplibrary.data.network.model.v7.MetaDataResp
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import org.json.JSONObject

internal class MockNetworkClient(
    private val logicUnifiedMess: ((messageReq: UnifiedMessageRequest, pSuccess: (UnifiedMessageResp) -> Unit, pError: (Throwable) -> Unit) -> Unit)? = null,
) : NetworkClient {

    override fun getUnifiedMessage(messageReq: UnifiedMessageRequest, pSuccess: (UnifiedMessageResp) -> Unit, pError: (Throwable) -> Unit, env: Env) {
        logicUnifiedMess?.invoke(messageReq, pSuccess, pError)
    }

    override fun sendConsent(consentReq: JSONObject, env: Env, consentActionImpl: ConsentActionImpl): Either<ConsentResp> {
        TODO("Not yet implemented")
    }

    override fun sendCustomConsent(customConsentReq: CustomConsentReq, env: Env): Either<CustomConsentResp> {
        TODO("Not yet implemented")
    }

    override fun deleteCustomConsentTo(customConsentReq: CustomConsentReq, env: Env): Either<CustomConsentResp> {
        TODO("Not yet implemented")
    }

    override fun getMetaData(param: MetaDataParamReq): Either<MetaDataResp> {
        TODO("Not yet implemented")
    }

    override fun getConsentStatus(param: ConsentStatusParamReq): Either<ConsentStatusResp> {
        TODO("Not yet implemented")
    }
}

package com.sourcepoint.cmplibrary.stub

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.network.NetworkClient
import com.sourcepoint.cmplibrary.data.network.model.v7.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import org.json.JSONObject

internal class MockNetworkClient(
    private val logicUnifiedMess: ((messageReq: UnifiedMessageRequest, pSuccess: (UnifiedMessageResp) -> Unit, pError: (Throwable) -> Unit) -> Unit)? = null,
) : NetworkClient {

    override fun getUnifiedMessage(messageReq: UnifiedMessageRequest, pSuccess: (UnifiedMessageResp) -> Unit, pError: (Throwable) -> Unit, env: Env) {
        logicUnifiedMess?.invoke(messageReq, pSuccess, pError)
    }

    override fun sendConsent(consentReq: JSONObject, env: Env, consentAction: ConsentAction): Either<ConsentResp> {
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

    override fun getMessages(param: MessagesParamReq): Either<MessagesResp> {
        TODO("Not yet implemented")
    }

    override fun savePvData(param: PvDataParamReq): Either<PvDataResp> {
        TODO("Not yet implemented")
    }

    override fun getMessages(
        messageReq: MessagesParamReq,
        pSuccess: (MessagesResp) -> Unit,
        pError: (Throwable) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getChoice(param: ChoiceParamReq): Either<ChoiceResp> {
        TODO("Not yet implemented")
    }

    override fun storeGdprChoice(param: PostChoiceParamReq): Either<GdprCS> {
        TODO("Not yet implemented")
    }

    override fun storeCcpaChoice(param: PostChoiceParamReq): Either<CcpaCS> {
        TODO("Not yet implemented")
    }
}

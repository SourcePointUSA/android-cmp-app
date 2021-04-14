package com.sourcepoint.cmplibrary.stub

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.network.NetworkClient
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import org.json.JSONObject

internal class MockNetworkClient(
    private val logicUnifiedMess2: ((messageReq: UnifiedMessageRequest, pSuccess: (UnifiedMessageResp1203) -> Unit, pError: (Throwable) -> Unit) -> Unit)? = null,
    private val logicNativeMess: ((messageReq: MessageReq, success: (NativeMessageResp) -> Unit, error: (Throwable) -> Unit) -> Unit)? = null
) : NetworkClient {

    override fun getUnifiedMessage(messageReq: UnifiedMessageRequest, pSuccess: (UnifiedMessageResp1203) -> Unit, pError: (Throwable) -> Unit, env: Env) {
        logicUnifiedMess2?.invoke(messageReq, pSuccess, pError)
    }

    override fun getNativeMessage(messageReq: MessageReq, success: (NativeMessageResp) -> Unit, error: (Throwable) -> Unit) {
        logicNativeMess?.invoke(messageReq, success, error)
    }

    override fun sendConsent(consentReq: JSONObject, env: Env, consentAction: ConsentAction): Either<ConsentResp> {
        TODO("Not yet implemented")
    }

    override fun getNativeMessageK(messageReq: MessageReq, success: (NativeMessageRespK) -> Unit, error: (Throwable) -> Unit) {}
}

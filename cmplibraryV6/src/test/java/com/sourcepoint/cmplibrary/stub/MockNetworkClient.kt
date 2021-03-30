package com.sourcepoint.cmplibrary.stub

import com.sourcepoint.cmplibrary.data.network.NetworkClient
import com.sourcepoint.cmplibrary.data.network.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.consent.ConsentResp
import okhttp3.HttpUrl
import org.json.JSONObject

internal class MockNetworkClient(
    private val logicUnifiedMess: ((messageReq: MessageReq, pSuccess: (UnifiedMessageResp) -> Unit, pError: (Throwable) -> Unit) -> Unit)? = null,
    private val logicUnifiedMess1203: ((messageReq: MessageReq, pSuccess: (UnifiedMessageResp1203) -> Unit, pError: (Throwable) -> Unit) -> Unit)? = null,
    private val logicUnifiedMess2: ((messageReq: UnifiedMessageRequest, pSuccess: (UnifiedMessageResp1203) -> Unit, pError: (Throwable) -> Unit) -> Unit)? = null,
    private val logicNativeMess: ((messageReq: MessageReq, success: (NativeMessageResp) -> Unit, error: (Throwable) -> Unit) -> Unit)? = null
) : NetworkClient {

    override fun getMessage(messageReq: MessageReq, pSuccess: (UnifiedMessageResp) -> Unit, pError: (Throwable) -> Unit) {
        logicUnifiedMess?.invoke(messageReq, pSuccess, pError)
    }

    override fun getMessage1203(messageReq: MessageReq, pSuccess: (UnifiedMessageResp1203) -> Unit, pError: (Throwable) -> Unit) {
        logicUnifiedMess1203?.invoke(messageReq, pSuccess, pError)
    }

    override fun getUnifiedMessage(messageReq: UnifiedMessageRequest, pSuccess: (UnifiedMessageResp1203) -> Unit, pError: (Throwable) -> Unit) {
        logicUnifiedMess2?.invoke(messageReq, pSuccess, pError)
    }

    override fun getNativeMessage(messageReq: MessageReq, success: (NativeMessageResp) -> Unit, error: (Throwable) -> Unit) {
        logicNativeMess?.invoke(messageReq, success, error)
    }

    override fun getNativeMessageK(messageReq: MessageReq, success: (NativeMessageRespK) -> Unit, error: (Throwable) -> Unit) {}
    override fun sendConsent(consentReq: JSONObject, success: (ConsentResp) -> Unit, error: (Throwable) -> Unit, url: HttpUrl) {
        TODO("Not yet implemented")
    }
}

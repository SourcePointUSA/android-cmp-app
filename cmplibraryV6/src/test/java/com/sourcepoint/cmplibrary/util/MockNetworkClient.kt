package com.sourcepoint.cmplibrary.util

import com.sourcepoint.cmplibrary.data.network.NetworkClient
import com.sourcepoint.cmplibrary.data.network.model.MessageReq
import com.sourcepoint.cmplibrary.data.network.model.MessageResp
import com.sourcepoint.cmplibrary.data.network.model.NativeMessageResp

class MockNetworkClient(
    private val logicMess: ((messageReq: MessageReq, pSuccess: (MessageResp) -> Unit, pError: (Throwable) -> Unit) -> Unit)? = null,
    private val logicNativeMess: ((messageReq: MessageReq, success: (NativeMessageResp) -> Unit, error: (Throwable) -> Unit) -> Unit)? = null
) : NetworkClient {

    override fun getMessage(messageReq: MessageReq, pSuccess: (MessageResp) -> Unit, pError: (Throwable) -> Unit) {
        logicMess?.invoke(messageReq, pSuccess, pError)
    }

    override fun getNativeMessage(messageReq: MessageReq, success: (NativeMessageResp) -> Unit, error: (Throwable) -> Unit) {
        logicNativeMess?.invoke(messageReq, success, error)
    }
}

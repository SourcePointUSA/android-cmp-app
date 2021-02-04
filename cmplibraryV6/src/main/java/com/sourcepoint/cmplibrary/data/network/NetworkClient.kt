package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.data.network.model.MessageReq
import com.sourcepoint.cmplibrary.data.network.model.MessageResp
import com.sourcepoint.cmplibrary.data.network.model.NativeMessageResp

/**
 * Component used to handle the network request
 */
internal interface NetworkClient {

    /**
     * Requesting a message object to the server
     * @param messageReq request content to send into the body
     * @param pSuccess success callback
     * @param pError error callback
     */
    fun getMessage(
        messageReq: MessageReq,
        pSuccess: (MessageResp) -> Unit,
        pError: (Throwable) -> Unit
    )

    /**
     * Requesting a native message object to the server
     * @param messageReq request content to send into the body
     * @param success success callback
     * @param error error callback
     */
    // TODO verify if we need it
    fun getNativeMessage(
        messageReq: MessageReq,
        success: (NativeMessageResp) -> Unit,
        error: (Throwable) -> Unit
    )
}

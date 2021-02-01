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
     * @param success success callback
     * @param error error callback
     */
    fun getMessage(
        messageReq: MessageReq,
        success: (MessageResp) -> Unit,
        error: (Throwable) -> Unit
    )

    /**
     * Requesting a native message object to the server
     * @param messageReq request content to send into the body
     * @param success success callback
     * @param error error callback
     */
    fun getNativeMessage(
        messageReq: MessageReq,
        success: (NativeMessageResp) -> Unit,
        error: (Throwable) -> Unit
    )
}

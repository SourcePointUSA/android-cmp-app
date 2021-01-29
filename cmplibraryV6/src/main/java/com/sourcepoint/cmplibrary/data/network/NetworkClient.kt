package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.data.network.model.UWReq
import com.sourcepoint.cmplibrary.data.network.model.MessageResp

/**
 * Component used to handle the network request
 */
internal interface NetworkClient {

    /**
     * Requesting a message object to the server
     * @param uwReq request content to send into the body
     * @param success success callback
     * @param error error callback
     */
    fun getMessage(
        uwReq: UWReq,
        success: (MessageResp) -> Unit,
        error: (Throwable) -> Unit
    )
}

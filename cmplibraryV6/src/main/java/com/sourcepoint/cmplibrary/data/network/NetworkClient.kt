package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.data.network.model.MessageReq
import com.sourcepoint.cmplibrary.data.network.model.NativeMessageResp
import com.sourcepoint.cmplibrary.data.network.model.NativeMessageRespK
import com.sourcepoint.cmplibrary.data.network.model.UnifiedMessageResp
import com.sourcepoint.cmplibrary.data.network.model.consent.ConsentResp
import com.sourcepoint.cmplibrary.exception.Legislation
import org.json.JSONObject

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
        pSuccess: (UnifiedMessageResp) -> Unit,
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

    // TODO verify if we need it
    fun getNativeMessageK(
        messageReq: MessageReq,
        success: (NativeMessageRespK) -> Unit,
        error: (Throwable) -> Unit
    )

    fun sendConsent(
        legislation: Legislation,
        consentReq: JSONObject,
        success: (ConsentResp) -> Unit,
        error: (Throwable) -> Unit,
    )
}

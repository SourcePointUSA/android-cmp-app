package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.data.network.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.MessageReq
import com.sourcepoint.cmplibrary.data.network.model.consent.ConsentResp
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.util.Either
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
        pError: (Throwable) -> Unit,
        stage: Env
    )

    fun getMessage1203(
        messageReq: MessageReq,
        pSuccess: (UnifiedMessageResp1203) -> Unit,
        pError: (Throwable) -> Unit
    )

    fun getUnifiedMessage(
        messageReq: UnifiedMessageRequest,
        pSuccess: (UnifiedMessageResp1203) -> Unit,
        pError: (Throwable) -> Unit,
        env: Env
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
        consentReq: JSONObject,
        success: (ConsentResp) -> Unit,
        error: (Throwable) -> Unit,
        env: Env,
        consentAction: ConsentAction
    )

    fun sendConsent(
        consentReq: JSONObject,
        env: Env,
        consentAction: ConsentAction
    ): Either<ConsentResp>
}

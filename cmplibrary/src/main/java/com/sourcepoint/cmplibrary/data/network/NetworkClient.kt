package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.ConsentResp
import com.sourcepoint.cmplibrary.model.UnifiedMessageRequest
import com.sourcepoint.cmplibrary.model.UnifiedMessageResp
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
    fun getUnifiedMessage(
        messageReq: UnifiedMessageRequest,
        pSuccess: (UnifiedMessageResp) -> Unit,
        pError: (Throwable) -> Unit,
        env: Env
    )

    fun sendConsent(
        consentReq: JSONObject,
        env: Env,
        consentAction: ConsentAction
    ): Either<ConsentResp>

    /**
     * Requesting a native message object to the server
     * @param messageReq request content to send into the body
     * @param success success callback
     * @param error error callback
     */
    // TODO verify if we need it
    fun getNativeMessage(
        messageReq: UnifiedMessageRequest,
        success: (NativeMessageResp) -> Unit,
        error: (Throwable) -> Unit
    )

    // TODO verify if we need it
    fun getNativeMessageK(
        messageReq: UnifiedMessageRequest,
        success: (NativeMessageRespK) -> Unit,
        error: (Throwable) -> Unit
    )
}

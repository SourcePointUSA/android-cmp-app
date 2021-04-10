package com.sourcepoint.cmplibrary.data.network.util

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.network.model.NativeMessageResp
import com.sourcepoint.cmplibrary.data.network.model.NativeMessageRespK
import com.sourcepoint.cmplibrary.data.network.model.UnifiedMessageResp
import com.sourcepoint.cmplibrary.data.network.model.UnifiedMessageResp1203
import com.sourcepoint.cmplibrary.data.network.model.consent.ConsentResp
import okhttp3.Response

/**
 * Component used to parse an OkHttp [Response] and extract a DTO
 */
internal interface ResponseManager {

    /**
     * Parsing a [UnifiedMessageResp]
     * @param r http response
     * @return [Either] object
     */
    fun parseResponse(r: Response): Either<UnifiedMessageResp>

    fun parseResponse1203(r: Response): Either<UnifiedMessageResp1203>

    /**
     * @param r http response
     * @return [Either] object
     */
    fun parseNativeMessRes(r: Response): Either<NativeMessageResp>

    fun parseNativeMessResK(r: Response): Either<NativeMessageRespK>

    fun parseConsentResEither(r: Response): Either<ConsentResp>
    fun parseConsentRes(r: Response): ConsentResp
    companion object
}

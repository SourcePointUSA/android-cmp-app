package com.sourcepoint.cmplibrary.data.network.util

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.model.optimized.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.optimized.сonsentStatus.CcpaCS
import com.sourcepoint.cmplibrary.data.network.model.optimized.сonsentStatus.GdprCS
import com.sourcepoint.cmplibrary.data.network.model.optimized.сonsentStatus.USNatCS
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.ChoiceResp
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.ChoiceTypeParam
import com.sourcepoint.cmplibrary.exception.* // ktlint-disable
import okhttp3.Response

/**
 * Factory method for creating a concrete instance of ResponseManager
 * @param jsonConverter abject used for converting a string to a DTO
 * @return an implementation of [ResponseManager]
 */
internal fun ResponseManager.Companion.create(
    jsonConverter: JsonConverter,
    logger: Logger
): ResponseManager = ResponseManagerImpl(jsonConverter, logger)

/**
 * An implementation od the [ResponseManager] interface
 */
private class ResponseManagerImpl(
    val jsonConverter: JsonConverter,
    val logger: Logger
) : ResponseManager {
    override fun parseMessagesResp(r: Response): MessagesResp {
        val body = r.body?.byteStream()?.reader()?.readText() ?: ""
        val status = r.code
        val mess = r.message
        logger.res(
            tag = "MessagesResp",
            msg = mess,
            body = body,
            status = status.toString()
        )
        return if (r.isSuccessful) {
            when (val either: Either<MessagesResp> = jsonConverter.toMessagesResp(body)) {
                is Either.Right -> either.r
                is Either.Left -> throw either.t
            }
        } else {
            throw RequestFailedException(
                description = body,
                apiRequestPostfix = ApiRequestPostfix.MESSAGES.apiPostfix,
                httpStatusCode = "_$status",
            )
        }
    }
}

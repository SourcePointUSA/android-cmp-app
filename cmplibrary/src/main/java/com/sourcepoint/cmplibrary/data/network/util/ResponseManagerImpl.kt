package com.sourcepoint.cmplibrary.data.network.util

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.model.optimized.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.ChoiceResp
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.ChoiceTypeParam
import com.sourcepoint.cmplibrary.exception.* // ktlint-disable
import com.sourcepoint.cmplibrary.exception.InvalidRequestException
import com.sourcepoint.cmplibrary.model.CustomConsentResp
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

    override fun parseCustomConsentRes(r: Response): CustomConsentResp {
        val body = r.body?.byteStream()?.reader()?.readText() ?: ""
        val status = r.code
        val mess = r.message
        logger.res(
            tag = "CustomConsentResp",
            msg = mess,
            body = body,
            status = status.toString()
        )
        return if (r.isSuccessful) {
            when (val either: Either<CustomConsentResp> = jsonConverter.toCustomConsentResp(body)) {
                is Either.Right -> either.r
                is Either.Left -> throw either.t
            }
        } else {
            throw InvalidRequestException(description = body)
        }
    }

    override fun parseMetaDataRes(r: Response): MetaDataResp {
        val body = r.body?.byteStream()?.reader()?.readText() ?: ""
        val status = r.code
        val mess = r.message
        logger.res(
            tag = "MetaDataResp",
            msg = mess,
            body = body,
            status = status.toString()
        )
        return if (r.isSuccessful) {
            when (val either: Either<MetaDataResp> = jsonConverter.toMetaDataRespResp(body)) {
                is Either.Right -> either.r
                is Either.Left -> throw either.t
            }
        } else {
            throw RequestFailedException(
                description = body,
                apiRequestPostfix = ApiRequestPostfix.META_DATA.apiPostfix,
                httpStatusCode = "_$status",
            )
        }
    }

    override fun parseConsentStatusResp(r: Response): ConsentStatusResp {
        val body = r.body?.byteStream()?.reader()?.readText() ?: ""
        val status = r.code
        val mess = r.message
        logger.res(
            tag = "ConsentStatusResp",
            msg = mess,
            body = body,
            status = status.toString()
        )
        return if (r.isSuccessful) {
            when (val either: Either<ConsentStatusResp> = jsonConverter.toConsentStatusResp(body)) {
                is Either.Right -> either.r
                is Either.Left -> throw either.t
            }
        } else {
            throw RequestFailedException(
                description = body,
                apiRequestPostfix = ApiRequestPostfix.CONSENT_STATUS.apiPostfix,
                httpStatusCode = "_$status",
            )
        }
    }

    override fun parseGetChoiceResp(r: Response, choice: ChoiceTypeParam): ChoiceResp {
        val body = r.body?.byteStream()?.reader()?.readText() ?: ""
        val status = r.code
        val mess = r.message
        logger.res(
            tag = "ChoiceResp",
            msg = mess,
            body = body,
            status = status.toString()
        )
        return if (r.isSuccessful) {
            when (val either: Either<ChoiceResp> = jsonConverter.toChoiceResp(body)) {
                is Either.Right -> either.r
                is Either.Left -> throw either.t
            }
        } else {
            throw RequestFailedException(
                description = body,
                apiRequestPostfix = ApiRequestPostfix.GET_CHOICE.apiPostfix,
                choice = "_${choice.type}",
                httpStatusCode = "_$status",
            )
        }
    }

    override fun parsePostGdprChoiceResp(r: Response): GdprCS {
        val body = r.body?.byteStream()?.reader()?.readText() ?: ""
        val status = r.code
        val mess = r.message
        logger.res(
            tag = "PostGdprChoiceResp",
            msg = mess,
            body = body,
            status = status.toString()
        )
        return if (r.isSuccessful) {
            when (val either: Either<GdprCS> = jsonConverter.toGdprPostChoiceResp(body)) {
                is Either.Right -> either.r
                is Either.Left -> throw either.t
            }
        } else {
            throw RequestFailedException(
                description = body,
                apiRequestPostfix = ApiRequestPostfix.POST_CHOICE_GDPR.apiPostfix,
                httpStatusCode = "_$status",
            )
        }
    }

    override fun parsePostCcpaChoiceResp(r: Response): CcpaCS {
        val body = r.body?.byteStream()?.reader()?.readText() ?: ""
        val status = r.code
        val mess = r.message
        logger.res(
            tag = "PostCcpaChoiceResp",
            msg = mess,
            body = body,
            status = status.toString()
        )
        return if (r.isSuccessful) {
            when (val either: Either<CcpaCS> = jsonConverter.toCcpaPostChoiceResp(body)) {
                is Either.Right -> either.r
                is Either.Left -> throw either.t
            }
        } else {
            throw RequestFailedException(
                description = body,
                apiRequestPostfix = ApiRequestPostfix.POST_CHOICE_CCPA.apiPostfix,
                httpStatusCode = "_$status",
            )
        }
    }

    override fun parsePvDataResp(r: Response): PvDataResp {
        val body = r.body?.byteStream()?.reader()?.readText() ?: ""
        val status = r.code
        val mess = r.message
        return if (r.isSuccessful) {
            when (val either: Either<PvDataResp> = jsonConverter.toPvDataResp(body)) {
                is Either.Right -> {
                    val campaign = either.getOrNull()?.let {
                        it.gdpr?.let { "GDPR" }
                            ?: it.ccpa?.let { "CCPA" }
                            ?: it.usnat?.let { "USNAT" }
                    }
                    logger.res(
                        tag = "PvDataResp - $campaign",
                        msg = mess,
                        body = body,
                        status = status.toString()
                    )
                    either.r
                }
                is Either.Left -> {
                    logger.res(
                        tag = "PvDataResp",
                        msg = mess,
                        body = body,
                        status = status.toString()
                    )
                    throw either.t
                }
            }
        } else {
            throw RequestFailedException(
                description = body,
                apiRequestPostfix = ApiRequestPostfix.PV_DATA.apiPostfix,
                httpStatusCode = "_$status",
            )
        }
    }

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

package com.sourcepoint.cmplibrary.data.network.util

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.model.optimized.* // ktlint-disable
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.InvalidRequestException
import com.sourcepoint.cmplibrary.exception.InvalidResponseWebMessageException
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.ConsentResp
import com.sourcepoint.cmplibrary.model.CustomConsentResp
import com.sourcepoint.cmplibrary.util.check
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

    override fun parseNativeMessRes(r: Response): Either<NativeMessageResp> = check {
        val body = r.body()?.byteStream()?.reader()?.readText() ?: fail("Body Response")
        if (r.isSuccessful) {
            when (val either: Either<NativeMessageResp> = jsonConverter.toNativeMessageResp(body)) {
                is Either.Right -> either.r
                is Either.Left -> throw either.t
            }
        } else {
            throw InvalidRequestException(description = body)
        }
    }

    override fun parseNativeMessResK(r: Response): Either<NativeMessageRespK> = check {
        val body = r.body()?.byteStream()?.reader()?.readText() ?: fail("Body Response")
        if (r.isSuccessful) {
            when (val either: Either<NativeMessageRespK> = jsonConverter.toNativeMessageRespK(body)) {
                is Either.Right -> either.r
                is Either.Left -> throw either.t
            }
        } else {
            throw InvalidRequestException(description = body)
        }
    }

    override fun parseConsentResEither(r: Response, campaignType: CampaignType): Either<ConsentResp> = check {
        val body = r.body()?.byteStream()?.reader()?.readText() ?: fail("Body Response")
        if (r.isSuccessful) {
            when (val either: Either<ConsentResp> = jsonConverter.toConsentResp(body, campaignType)) {
                is Either.Right -> either.r
                is Either.Left -> throw either.t
            }
        } else {
            throw InvalidRequestException(description = body)
        }
    }

    override fun parseConsentRes(r: Response, campaignType: CampaignType): ConsentResp {
        val body = r.body()?.byteStream()?.reader()?.readText() ?: fail("Body Response")
        val status = r.code()
        val mess = r.message()
        logger.res(
            tag = "ConsentResp",
            msg = mess,
            body = body,
            status = status.toString()
        )
        return if (r.isSuccessful) {
            when (val either: Either<ConsentResp> = jsonConverter.toConsentResp(body, campaignType)) {
                is Either.Right -> either.r
                is Either.Left -> throw either.t
            }
        } else {
            throw InvalidRequestException(description = body)
        }
    }

    override fun parseCustomConsentRes(r: Response): CustomConsentResp {
        val body = r.body()?.byteStream()?.reader()?.readText() ?: fail("Body Response")
        val status = r.code()
        val mess = r.message()
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
        val body = r.body()?.byteStream()?.reader()?.readText() ?: fail("Body Response")
        val status = r.code()
        val mess = r.message()
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
            throw InvalidRequestException(description = body)
        }
    }

    override fun parseConsentStatusResp(r: Response): ConsentStatusResp {
        val body = r.body()?.byteStream()?.reader()?.readText() ?: fail("Body Response")
        val status = r.code()
        val mess = r.message()
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
            throw InvalidRequestException(description = body)
        }
    }

    override fun parseGetChoiceResp(r: Response): ChoiceResp {
        val body = r.body()?.byteStream()?.reader()?.readText() ?: fail("Body Response")
        val status = r.code()
        val mess = r.message()
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
            throw InvalidRequestException(description = body)
        }
    }

    override fun parsePostGdprChoiceResp(r: Response): GdprCS {
        val body = r.body()?.byteStream()?.reader()?.readText() ?: fail("Body Response")
        val status = r.code()
        val mess = r.message()
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
            throw InvalidRequestException(description = body)
        }
    }

    override fun parsePostCcpaChoiceResp(r: Response): CcpaCS {
        val body = r.body()?.byteStream()?.reader()?.readText() ?: fail("Body Response")
        val status = r.code()
        val mess = r.message()
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
            throw InvalidRequestException(description = body)
        }
    }

    override fun parsePvDataResp(r: Response): PvDataResp {
        val body = r.body()?.byteStream()?.reader()?.readText() ?: fail("Body Response")
        val status = r.code()
        val mess = r.message()
        return if (r.isSuccessful) {
            when (val either: Either<PvDataResp> = jsonConverter.toPvDataResp(body)) {
                is Either.Right -> {
                    val campaign = either.r.gdpr?.let { "GDPR" } ?: ("" + either.r.ccpa?.let { "CCPA" }) ?: ""
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
            throw InvalidRequestException(description = body)
        }
    }

    override fun parseMessagesResp(r: Response): MessagesResp {
        val body = r.body()?.byteStream()?.reader()?.readText() ?: fail("Body Response")
        val status = r.code()
        val mess = r.message()
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
            throw InvalidRequestException(description = body)
        }
    }

    override fun parseMessagesResp2(r: Response): Either<MessagesResp> = check {
        val body = r.body()?.byteStream()?.reader()?.readText() ?: fail("Body Response")
        val status = r.code()
        val mess = r.message()
        logger.res(
            tag = "MessagesResp",
            msg = mess,
            body = body,
            status = status.toString()
        )
        if (r.isSuccessful) {
            when (val either: Either<MessagesResp> = jsonConverter.toMessagesResp(body)) {
                is Either.Right -> either.r
                is Either.Left -> throw either.t
            }
        } else {
            throw InvalidRequestException(description = body)
        }
    }

    private fun fail(param: String): Nothing {
        throw InvalidResponseWebMessageException(description = "$param object is null")
    }
}

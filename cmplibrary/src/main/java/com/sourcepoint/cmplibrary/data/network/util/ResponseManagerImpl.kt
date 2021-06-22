package com.sourcepoint.cmplibrary.data.network.util

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.InvalidRequestException
import com.sourcepoint.cmplibrary.exception.InvalidResponseWebMessageException
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.ConsentResp
import com.sourcepoint.cmplibrary.model.CustomConsentResp
import com.sourcepoint.cmplibrary.model.UnifiedMessageResp
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

    /**
     * @param r http response
     * @return an [Either] object of a [MessageResp] type parameter
     */
    override fun parseResponse(r: Response): Either<UnifiedMessageResp> = check {
        val body = r.body()?.byteStream()?.reader()?.readText() ?: fail("Body Response")
        val status = r.code()
        val mess = r.message()
        logger.res(
            tag = "UnifiedMessageResp",
            msg = mess,
            body = body,
            status = status.toString()
        )
        if (r.isSuccessful) {
            when (val either: Either<UnifiedMessageResp> = jsonConverter.toUnifiedMessageResp(body)) {
                is Either.Right -> either.r
                is Either.Left -> throw either.t
            }
        } else {
            throw InvalidRequestException(description = body)
        }
    }

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

    private fun fail(param: String): Nothing {
        throw InvalidResponseWebMessageException(description = "$param object is null")
    }
}

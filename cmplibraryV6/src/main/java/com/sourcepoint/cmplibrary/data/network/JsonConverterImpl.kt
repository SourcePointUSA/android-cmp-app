package com.sourcepoint.cmplibrary.data.network

import com.fasterxml.jackson.jr.ob.JSON
import com.fasterxml.jackson.jr.ob.impl.DeferredMap
import com.sourcepoint.cmplibrary.util.Either
import com.sourcepoint.cmplibrary.util.check
import com.sourcepoint.gdpr_cmplibrary.exception.InvalidResponseWebMessageException

/**
 * Factory method for building an instance of JsonConverter
 */
internal fun JsonConverter.Companion.create(): JsonConverter = JsonConverterImpl()


private class JsonConverterImpl : JsonConverter {

    override fun toUWResp(body: String): Either<UWResp> = check {

        val map: MutableMap<String, Any> = JSON.std.mapFrom(body)

        val gdpr = (map["gdpr"] as? DeferredMap) ?: fail("gdpr")
        val uuid = (gdpr["uuid"] as? String) ?: fail("uuid")
        val meta = (gdpr["meta"] as? String) ?: fail("meta")
        val message = gdpr["message"]?.let { JSON.std.asString(it) } ?: fail("message")
        val userConsentMap = JSON.std.asString(gdpr["userConsent"] as? DeferredMap) ?: fail("userConsent")
        val userConsent = JSON.std.beanFrom(UserConsent::class.java, userConsentMap)

        UWResp(
            Gdpr(
                message = message,
                meta = meta,
                userConsent = userConsent,
                uuid = uuid
            )
        )
    }

    private fun fail(param: String): Nothing {
        throw InvalidResponseWebMessageException(description = "$param object is null")
    }
}
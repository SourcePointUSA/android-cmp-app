package com.sourcepoint.cmplibrary.data.network.converter

import com.fasterxml.jackson.jr.ob.JSON
import com.fasterxml.jackson.jr.ob.impl.DeferredMap
import com.sourcepoint.cmplibrary.core.layout.model.NativeMessageDto
import com.sourcepoint.cmplibrary.data.network.model.ConsentAction
import com.sourcepoint.cmplibrary.data.network.model.NativeMessageResp
import com.sourcepoint.cmplibrary.data.network.model.NativeMessageRespK
import com.sourcepoint.cmplibrary.data.network.model.UnifiedMessageResp
import com.sourcepoint.cmplibrary.data.network.model.consent.ConsentResp
import com.sourcepoint.cmplibrary.exception.InvalidResponseWebMessageException
import com.sourcepoint.cmplibrary.model.ActionType
import com.sourcepoint.cmplibrary.util.Either
import com.sourcepoint.cmplibrary.util.check
import org.json.JSONObject

/**
 * Factory method to create an instance of a [JsonConverter] using its implementation
 * @return an instance of the [JsonConverterImpl] implementation
 */
internal fun JsonConverter.Companion.create(): JsonConverter = JsonConverterImpl()

/**
 * Implementation of the [JsonConverter] interface
 */
private class JsonConverterImpl : JsonConverter {

    override fun toUnifiedMessageResp(body: String): Either<UnifiedMessageResp> = check {
        body.toUnifiedMessageRespDto()
    }

    override fun toConsentAction(json: String): Either<ConsentAction> = check {

        val map: MutableMap<String, Any> = JSON.std.mapFrom(json)

        val actionType = (map["actionType"] as? Int)?.let { ActionType.values().find { v -> v.code == it } } ?: fail("actionType")
        val choiceId = (map["choiceId"] as? String)
        val privacyManagerId = (map["privacyManagerId"] as? String)
        val pmTab = (map["pmTab"] as? String)
        val requestFromPm = (map["requestFromPm"] as? Boolean) ?: fail("requestFromPm")
        val saveAndExitVariables = (map["saveAndExitVariables"] as? String)?.let { JSONObject(it) } ?: JSONObject()
        val consentLanguage = (map["consentLanguage"] as? String) ?: "EN"

        ConsentAction(
            actionType = actionType,
            choiceId = choiceId,
            privacyManagerId = privacyManagerId,
            pmTab = pmTab,
            requestFromPm = requestFromPm,
            saveAndExitVariables = saveAndExitVariables,
            consentLanguage = consentLanguage
        )
    }

    override fun toNativeMessageResp(body: String): Either<NativeMessageResp> = check {
        val map: MutableMap<String, Any> = JSON.std.mapFrom(body)
        val msgJSON = (map["msgJSON"] as? DeferredMap) ?: fail("msgJSON")
        NativeMessageResp(msgJSON = JSONObject(JSON.std.asString(msgJSON)))
    }

    override fun toNativeMessageRespK(body: String): Either<NativeMessageRespK> = check {
        val map: MutableMap<String, Any> = JSON.std.mapFrom(body)
        val bean: NativeMessageDto = JSON.std.beanFrom(NativeMessageDto::class.java, JSON.std.asString(map["msgJSON"]))
        NativeMessageRespK(msg = bean)
    }

    override fun toConsentResp(body: String): Either<ConsentResp> = check {
        ConsentResp(JSONObject(body))
    }

    override fun toNativeMessageDto(body: String): Either<NativeMessageDto> = check {
        JSON.std.beanFrom(NativeMessageDto::class.java, body)
    }

    /**
     * Util method to throws a [ConsentLibExceptionK] with a custom message
     * @param param name of the null object
     */
    private fun fail(param: String): Nothing {
        throw InvalidResponseWebMessageException(description = "$param object is null")
    }
}

package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.core.layout.model.NativeMessageDto
import com.sourcepoint.cmplibrary.core.layout.model.toNativeMessageDto
import com.sourcepoint.cmplibrary.data.network.model.*
import com.sourcepoint.cmplibrary.data.network.model.consent.ConsentResp
import com.sourcepoint.cmplibrary.exception.InvalidResponseWebMessageException
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.model.ActionType
import com.sourcepoint.cmplibrary.model.getMap
import com.sourcepoint.cmplibrary.model.toTreeMap
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

    override fun toUnifiedMessageResp1203(body: String): Either<UnifiedMessageResp1203> = check {
        body.toUnifiedMessageRespDto1203()
    }

    override fun toConsentAction(body: String): Either<ConsentAction> = check {

        val map: Map<String, Any?> = JSONObject(body).toTreeMap()

        val actionType = (map["actionType"] as? Int)?.let { ActionType.values().find { v -> v.code == it } } ?: fail("actionType")
        val choiceId = (map["choiceId"] as? String)
        val legislation = (map["legislation"] as? String) ?: "GDPR" // fail("legislation") // TODO In case of PM we don't receive this value!!!!
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
            consentLanguage = consentLanguage,
            legislation = Legislation.valueOf(legislation)
        )
    }

    override fun toNativeMessageResp(body: String): Either<NativeMessageResp> = check {
        val map: Map<String, Any?> = JSONObject(body).toTreeMap()
        val msgJSON = map.getMap("msgJSON") ?: fail("msgJSON")
        NativeMessageResp(msgJSON = JSONObject(msgJSON))
    }

    override fun toNativeMessageRespK(body: String): Either<NativeMessageRespK> = check {
        val map: Map<String, Any?> = JSONObject(body).toTreeMap()
        val bean: NativeMessageDto = map.getMap("msgJSON")!!.toNativeMessageDto()
        NativeMessageRespK(msg = bean)
    }

    override fun toConsentResp(body: String): Either<ConsentResp> = check {
        val obj = JSONObject(body)
        val map: Map<String, Any?> = JSONObject(body).toTreeMap()
        map.getMap("userConsent")?.let { it.toGDPRUserConsent() }
        obj.get("userConsent")
        ConsentResp(
            content = JSONObject(body),
            userConsent = obj["userConsent"].toString(),

        )
    }

    override fun toNativeMessageDto(body: String): Either<NativeMessageDto> = check {
        JSONObject(body).toTreeMap().toNativeMessageDto()
    }

    /**
     * Util method to throws a [ConsentLibExceptionK] with a custom message
     * @param param name of the null object
     */
    private fun fail(param: String): Nothing {
        throw InvalidResponseWebMessageException(description = "$param object is null")
    }
}

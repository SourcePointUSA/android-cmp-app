package com.sourcepoint.cmplibrary.data.network.converter

import com.fasterxml.jackson.jr.ob.JSON
import com.fasterxml.jackson.jr.ob.impl.DeferredMap
import com.sourcepoint.cmplibrary.data.network.model.ConsentAction
import com.sourcepoint.cmplibrary.data.network.model.Gdpr
import com.sourcepoint.cmplibrary.data.network.model.MessageResp
import com.sourcepoint.cmplibrary.data.network.model.UserConsent
import com.sourcepoint.cmplibrary.util.Either
import com.sourcepoint.cmplibrary.util.check
import com.sourcepoint.gdpr_cmplibrary.ActionTypes
import com.sourcepoint.gdpr_cmplibrary.exception.InvalidResponseWebMessageException
import org.json.JSONObject

/**
 * Factory method for building an instance of JsonConverter
 */
internal fun JsonConverter.Companion.create(): JsonConverter = JsonConverterImpl()

/**
 * Implementation of the [JsonConverter] interface
 */
private class JsonConverterImpl : JsonConverter {

    override fun toUWResp(body: String): Either<MessageResp> = check {

        val map: MutableMap<String, Any> = JSON.std.mapFrom(body)

        val gdpr = (map["gdpr"] as? DeferredMap) ?: fail("gdpr")
        val uuid = (gdpr["uuid"] as? String) ?: fail("uuid")
        val meta = (gdpr["meta"] as? String) ?: fail("meta")
        val message = gdpr["message"]?.let { JSON.std.asString(it) } ?: fail("message")
        val userConsentMap = JSON.std.asString(gdpr["userConsent"] as? DeferredMap) ?: fail("userConsent")
        val userConsent = JSON.std.beanFrom(UserConsent::class.java, userConsentMap)

        MessageResp(
            Gdpr(
                message = message,
                meta = meta,
                userConsent = userConsent,
                uuid = uuid
            )
        )
    }

    override fun toConsentAction(json: String): Either<ConsentAction> = check {

        val map: MutableMap<String, Any> = JSON.std.mapFrom(json)

        val actionType = (map["actionType"] as? Int)?.let { ActionTypes.valueOf(it) } ?: fail("actionType")
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

    /**
     * Util method to throws a [ConsentLibExceptionK] with a custom message
     * @param param name of the null object
     */
    private fun fail(param: String): Nothing {
        throw InvalidResponseWebMessageException(description = "$param object is null")
    }
}

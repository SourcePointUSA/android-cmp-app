package com.sourcepoint.cmplibrary.data.network.converter

import com.fasterxml.jackson.jr.ob.JSON
import com.fasterxml.jackson.jr.ob.impl.DeferredMap
import com.sourcepoint.cmplibrary.data.network.model.ConsentAction
import com.sourcepoint.cmplibrary.data.network.model.MessageResp
import com.sourcepoint.cmplibrary.data.network.model.NativeMessageResp
import com.sourcepoint.cmplibrary.data.network.model.getAppliedLegislation
import com.sourcepoint.cmplibrary.exception.InvalidResponseWebMessageException
import com.sourcepoint.cmplibrary.util.Either
import com.sourcepoint.cmplibrary.util.check
import com.sourcepoint.gdpr_cmplibrary.ActionTypes
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

    override fun toMessageResp(body: String): Either<MessageResp> = check {

        val map: MutableMap<String, Any> = JSON.std.mapFrom(body)

        // check the number of message contained. We can only have 1 instance
        val numberOfMessages = map.toList().count { (it.second as DeferredMap).containsKey("message") }
        if (numberOfMessages != 1) fail("We have [$numberOfMessages] inst. of Message. Only one Message object can exist int the MessageResp!!!")

        // create a message JSONObject
        val legislationEntry = map.toList().first { (it.second as DeferredMap).containsKey("message") }

        val legislationContent = legislationEntry.second
        val uuid = (legislationContent as? DeferredMap)?.get("uuid") as? String ?: fail("uuid")
        val meta = (legislationContent as? DeferredMap)?.get("meta") as? String ?: fail("uuid")
        val userConsent = (legislationContent as? DeferredMap)?.get("userConsent") ?: fail("uuid")
        val message = (legislationContent as? DeferredMap)?.get("message") ?: fail("message")
        val messageObj = JSONObject(JSON.std.asString(message))

        // extract the applied Legislation
        val legislation = legislationEntry.first.getAppliedLegislation()

        // build the object
        MessageResp(
            legislation = legislation,
            message = messageObj,
            meta = meta,
            uuid = uuid
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

    override fun toNativeMessageResp(body: String): Either<NativeMessageResp> = check {
        val map: MutableMap<String, Any> = JSON.std.mapFrom(body)
        val msgJSON = (map["msgJSON"] as? DeferredMap) ?: fail("msgJSON")
        NativeMessageResp(msgJSON = JSONObject(JSON.std.asString(msgJSON)))
    }

    /**
     * Util method to throws a [ConsentLibExceptionK] with a custom message
     * @param param name of the null object
     */
    private fun fail(param: String): Nothing {
        throw InvalidResponseWebMessageException(description = "$param object is null")
    }
}

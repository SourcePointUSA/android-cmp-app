package com.sourcepoint.cmplibrary.data.network.converter

import com.fasterxml.jackson.jr.ob.JSON
import com.fasterxml.jackson.jr.ob.impl.DeferredMap
import com.sourcepoint.cmplibrary.data.network.model.* // ktlint-disable
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

    override fun toMessageResp(body: String): Either<MessageResp> = check {

        val map: MutableMap<String, Any> = JSON.std.mapFrom(body)

        val gdpr = (map["gdpr"] as? DeferredMap)?.let {
            val uuid = (it["uuid"] as? String) ?: fail("uuid")
            val meta = (it["meta"] as? String) ?: fail("meta")
            val message = it["message"]?.let { JSON.std.asString(it) } ?: fail("message")

            val userConsentMap = (it["userConsent"] as? DeferredMap) ?: fail("userConsent")
//        val userConsentJson = JSON.std.asString(userConsentMap) ?: fail("userConsent")

            val uc = JSONObject(JSON.std.asString(userConsentMap)!!)
            com.sourcepoint.gdpr_cmplibrary.GDPRUserConsent(uc, "consentUUID", null)

            val userConsent = GDPRUserConsent(
                acceptedCategories = (userConsentMap["acceptedCategories"] as? Iterable<Any?>)?.filterNotNull()
                    ?: fail("acceptedCategories"),
                acceptedVendors = (userConsentMap["acceptedVendors"] as? Iterable<Any?>)?.filterNotNull()
                    ?: fail("acceptedVendors"),
                legIntCategories = (userConsentMap["legIntCategories"] as? Iterable<Any?>)?.filterNotNull()
                    ?: fail("legIntCategories"),
                specialFeatures = (userConsentMap["specialFeatures"] as? Iterable<Any?>)?.filterNotNull()
                    ?: fail("specialFeatures"),
                tcData = (userConsentMap["TCData"] as? DeferredMap) ?: DeferredMap(false),
                vendorsGrants = (userConsentMap["grants"] as? DeferredMap) ?: DeferredMap(false),
                euconsent = (userConsentMap["euconsent"] as? String) ?: ""
            )

            Gdpr(
                uuid = uuid,
                GDPRUserConsent = userConsent,
                meta = meta,
                message = message
            )
        }

        MessageResp(
            gdpr = gdpr
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

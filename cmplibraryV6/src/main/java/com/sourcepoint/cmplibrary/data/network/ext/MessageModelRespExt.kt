package com.sourcepoint.cmplibrary.data.network.ext

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.map
import com.sourcepoint.cmplibrary.data.network.converter.failParam
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.model.CampaignResp
import com.sourcepoint.cmplibrary.model.Ccpa
import com.sourcepoint.cmplibrary.model.GDPRConsent
import com.sourcepoint.cmplibrary.model.Gdpr
import com.sourcepoint.cmplibrary.model.UnifiedMessageResp
import com.sourcepoint.cmplibrary.model.getFieldValue
import com.sourcepoint.cmplibrary.model.getMap
import com.sourcepoint.cmplibrary.model.toCCPAUserConsent
import com.sourcepoint.cmplibrary.model.toJSONObj
import com.sourcepoint.cmplibrary.model.toTreeMap
import com.sourcepoint.cmplibrary.util.check
import org.json.JSONObject

internal fun String.toUnifiedMessageRespDto(): UnifiedMessageResp {
    return JSONObject(this).toUnifiedMessageRespDto()
}

internal fun JSONObject.toUnifiedMessageRespDto(): UnifiedMessageResp {
    val map: Map<String, Any?> = this.toTreeMap()
    val localState = map.getFieldValue<String>("localState") ?: ""
    val propertyPriorityData = map.getMap("propertyPriorityData")?.toJSONObj() ?: failParam("propertyPriorityData")

    val listEither: List<Either<CampaignResp?>> = map
        .getFieldValue<List<Map<String, Any?>>>("campaigns")
        ?.map { check { it.toCampaignResp1203() } }
        ?: emptyList()

    val list = listEither.fold(mutableListOf<CampaignResp>()) { acc, elem ->
        elem.map { content -> content?.let { acc.add(content) } }
        acc
    }

    return UnifiedMessageResp(
        thisContent = this,
        campaigns = list,
        localState = localState,
        propertyPriorityData = propertyPriorityData
    )
}

internal fun Map<String, Any?>.toCampaignResp1203(): CampaignResp? {
    return when (getFieldValue<String>("type")?.toUpperCase() ?: failParam("type")) {
        Legislation.GDPR.name -> this.toGDPR()
        Legislation.CCPA.name -> this.toCCPA()
        else -> null
    }
}

internal fun String.toCCPA(): Ccpa? {
    val map: Map<String, Any?> = JSONObject(this).toTreeMap()
    return map.toCCPA()
}

private fun Map<String, Any?>.toCCPA(): Ccpa? {

    val message = getMap("message")?.toJSONObj()
    val messageMetaData = getMap("messageMetaData")?.toJSONObj()

    return Ccpa(
        thisContent = JSONObject(this),
        applies = getFieldValue<Boolean>("applies") ?: false,
        message = message,
        messageMetaData = messageMetaData,
        userConsent = getMap("userConsent")?.toCCPAUserConsent() ?: failParam("CCPAUserConsent")
    )
}

internal fun Map<String, Any?>.toGDPR(): Gdpr {

    val message = getMap("message")?.toJSONObj()
    val messageMetaData = getMap("messageMetaData")?.toJSONObj()

    return Gdpr(
        thisContent = JSONObject(this),
        applies = getFieldValue<Boolean>("applies") ?: false,
        message = message,
        messageMetaData = messageMetaData,
        userConsent = getMap("userConsent")?.toGDPRUserConsent1203() ?: failParam("GDPRUserConsent")
    )
}

internal fun String.toGDPR(): Gdpr? {
    val map: Map<String, Any?> = JSONObject(this).toTreeMap()
    return map.toGDPR()
}

internal fun Map<String, Any?>.toGDPRUserConsent1203(): GDPRConsent {

    val tcData: Map<String, Any?> = getMap("TCData") ?: emptyMap()
    val vendorsGrants = getMap("grants") ?: failParam("grants")
    val euConsent = getFieldValue<String>("euconsent") ?: failParam("euconsent")

    return GDPRConsent(
        thisContent = JSONObject(this),
        tcData = tcData,
        vendorsGrants = vendorsGrants,
        euConsent = euConsent
    )
}

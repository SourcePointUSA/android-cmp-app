package com.sourcepoint.cmplibrary.model.ext

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.map
import com.sourcepoint.cmplibrary.data.network.converter.failParam
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.CampaignResp
import com.sourcepoint.cmplibrary.model.Ccpa
import com.sourcepoint.cmplibrary.model.Gdpr
import com.sourcepoint.cmplibrary.model.UnifiedMessageResp
import com.sourcepoint.cmplibrary.util.check
import okhttp3.HttpUrl
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
        ?.map { check { it.toCampaignResp() } }
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

internal fun Map<String, Any?>.toCampaignResp(): CampaignResp? {
    return when (getFieldValue<String>("type")?.toUpperCase() ?: failParam("type")) {
        CampaignType.GDPR.name -> this.toGDPR()
        CampaignType.CCPA.name -> this.toCCPA()
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
    val url = getFieldValue<String>("url")

    return Ccpa(
        thisContent = JSONObject(this),
        applies = getFieldValue<Boolean>("applies") ?: false,
        message = message,
        url = url?.let { HttpUrl.parse(it) },
        messageMetaData = messageMetaData,
        userConsent = getMap("userConsent")?.toCCPAUserConsent() ?: failParam("CCPAUserConsent")
    )
}

internal fun Map<String, Any?>.toGDPR(): Gdpr {

    val message = getMap("message")?.toJSONObj()
    val messageMetaData = getMap("messageMetaData")?.toJSONObj()
    val url = getFieldValue<String>("url")

    return Gdpr(
        thisContent = JSONObject(this),
        applies = getFieldValue<Boolean>("applies") ?: false,
        message = message,
        url = url?.let { HttpUrl.parse(it) },
        messageMetaData = messageMetaData,
        userConsent = getMap("userConsent")?.toGDPRUserConsent() ?: failParam("GDPRUserConsent")
    )
}

internal fun String.toGDPR(): Gdpr? {
    val map: Map<String, Any?> = JSONObject(this).toTreeMap()
    return map.toGDPR()
}

package com.sourcepoint.cmplibrary.data.network.model

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.map
import com.sourcepoint.cmplibrary.data.network.converter.failParam
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.CampaignResp
import com.sourcepoint.cmplibrary.model.Ccpa
import com.sourcepoint.cmplibrary.model.Gdpr
import com.sourcepoint.cmplibrary.model.UnifiedMessageResp
import com.sourcepoint.cmplibrary.model.exposed.MessageSubCategory
import com.sourcepoint.cmplibrary.util.check
import okhttp3.HttpUrl
import org.json.JSONObject
import java.util.* // ktlint-disable

internal fun String.toUnifiedMessageRespDto(): UnifiedMessageResp {
    return JSONObject(this).toUnifiedMessageRespDto()
}

internal fun JSONObject.toUnifiedMessageRespDto(): UnifiedMessageResp {
    val map: Map<String, Any?> = this.toTreeMap()
    val localState = map.getMap("localState")?.toJSONObj() ?: JSONObject()
    val propertyPriorityData = map.getMap("propertyPriorityData")?.toJSONObj() ?: failParam("propertyPriorityData")

    val listEither: List<Either<CampaignResp?>> = map
        .getFieldValue<List<Map<String, Any?>>>("campaigns")
        ?.map {
            check {
                val campaignType = it.getFieldValue<String>("type")?.lowercase() ?: ""
                val uuid: String? = map.getMap("localState")?.getMap(campaignType)?.getFieldValue("uuid")
                it.toCampaignResp(uuid)
            }
        }
        ?: emptyList()

    val list = listEither.fold(mutableListOf<CampaignResp>()) { acc, elem ->
        elem.map { content -> content?.let { acc.add(content) } }
        acc
    }

    return UnifiedMessageResp(
        thisContent = this,
        campaigns = list,
        localState = localState.toString(),
        propertyPriorityData = propertyPriorityData
    )
}

internal fun Map<String, Any?>.toCampaignResp(uuid: String?): CampaignResp? {
    return when (getFieldValue<String>("type")?.uppercase(Locale.getDefault()) ?: failParam("type")) {
        CampaignType.GDPR.name -> this.toGDPR(uuid)
        CampaignType.CCPA.name -> this.toCCPA(uuid)
        else -> null
    }
}

internal fun String.toCCPA(uuid: String?): Ccpa {
    val map: Map<String, Any?> = JSONObject(this).toTreeMap()
    return map.toCCPA(uuid)
}

internal fun Map<String, Any?>.toCCPA(uuid: String?): Ccpa {

    val message = getMap("message")?.toJSONObj()
    val messageMetaData = getMap("messageMetaData")?.toJSONObj()
    val url = getFieldValue<String>("url")
    val messageSubCategory = MessageSubCategory.values().find { m -> m.code == messageMetaData?.getInt("subCategoryId") } ?: MessageSubCategory.TCFv2
    val applies = getFieldValue<Boolean>("applies") ?: false

    return Ccpa(
        thisContent = JSONObject(this),
        applies = applies,
        message = message,
        url = url?.let { HttpUrl.parse(it) },
        messageMetaData = messageMetaData,
        userConsent = getMap("userConsent")?.toCCPAUserConsent(uuid, applies) ?: failParam("CCPAUserConsent"),
        messageSubCategory = messageSubCategory
    )
}

internal fun Map<String, Any?>.toGDPR(uuid: String?): Gdpr {

    val message = getMap("message")?.toJSONObj()
    val messageMetaData = getMap("messageMetaData")?.toJSONObj()
    val url = getFieldValue<String>("url")
    val messageSubCategory = MessageSubCategory.values().find { m -> m.code == messageMetaData?.getInt("subCategoryId") } ?: MessageSubCategory.TCFv2
    val applies = getFieldValue<Boolean>("applies") ?: false

    return Gdpr(
        thisContent = JSONObject(this),
        applies = applies,
        message = message,
        url = url?.let { HttpUrl.parse(it) },
        messageMetaData = messageMetaData,
        userConsent = getMap("userConsent")?.toGDPRUserConsent(uuid = uuid, applies) ?: failParam("GDPRUserConsent"),
        messageSubCategory = messageSubCategory
    )
}

internal fun String.toGDPR(uuid: String?): Gdpr {
    val map: Map<String, Any?> = JSONObject(this).toTreeMap()
    return map.toGDPR(uuid)
}

package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.map
import com.sourcepoint.cmplibrary.data.network.converter.fail
import com.sourcepoint.cmplibrary.data.network.converter.failParam
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.CcpaStatus
import com.sourcepoint.cmplibrary.model.exposed.GDPRPurposeGrants
import com.sourcepoint.cmplibrary.model.exposed.MessageSubCategory
import com.sourcepoint.cmplibrary.model.getFieldValue
import com.sourcepoint.cmplibrary.model.getMap
import com.sourcepoint.cmplibrary.model.toJSONObj
import com.sourcepoint.cmplibrary.util.check
import okhttp3.HttpUrl
import org.json.JSONObject
import java.util.* //ktlint-disable

internal fun Map<String, Any?>.toMessagesResp(): MessagesResp {
    val propertyId = getFieldValue<Int>("propertyId") ?: failParam("propertyId")
    val localState = getMap("localState")?.toJSONObj()

    val listEither: List<Either<MessagesCampaign?>> = getFieldValue<List<Map<String, Any?>>>("campaigns")
        ?.map { check { it.toMessageCampaignResp() } }
        ?: emptyList()

    val list = listEither.fold(mutableListOf<MessagesCampaign>()) { acc, elem ->
        elem.map { content -> content?.let { acc.add(content) } }
        acc
    }

    return MessagesResp(
        localState = localState,
        campaigns = list,
        propertyId = propertyId,
        thisContent = JSONObject(this)
    )
}

internal fun Map<String, Any?>.toMessageCampaignResp(): MessagesCampaign? {
    return when (getFieldValue<String>("type")?.uppercase(Locale.getDefault()) ?: failParam("type")) {
        CampaignType.GDPR.name -> this.toGdprMess()
        CampaignType.CCPA.name -> this.toCcpaMess()
        else -> null
    }
}

internal fun Map<String, Any?>.toGdprMess(): GdprMessage {

    val message = getMap("message")?.toJSONObj()
    val messageMetaData = getMap("messageMetaData")?.toMessageMetaData()
    val url = getFieldValue<String>("url")
    val consentStatusCS = getMap("consentStatus")?.toConsentStatus()
    val dateCreated = getFieldValue<String>("dateCreated")
    val euconsent = getFieldValue<String>("euconsent")
    val addtlConsent = getFieldValue<String>("addtlConsent")
    val childPmId = getFieldValue<String>("childPmId")
    val hasLocalData = getFieldValue<Boolean>("hasLocalData")
    val vendorsGranted: Map<String, GDPRPurposeGrants> = getMap("grants")
        ?.map {
            Pair(
                it.key,
                GDPRPurposeGrants(
                    granted = ((it.value as? Map<String, Any?>)?.get("vendorGrant") as? Boolean) ?: false,
                    purposeGrants = ((it.value as? Map<String, Any?>)?.get("purposeGrants") as? Map<String, Boolean>)
                        ?: emptyMap()
                )
            )
        }
        ?.toMap() ?: emptyMap()

    return GdprMessage(
        thisContent = JSONObject(this),
        message = message,
        url = url?.let { HttpUrl.parse(it) },
        messageMetaData = messageMetaData,
        consentStatusCS = consentStatusCS,
        dateCreated = dateCreated,
        type = CampaignType.GDPR.name,
        customVendorsResponse = getMap("customVendorsResponse")?.toJSONObj(),
        euconsent = euconsent,
        addtlConsent = addtlConsent,
        childPmId = childPmId,
        grants = vendorsGranted,
        hasLocalData = hasLocalData ?: false,
        messageSubCategory = messageMetaData?.subCategoryId ?: MessageSubCategory.TCFv2,
    )
}

internal fun Map<String, Any?>.toCcpaMess(): CcpaMessage {

    val message = getMap("message")?.toJSONObj()
    val messageMetaData = getMap("messageMetaData")?.toMessageMetaData()
    val url = getFieldValue<String>("url")
    val applies = getFieldValue<Boolean>("applies") ?: false
    val dateCreated = getFieldValue<String>("dateCreated")
    val newUser = getFieldValue<Boolean>("newUser") ?: false
    val signedLspa = getFieldValue<Boolean>("signedLspa") ?: false
    val rejectedAll = getFieldValue<Boolean>("rejectedAll") ?: false
    val consentedAll = getFieldValue<Boolean>("consentedAll") ?: false
    val uspString: String = getFieldValue("uspstring") ?: failParam("uspString")
    val status: CcpaStatus = getFieldValue<String>("status")
        ?.let { s ->
            CcpaStatus.values().find {
                it.name == s
            }
        }
        ?: fail("CCPAStatus cannot be null!!!")

    return CcpaMessage(
        thisContent = JSONObject(this),
        applies = applies,
        type = CampaignType.CCPA.name,
        message = message,
        dateCreated = dateCreated,
        url = url?.let { HttpUrl.parse(it) },
        messageMetaData = messageMetaData,
        newUser = newUser,
        messageSubCategory = messageMetaData?.subCategoryId ?: MessageSubCategory.TCFv2,
        status = status,
        uspstring = uspString,
        signedLspa = signedLspa,
        consentedAll = consentedAll,
        rejectedAll = rejectedAll
    )
}

internal fun MessageMetaData.toJsonObj(): JSONObject {
    return JSONObject().apply {
        put("bucket", bucket)
        put("categoryId", categoryId)
        put("messageId", messageId)
        put("msgDescription", msgDescription)
        put("prtnUUID", prtnUUID)
        put("subCategoryId", subCategoryId.code)
    }
}

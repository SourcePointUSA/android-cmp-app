package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.data.network.converter.failParam
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.getFieldValue
import com.sourcepoint.cmplibrary.model.getMap
import org.json.JSONObject
import java.util.*

internal fun Map<String, Any?>.toChoiceAllResp(): ChoiceAllResp {

    val gdpr = getMap("gdpr")?.toGdprCA()
    val ccpa = getMap("ccpa")?.toCcpaCA()

    return ChoiceAllResp(
        ccpa = ccpa,
        gdpr = gdpr
    )
}

internal fun Map<String, Any?>.toCcpaCA(): CcpaCA {
    val applies = getFieldValue<Boolean>("applies") ?: failParam("ChoiceAllResp.ccpa - applies")
    val consentedAll = getFieldValue<Boolean>("consentedAll") ?: failParam("ChoiceAllResp.ccpa - consentedAll")
    val dateCreated = getFieldValue<String>("dateCreated") ?: failParam("ChoiceAllResp.ccpa - dateCreated")
    val gpcEnabled = getFieldValue<Boolean>("gpcEnabled") ?: failParam("ChoiceAllResp.ccpa - gpcEnabled")
    val rejectedAll = getFieldValue<Boolean>("rejectedAll") ?: failParam("ChoiceAllResp.ccpa - rejectedAll")
    val newUser = getFieldValue<Boolean>("newUser") ?: failParam("ChoiceAllResp.ccpa - newUser")
    val status = getFieldValue<String>("status") ?: failParam("ChoiceAllResp.ccpa - dateCrstatuseated")
    val uspstring = getFieldValue<String>("uspstring") ?: failParam("ChoiceAllResp.ccpa - uspstring")
    val uuid = getFieldValue<String>("uuid") ?: failParam("ChoiceAllResp.ccpa - uuid")

    val rejectedCategories = getFieldValue<Array<String>>("rejectedCategories") ?: failParam("ChoiceAllResp.ccpa - rejectedCategories")
    val rejectedVendors = getFieldValue<Array<String>>("rejectedCategories") ?: failParam("ChoiceAllResp.ccpa - rejectedVendors")

    return CcpaCA(
        applies = applies,
        consentedAll = consentedAll,
        dateCreated = dateCreated,
        gpcEnabled = gpcEnabled,
        rejectedCategories = rejectedCategories,
        rejectedVendors = rejectedVendors,
        rejectedAll = rejectedAll,
        newUser = newUser,
        status = status,
        uspstring = uspstring,
        uuid = uuid
    )
}
internal fun Map<String, Any?>.toGdprCA(): GdprCA {
    val addtlConsent = getFieldValue<String?>("addtlConsent") ?: failParam("ChoiceAllResp.gdpr - addtlConsent")
    val applies = getFieldValue<Boolean?>("applies") ?: failParam("ChoiceAllResp.gdpr - applies")
    val dateCreated = getFieldValue<String?>("dateCreated") ?: failParam("ChoiceAllResp.gdpr - dateCreated")
    val childPmId = getFieldValue<String>("childPmId") ?: failParam("ChoiceAllResp.gdpr - childPmId")
    val consentStatus = getFieldValue<JSONObject?>("consentStatus") ?: failParam("ChoiceAllResp.gdpr - consentStatus")
    val euconsent = getFieldValue<String?>("euconsent") ?: failParam("ChoiceAllResp.gdpr - euconsent")
    val grants = getFieldValue<JSONObject?>("grants") ?: failParam("ChoiceAllResp.gdpr - grants")
    val hasLocalData = getFieldValue<Boolean?>("hasLocalData") ?: failParam("ChoiceAllResp.gdpr - hasLocalData")
    val TCData = getFieldValue<JSONObject?>("TCData") ?: failParam("ChoiceAllResp.gdpr - TCData")
    val postPayload = getFieldValue<PostPayload?>("postPayload") ?: failParam("ChoiceAllResp.gdpr - postPayload")

    return GdprCA(
        addtlConsent = addtlConsent,
        applies = applies,
        dateCreated = dateCreated,
        childPmId = childPmId,
        consentStatus = consentStatus,
        euconsent = euconsent,
        grants = grants,
        hasLocalData = hasLocalData,
        TCData = TCData,
        postPayload = postPayload
    )
//    val addtlConsent: String?,
//    val applies: Boolean?,
//    val childPmId: String,
//    val consentStatus: JSONObject?,
//    val dateCreated: String?,
//    val euconsent: String?,
//    val grants: JSONObject?,
//    val hasLocalData: Boolean?,
//    val TCData: JSONObject?,
//    val postPayload: PostPayload?,
}


//internal fun toPvDataBody(
//    messages: MessagesResp,
//    accountId: Long,
//    siteId: Long,
//    gdprApplies: Boolean,
//    ccpaUuid: String,
//    gdprUuid: String
//): JSONObject {
//
//    val gdpr = messages.campaigns
//        .find { it.type == CampaignType.GDPR.name }
//        ?.let { it as? GdprMessage }
//        ?.let {
//            JSONObject().apply {
//                put("applies", gdprApplies)
//                put("uuid", gdprUuid)
//                put("accountId", accountId)
//                put("siteId", siteId)
//                put("euconsent", it.euconsent)
//                put("pubData", "string")
//                put("msgId", it.messageMetaData?.messageId)
//                put("categoryId", it.messageMetaData?.categoryId)
//                put("subCategoryId", it.messageMetaData?.subCategoryId?.code)
//                put("prtnUUID", it.messageMetaData?.prtnUUID)
//                put("sampleRate", BuildConfig.SAMPLE_RATE)
//                put("consentStatus", "string")
//            }
//        }
//
//    val ccpa = messages.campaigns
//        .find { it.type == CampaignType.CCPA.name }
//        ?.let { it as? CcpaMessage }
//        ?.let {
//            JSONObject().apply {
//                put("applies", it.applies)
//                put("uuid", ccpaUuid)
//                put("accountId", accountId)
//                put("siteId", siteId)
//                put("messageId", it.messageMetaData?.messageId)
//                put("pubData", "string")
//                put("sampleRate", BuildConfig.SAMPLE_RATE)
//                put("consentStatus", "string")
//            }
//        }
//
//    return JSONObject().apply {
//        put("gdpr", gdpr)
//        put("ccpa", ccpa)
//    }
//}

package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.data.network.converter.failParam
import com.sourcepoint.cmplibrary.model.getFieldValue
import com.sourcepoint.cmplibrary.model.getMap
import org.json.JSONObject

internal fun Map<String, Any?>.toChoiceAllResp(): ChoiceAllResp {

    val gdpr = getMap("gdpr")?.toGdprCA()
    val ccpa = getMap("ccpa")?.toCcpaCA()

    return ChoiceAllResp(
        thisContent = JSONObject(this),
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
        thisContent = JSONObject(this),
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
        thisContent = JSONObject(this),
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
}

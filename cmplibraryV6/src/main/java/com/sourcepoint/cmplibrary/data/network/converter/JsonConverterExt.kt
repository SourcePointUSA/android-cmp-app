package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.data.network.model.CampaignResp
import com.sourcepoint.cmplibrary.data.network.model.ConsentAction
import com.sourcepoint.cmplibrary.data.network.model.UnifiedMessageResp
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.model.ActionType
import com.sourcepoint.cmplibrary.model.getMap
import com.sourcepoint.cmplibrary.model.toTreeMap
import org.json.JSONObject

internal fun String.toUnifiedMessageRespDto(): UnifiedMessageResp {
    return JSONObject(this).toUnifiedMessageRespDto()
}

internal fun JSONObject.toUnifiedMessageRespDto(): UnifiedMessageResp {
    val map: Map<String, Any?> = this.toTreeMap()

    val list = mutableListOf<CampaignResp>()

    map.getMap("gdpr")?.toGDPR()?.also { list.add(it) }
    map.getMap("ccpa")?.toCCPA()?.also { list.add(it) }

    return UnifiedMessageResp(
        campaigns = list,
        thisContent = this
    )
}

internal fun String.toConsentAction(): ConsentAction {

    val map: Map<String, Any?> = JSONObject(this).toTreeMap()

    val actionType = (map["actionType"] as? Int)?.let { ActionType.values().find { v -> v.code == it } }
        ?: fail("actionType")
    val choiceId = (map["choiceId"] as? String)
    val legislation = (map["legislation"] as? String)
        ?: "CCPA" // fail("legislation") // TODO In case of PM we don't receive this value!!!!
    val privacyManagerId = (map["privacyManagerId"] as? String)
    val pmTab = (map["pmTab"] as? String)
    val requestFromPm = (map["requestFromPm"] as? Boolean) ?: fail("requestFromPm")
    val saveAndExitVariables = (map["saveAndExitVariables"] as? String)?.let { JSONObject(it) } ?: JSONObject()
    val consentLanguage = (map["consentLanguage"] as? String) ?: "EN"

    return ConsentAction(
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

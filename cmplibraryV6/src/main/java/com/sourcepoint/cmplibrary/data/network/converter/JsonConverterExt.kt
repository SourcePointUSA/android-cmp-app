package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.data.network.model.CampaignResp
import com.sourcepoint.cmplibrary.data.network.model.UnifiedMessageResp
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

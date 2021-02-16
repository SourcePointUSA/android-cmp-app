package com.sourcepoint.cmplibrary.data.network.converter

import com.fasterxml.jackson.jr.ob.JSON
import com.fasterxml.jackson.jr.ob.impl.DeferredMap
import com.sourcepoint.cmplibrary.data.network.model.* // ktlint-disable

internal fun String.toUnifiedMessageRespDto(): UnifiedMessageResp {
    val map: MutableMap<String, Any> = JSON.std.mapFrom(this)

    val list = mutableListOf<CampaignResp>()
    (map["gdpr"] as? DeferredMap)?.toGDPR()?.also { list.add(it) }
    (map["ccpa"] as? DeferredMap)?.toCCPA()?.also { list.add(it) }

    return UnifiedMessageResp(list)
}

// internal fun DeferredMap.toUserConsent(legislation: Legislation): SpConsent {
//    SpConsent(
//
//    )
//    return when (legislation) {
//        Legislation.GDPR -> this.toGDPRUserConsent()
//        Legislation.CCPA -> this.toCCPAUserConsent()
//    }
// }

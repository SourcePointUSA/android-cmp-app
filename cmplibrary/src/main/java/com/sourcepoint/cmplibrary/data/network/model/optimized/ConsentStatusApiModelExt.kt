package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.converter.toMapOfAny
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.ConsentResp
import com.sourcepoint.cmplibrary.model.exposed.CCPAConsentInternal
import com.sourcepoint.cmplibrary.model.exposed.GDPRConsentInternal
import com.sourcepoint.cmplibrary.util.check
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import org.json.JSONObject

internal fun GdprCS.toGDPRUserConsent(): GDPRConsentInternal {
    return GDPRConsentInternal(
        uuid = uuid,
        tcData = TCData?.toMapOfAny() ?: emptyMap(),
        grants = grants ?: emptyMap(),
        euconsent = euconsent ?: "",
        acceptedCategories = acceptedCategories,
        childPmId = null,
        applies = gdprApplies ?: applies ?: TCData?.fromTcDataToGdprApplies(),
        thisContent = JSONObject()
    )
}

internal fun CcpaCS.toCCPAConsentInternal(): CCPAConsentInternal {
    return CCPAConsentInternal(
        uuid = uuid,
        uspstring = uspstring ?: "",
        applies = ccpaApplies ?: applies ?: false,
        status = status,
        childPmId = null,
        rejectedVendors = rejectedVendors ?: emptyList(),
        rejectedCategories = rejectedCategories ?: emptyList(),
        thisContent = JSONObject()
    )
}

internal fun CcpaCS.toConsentResp(localState: String): Either<ConsentResp> = check {
    ConsentResp(
        uuid = uuid,
        localState = localState,
        campaignType = CampaignType.CCPA,
        userConsent = JsonConverter.converter.encodeToString(this),
        content = JSONObject()
    )
}

internal fun GdprCS.toConsentResp(localState: String): Either<ConsentResp> = check {
    ConsentResp(
        uuid = uuid,
        localState = localState,
        campaignType = CampaignType.GDPR,
        userConsent = JsonConverter.converter.encodeToString(this),
        content = JSONObject()
    )
}

internal fun Map<String, String>.toMapOfAny(): Map<String, Any> {
    val map = mutableMapOf<String, Any>()
    this.forEach { (k, v) -> map[k] = v }
    return map
}

internal fun Map<String, JsonElement>?.fromTcDataToGdprApplies(): Boolean? {
    return when (this?.get("IABTCF_gdprApplies")?.jsonPrimitive?.intOrNull) {
        1 -> true
        0 -> false
        else -> null
    }
}

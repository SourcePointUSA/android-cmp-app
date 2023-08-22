package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.model.exposed.CCPAConsentInternal
import com.sourcepoint.cmplibrary.model.exposed.GDPRConsentInternal
import com.sourcepoint.cmplibrary.model.exposed.GDPRPurposeGrants
import com.sourcepoint.cmplibrary.util.extensions.toMapOfAny
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
        acceptedCategories = grants?.toAcceptedCategories()?.toList(),
        childPmId = null,
        applies = TCData?.fromTcDataToGdprApplies(),
        thisContent = JSONObject(),
        webConsentPayload = webConsentPayload,
    )
}

internal fun CcpaCS.toCCPAConsentInternal(): CCPAConsentInternal {
    return CCPAConsentInternal(
        uuid = uuid,
        applies = applies ?: false,
        status = status,
        childPmId = null,
        rejectedVendors = rejectedVendors ?: emptyList(),
        rejectedCategories = rejectedCategories ?: emptyList(),
        thisContent = JSONObject(),
        signedLspa = signedLspa,
        webConsentPayload = webConsentPayload,
    )
}

internal fun Map<String, JsonElement>?.fromTcDataToGdprApplies(): Boolean? {
    return when (this?.get("IABTCF_gdprApplies")?.jsonPrimitive?.intOrNull) {
        1 -> true
        0 -> false
        else -> null
    }
}

internal fun Map<String, GDPRPurposeGrants>.toAcceptedCategories(): Iterable<String> {
    val map = this.toList().fold(mutableMapOf<String, Map<String, Boolean>>()) { acc, elem ->
        acc[elem.first] = elem.second.purposeGrants
        acc
    }
    val partitions = map.flatMap { it.value.toList() }.partition { it.second }
    val trueCategories = partitions.first.map { it.first }.toSet()
    val falseCategories = partitions.second.map { it.first }.toSet()
    return trueCategories.minus(falseCategories)
}

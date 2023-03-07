package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.CampaignReq
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.* // ktlint-disable

internal fun getMessageBody(
    propertyHref: String,
    accountId: Long,
    localState: JsonObject,
    campaigns: List<CampaignReq>,
    cs: ConsentStatus?,
    ccpaStatus: String?,
    consentLanguage: String?
): JsonObject {
    return buildJsonObject {
        put("accountId", accountId)
        putJsonObject("includeData") {
            putJsonObject("TCData") {
                put("type", "RecordString")
            }
            putJsonObject("campaigns") {
                put("type", "RecordString")
            }
        }
        put("propertyHref", "https://$propertyHref")
        put("hasCSP", true)
        put("campaigns", campaigns.toMetadataBody(cs, ccpaStatus))
        put("localState", localState)
        put("consentLanguage", consentLanguage)
    }
}

internal fun List<CampaignReq>.toMetadataBody(
    cs: ConsentStatus? = null,
    ccpaStatus: String? = null
): JsonObject {
    return buildJsonObject {
        this@toMetadataBody.forEach { c ->
            putJsonObject(c.campaignType.name.lowercase()) {
                if (c.campaignType == CampaignType.GDPR) {
                    put(
                        "consentStatus",
                        cs?.let { JsonConverter.converter.encodeToJsonElement(it) } ?: JsonObject(mapOf())
                    )
                    cs?.let { put("hasLocalData", true) }
                }
                if (c.campaignType == CampaignType.CCPA) {
                    put("status", ccpaStatus ?: "")
                    ccpaStatus?.let { put("hasLocalData", true) }
                }
                putJsonObject("targetingParams") {
                    c.targetingParams.forEach { t -> put(t.key, t.value) }
                }
                put("groupPmId", c.groupPmId)
            }
        }
    }
}

internal fun List<CampaignReq>.toMetadataArgs(): MetaDataArg {
    val json = buildJsonObject {
        this@toMetadataArgs.forEach { c ->
            putJsonObject(c.campaignType.name.lowercase()) {
                putJsonObject("targetingParams") {
                    c.targetingParams.forEach { t -> put(t.key, t.value) }
                }
                put("groupPmId", c.groupPmId)
            }
        }
    }

    return JsonConverter.converter.decodeFromJsonElement<MetaDataArg>(json)
}

internal fun MessagesParamReq.toMetaDataParamReq(): MetaDataParamReq {
    return MetaDataParamReq(
        env = env,
        accountId = accountId,
        propertyId = propertyId,
        metadata = metadataArg?.let { JsonConverter.converter.encodeToString(it) } ?: "{}",
    )
}

internal fun MessagesParamReq.toConsentStatusParamReq(
    gdprUuid: String?,
    ccpaUuid: String?,
    localState: JsonElement?
): ConsentStatusParamReq {

    val mdArg = metadataArg?.copy(
        gdpr = metadataArg.gdpr?.copy(uuid = gdprUuid),
        ccpa = metadataArg.ccpa?.copy(uuid = ccpaUuid)
    )

    return ConsentStatusParamReq(
        env = env,
        accountId = accountId,
        propertyId = propertyId,
        metadata = mdArg?.let { JsonConverter.converter.encodeToString(it) } ?: "{}",
        authId = authId,
        localState = localState
    )
}

internal fun CCPA.toCcpaCS() = CcpaCS(
    applies = applies,
    consentedAll = consentedAll,
    dateCreated = dateCreated,
    newUser = newUser,
    rejectedAll = rejectedAll,
    rejectedCategories = rejectedCategories,
    rejectedVendors = rejectedVendors,
    signedLspa = signedLspa,
    status = status,
    uspstring = uspstring,
    cookies = null,
    ccpaApplies = null,
    uuid = null,
    gpcEnabled = null,
    actions = null
)

internal fun GDPR.toGdprCS() = GdprCS(
    applies = null,
    gdprApplies = null,
    categories = null,
    consentAllRef = null,
    consentedToAll = null,
    cookies = null,
    legIntCategories = null,
    legIntVendors = null,
    postPayload = null,
    rejectedAny = null,
    specialFeatures = null,
    vendors = null,
    addtlConsent = addtlConsent,
    consentStatus = consentStatus,
    consentUUID = null,
    cookieExpirationDays = null,
    customVendorsResponse = customVendorsResponse,
    dateCreated = dateCreated,
    euconsent = euconsent,
    grants = grants,
    TCData = TCData,
    localDataCurrent = null,
    uuid = null,
    vendorListId = null
)

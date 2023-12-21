package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.model.optimized.includeData.buildIncludeData
import com.sourcepoint.cmplibrary.data.network.model.optimized.messages.OperatingSystemInfoParam
import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.CampaignReq
import com.sourcepoint.cmplibrary.util.extensions.stringifyGppCustomOption
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.* // ktlint-disable

internal fun getMessageBody(
    propertyHref: String,
    accountId: Long,
    campaigns: List<CampaignReq>,
    gdprConsentStatus: ConsentStatus?,
    ccpaConsentStatus: String?,
    usNatConsentStatus: USNatConsentStatus?,
    consentLanguage: String?,
    campaignEnv: CampaignsEnv?,
    includeData: JsonObject,
    os: OperatingSystemInfoParam = OperatingSystemInfoParam(),
): JsonObject {
    return buildJsonObject {
        put("accountId", accountId)
        campaignEnv?.env?.let { put("campaignEnv", it) }
        put("includeData", includeData)
        put("propertyHref", "https://$propertyHref")
        put("hasCSP", true)
        put("campaigns", campaigns.toMetadataBody(gdprConsentStatus, ccpaConsentStatus, usNatConsentStatus))
        put("consentLanguage", consentLanguage)
        putJsonObject("os") {
            put("name", os.name)
            put("version", os.version)
        }
    }
}

internal fun List<CampaignReq>.toMetadataBody(
    gdprConsentStatus: ConsentStatus? = null,
    ccpaConsentStatus: String? = null,
    usNatConsentStatus: USNatConsentStatus? = null,
): JsonObject {
    return buildJsonObject {
        this@toMetadataBody.forEach { c ->
            putJsonObject(c.campaignType.name.lowercase()) {
                if (c.campaignType == CampaignType.GDPR) {
                    put(
                        "consentStatus",
                        gdprConsentStatus?.let { JsonConverter.converter.encodeToJsonElement(it) } ?: JsonObject(mapOf())
                    )
                    put("hasLocalData", gdprConsentStatus != null)
                }
                if (c.campaignType == CampaignType.CCPA) {
                    put("status", ccpaConsentStatus ?: "")
                    put("hasLocalData", ccpaConsentStatus != null)
                }
                if (c.campaignType == CampaignType.USNAT) {
                    put(
                        "consentStatus",
                        usNatConsentStatus?.let { JsonConverter.converter.encodeToJsonElement(it) } ?: JsonObject(mapOf())
                    )
                    put("hasLocalData", usNatConsentStatus != null)
                }
                putJsonObject("targetingParams") {
                    c.targetingParams.forEach { t -> put(t.key, t.value) }
                }
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

internal fun MessagesParamReq.toMetaDataParamReq(campaigns: List<CampaignReq>): MetaDataParamReq {
    return MetaDataParamReq(
        env = env,
        accountId = accountId,
        propertyId = propertyId,
        metadata = JsonConverter.converter.encodeToString(
            MetaDataMetaDataParam(
                gdpr = campaigns
                    .firstOrNull { it.campaignType == CampaignType.GDPR }
                    ?.let {
                        MetaDataMetaDataParam.MetaDataCampaign(groupPmId = it.groupPmId)
                    },
                ccpa = campaigns
                    .firstOrNull { it.campaignType == CampaignType.CCPA }
                    ?.let {
                        MetaDataMetaDataParam.MetaDataCampaign(groupPmId = it.groupPmId)
                    },
                usnat = campaigns
                    .firstOrNull { it.campaignType == CampaignType.USNAT }
                    ?.let {
                        MetaDataMetaDataParam.MetaDataCampaign(groupPmId = it.groupPmId)
                    },
            )
        ),
    )
}

internal fun MessagesParamReq.toConsentStatusParamReq(
    campaignManager: CampaignManager
): ConsentStatusParamReq {

    val gdprUuid = campaignManager.gdprUuid
    val ccpaUuid = campaignManager.ccpaUuid ?: campaignManager.ccpaConsentStatus?.uuid
    val localState = campaignManager.messagesOptimizedLocalState

    val mdArg = metadataArg?.copy(
        gdpr = metadataArg.gdpr?.copy(uuid = gdprUuid),
        ccpa = metadataArg.ccpa?.copy(uuid = ccpaUuid),
        usNat = metadataArg.usNat?.createMetadataArg(campaignManager),
    )

    return ConsentStatusParamReq(
        env = env,
        accountId = accountId,
        propertyId = propertyId,
        metadata = mdArg?.let { JsonConverter.converter.encodeToString(it) } ?: "{}",
        authId = authId,
        localState = localState,
        includeData = buildIncludeData(gppDataValue = campaignManager.spConfig.stringifyGppCustomOption())
    )
}

internal fun CCPA.toCcpaCS(applies: Boolean?) = CcpaCS(
    applies = applies,
    consentedAll = consentedAll,
    dateCreated = dateCreated,
    gpcEnabled = null,
    newUser = newUser,
    rejectedAll = rejectedAll,
    rejectedCategories = rejectedCategories,
    rejectedVendors = rejectedVendors,
    signedLspa = signedLspa,
    uspstring = uspstring,
    status = status,
    gppData = gppData,
    uuid = null,
    webConsentPayload = webConsentPayload,
    expirationDate = expirationDate
)

internal fun GDPR.toGdprCS(applies: Boolean?) = GdprCS(
    applies = applies,
    categories = null,
    consentAllRef = null,
    consentedToAll = null,
    legIntCategories = null,
    legIntVendors = null,
    postPayload = null,
    rejectedAny = null,
    specialFeatures = null,
    vendors = null,
    addtlConsent = addtlConsent,
    consentStatus = consentStatus,
    cookieExpirationDays = null,
    customVendorsResponse = customVendorsResponse,
    dateCreated = dateCreated,
    euconsent = euconsent,
    grants = grants,
    TCData = TCData,
    localDataCurrent = null,
    uuid = null,
    vendorListId = null,
    webConsentPayload = webConsentPayload,
    expirationDate = expirationDate
)

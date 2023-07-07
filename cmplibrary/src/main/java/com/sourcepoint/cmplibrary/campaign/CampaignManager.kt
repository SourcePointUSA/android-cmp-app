package com.sourcepoint.cmplibrary.campaign

import com.example.cmplibrary.BuildConfig
import com.sourcepoint.cmplibrary.campaign.CampaignManager.Companion.selectPmId
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.creation.validPattern
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.LOCAL_STATE
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.LOCAL_STATE_OLD
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.KEY_GDPR_APPLIES_OLD
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.KEY_GDPR_MESSAGE_SUBCATEGORY_OLD
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.converter.fail
import com.sourcepoint.cmplibrary.data.network.model.optimized.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.* //ktlint-disable
import com.sourcepoint.cmplibrary.util.check
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.json.JSONObject
import java.time.Instant

internal interface CampaignManager {
    val spConfig: SpConfig
    val messageLanguage: MessageLanguage
    val campaigns4Config: List<CampaignReq>
    val ccpaMessageSubCategory: MessageSubCategory
    val gdprMessageSubCategory: MessageSubCategory
    fun addCampaign(campaignType: CampaignType, campaign: CampaignTemplate)

    fun getMessSubCategoryByCamp(campaignType: CampaignType): MessageSubCategory

    fun getAppliedCampaign(): Either<Pair<CampaignType, CampaignTemplate>>
    fun getCampaignTemplate(campaignType: CampaignType): Either<CampaignTemplate>

    fun getPmConfig(
        campaignType: CampaignType,
        pmId: String?,
        pmTab: PMTab?,
        useGroupPmIfAvailable: Boolean,
        groupPmId: String?
    ): Either<PmUrlConfig>

    fun getPmConfig(
        campaignType: CampaignType,
        pmId: String?,
        pmTab: PMTab?
    ): Either<PmUrlConfig>

    fun getMessageOptimizedReq(authId: String?, pubData: JSONObject?): MessagesParamReq

    fun getGroupId(campaignType: CampaignType): String?

    fun clearConsents()

    // Optimized
    val shouldCallMessages: Boolean
    val shouldCallConsentStatus: Boolean
    var gdprMessageMetaData: MessageMetaData?
    var ccpaMessageMetaData: MessageMetaData?

    // Consent Status
    fun saveConsentStatusResponse(c: ConsentStatusResp)
    var gdprConsentStatus: GdprCS?
    var ccpaConsentStatus: CcpaCS?
    var messagesOptimizedLocalState: JsonElement?
    var nonKeyedLocalState: JsonElement?
    var gdprUuid: String?
    var ccpaUuid: String?
    val hasLocalData: Boolean

    var metaDataResp: MetaDataResp?
    var choiceResp: ChoiceResp?
    var dataRecordedConsent: Instant?
    var authId: String?

    fun handleMetaDataLogic(md: MetaDataResp?)
    fun handleOldLocalData()
    fun getGdprChoiceBody(): JsonObject
    fun getCcpaChoiceBody(): JsonObject
    fun getGdprPvDataBody(messageReq: MessagesParamReq): JsonObject
    fun getCcpaPvDataBody(messageReq: MessagesParamReq): JsonObject

    companion object {
        fun selectPmId(userPmId: String?, childPmId: String?, useGroupPmIfAvailable: Boolean): String {
            return when {
                useGroupPmIfAvailable && !childPmId.isNullOrEmpty() && childPmId.isNotBlank() -> childPmId
                else -> userPmId ?: ""
            }
        }
    }
}

internal fun CampaignManager.Companion.create(
    dataStorage: DataStorage,
    spConfig: SpConfig
): CampaignManager = CampaignManagerImpl(dataStorage, spConfig)

private class CampaignManagerImpl(
    val dataStorage: DataStorage,
    override val spConfig: SpConfig
) : CampaignManager {

    override val messageLanguage: MessageLanguage = spConfig.messageLanguage

    private val mapTemplate = mutableMapOf<String, CampaignTemplate>()
    val logger: Logger? = spConfig.logger

    init {
        if (!spConfig.propertyName.contains(validPattern)) {
            throw InvalidArgumentException(
                description = """
                PropertyName can only include letters, numbers, '.', ':', '-' and '/'. (string) passed is invalid
                """.trimIndent()
            )
        }
        spConfig.also { spp ->
            spp.campaigns.forEach {
                when (it.campaignType) {
                    CampaignType.GDPR -> {
                        addCampaign(
                            it.campaignType,
                            CampaignTemplate(
                                spp.campaignsEnv,
                                it.targetingParams.filter { tp -> tp.key != "campaignEnv" },
                                it.campaignType,
                                it.groupPmId
                            )
                        )
                    }

                    CampaignType.CCPA -> {
                        addCampaign(
                            it.campaignType,
                            CampaignTemplate(
                                spp.campaignsEnv,
                                it.targetingParams.filter { tp -> tp.key != "campaignEnv" },
                                it.campaignType,
                                it.groupPmId
                            )
                        )
                    }
                }
            }
        }
    }

    override val campaigns4Config: List<CampaignReq>
        get() {
            val campaigns = mutableListOf<CampaignReq>()
            mapTemplate[CampaignType.GDPR.name]
                ?.let {
                    it.toCampaignReqImpl(
                        targetingParams = it.targetingParams,
                        campaignsEnv = it.campaignsEnv,
                        groupPmId = it.groupPmId
                    )
                }
                ?.let { campaigns.add(it) }
            mapTemplate[CampaignType.CCPA.name]
                ?.let {
                    it.toCampaignReqImpl(
                        targetingParams = it.targetingParams,
                        campaignsEnv = it.campaignsEnv
                    )
                }
                ?.let { campaigns.add(it) }
            return campaigns
        }

    override val ccpaMessageSubCategory: MessageSubCategory
        get() = ccpaMessageMetaData?.subCategoryId ?: MessageSubCategory.TCFv2

    override val gdprMessageSubCategory: MessageSubCategory
        get() = gdprMessageMetaData?.subCategoryId ?: MessageSubCategory.TCFv2

    override fun addCampaign(campaignType: CampaignType, campaign: CampaignTemplate) {
        mapTemplate[campaignType.name] = campaign
    }

    override fun getCampaignTemplate(campaignType: CampaignType): Either<CampaignTemplate> = check {
        mapTemplate[campaignType.name] ?: fail("${campaignType.name} Campain is missing!!!")
    }

    override fun getPmConfig(
        campaignType: CampaignType,
        pmId: String?,
        pmTab: PMTab?,
        useGroupPmIfAvailable: Boolean,
        groupPmId: String?
    ): Either<PmUrlConfig> {
        return when (campaignType) {
            CampaignType.GDPR -> getGdprPmConfig(pmId, pmTab ?: PMTab.PURPOSES, useGroupPmIfAvailable, groupPmId)
            CampaignType.CCPA -> getCcpaPmConfig(pmId)
        }
    }

    override fun getPmConfig(
        campaignType: CampaignType,
        pmId: String?,
        pmTab: PMTab?
    ): Either<PmUrlConfig> {
        return when (campaignType) {
            CampaignType.GDPR -> getGdprPmConfig(pmId, pmTab ?: PMTab.PURPOSES, false, null)
            CampaignType.CCPA -> getCcpaPmConfig(pmId)
        }
    }

    private fun getGdprPmConfig(
        pmId: String?,
        pmTab: PMTab,
        useGroupPmIfAvailable: Boolean,
        groupPmId: String?
    ): Either<PmUrlConfig> = check {
        val childPmId: String? = dataStorage.gdprChildPmId
        val isChildPmIdAbsent: Boolean = childPmId == null
        val hasGroupPmId: Boolean = groupPmId != null

        val usedPmId = selectPmId(pmId, childPmId, useGroupPmIfAvailable)

        if (hasGroupPmId && useGroupPmIfAvailable && isChildPmIdAbsent) {
            logger?.error(
                ChildPmIdNotFound(
                    description = """
                              childPmId not found!!!
                              GroupPmId[$groupPmId]
                              useGroupPmIfAvailable [true] 
                    """.trimIndent()
                )
            )
            logger?.e(
                tag = "The childPmId is missing",
                msg = """
                              childPmId [null]
                              GroupPmId[$groupPmId]
                              useGroupPmIfAvailable [true] 
                """.trimIndent()
            )
        }

        logger?.computation(
            tag = "Property group - GDPR PM",
            msg = """
                pmId[$pmId]
                childPmId[$childPmId]
                useGroupPmIfAvailable [$useGroupPmIfAvailable] 
                Query Parameter pmId[$usedPmId]
            """.trimIndent()
        )

        PmUrlConfig(
            pmTab = pmTab,
            consentLanguage = spConfig.messageLanguage.value,
            uuid = gdprUuid,
            siteId = spConfig.propertyId.toString(),
            messageId = usedPmId
        )
    }

    private fun getCcpaPmConfig(pmId: String?): Either<PmUrlConfig> = check {
        val childPmId: String? = dataStorage.ccpaChildPmId
        val isChildPmIdAbsent: Boolean = childPmId == null
        val hasGroupPmId = false // feature not yet implemented
        val useGroupPmIfAvailable = false // feature not yet implemented

        if (hasGroupPmId && useGroupPmIfAvailable && isChildPmIdAbsent) {
            logger?.error(
                ChildPmIdNotFound(
                    description = """
                              childPmId not found!!!
                              GroupPmId[groupPmId]
                              useGroupPmIfAvailable [true] 
                    """.trimIndent()
                )
            )
        }

        val usedPmId = childPmId ?: pmId

        PmUrlConfig(
            consentLanguage = spConfig.messageLanguage.value,
            uuid = ccpaUuid,
            siteId = spConfig.propertyId.toString(),
            messageId = usedPmId
        )
    }

    override fun getMessSubCategoryByCamp(campaignType: CampaignType): MessageSubCategory {
        return when (campaignType) {
            CampaignType.GDPR -> gdprMessageSubCategory
            CampaignType.CCPA -> ccpaMessageSubCategory
        }
    }

    override fun getAppliedCampaign(): Either<Pair<CampaignType, CampaignTemplate>> = check {
        when {
            dataStorage
                .getGdprMessage().isNotBlank() -> Pair(CampaignType.GDPR, mapTemplate[CampaignType.GDPR.name]!!)
            dataStorage
                .getCcpaMessage().isNotBlank() -> Pair(CampaignType.CCPA, mapTemplate[CampaignType.CCPA.name]!!)
            else -> throw MissingPropertyException(description = "Inconsistent Legislation!!!")
        }
    }

    override fun getMessageOptimizedReq(authId: String?, pubData: JSONObject?): MessagesParamReq {
        val campaigns = mutableListOf<CampaignReq>()
        mapTemplate[CampaignType.GDPR.name]
            ?.let {
                it.toCampaignReqImpl(
                    targetingParams = it.targetingParams,
                    campaignsEnv = it.campaignsEnv,
                    groupPmId = it.groupPmId
                )
            }
            ?.let { campaigns.add(it) }
        mapTemplate[CampaignType.CCPA.name]
            ?.let {
                it.toCampaignReqImpl(
                    targetingParams = it.targetingParams,
                    campaignsEnv = it.campaignsEnv
                )
            }
            ?.let { campaigns.add(it) }

        return MessagesParamReq(
            metadataArg = campaigns.toMetadataArgs(),
            body = "",
            env = Env.values().find { it.name == BuildConfig.SDK_ENV } ?: Env.PROD,
            propertyHref = spConfig.propertyName,
            accountId = spConfig.accountId.toLong(),
            authId = authId,
            propertyId = spConfig.propertyId.toLong(),
            pubData = pubData?.toString()
                ?.let { check { JsonConverter.converter.decodeFromString<JsonObject>(it) }.getOrNull() }
                ?: JsonObject(mapOf())
        )
    }

    override fun getGroupId(campaignType: CampaignType): String? {
        return spConfig.campaigns.find { it.campaignType == campaignType }?.groupPmId
    }

    override fun clearConsents() {
        dataStorage.clearGdprConsent()
        dataStorage.clearCcpaConsent()
    }

    override var authId: String?
        get() = dataStorage.getAuthId()
        set(value) {
            dataStorage.saveAuthId(value)
        }

    // Optimized Implementation below

    val isNewUser: Boolean
        get() {
            val localStateSize = messagesOptimizedLocalState?.jsonObject?.size ?: 0
            return messagesOptimizedLocalState == null || localStateSize == 0 || (
                gdprUuid == null &&
                    (ccpaConsentStatus?.newUser == null || ccpaConsentStatus?.newUser == true)
                )
        }

    override val shouldCallMessages: Boolean
        get() {

            val gdprToBeCompleted: Boolean = spConfig.campaigns.find { it.campaignType == CampaignType.GDPR }
                ?.let {
                    dataStorage.gdprApplies && (gdprConsentStatus?.consentStatus?.consentedAll != true)
                }
                ?: false

            val ccpaToBeCompleted: Boolean = spConfig.campaigns.find { it.campaignType == CampaignType.CCPA }
                ?.let { true }
                ?: false

            val res = (isNewUser || ccpaToBeCompleted || gdprToBeCompleted)

            logger?.computation(
                tag = "shouldCallMessages",
                msg = """
                isNewUser[$isNewUser]
                ccpaToBeCompleted[$ccpaToBeCompleted]
                gdprToBeCompleted[$gdprToBeCompleted]
                shouldCallMessages[$res]  
                """.trimIndent()
            )

            return res
        }

    override val shouldCallConsentStatus: Boolean
        get() {
            val localStateSize = messagesOptimizedLocalState?.jsonObject?.size ?: 0
            val isV6LocalStatePresent = dataStorage.preference.all.containsKey(LOCAL_STATE)
            val isV6LocalStatePresent2 = dataStorage.preference.all.containsKey(LOCAL_STATE_OLD)
            val res =
                ((gdprUuid != null || ccpaUuid != null) && localStateSize == 0) || isV6LocalStatePresent || isV6LocalStatePresent2

            logger?.computation(
                tag = "shouldCallConsentStatus",
                msg = """
                gdprUUID != null [${gdprUuid != null}] - ccpaUUID != null [${ccpaUuid != null}]
                localStateSize empty [${localStateSize == 0}]
                V6.7 ls [$isV6LocalStatePresent] or V6.3 ls [$isV6LocalStatePresent2]  
                shouldCallConsentStatus[$res]  
                """.trimIndent()
            )

            return res
        }

    override var gdprMessageMetaData: MessageMetaData?
        get() {
            return dataStorage.gdprMessageMetaData?.let { JsonConverter.converter.decodeFromString<MessageMetaData>(it) }
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.gdprMessageMetaData = serialised
        }

    override var ccpaMessageMetaData: MessageMetaData?
        get() {
            return dataStorage.ccpaMessageMetaData?.let { JsonConverter.converter.decodeFromString<MessageMetaData>(it) }
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.ccpaMessageMetaData = serialised
        }

    override fun saveConsentStatusResponse(c: ConsentStatusResp) {
        gdprConsentStatus = c.consentStatusData?.gdpr
        ccpaConsentStatus = c.consentStatusData?.ccpa
        messagesOptimizedLocalState = c.localState
    }

    override var gdprConsentStatus: GdprCS?
        get() {
            return dataStorage.gdprConsentStatus?.let { JsonConverter.converter.decodeFromString<GdprCS>(it) }
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.apply {
                value?.gdprApplies?.let {
                    gdprApplies = it
                }
                gdprConsentStatus = serialised
                value?.TCData
                    ?.let { this.tcData = it }
                    ?: run { clearTCData() }
            }
        }

    override var ccpaConsentStatus: CcpaCS?
        get() {
            return dataStorage.ccpaConsentStatus?.let { JsonConverter.converter.decodeFromString<CcpaCS>(it) }
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.apply {
                value?.ccpaApplies?.let {
                    ccpaApplies = it
                }
                ccpaConsentStatus = serialised
                usPrivacyString = value?.uspstring
            }
        }

    override var messagesOptimizedLocalState: JsonElement?
        get() {
            return dataStorage.messagesOptimizedLocalState?.let {
                JsonConverter.converter.decodeFromString<JsonElement>(
                    it
                )
            }
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.messagesOptimizedLocalState = serialised
        }

    override var nonKeyedLocalState: JsonElement?
        get() {
            return dataStorage.nonKeyedLocalState?.let {
                JsonConverter.converter.decodeFromString<JsonElement>(
                    it
                )
            }
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.nonKeyedLocalState = serialised
        }


    override var gdprUuid: String?
        get() {
            return gdprConsentStatus?.uuid
        }
        set(value) {
            gdprConsentStatus = gdprConsentStatus?.copy(uuid = value)
        }

    override var ccpaUuid: String?
        get() {
            return ccpaConsentStatus?.uuid
        }
        set(value) {
            ccpaConsentStatus = ccpaConsentStatus?.copy(uuid = value)
        }

    override val hasLocalData: Boolean
        get() = dataStorage.gdprConsentStatus != null || dataStorage.usPrivacyString != null

    override fun handleMetaDataLogic(md: MetaDataResp?) {
        metaDataResp = md
        md?.let {
            it.ccpa?.apply {
                applies?.let { i -> dataStorage.ccpaApplies = i }
                sampleRate?.let { i ->
                    if (i != dataStorage.ccpaSamplingValue) {
                        dataStorage.ccpaSamplingValue = i
                        dataStorage.ccpaSamplingResult = null
                    }
                }
            }
            it.gdpr?.apply {
                applies?.let { i -> dataStorage.gdprApplies = i }
                childPmId?.let { i -> dataStorage.gdprChildPmId = i }
                sampleRate?.let { i ->
                    if (i != dataStorage.gdprSamplingValue) {
                        dataStorage.gdprSamplingValue = i
                        dataStorage.gdprSamplingResult = null
                    }
                }
            }
        }
    }

    override fun handleOldLocalData() {
        if (dataStorage.preference.contains(DataStorage.LOCAL_STATE) || dataStorage.preference.contains(DataStorage.LOCAL_STATE_OLD)) {
            dataStorage.preference
                .edit().apply {
                    remove(LOCAL_STATE)
                    remove(LOCAL_STATE_OLD)
                    remove(KEY_GDPR_APPLIES_OLD)
                    remove(KEY_GDPR_MESSAGE_SUBCATEGORY_OLD)
                    remove("key_property_id")
                    remove("key_ccpa")
                    remove("key_gdpr")
                    remove("ccpa_consent_resp")
                    remove("gdpr_consent_resp")
                }
                .apply()
        }
    }

    override var metaDataResp: MetaDataResp?
        get() {
            return dataStorage.metaDataResp?.let { JsonConverter.converter.decodeFromString<MetaDataResp>(it) }
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.metaDataResp = serialised
        }

    override var choiceResp: ChoiceResp?
        get() {
            return dataStorage.choiceResp?.let { JsonConverter.converter.decodeFromString<ChoiceResp>(it) }
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.choiceResp = serialised
        }

    override var dataRecordedConsent: Instant?
        get() {
            return dataStorage.dataRecordedConsent?.let { Instant.parse(it) }
        }
        set(value) {
            dataStorage.dataRecordedConsent = value?.toString()
        }

    override fun getGdprChoiceBody(): JsonObject {
        return toGdprChoiceBody(
            accountId = spConfig.accountId,
            propertyId = spConfig.propertyId,
            gdprCs = gdprConsentStatus?.consentStatus,
            gdprMessageMetaData = gdprMessageMetaData,
            gdprApplies = dataStorage.gdprApplies,
            sampleRate = dataStorage.gdprSamplingValue
        )
    }

    override fun getCcpaChoiceBody(): JsonObject {
        return toCcpaChoiceBody(
            accountId = spConfig.accountId,
            propertyId = spConfig.propertyId,
            gdprCs = gdprConsentStatus?.consentStatus,
            gdprMessageMetaData = gdprMessageMetaData,
            gdprApplies = dataStorage.gdprApplies,
            sampleRate = dataStorage.gdprSamplingValue
        )
    }

    override fun getGdprPvDataBody(messageReq: MessagesParamReq): JsonObject {
        return toPvDataBody(
            accountId = messageReq.accountId,
            propertyId = messageReq.propertyId,
            gdprMessageMetaData = gdprMessageMetaData,
            ccpaMessageMetaData = null,
            gdprApplies = dataStorage.gdprApplies,
            ccpaApplies = dataStorage.ccpaApplies,
            pubData = messageReq.pubData,
            gdprCs = gdprConsentStatus,
            ccpaCS = null,
        )
    }

    override fun getCcpaPvDataBody(messageReq: MessagesParamReq): JsonObject {
        return toPvDataBody(
            accountId = messageReq.accountId,
            propertyId = messageReq.propertyId,
            gdprMessageMetaData = null,
            ccpaMessageMetaData = ccpaMessageMetaData,
            gdprApplies = dataStorage.gdprApplies,
            ccpaApplies = dataStorage.ccpaApplies,
            pubData = messageReq.pubData,
            gdprCs = null,
            ccpaCS = ccpaConsentStatus
        )
    }
}

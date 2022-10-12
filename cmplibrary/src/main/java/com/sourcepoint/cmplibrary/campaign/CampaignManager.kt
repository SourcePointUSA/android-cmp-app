package com.sourcepoint.cmplibrary.campaign

import com.example.cmplibrary.BuildConfig
import com.sourcepoint.cmplibrary.campaign.CampaignManager.Companion.selectPmId
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.core.map
import com.sourcepoint.cmplibrary.creation.validPattern
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.local.getCCPAConsent
import com.sourcepoint.cmplibrary.data.local.getGDPRConsent
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.converter.fail
import com.sourcepoint.cmplibrary.data.network.model.toCCPA
import com.sourcepoint.cmplibrary.data.network.model.toGDPR
import com.sourcepoint.cmplibrary.data.network.model.v7.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.Campaigns
import com.sourcepoint.cmplibrary.model.exposed.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.CCPAConsentInternal
import com.sourcepoint.cmplibrary.model.exposed.GDPRConsentInternal
import com.sourcepoint.cmplibrary.util.check
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import org.json.JSONObject
import java.time.Instant

internal interface CampaignManager {

    val spConfig: SpConfig
    val messageLanguage: MessageLanguage
    fun addCampaign(campaignType: CampaignType, campaign: CampaignTemplate)

    fun isAppliedCampaign(campaignType: CampaignType): Boolean
    fun getMessSubCategoryByCamp(campaignType: CampaignType): MessageSubCategory
    fun getUnifiedMessageResp(): Either<UnifiedMessageResp>

    fun getGdpr(): Either<Gdpr>
    fun getCcpa(): Either<Ccpa>
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

    fun getUnifiedMessageReq(authId: String?, pubData: JSONObject?): UnifiedMessageRequest
    fun getMessageV7Req(authId: String?): MessagesParamReq

    fun getGDPRConsent(): Either<GDPRConsentInternal>
    fun getCCPAConsent(): Either<CCPAConsentInternal>

    fun getGroupId(campaignType: CampaignType): String?

    fun saveGdpr(gdpr: Gdpr)
    fun saveCcpa(ccpa: Ccpa)
    fun saveUnifiedMessageResp(unifiedMessageResp: UnifiedMessageResp)

    fun parseRenderingMessage()

    fun clearConsents()

    // V7
    val shouldCallMessages: Boolean
    var messagesV7: MessagesResp?
    var consentStatusResponse: ConsentStatusResp?
    var gdprConsentStatus: ConsentStatus?
    var metaDataResp: MetaDataResp?
    var pvDataResp: PvDataResp?
    var choiceResp: ChoiceResp?
    var dataRecordedConsent: Instant?

    fun getChoiceBody(choiceParam: MessagesParamReq): JsonObject
    fun getPvDataBody(messageReq: MessagesParamReq): JsonObject

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
    private val campaignsEnv: CampaignsEnv = spConfig.campaignsEnv
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
                        val ce: CampaignsEnv = it.targetingParams
                            .find { c -> c.key == "campaignEnv" }
                            ?.let { env ->
                                CampaignsEnv.values().find { t -> t.env == env.value }
                            } ?: CampaignsEnv.PUBLIC
                        addCampaign(
                            it.campaignType,
                            CampaignTemplate(
                                ce,
                                it.targetingParams.filter { tp -> tp.key != "campaignEnv" },
                                it.campaignType,
                                it.groupPmId
                            )
                        )
                    }

                    CampaignType.CCPA -> {
                        val ce: CampaignsEnv = it.targetingParams
                            .find { c -> c.key == "campaignEnv" }
                            ?.let { env ->
                                CampaignsEnv.values().find { t -> t.env == env.value }
                            } ?: CampaignsEnv.PUBLIC
                        addCampaign(
                            it.campaignType,
                            CampaignTemplate(
                                ce,
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

    override fun addCampaign(campaignType: CampaignType, campaign: CampaignTemplate) {
        mapTemplate[campaignType.name] = campaign
    }

    override fun getGdpr(): Either<Gdpr> = check {
        dataStorage.getGdpr()?.toGDPR(dataStorage.getGdprConsentUuid()) ?: fail("GDPR is not stored in memory!!!")
    }

    override fun getCcpa(): Either<Ccpa> = check {
        dataStorage.getCcpa()?.toCCPA(dataStorage.getCcpaConsentUuid()) ?: fail("CCPA is not stored in memory!!!")
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

    private fun getGdprPmConfig(pmId: String?, pmTab: PMTab, useGroupPmIfAvailable: Boolean, groupPmId: String?): Either<PmUrlConfig> = check {
        val uuid = dataStorage.getGdprConsentUuid()
        val siteId = dataStorage.getPropertyId().toString()

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
            uuid = uuid,
            siteId = siteId,
            messageId = usedPmId
        )
    }

    private fun getCcpaPmConfig(pmId: String?): Either<PmUrlConfig> = check {
        val uuid = dataStorage.getCcpaConsentUuid()
        val siteId = dataStorage.getPropertyId().toString()

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

//        logger?.computation(
//            tag = "Property group - CCPA PM",
//            msg = "pmId[$pmId] - childPmId[$childPmId] -> used[$usedPmId]"
//        )

        PmUrlConfig(
            consentLanguage = spConfig.messageLanguage.value,
            uuid = uuid,
            siteId = siteId,
            messageId = usedPmId
        )
    }

    override fun isAppliedCampaign(campaignType: CampaignType): Boolean {
        return getAppliedCampaign()
            .map { it.first == campaignType }
            .getOrNull()
            ?: false
    }

    override fun getMessSubCategoryByCamp(campaignType: CampaignType): MessageSubCategory {
        return when (campaignType) {
            CampaignType.GDPR -> dataStorage.gdprMessageSubCategory
            CampaignType.CCPA -> dataStorage.ccpaMessageSubCategory
        }
    }

    override fun getUnifiedMessageResp(): Either<UnifiedMessageResp> = check {
        val campaigns = mutableListOf<CampaignResp>()
        getGdpr().map { campaigns.add(it) }
        getCcpa().map { campaigns.add(it) }
        val localState: String = dataStorage.getLocalState() ?: ""
        val propertyPriorityData: String? = dataStorage.getPropertyPriorityData()
        UnifiedMessageResp(
            campaigns = campaigns,
            localState = localState,
            propertyPriorityData = propertyPriorityData?.let { JSONObject(it) } ?: JSONObject(),
            thisContent = JSONObject(),
        )
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

    override fun getUnifiedMessageReq(authId: String?, pubData: JSONObject?): UnifiedMessageRequest {
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

        val localState = dataStorage.getLocalState()

        return UnifiedMessageRequest(
            requestUUID = "test",
            propertyHref = spConfig.propertyName,
            accountId = spConfig.accountId,
            campaigns = Campaigns(list = campaigns),
            consentLanguage = messageLanguage,
            localState = localState,
            authId = authId,
            campaignsEnv = campaignsEnv,
            pubData = pubData
        )
    }

    override fun getMessageV7Req(authId: String?): MessagesParamReq {
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
            metadata = campaigns.toMetadataBody().toString(),
            nonKeyedLocalState = "",
            body = "",
            env = Env.values().find { it.name == BuildConfig.SDK_ENV } ?: Env.PROD,
            propertyHref = spConfig.propertyName,
            accountId = spConfig.accountId.toLong(),
            authId = authId,
            propertyId = spConfig.propertyId?.let { it.toLong() } ?: fail("The propertyId field is missing in the setup phase!!!!!")
        )
    }

    override fun getGDPRConsent(): Either<GDPRConsentInternal> = dataStorage.getGDPRConsent()

    override fun getCCPAConsent(): Either<CCPAConsentInternal> = dataStorage.getCCPAConsent()

    override fun getGroupId(campaignType: CampaignType): String? {
        return spConfig.campaigns.find { it.campaignType == campaignType }?.groupPmId
    }

    override fun saveGdpr(gdpr: Gdpr) {
        dataStorage.run {
            saveGdpr(gdpr.thisContent.toString())
            saveGdprConsentResp(gdpr.userConsent.thisContent.toString())
            gdprApplies = gdpr.applies
            gdprChildPmId = gdpr.userConsent.childPmId
            gdprMessageSubCategory = gdpr.messageSubCategory
        }
    }

    override fun saveCcpa(ccpa: Ccpa) {
        dataStorage.run {
            saveCcpa(ccpa.thisContent.toString())
            saveCcpaConsentResp(ccpa.userConsent.thisContent.toString())
            saveUsPrivacyString(ccpa.userConsent.uspstring)
            ccpaApplies = ccpa.applies
            ccpaChildPmId = ccpa.userConsent.childPmId
        }
    }

    override fun saveUnifiedMessageResp(unifiedMessageResp: UnifiedMessageResp) {
        dataStorage.saveLocalState(unifiedMessageResp.localState)
        val map = JSONObject(unifiedMessageResp.localState).toTreeMap()
        // save GDPR uuid
        map.getMap("gdpr")?.apply {
            getFieldValue<String>("uuid")?.let { dataStorage.saveGdprConsentUuid(it) }
            getFieldValue<Int>("propertyId")?.let { dataStorage.savePropertyId(it) }
        }
        // save GDPR uuid
        map.getMap("ccpa")?.apply {
            getFieldValue<String>("uuid")?.let { dataStorage.saveCcpaConsentUuid(it) }
            getFieldValue<Int>("propertyId")?.let { dataStorage.savePropertyId(it) }
        }

        // save campaigns and consents
        unifiedMessageResp
            .campaigns
            .forEach {
                when (it) {
                    is Gdpr -> saveGdpr(it)
                    is Ccpa -> saveCcpa(it)
                }
            }
    }

    override fun parseRenderingMessage() {
    }

    override fun clearConsents() {
        dataStorage.clearGdprConsent()
        dataStorage.clearCcpaConsent()
    }

    // V7 Implementation below

    val isNewUser: Boolean
        get() {
            return consentStatusResponse?.consentStatusData?.gdpr?.uuid == null &&
                consentStatusResponse?.consentStatusData?.ccpa?.newUser == true
        }

    override val shouldCallMessages: Boolean
        get() {
            return isNewUser ||
                // applies from Message response
                (consentStatusResponse?.consentStatusData?.gdpr?.gdprApplies == true && consentStatusResponse?.consentStatusData?.gdpr?.consentStatus?.consentedAll == false) ||

                (consentStatusResponse?.consentStatusData?.ccpa?.ccpaApplies == true && consentStatusResponse?.consentStatusData?.ccpa?.status != CcpaStatus.consentedAll)
        }

    private var messagesV7Local: MessagesResp? = null
    override var messagesV7: MessagesResp?
        get() {
            return messagesV7Local
                ?: run {
                    messagesV7Local = dataStorage.messagesV7?.let { JsonConverter.converter.decodeFromString<MessagesResp>(it) }
                    messagesV7Local
                }
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.messagesV7 = serialised
            messagesV7Local = value
        }

    private var consentStatusLocal: ConsentStatusResp? = null
    override var consentStatusResponse: ConsentStatusResp?
        get() {
            return consentStatusLocal
                ?: run {
                    consentStatusLocal = dataStorage.consentStatusResponse?.let { JsonConverter.converter.decodeFromString<ConsentStatusResp>(it) }
                    consentStatusLocal
                }
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.consentStatusResponse = serialised
            consentStatusLocal = value
        }

    private var gdprConsentStatusLocal: ConsentStatus? = null
    override var gdprConsentStatus: ConsentStatus?
        get() {
            return gdprConsentStatusLocal
                ?: run {
                    gdprConsentStatusLocal = dataStorage.gdprConsentStatus?.let { JsonConverter.converter.decodeFromString<ConsentStatus>(it) }
                    gdprConsentStatusLocal
                }
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.gdprConsentStatus = serialised
            gdprConsentStatusLocal = value
        }

    private var metaDataResp2Local: MetaDataResp? = null
    override var metaDataResp: MetaDataResp?
        get() {
            return metaDataResp2Local
                ?: run {
                    metaDataResp2Local = dataStorage.metaDataResp?.let { JsonConverter.converter.decodeFromString<MetaDataResp>(it) }
                    metaDataResp2Local
                }
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.metaDataResp = serialised
            metaDataResp2Local = value
        }

    private var pvDataRespLocal: PvDataResp? = null
    override var pvDataResp: PvDataResp?
        get() {
            return pvDataRespLocal
                ?: run {
                    pvDataRespLocal = dataStorage.pvDataResp?.let { JsonConverter.converter.decodeFromString<PvDataResp>(it) }
                    pvDataRespLocal
                }
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.pvDataResp = serialised
            pvDataRespLocal = value
        }

    private var choiceRespLocal: ChoiceResp? = null
    override var choiceResp: ChoiceResp?
        get() {
            return choiceRespLocal
                ?: run {
                    choiceRespLocal = dataStorage.choiceResp?.let { JsonConverter.converter.decodeFromString<ChoiceResp>(it) }
                    choiceRespLocal
                }
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.pvDataResp = serialised
            choiceRespLocal = value
        }

    private var dataRecordedConsentLocal: Instant? = null
    override var dataRecordedConsent: Instant?
        get() {
            return dataRecordedConsentLocal
                ?: run {
                    dataRecordedConsentLocal = dataStorage.dataRecordedConsent?.let { Instant.parse(it) }
                    dataRecordedConsentLocal
                }
        }
        set(value) {
            dataStorage.dataRecordedConsent = value?.toString()
            dataRecordedConsentLocal = value
        }

    override fun getChoiceBody(messageReq: MessagesParamReq): JsonObject {
        return toChoiceBody(
            accountId = messageReq.accountId,
            propertyId = messageReq.propertyId,
            gdprCs = gdprConsentStatus,
            gdprMessageMetaData = messagesV7?.campaigns?.gdpr?.messageMetaData,
            gdprApplies = consentStatusResponse?.consentStatusData?.gdpr?.gdprApplies,
        )
    }

    override fun getPvDataBody(messageReq: MessagesParamReq): JsonObject {
        return toPvDataBody2(
            accountId = messageReq.accountId,
            propertyId = messageReq.propertyId,
            gdprCs = gdprConsentStatus,
            gdprMessageMetaData = messagesV7?.campaigns?.gdpr?.messageMetaData,
            ccpaMessageMetaData = messagesV7?.campaigns?.ccpa?.messageMetaData,
            gdprApplies = consentStatusResponse?.consentStatusData?.gdpr?.gdprApplies,
            ccpaApplies = messagesV7?.campaigns?.ccpa?.applies,
            pubData = messageReq.pubData,
            ccpaCS = consentStatusResponse?.consentStatusData?.ccpa
        )
    }
}

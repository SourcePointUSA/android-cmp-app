package com.sourcepoint.cmplibrary.campaign

import com.sourcepoint.cmplibrary.campaign.CampaignManager.Companion.selectPmId
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.core.map
import com.sourcepoint.cmplibrary.creation.validPattern
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.converter.fail
import com.sourcepoint.cmplibrary.data.network.model.toCCPA
import com.sourcepoint.cmplibrary.data.network.model.toCCPAUserConsent
import com.sourcepoint.cmplibrary.data.network.model.toGDPR
import com.sourcepoint.cmplibrary.data.network.model.toGDPRUserConsent
import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.exception.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.CCPAConsentInternal
import com.sourcepoint.cmplibrary.model.exposed.GDPRConsentInternal
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepoint.cmplibrary.util.check
import org.json.JSONObject

internal interface CampaignManager {

    val spConfig: SpConfig
    val messageLanguage: MessageLanguage
    fun addCampaign(campaignType: CampaignType, campaign: CampaignTemplate)

    fun isAppliedCampaign(campaignType: CampaignType): Boolean
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

    fun getUnifiedMessageReq(): UnifiedMessageRequest
    fun getUnifiedMessageReq(authId: String?, pubData: JSONObject?): UnifiedMessageRequest

    fun getGDPRConsent(): Either<GDPRConsentInternal>
    fun getCCPAConsent(): Either<CCPAConsentInternal>

    fun getGroupId(campaignType: CampaignType): String?

    fun saveGdpr(gdpr: Gdpr)
    fun saveCcpa(ccpa: Ccpa)
    fun saveUnifiedMessageResp(unifiedMessageResp: UnifiedMessageResp)

    fun parseRenderingMessage()

    fun clearConsents()

    companion object {
        fun selectPmId(userPmId: String?, childPmId: String?, useGroupPmIfAvailable: Boolean): String {
            return when {
                useGroupPmIfAvailable && childPmId != null -> childPmId
                else -> userPmId ?: ""
            }
        }
    }
}

internal fun CampaignManager.Companion.create(
    dataStorage: DataStorage,
    spConfig: SpConfig,
    messageLanguage: MessageLanguage
): CampaignManager = CampaignManagerImpl(dataStorage, spConfig, messageLanguage)

private class CampaignManagerImpl(
    val dataStorage: DataStorage,
    override val spConfig: SpConfig,
    override val messageLanguage: MessageLanguage
) : CampaignManager {

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

    fun getCcpaPmConfig(pmId: String?): Either<PmUrlConfig> = check {
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

    override fun getUnifiedMessageReq(): UnifiedMessageRequest {
        return getUnifiedMessageReq(null, null)
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

        val localState = when (dataStorage.savedConsent) {
            true -> dataStorage.getLocalState()
            false -> null
        }

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

    override fun getGDPRConsent(): Either<GDPRConsentInternal> = check {
        dataStorage
            .getGdprConsentResp()
            .also { if (it.isBlank()) fail("GDPRConsent is not saved in the the storage!!") }
            .let { JSONObject(it) }
            .toTreeMap()
            .toGDPRUserConsent(uuid = dataStorage.getGdprConsentUuid(), applies = dataStorage.gdprApplies)
    }

    override fun getCCPAConsent(): Either<CCPAConsentInternal> = check {
        dataStorage
            .getCcpaConsentResp()
            .also { if (it.isBlank()) fail("CCPAConsent is not saved in the the storage!!") }
            .let { JSONObject(it) }
            .toTreeMap()
            .toCCPAUserConsent(uuid = dataStorage.getCcpaConsentUuid(), applies = dataStorage.ccpaApplies)
    }

    override fun getGroupId(campaignType: CampaignType): String? {
        return spConfig.campaigns.find { it.campaignType == campaignType }?.groupPmId
    }

    override fun saveGdpr(gdpr: Gdpr) {
        dataStorage.saveGdpr(gdpr.thisContent.toString())
        dataStorage.saveGdprConsentResp(gdpr.userConsent.thisContent.toString())
        dataStorage.gdprApplies = gdpr.applies
        dataStorage.gdprChildPmId = gdpr.userConsent.childPmId
    }

    override fun saveCcpa(ccpa: Ccpa) {
        dataStorage.saveCcpa(ccpa.thisContent.toString())
        dataStorage.saveCcpaConsentResp(ccpa.userConsent.thisContent.toString())
        dataStorage.ccpaApplies = ccpa.applies
        dataStorage.ccpaChildPmId = ccpa.userConsent.childPmId
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
}

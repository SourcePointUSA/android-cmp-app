package com.sourcepoint.cmplibrary.campaign

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.core.map
import com.sourcepoint.cmplibrary.creation.validPattern
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.converter.fail
import com.sourcepoint.cmplibrary.data.network.util.CampaignEnv
import com.sourcepoint.cmplibrary.exception.InvalidArgumentException
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.exception.MissingPropertyException
import com.sourcepoint.cmplibrary.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.Campaigns
import com.sourcepoint.cmplibrary.model.exposed.CCPAConsent
import com.sourcepoint.cmplibrary.model.exposed.GDPRConsent
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepoint.cmplibrary.model.toCCPAUserConsent
import com.sourcepoint.cmplibrary.model.toGDPRUserConsent
import com.sourcepoint.cmplibrary.util.check
import org.json.JSONObject

internal interface CampaignManager {

    val spConfig: SpConfig
    val messageLanguage: MessageLanguage
    fun addCampaign(legislation: Legislation, campaign: CampaignTemplate)

    fun isAppliedCampaign(legislation: Legislation): Boolean
    fun getUnifiedMessageResp(): Either<UnifiedMessageResp>

    fun getGdpr(): Either<Gdpr>
    fun getCcpa(): Either<Ccpa>
    fun getAppliedCampaign(): Either<Pair<Legislation, CampaignTemplate>>
    fun getCampaignTemplate(legislation: Legislation): Either<CampaignTemplate>

    fun getPmConfig(legislation: Legislation, pmId: String?, pmTab: PMTab?): Either<PmUrlConfig>

    fun getUnifiedMessageReq(): UnifiedMessageRequest

    fun getGDPRConsent(): Either<GDPRConsent>
    fun getCCPAConsent(): Either<CCPAConsent>

    fun saveGdpr(gdpr: Gdpr)
    fun saveCcpa(ccpa: Ccpa)
    fun saveGDPRConsent(consent: GDPRConsent?)
    fun saveCCPAConsent(consent: CCPAConsent?)
    fun saveUnifiedMessageResp(unifiedMessageResp: UnifiedMessageResp)

    fun parseRenderingMessage()

    fun clearConsents()

    companion object
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

    companion object {
        private var gdprConsent: GDPRConsent? = null
        private var ccpaConsent: CCPAConsent? = null
    }

    private val mapTemplate = mutableMapOf<String, CampaignTemplate>()

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
                when (it.legislation) {
                    Legislation.GDPR -> addCampaign(
                        it.legislation,
                        CampaignTemplate(CampaignEnv.PUBLIC, it.targetingParams, it.legislation)
                    )
                    Legislation.CCPA -> addCampaign(
                        it.legislation,
                        CampaignTemplate(CampaignEnv.PUBLIC, it.targetingParams, it.legislation)
                    )
                }
            }
        }
    }

    override fun addCampaign(legislation: Legislation, campaign: CampaignTemplate) {
        mapTemplate[legislation.name] = campaign
    }

    override fun getGdpr(): Either<Gdpr> = check {
        dataStorage.getGdpr1203()?.toGDPR() ?: fail("GDPR is not stored in memory!!!")
    }

    override fun getCcpa(): Either<Ccpa> = check {
        dataStorage.getCcpa1203()?.toCCPA() ?: fail("CCPA is not stored in memory!!!")
    }

    override fun getCampaignTemplate(legislation: Legislation): Either<CampaignTemplate> = check {
        mapTemplate[legislation.name] ?: fail("${legislation.name} Campain is missing!!!")
    }

    override fun getPmConfig(legislation: Legislation, pmId: String?, pmTab: PMTab?): Either<PmUrlConfig> {
        return when (legislation) {
            Legislation.GDPR -> getGdprPmConfig(pmId, pmTab ?: PMTab.PURPOSES)
            Legislation.CCPA -> getCcpaPmConfig(pmId)
        }
    }

    fun getGdprPmConfig(pmId: String?, pmTab: PMTab): Either<PmUrlConfig> = check {
        val uuid = dataStorage.getGdprConsentUuid()
        PmUrlConfig(
            pmTab = pmTab, // gdprConfig.uuid ?: fail("consentUUID cannot be null!!!"),
            consentLanguage = null,
            consentUUID = uuid,
            siteId = null,
            messageId = pmId
        )
    }

    fun getCcpaPmConfig(pmId: String?): Either<PmUrlConfig> = check {
        val uuid = dataStorage.getCcpaConsentUuid()
        PmUrlConfig(
            consentLanguage = null, // ccpaConfig.uuid ?: fail("consentUUID cannot be null!!!"),
            consentUUID = uuid,
            siteId = null,
            messageId = pmId
        )
    }

    override fun isAppliedCampaign(legislation: Legislation): Boolean {
        return getAppliedCampaign()
            .map { it.first == legislation }
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
            thisContent = JSONObject()
        )
    }

    override fun getAppliedCampaign(): Either<Pair<Legislation, CampaignTemplate>> = check {
        when {
            dataStorage
                .getGdprMessage().isNotBlank() -> Pair(Legislation.GDPR, mapTemplate[Legislation.GDPR.name]!!)
            dataStorage
                .getCcpaMessage().isNotBlank() -> Pair(Legislation.CCPA, mapTemplate[Legislation.CCPA.name]!!)
            else -> throw MissingPropertyException(description = "Inconsistent Legislation!!!")
        }
    }

    override fun getUnifiedMessageReq(): UnifiedMessageRequest {
        val campaigns = mutableListOf<CampaignReq>()
        mapTemplate[Legislation.GDPR.name]
            ?.let { it.toCampaignReqImpl(targetingParams = it.targetingParams, campaignEnv = it.campaignEnv) }
            ?.let { campaigns.add(it) }
        mapTemplate[Legislation.CCPA.name]
            ?.let { it.toCampaignReqImpl(targetingParams = it.targetingParams, campaignEnv = it.campaignEnv) }
            ?.let { campaigns.add(it) }

        return UnifiedMessageRequest(
            requestUUID = "test",
            propertyHref = spConfig.propertyName,
            accountId = spConfig.accountId,
            campaigns = Campaigns(list = campaigns),
            consentLanguage = messageLanguage,
            localState = dataStorage.getLocalState()
        )
    }

    override fun getGDPRConsent(): Either<GDPRConsent> = check {
        gdprConsent ?: dataStorage
            .getGdprConsentResp()
            .also { if (it.isBlank()) fail("GDPRConsent is not saved in the the storage!!") }
            .let { JSONObject(it) }
            .toTreeMap()
            .toGDPRUserConsent()
    }

    override fun getCCPAConsent(): Either<CCPAConsent> = check {
        ccpaConsent ?: dataStorage
            .getCcpaConsentResp()
            .also { if (it.isBlank()) fail("CCPAConsent is not saved in the the storage!!") }
            .let { JSONObject(it) }
            .toTreeMap()
            .toCCPAUserConsent()
    }

    override fun saveGdpr(gdpr: Gdpr) {
        dataStorage.saveGdpr1203(gdpr.thisContent.toString())
        dataStorage.saveGdprConsentResp(gdpr.userConsent.thisContent.toString())
    }

    override fun saveCcpa(ccpa: Ccpa) {
        dataStorage.saveCcpa1203(ccpa.thisContent.toString())
        dataStorage.saveCcpaConsentResp(ccpa.userConsent.thisContent.toString())
    }

    override fun saveGDPRConsent(consent: GDPRConsent?) {
        gdprConsent = consent
        dataStorage.saveGdprConsentResp(consent?.thisContent?.toString() ?: "")
    }

    override fun saveCCPAConsent(consent: CCPAConsent?) {
        ccpaConsent = consent
        dataStorage.saveCcpaConsentResp(consent?.thisContent?.toString() ?: "")
    }

    override fun saveUnifiedMessageResp(unifiedMessageResp: UnifiedMessageResp) {
        dataStorage.saveLocalState(unifiedMessageResp.localState)
        val map = JSONObject(unifiedMessageResp.localState).toTreeMap()
        // save GDPR uuid
        map.getMap("gdpr")
            ?.getFieldValue<String>("uuid")
            ?.let { dataStorage.saveGdprConsentUuid(it) }
        // save GDPR uuid
        map.getMap("ccpa")
            ?.getFieldValue<String>("uuid")
            ?.let { dataStorage.saveCcpaConsentUuid(it) }
        map.getFieldValue<String>("localState")?.let { dataStorage.saveLocalState(it) }
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
        gdprConsent = null
        ccpaConsent = null
        dataStorage.clearGdprConsent()
        dataStorage.clearCcpaConsent()
    }
}

package com.sourcepoint.cmplibrary.campaign

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.core.map
import com.sourcepoint.cmplibrary.creation.validPattern
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.converter.fail
import com.sourcepoint.cmplibrary.data.network.ext.* //ktlint-disable
import com.sourcepoint.cmplibrary.exception.InvalidArgumentException
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.exception.MissingPropertyException
import com.sourcepoint.cmplibrary.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.Campaigns
import com.sourcepoint.cmplibrary.model.MessageReq
import com.sourcepoint.cmplibrary.model.exposed.CCPAConsent
import com.sourcepoint.cmplibrary.model.exposed.GDPRConsent
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepoint.cmplibrary.model.toCCPAUserConsent
import com.sourcepoint.cmplibrary.model.toCcpaReq
import com.sourcepoint.cmplibrary.model.toGDPRUserConsent
import com.sourcepoint.cmplibrary.model.toGdprReq
import com.sourcepoint.cmplibrary.util.check
import org.json.JSONObject

internal interface CampaignManager {

    val spConfig: SpConfig
    fun addCampaign(legislation: Legislation, campaign: CampaignTemplate)

    fun isAppliedCampaign(legislation: Legislation): Boolean
    fun getUnifiedMessageResp1203(): Either<UnifiedMessageResp>

    fun getGdpr1203(): Either<Gdpr>
    fun getCcpa1203(): Either<Ccpa>
    fun getAppliedCampaign(): Either<Pair<Legislation, CampaignTemplate>>
    fun getCampaignTemplate(legislation: Legislation): Either<CampaignTemplate>

    fun getGdprPmConfig(pmId: String?, pmTab: PMTab): Either<PmUrlConfig>
    fun getCcpaPmConfig(pmId: String?): Either<PmUrlConfig>

    fun getMessageReq(): MessageReq
    fun getUnifiedMessageReq(): UnifiedMessageRequest

    fun getGDPRConsent(): Either<GDPRConsent>
    fun getCCPAConsent(): Either<CCPAConsent>

    fun saveGdpr1203(gdpr: Gdpr)
    fun saveCcpa1203(ccpa: Ccpa)
    fun saveGDPRConsent(consent: GDPRConsent?)
    fun saveCCPAConsent(consent: CCPAConsent?)
    fun saveUnifiedMessageResp1203(unifiedMessageResp: UnifiedMessageResp)

    fun parseRenderingMessage()

    fun clearConsents()

    companion object
}

internal fun CampaignManager.Companion.create(
    dataStorage: DataStorage,
    spConfig: SpConfig
): CampaignManager = CampaignManagerImpl(dataStorage, spConfig)

private class CampaignManagerImpl(
    val dataStorage: DataStorage,
    override val spConfig: SpConfig
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
                        GDPRCampaign(it.environment, it.targetingParams)
                    )
                    Legislation.CCPA -> addCampaign(
                        it.legislation,
                        CCPACampaign(it.environment, it.targetingParams)
                    )
                }
            }
        }
    }

    override fun addCampaign(legislation: Legislation, campaign: CampaignTemplate) {
        mapTemplate[legislation.name] = campaign
    }

    override fun getGdpr1203(): Either<Gdpr> = check {
        dataStorage.getGdpr1203()?.toGDPR1203() ?: fail("GDPR is not stored in memory!!!")
    }

    override fun getCcpa1203(): Either<Ccpa> = check {
        dataStorage.getCcpa1203()?.toCCPA1203() ?: fail("CCPA is not stored in memory!!!")
    }

    override fun getCampaignTemplate(legislation: Legislation): Either<CampaignTemplate> = check {
        mapTemplate[legislation.name] ?: fail("${legislation.name} Campain is missing!!!")
    }

    override fun getGdprPmConfig(pmId: String?, pmTab: PMTab): Either<PmUrlConfig> = check {
        val gdpr: CampaignTemplate = mapTemplate[Legislation.GDPR.name]
            ?: fail("===> Privacy manager url config is missing!!! GDPR user config is missing.")

        val gdprConfig = dataStorage.getGdpr1203()?.toGDPR1203()
            ?: fail("===> Privacy manager url config is missing!!! GDPR object is missing from DataStorage.")

        val uuid = dataStorage.getGdprConsentUuid()

        PmUrlConfig(
            pmTab = pmTab, // gdprConfig.uuid ?: fail("consentUUID cannot be null!!!"),
            consentLanguage = null,
            consentUUID = uuid,
            siteId = null,
            messageId = pmId
        )
    }

    override fun getCcpaPmConfig(pmId: String?): Either<PmUrlConfig> = check {
        val ccpa: CampaignTemplate = mapTemplate[Legislation.CCPA.name]
            ?: fail("===> Privacy manager url config is missing!!! CCPA user config is missing.")

        val ccpaConfig = dataStorage.getCcpa1203()?.toCCPA1203()
            ?: fail("===> Privacy manager url config is missing!!! CCPA object is missing from DataStorage.")

        val uuid = dataStorage.getGdprConsentUuid()

        PmUrlConfig(
            consentLanguage = null, // ccpaConfig.uuid ?: fail("consentUUID cannot be null!!!"),
            consentUUID = null,
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

    override fun getUnifiedMessageResp1203(): Either<UnifiedMessageResp> = check {
        val campaigns = mutableListOf<CampaignResp>()
        getGdpr1203().map { campaigns.add(it) }
        getCcpa1203().map { campaigns.add(it) }
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

    override fun getMessageReq(): MessageReq {
        val gdpr: CampaignTemplate? = mapTemplate[Legislation.GDPR.name]
        val ccpa: CampaignTemplate? = mapTemplate[Legislation.CCPA.name]

        // TODO this is a test location
        val location = "EU"
        return MessageReq(
            requestUUID = "test",
            campaigns = Campaigns(
                gdpr = gdpr?.toGdprReq(targetingParams = gdpr.targetingParams, campaignEnv = gdpr.campaignEnv),
                ccpa = ccpa?.toCcpaReq(targetingParams = ccpa.targetingParams, campaignEnv = ccpa.campaignEnv)
            )
        )
    }

    override fun getUnifiedMessageReq(): UnifiedMessageRequest {
        val gdpr: CampaignTemplate? = mapTemplate[Legislation.GDPR.name]
        val ccpa: CampaignTemplate? = mapTemplate[Legislation.CCPA.name]

        // TODO this is a test location
        val location = "EU"
        return UnifiedMessageRequest(
            requestUUID = "test",
            propertyHref = spConfig.propertyName,
            accountId = spConfig.accountId,
            campaigns = Campaigns(
                gdpr = gdpr?.toGdprReq(targetingParams = gdpr.targetingParams, campaignEnv = gdpr.campaignEnv),
                ccpa = ccpa?.toCcpaReq(targetingParams = ccpa.targetingParams, campaignEnv = ccpa.campaignEnv)
            ),
            consentLanguage = MessageLanguage.ENGLISH
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

    override fun saveGdpr1203(gdpr: Gdpr) {
        dataStorage.saveGdpr1203(gdpr.thisContent.toString())
    }

    override fun saveCcpa1203(ccpa: Ccpa) {
        dataStorage.saveCcpa1203(ccpa.thisContent.toString())
    }

    override fun saveGDPRConsent(consent: GDPRConsent?) {
        gdprConsent = consent
        dataStorage.saveGdprConsentResp(consent?.thisContent?.toString() ?: "")
    }

    override fun saveCCPAConsent(consent: CCPAConsent?) {
        ccpaConsent = consent
        dataStorage.saveCcpaConsentResp(consent?.thisContent?.toString() ?: "")
    }

    override fun saveUnifiedMessageResp1203(unifiedMessageResp: UnifiedMessageResp) {
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
        unifiedMessageResp
            .campaigns
            .forEach {
                when (it) {
                    is Gdpr -> saveGdpr1203(it)
                    is Ccpa -> saveCcpa1203(it)
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

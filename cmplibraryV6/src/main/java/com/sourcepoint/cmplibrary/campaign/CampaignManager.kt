package com.sourcepoint.cmplibrary.campaign

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.core.map
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.converter.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.converter.fail
import com.sourcepoint.cmplibrary.data.network.converter.toCCPAUserConsent
import com.sourcepoint.cmplibrary.data.network.converter.toGDPR
import com.sourcepoint.cmplibrary.data.network.converter.toGDPRUserConsent
import com.sourcepoint.cmplibrary.data.network.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.Campaigns
import com.sourcepoint.cmplibrary.data.network.model.MessageReq
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.exception.MissingPropertyException
import com.sourcepoint.cmplibrary.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.toCcpaReq
import com.sourcepoint.cmplibrary.model.toGdprReq
import com.sourcepoint.cmplibrary.util.check
import org.json.JSONObject

internal interface CampaignManager {

    fun addCampaign(legislation: Legislation, campaign: CampaignTemplate)

    fun getEnv(legislation: Legislation): Env

    fun isAppliedCampaign(legislation: Legislation): Boolean
    fun getUnifiedMessageResp1203(): Either<UnifiedMessageResp1203>

    fun getGdpr(): Either<Gdpr>
    fun getCcpa(): Either<Ccpa>
    fun getGdpr1203(): Either<Gdpr1203>
    fun getCcpa1203(): Either<Ccpa1203>
    fun getAppliedCampaign(): Either<Pair<Legislation, CampaignTemplate>>
    fun getCampaignTemplate(legislation: Legislation): Either<CampaignTemplate>

    fun getGdprPmConfig(): Either<PmUrlConfig>
    fun getCcpaPmConfig(): Either<PmUrlConfig>

    fun getMessageReq(): MessageReq
    fun getUnifiedMessageReq(): UnifiedMessageRequest

    fun getGDPRConsent(): Either<GDPRConsent>
    fun getCCPAConsent(): Either<CCPAConsent>

    fun saveGdpr(gdpr: Gdpr)
    fun saveCcpa(ccpa: Ccpa)
    fun saveGdpr1203(gdpr: Gdpr1203)
    fun saveCcpa1203(ccpa: Ccpa1203)
    fun saveGDPRConsent(consent: GDPRConsent?)
    fun saveCCPAConsent(consent: CCPAConsent?)
    fun saveUnifiedMessageResp1203(unifiedMessageResp: UnifiedMessageResp1203)

    fun parseRenderingMessage()

    fun clearConsents()

    companion object
}

internal fun CampaignManager.Companion.create(
    dataStorage: DataStorage
): CampaignManager = CampaignManagerImpl(dataStorage)

private class CampaignManagerImpl(
    val dataStorage: DataStorage
) : CampaignManager {

    companion object {
        private var gdprConsent: GDPRConsent? = null
        private var ccpaConsent: CCPAConsent? = null
    }

    private val mapTemplate = mutableMapOf<String, CampaignTemplate>()

    override fun addCampaign(legislation: Legislation, campaign: CampaignTemplate) {
        mapTemplate[legislation.name] = campaign
    }

    override fun getEnv(legislation: Legislation): Env {
        return mapTemplate[legislation.name]?.env ?: Env.STAGE
    }

    override fun getGdpr(): Either<Gdpr> = check {
        dataStorage.getGdpr()?.toGDPR() ?: fail("GDPR is not stored in memory!!!")
    }

    override fun getCcpa(): Either<Ccpa> = check {
        dataStorage.getCcpa()?.toCCPA() ?: fail("CCPA is not stored in memory!!!")
    }

    override fun getGdpr1203(): Either<Gdpr1203> = check {
        dataStorage.getGdpr1203()?.toGDPR1203() ?: fail("GDPR is not stored in memory!!!")
    }

    override fun getCcpa1203(): Either<Ccpa1203> = check {
        dataStorage.getCcpa1203()?.toCCPA1203() ?: fail("CCPA is not stored in memory!!!")
    }

    override fun getCampaignTemplate(legislation: Legislation): Either<CampaignTemplate> = check {
        mapTemplate[legislation.name] ?: fail("${legislation.name} Campain is missing!!!")
    }

    override fun getGdprPmConfig(): Either<PmUrlConfig> = check {
        val gdpr: CampaignTemplate = mapTemplate[Legislation.GDPR.name]
            ?: fail("===> Privacy manager url config is missing!!! GDPR user config is missing.")

        val gdprConfig = dataStorage.getGdpr1203()?.toGDPR1203()
            ?: fail("===> Privacy manager url config is missing!!! GDPR object is missing from DataStorage.")

        PmUrlConfig(
            consentUUID = "", // gdprConfig.uuid ?: fail("consentUUID cannot be null!!!"),
            siteId = "7639",
            messageId = "" // gdpr.pmId
        )
    }

    override fun getCcpaPmConfig(): Either<PmUrlConfig> = check {
        val ccpa: CampaignTemplate = mapTemplate[Legislation.CCPA.name]
            ?: fail("===> Privacy manager url config is missing!!! CCPA user config is missing.")

        val ccpaConfig = dataStorage.getCcpa1203()?.toCCPA1203()
            ?: fail("===> Privacy manager url config is missing!!! CCPA object is missing from DataStorage.")

        PmUrlConfig(
            consentUUID = "", // ccpaConfig.uuid ?: fail("consentUUID cannot be null!!!"),
            siteId = "7639",
            messageId = "" // ccpa.pmId
        )
    }

    override fun isAppliedCampaign(legislation: Legislation): Boolean {
        return getAppliedCampaign()
            .map { it.first == legislation }
            .getOrNull()
            ?: false
    }

    override fun getUnifiedMessageResp1203(): Either<UnifiedMessageResp1203> = check {
        val campaigns = mutableListOf<CampaignResp1203>()
        getGdpr1203().map { campaigns.add(it) }
        getCcpa1203().map { campaigns.add(it) }
        val localState: String = dataStorage.getLocalState() ?: ""
        val propertyPriorityData: String? = dataStorage.getPropertyPriorityData()
        UnifiedMessageResp1203(
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
        val storedGdpr = dataStorage.getGdpr()?.toGDPR()
        val storedCcpa = dataStorage.getCcpa()?.toCCPA()

        val gdprUuid = storedGdpr?.uuid
        val gdprMeta = storedGdpr?.meta

        val ccpaUuid = storedCcpa?.uuid
        val ccpaMeta = storedCcpa?.meta

        // TODO this is a test location
        val location = "EU"
        return MessageReq(
            requestUUID = "test",
            campaigns = Campaigns(
                gdpr = gdpr?.toGdprReq(location = location, uuid = gdprUuid, meta = gdprMeta),
                ccpa = ccpa?.toCcpaReq(location = location, uuid = ccpaUuid, meta = ccpaMeta)
            )
        )
    }

    override fun getUnifiedMessageReq(): UnifiedMessageRequest {
        val gdpr: CampaignTemplate? = mapTemplate[Legislation.GDPR.name]
        val ccpa: CampaignTemplate? = mapTemplate[Legislation.CCPA.name]
        val storedGdpr = dataStorage.getGdpr()?.toGDPR()
        val storedCcpa = dataStorage.getCcpa()?.toCCPA()

        val gdprUuid = storedGdpr?.uuid
        val gdprMeta = storedGdpr?.meta

        val ccpaUuid = storedCcpa?.uuid
        val ccpaMeta = storedCcpa?.meta

        // TODO this is a test location
        val location = "EU"
        return UnifiedMessageRequest(
            requestUUID = "test",
            propertyHref = gdpr?.propertyName ?: "",
            accountId = gdpr?.accountId ?: 1,
            campaigns = Campaigns(
                gdpr = gdpr?.toGdprReq(location = location, uuid = gdprUuid, meta = gdprMeta),
                ccpa = ccpa?.toCcpaReq(location = location, uuid = ccpaUuid, meta = ccpaMeta)
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

    override fun saveGdpr(gdpr: Gdpr) {
        dataStorage.saveGdpr(gdpr.thisContent.toString())
    }

    override fun saveCcpa(ccpa: Ccpa) {
        dataStorage.saveCcpa(ccpa.thisContent.toString())
    }

    override fun saveGdpr1203(gdpr: Gdpr1203) {
        dataStorage.saveGdpr1203(gdpr.thisContent.toString())
    }

    override fun saveCcpa1203(ccpa: Ccpa1203) {
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

    override fun saveUnifiedMessageResp1203(unifiedMessageResp: UnifiedMessageResp1203) {
        dataStorage.saveLocalState(unifiedMessageResp.localState)
        unifiedMessageResp
            .campaigns
            .forEach {
                when (it) {
                    is Gdpr1203 -> saveGdpr1203(it)
                    is Ccpa1203 -> saveCcpa1203(it)
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

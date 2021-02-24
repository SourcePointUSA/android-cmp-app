package com.sourcepoint.cmplibrary.campaign

import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.converter.fail
import com.sourcepoint.cmplibrary.data.network.converter.toCCPAUserConsent
import com.sourcepoint.cmplibrary.data.network.converter.toGDPRUserConsent
import com.sourcepoint.cmplibrary.data.network.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.Campaigns
import com.sourcepoint.cmplibrary.data.network.model.MessageReq
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.exception.MissingPropertyException
import com.sourcepoint.cmplibrary.model.CampaignTemplate
import com.sourcepoint.cmplibrary.model.toCcpaReq
import com.sourcepoint.cmplibrary.model.toGdprReq
import com.sourcepoint.cmplibrary.model.toTreeMap
import com.sourcepoint.cmplibrary.util.Either
import com.sourcepoint.cmplibrary.util.check
import com.sourcepoint.cmplibrary.util.getOrNull
import org.json.JSONObject

internal interface CampaignManager {

    fun addCampaign(legislation: Legislation, campaign: CampaignTemplate)

    fun isAppliedCampaign(legislation: Legislation): Boolean

    fun getAppliedCampaign(): Either<Pair<Legislation, CampaignTemplate>>
    fun getCampaignTemplate(legislation: Legislation): Either<CampaignTemplate>
    fun getPmGDPRConfig(): Either<PmUrlConfig>
    fun getMessageReq(): MessageReq

    fun getGDPRConsent(): Either<GDPRConsent>
    fun getCCPAConsent(): Either<CCPAConsent>

    fun saveGDPRConsent(consent: GDPRConsent?)
    fun saveCCPAConsent(consent: CCPAConsent?)

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

    override fun getCampaignTemplate(legislation: Legislation): Either<CampaignTemplate> = check {
        mapTemplate[legislation.name] ?: fail("${legislation.name} Campain is not missing!!!")
    }

    override fun getPmGDPRConfig(): Either<PmUrlConfig> = check {
        val gdpr: CampaignTemplate = mapTemplate[Legislation.GDPR.name]
            ?: fail("Privacy manager url config is missing!!!")

        val gdprConfig = dataStorage.getGdpr().getOrNull() ?: fail("Privacy manager url config is missing!!!")
        PmUrlConfig(
            consentUUID = gdprConfig.uuid ?: fail("consentUUID cannot be null!!!"),
            siteId = "7639",
            messageId = gdpr.pmId
        )
    }

    override fun isAppliedCampaign(legislation: Legislation): Boolean {
        TODO("Not yet implemented")
        return true
    }

    override fun getAppliedCampaign(): Either<Pair<Legislation, CampaignTemplate>> = check {
        when {
            dataStorage
                .getGdprMessage() != null -> Pair(Legislation.GDPR, mapTemplate[Legislation.GDPR.name]!!)
            dataStorage
                .getCcpaMessage() != null -> Pair(Legislation.CCPA, mapTemplate[Legislation.CCPA.name]!!)
            else -> throw MissingPropertyException(description = "Inconsistent Legislation!!!")
        }
    }

    override fun getMessageReq(): MessageReq {
        val gdpr: CampaignTemplate? = mapTemplate[Legislation.GDPR.name]
        val ccpa: CampaignTemplate? = mapTemplate[Legislation.CCPA.name]
        val gdprUuid = dataStorage.getGdpr().getOrNull()?.uuid
        val gdprMeta = dataStorage.getGdpr().getOrNull()?.meta
        val ccpaUuid = dataStorage.getCcpa().getOrNull()?.uuid
        val ccpaMeta = dataStorage.getCcpa().getOrNull()?.meta
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

    override fun saveGDPRConsent(consent: GDPRConsent?) {
        gdprConsent = consent
        dataStorage.saveGdprConsentResp(consent?.let { it.thisContent.toString() } ?: "")
    }

    override fun saveCCPAConsent(consent: CCPAConsent?) {
        ccpaConsent = consent
        dataStorage.saveCcpaConsentResp(consent?.let { it.thisContent.toString() } ?: "")
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

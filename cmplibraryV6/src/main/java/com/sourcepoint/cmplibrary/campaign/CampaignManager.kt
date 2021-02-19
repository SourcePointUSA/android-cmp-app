package com.sourcepoint.cmplibrary.campaign

import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.converter.fail
import com.sourcepoint.cmplibrary.data.network.model.Campaigns
import com.sourcepoint.cmplibrary.data.network.model.MessageReq
import com.sourcepoint.cmplibrary.data.network.model.PmUrlConfig
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.exception.MissingPropertyException
import com.sourcepoint.cmplibrary.model.Campaign
import com.sourcepoint.cmplibrary.model.CampaignTemplate
import com.sourcepoint.cmplibrary.model.toCcpaReq
import com.sourcepoint.cmplibrary.model.toGdprReq
import com.sourcepoint.cmplibrary.util.Either
import com.sourcepoint.cmplibrary.util.check
import com.sourcepoint.cmplibrary.util.getOrNull

internal interface CampaignManager {
    fun addCampaign(legislation: Legislation, campaign: Campaign)
    fun addCampaign(legislation: Legislation, campaign: CampaignTemplate)

    fun getAppliedCampaign(): Either<Pair<Legislation, CampaignTemplate>>
    fun getCampaignTemplate(legislation: Legislation): Either<CampaignTemplate>
    fun getPmGDPRConfig(): Either<PmUrlConfig>
    fun getMessageReq(): MessageReq

    companion object
}

internal fun CampaignManager.Companion.create(
    dataStorage: DataStorage
): CampaignManager = CampaignManagerImpl(dataStorage)

private class CampaignManagerImpl(
    val dataStorage: DataStorage
) : CampaignManager {

    private val map = mutableMapOf<String, Campaign>()
    private val mapTemplate = mutableMapOf<String, CampaignTemplate>()

    override fun addCampaign(legislation: Legislation, campaign: Campaign) {
        map[legislation.name] = campaign
    }

    override fun addCampaign(legislation: Legislation, campaign: CampaignTemplate) {
        mapTemplate[legislation.name] = campaign
    }

    override fun getCampaignTemplate(legislation: Legislation): Either<CampaignTemplate> = check {
        mapTemplate[legislation.name] ?: fail("${legislation.name} Campain is not missing!!!")
    }

    override fun getPmGDPRConfig(): Either<PmUrlConfig> = check {
        val gdpr: Campaign = map[Legislation.GDPR.name] ?: fail("Privacy manager url config is missing!!!")

        val gdprConfig = dataStorage.getGdpr().getOrNull() ?: fail("Privacy manager url config is missing!!!")
        PmUrlConfig(
            consentUUID = gdprConfig.uuid ?: fail("consentUUID cannot be null!!!"),
            siteId = "7639",
            messageId = gdpr.pmId
        )
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
        val location = "EU"
        return MessageReq(
            requestUUID = "test",
            campaigns = Campaigns(
                gdpr = gdpr?.toGdprReq(location),
                ccpa = ccpa?.toCcpaReq(location)
            )
        )
    }
}

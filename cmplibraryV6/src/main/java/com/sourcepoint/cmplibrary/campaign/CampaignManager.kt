package com.sourcepoint.cmplibrary.campaign

import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.converter.fail
import com.sourcepoint.cmplibrary.data.network.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.exception.Legislation
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
    fun getCampaign(legislation: Legislation): Either<Campaign>
    fun getAppliedCampaign(): Either<CampaignTemplate>
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

    override fun getCampaign(legislation: Legislation): Either<Campaign> = check {
        map[legislation.name] ?: fail("${legislation.name} Campain is not missing!!!")
    }

    override fun getCampaignTemplate(legislation: Legislation): Either<CampaignTemplate> = check {
        mapTemplate[legislation.name] ?: fail("${legislation.name} Campain is not missing!!!")
    }

    override fun getPmGDPRConfig(): Either<PmUrlConfig> = check {
        val gdpr: Campaign = map[Legislation.GDPR.name] ?: fail("Privacy manager url config is missing!!!")

        val gdprConfig = dataStorage.getGdpr().getOrNull() ?: fail("Privacy manager url config is missing!!!")
        PmUrlConfig(
            consentUUID = gdprConfig.uuid!!, // TODO we cannot force not null!!!
            siteId = "7639",
            messageId = gdpr.pmId
        )
    }

    override fun getAppliedCampaign(): Either<CampaignTemplate> = check {
        val gdpr: CampaignTemplate = mapTemplate[Legislation.GDPR.name] ?: fail("No applied campaign found!!!")
        // TODO create logic for applied template
        gdpr
    }

    override fun getMessageReq(): MessageReq {
        val gdpr: CampaignTemplate? = mapTemplate[Legislation.GDPR.name]
        val ccpa: CampaignTemplate? = mapTemplate[Legislation.CCPA.name]
        return MessageReq(
            requestUUID = "test",
            campaigns = Campaigns(
                gdpr = gdpr?.toGdprReq("EU"),
                ccpa = ccpa?.toCcpaReq("US")
            )
        )
    }
}

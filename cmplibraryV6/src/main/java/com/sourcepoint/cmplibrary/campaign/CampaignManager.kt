package com.sourcepoint.cmplibrary.campaign

import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.converter.fail
import com.sourcepoint.cmplibrary.data.network.model.PmUrlConfig
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.model.Campaign
import com.sourcepoint.cmplibrary.util.Either
import com.sourcepoint.cmplibrary.util.check
import com.sourcepoint.cmplibrary.util.getOrNull

internal interface CampaignManager {
    fun addCampaign(legislation: Legislation, campaign: Campaign)
    fun getCampaign(legislation: Legislation): Either<Campaign>
    fun getPmGDPRConfig(): Either<PmUrlConfig>

    companion object
}

internal fun CampaignManager.Companion.create(dataStorage: DataStorage): CampaignManager = CampaignManagerImpl(dataStorage)

private class CampaignManagerImpl(val dataStorage: DataStorage) : CampaignManager {

    private val map = mutableMapOf<String, Campaign>()

    override fun addCampaign(legislation: Legislation, campaign: Campaign) {
        map[legislation.name] = campaign
    }

    override fun getCampaign(legislation: Legislation): Either<Campaign> = check {
        map[legislation.name] ?: fail("${legislation.name} Campain is not missing!!!")
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
}

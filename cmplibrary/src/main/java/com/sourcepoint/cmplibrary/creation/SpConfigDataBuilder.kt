package com.sourcepoint.cmplibrary.creation

import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.exposed.SpCampaign
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepoint.cmplibrary.model.exposed.TargetingParam
import com.sourcepoint.cmplibrary.model.exposed.toTParam
import com.sourcepoint.cmplibrary.model.toTreeMap
import org.json.JSONObject
import kotlin.properties.Delegates

@SpDSL
class SpConfigDataBuilder {

    private val campaigns = mutableListOf<SpCampaign>()
    var accountId by Delegates.notNull<Int>()
    var propertyName by Delegates.notNull<String>()
    var messLanguage: MessageLanguage = MessageLanguage.ENGLISH
    var messageTimeout: Long = 5000
    var logger: Logger? = null

    operator fun CampaignType.unaryPlus() {
        campaigns.add(SpCampaign(this, emptyList()))
    }

    operator fun Pair<CampaignType, List<Pair<String, String>>>.unaryPlus() {
        campaigns.add(SpCampaign(this.first, this.second.map { it.toTParam() }))
    }

    fun addAccountId(accountId: Int): SpConfigDataBuilder = apply {
        this.accountId = accountId
    }

    fun addPropertyName(propertyName: String): SpConfigDataBuilder = apply {
        this.propertyName = propertyName
    }

    fun addMessageLanguage(messLanguage: MessageLanguage) = apply {
        this.messLanguage = messLanguage
    }

    fun addMessageTimeout(messageTimeout: Long) = apply {
        this.messageTimeout = messageTimeout
    }

    fun addLogger(logger: Logger) = apply {
        this.logger = logger
    }

    fun addCampaign(
        campaignType: CampaignType,
        targetingParams: String
    ): SpConfigDataBuilder = apply {

        val tp = JSONObject(targetingParams)
        val array = tp
            .toTreeMap()
            .entries
            .fold(mutableListOf<TargetingParam>()) { acc, elem ->
                acc.add(TargetingParam(elem.key, (elem.value as? String) ?: ""))
                acc
            }
        campaigns.add(SpCampaign(campaignType, array))
    }

    fun addCampaign(
        campaignType: CampaignType
    ): SpConfigDataBuilder = apply {
        campaigns.add(SpCampaign(campaignType, emptyList()))
    }

    fun addCampaign(
        campaignType: CampaignType,
        params: List<TargetingParam>
    ): SpConfigDataBuilder = apply {
        campaigns.add(SpCampaign(campaignType, params))
    }

    fun addCampaign(campaign: SpCampaign): SpConfigDataBuilder = apply {
        campaigns.add(campaign)
    }

    fun build(): SpConfig {
        return SpConfig(
            accountId = accountId,
            propertyName = propertyName,
            campaigns = campaigns,
            messageLanguage = messLanguage,
            messageTimeout = messageTimeout,
            logger = logger
        )
    }
}

fun config(dsl: SpConfigDataBuilder.() -> Unit): SpConfig {
    return SpConfigDataBuilder().apply(dsl).build()
}

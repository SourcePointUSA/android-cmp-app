package com.sourcepoint.cmplibrary.creation

import com.sourcepoint.cmplibrary.data.network.util.CampaignType
import com.sourcepoint.cmplibrary.model.CampaignsEnv
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.exposed.SpCampaign
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepoint.cmplibrary.model.exposed.SpGppConfig
import com.sourcepoint.cmplibrary.model.exposed.TargetingParam
import com.sourcepoint.cmplibrary.model.exposed.toTParam
import com.sourcepoint.cmplibrary.model.toTreeMap
import org.json.JSONObject
import kotlin.properties.Delegates

@SpDSL
class SpConfigDataBuilder {
    private val campaigns = mutableListOf<SpCampaign>()
    var accountId by Delegates.notNull<Int>()
    var propertyId by Delegates.notNull<Int>()
    var propertyName by Delegates.notNull<String>()
    var messLanguage: MessageLanguage = MessageLanguage.ENGLISH
    var campaignsEnv: CampaignsEnv = CampaignsEnv.PUBLIC
    var messageTimeout: Long = 5000
    var spGppConfig: SpGppConfig? = null

    operator fun CampaignType.unaryPlus() {
        campaigns.add(SpCampaign(this, emptyList()))
    }

    operator fun SpCampaign.unaryPlus() {
        campaigns.add(this)
    }

    operator fun Pair<CampaignType, List<Pair<String, String>>>.unaryPlus() {
        campaigns.add(SpCampaign(this.first, this.second.map { it.toTParam() }))
    }

    operator fun Map<CampaignType, Set<ConfigOption>>.unaryPlus() {
        this.entries.firstOrNull()?.let {
            campaigns.add(SpCampaign(it.key, configParams = it.value))
        }
    }

    fun addAccountId(accountId: Int): SpConfigDataBuilder = apply {
        this.accountId = accountId
    }

    fun addPropertyId(propertyId: Int): SpConfigDataBuilder = apply {
        this.propertyId = propertyId
    }

    fun addPropertyName(propertyName: String): SpConfigDataBuilder = apply {
        this.propertyName = propertyName
    }

    fun addMessageLanguage(messLanguage: MessageLanguage) = apply {
        this.messLanguage = messLanguage
    }

    fun addCampaignsEnv(campaignsEnv: CampaignsEnv) = apply {
        this.campaignsEnv = campaignsEnv
    }

    fun addMessageTimeout(messageTimeout: Long) = apply {
        this.messageTimeout = messageTimeout
    }

    fun addGppConfig(spGppConfig: SpGppConfig) = apply {
        this.spGppConfig = spGppConfig
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
        params: List<TargetingParam>,
        groupPmId: String?
    ): SpConfigDataBuilder = apply {
        campaigns.add(SpCampaign(campaignType, params, groupPmId))
    }

    fun addCampaign(
        campaignType: CampaignType,
        params: List<TargetingParam>,
        groupPmId: String?,
        configParams: Set<ConfigOption> = emptySet(),
    ): SpConfigDataBuilder = apply {
        campaigns.add(SpCampaign(campaignType, params, groupPmId, configParams))
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
            campaignsEnv = campaignsEnv,
            propertyId = propertyId,
            spGppConfig = spGppConfig,
        )
    }
}

fun config(dsl: SpConfigDataBuilder.() -> Unit): SpConfig {
    return SpConfigDataBuilder().apply(dsl).build()
}
enum class ConfigOption(option: String) {
    TRANSITION_CCPA_AUTH("transitionCCPAAuth"),
    SUPPORT_LEGACY_USPSTRING("supportLegacyUSPString")
}

infix fun CampaignType.to(config: Set<ConfigOption>): Map<CampaignType, Set<ConfigOption>> {
    return mapOf(Pair(this, config))
}

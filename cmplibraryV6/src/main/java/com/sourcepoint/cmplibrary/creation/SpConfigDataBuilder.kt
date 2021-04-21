package com.sourcepoint.cmplibrary.creation

import com.sourcepoint.cmplibrary.data.network.util.CampaignEnv
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.model.exposed.SpCampaign
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepoint.cmplibrary.model.exposed.TargetingParam
import com.sourcepoint.cmplibrary.model.toTreeMap
import org.json.JSONObject
import kotlin.properties.Delegates

class SpConfigDataBuilder {

    private val campaigns = mutableListOf<SpCampaign>()
    private var accountId by Delegates.notNull<Int>()
    private var propertyName by Delegates.notNull<String>()

    fun addAccountId(accountId: Int): SpConfigDataBuilder = apply {
        this.accountId = accountId
    }

    fun addPropertyName(propertyName: String): SpConfigDataBuilder = apply {
        this.propertyName = propertyName
    }

    fun addCampaign(
        legislation: Legislation,
        env: CampaignEnv,
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
            .toTypedArray()
        campaigns.add(SpCampaign(legislation, env, array))
    }

    fun addCampaign(
        legislation: Legislation,
        env: CampaignEnv,
        params: Array<TargetingParam>
    ): SpConfigDataBuilder = apply {
        campaigns.add(SpCampaign(legislation, env, params))
    }

    fun addCampaign(campaign: SpCampaign): SpConfigDataBuilder = apply {
        campaigns.add(campaign)
    }

    fun build(): SpConfig {
        return SpConfig(
            accountId,
            propertyName,
            campaigns.toTypedArray()
        )
    }
}

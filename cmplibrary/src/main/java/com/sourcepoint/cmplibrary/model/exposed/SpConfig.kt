package com.sourcepoint.cmplibrary.model.exposed

import com.sourcepoint.cmplibrary.creation.ConfigOption
import com.sourcepoint.cmplibrary.data.network.DEFAULT_TIMEOUT
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.model.MessageLanguage
import kotlinx.serialization.json.encodeToJsonElement

data class SpConfig(
    @JvmField val accountId: Int,
    @JvmField val propertyName: String,
    @JvmField val campaigns: List<SpCampaign>,
    @JvmField val messageLanguage: MessageLanguage,
    @JvmField val messageTimeout: Long = DEFAULT_TIMEOUT,
    @JvmField val propertyId: Int,
    @JvmField val campaignsEnv: CampaignsEnv = CampaignsEnv.PUBLIC,
    @JvmField val logger: Logger? = null,
    @JvmField val spGppConfig: SpGppConfig? = null,
)

data class SpCampaign(
    @JvmField val campaignType: CampaignType,
    @JvmField internal var targetingParams: List<TargetingParam> = emptyList(),
    @JvmField var groupPmId: String? = null,
    @JvmField var configParams: Set<ConfigOption> = emptySet(),
) {
    constructor(
        campaignType: CampaignType,
        targetingParams: List<TargetingParam>
    ) : this(campaignType, targetingParams, null)

    constructor(
        campaignType: CampaignType,
        targetingParams: List<TargetingParam>,
        configParams: Set<ConfigOption>,
    ) : this(campaignType, targetingParams, null, configParams)

    constructor(
        campaignType: CampaignType,
        groupPmId: String
    ) : this(campaignType, emptyList(), groupPmId)

    constructor(
        campaignType: CampaignType,
        groupPmId: String,
        configParams: Set<ConfigOption>,
    ) : this(campaignType, emptyList(), groupPmId, configParams)
}

data class TargetingParam(val key: String, val value: String)

fun List<TargetingParam>.toJsonElement() = JsonConverter.converter.encodeToJsonElement(
    fold(mutableMapOf<String, String>()) { params, targetingParam ->
        params[targetingParam.key] = targetingParam.value
        return@fold params
    }
)

fun Pair<String, String>.toTParam() = TargetingParam(this.first, this.second)

enum class MessageType {
    MOBILE,
    OTT,
    LEGACY_OTT
}

internal fun MessageSubCategory.toMessageType(): MessageType = when (this) {
    MessageSubCategory.OTT -> MessageType.LEGACY_OTT
    MessageSubCategory.NATIVE_OTT -> MessageType.OTT
    else -> MessageType.MOBILE
}

package com.sourcepointmeta.metaapp.tv.ui

import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepointmeta.metaapp.data.localdatasource.Property

data class PropertyTvDTO(
    val propertyName: String,
    val accountId: Long,
    val campaignEnv: String,
    val gdprPmId: String,
    val ccpaPmId: String,
    val authId: String,
    val messageLanguage: MessageLanguage,
    val gdprEnabled: Boolean,
    val ccpaEnabled: Boolean,
    val property: Property,
    val timeout: Long?,
    var saving: Boolean = false
)

fun Property.toPropertyTvDTO(): PropertyTvDTO {
    val env = if (is_staging) "stage" else "prod"
    return PropertyTvDTO(
        campaignEnv = env,
        propertyName = propertyName,
        accountId = accountId,
        ccpaEnabled = statusCampaignSet.find { s -> s.campaignType == CampaignType.CCPA }?.enabled
            ?: false,
        gdprEnabled = statusCampaignSet.find { s -> s.campaignType == CampaignType.GDPR }?.enabled
            ?: false,
        property = this,
        ccpaPmId = ccpaPmId?.toString() ?: "",
        gdprPmId = gdprPmId?.toString() ?: "",
        authId = authId ?: "",
        messageLanguage = MessageLanguage.values().find { it.name == messageLanguage }
            ?: MessageLanguage.ENGLISH,
        timeout = timeout
    )
}

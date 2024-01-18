package com.sourcepointmeta.metaapp.ui.component

import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepointmeta.metaapp.data.localdatasource.Property

data class PropertyDTO(
    val propertyName: String,
    val accountId: Long,
    val campaignEnv: String,
    val gdprPmId: String,
    val ccpaPmId: String,
    val authId: String,
    val pmTab: PMTab,
    val messageLanguage: MessageLanguage,
    val gdprEnabled: Boolean,
    val ccpaEnabled: Boolean,
    val usnatEnabled: Boolean,
    val property: Property,
    val timeout: Long?,
    var saving: Boolean = false
)

fun Property.toPropertyDTO(): PropertyDTO {
    val env = if (is_staging) "stage" else "prod"
    return PropertyDTO(
        campaignEnv = env,
        propertyName = propertyName,
        accountId = accountId,
        ccpaEnabled = statusCampaignSet.find { s -> s.campaignType == CampaignType.CCPA }?.enabled
            ?: false,
        gdprEnabled = statusCampaignSet.find { s -> s.campaignType == CampaignType.GDPR }?.enabled
            ?: false,
        usnatEnabled = statusCampaignSet.find { s -> s.campaignType == CampaignType.USNAT }?.enabled
            ?: false,
        property = this,
        ccpaPmId = ccpaPmId?.toString() ?: "",
        gdprPmId = gdprPmId?.toString() ?: "",
        pmTab = PMTab.values().find { it.name == pmTab } ?: PMTab.DEFAULT,
        authId = authId ?: "",
        messageLanguage = MessageLanguage.values().find { it.name == messageLanguage }
            ?: MessageLanguage.ENGLISH,
        timeout = timeout
    )
}

data class DemoActionItem(
    val message: String,
    val code: Int
)

data class LogItem(
    val id: Long?,
    val status: String?,
    val propertyName: String,
    val timestamp: Long,
    val type: String,
    val tag: String,
    val message: String,
    val jsonBody: String? = null,
    val selected: Boolean = false
)

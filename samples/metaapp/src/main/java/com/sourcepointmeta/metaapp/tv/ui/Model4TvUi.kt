package com.sourcepointmeta.metaapp.tv.ui

import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.tv.ui.edit.PropertyField

data class PropertyTvDTO(
    val propertyName: String,
    val accountId: Long,
    val campaignEnv: String,
    val gdprPmId: String,
    val ccpaPmId: String,
    val is_staging: Boolean = false,
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
        is_staging = is_staging,
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

fun PropertyTvDTO.toProperty(fieldType: PropertyField, newField: String?): Property {

    val messLanguageTv = if (newField != null && fieldType == PropertyField.MESSAGE_LANGUAGE) {
        MessageLanguage.values().find { it.value == newField }?.name
            ?: MessageLanguage.ENGLISH.name
    } else messageLanguage.name

    val env = if (is_staging) "stage" else "prod"

    return Property(
        campaignsEnv = CampaignsEnv.values().find { it.env == env } ?: CampaignsEnv.PUBLIC,
        propertyName = if (newField != null && fieldType == PropertyField.PROPERTY_NAME) newField else propertyName,
        accountId = if (newField != null && fieldType == PropertyField.ACCOUNT_ID) newField.toLong() else accountId,
        ccpaPmId = ccpaPmId.toLongOrNull(),
        gdprPmId = gdprPmId.toLongOrNull(),
        authId = authId,
        messageLanguage = messLanguageTv,
        timeout = if (newField != null && fieldType == PropertyField.TIMEOUT) newField.toLong() else timeout,
        is_staging = is_staging,
        statusCampaignSet = property.statusCampaignSet,
        messageType = "App",
        gdprGroupPmId = null,
        pmTab = PMTab.DEFAULT.name,
        ccpaGroupPmId = null,
        targetingParameters = emptyList(),
        timestamp = property.timestamp,
        useCcpaGroupPmIfAvailable = false,
        useGdprGroupPmIfAvailable = false
    )
}

package com.sourcepointmeta.metaapp.data.localdatasource

import com.sourcepoint.cmplibrary.exception.CampaignType
import java.util.* // ktlint-disable

data class Property(
    val propertyName: String,
    val accountId: Long,
    val pmId: String,
    val messageType: String,
    val is_staging: Boolean = false,
    val targetingParameters: List<MetaTargetingParam> = emptyList(),
    val propertyId: Long? = null,
    val authId: String? = null,
    val messageLanguage: String? = null,
    val pmTab: String? = null,
    val statusCampaign: StatusCampaign = StatusCampaign(propertyName = propertyName),
    val timestamp: Long = Date().time
)

data class MetaTargetingParam(
    val propertyName: String,
    val campaign: CampaignType,
    val key: String,
    val value: String
)

data class StatusCampaign(
    val propertyName: String,
    val gdprEnabled: Boolean = false,
    val ccpaEnabled: Boolean = false
)

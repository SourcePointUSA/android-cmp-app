package com.sourcepointmeta.metaapp.data.localdatasource

data class Property(
    val property_name: String,
    val account_id: Long,
    val pm_id: String,
    val is_staging: Boolean = false,
    val targetingParameters: List<TargetingParam> = emptyList(),
    val property_id: Long? = null,
    val auth_Id: String? = null,
    val message_language: String? = null,
    val pm_tab: String? = null
)

data class TargetingParam(
    val propertyName: String,
    val key: String,
    val value: String
)
package com.sourcepointmeta.metaapp.ui.component

import com.sourcepointmeta.metaapp.data.localdatasource.Property

data class PropertyDTO(
    val propertyName: String,
    val accountId: Long,
    val campaignEnv: String,
    val messageType: String,
    val gdprEnabled: Boolean,
    val ccpaEnabled: Boolean,
    val property: Property
)

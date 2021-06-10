package com.sourcepointmeta.metaapp.ui.component

import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepointmeta.metaapp.data.localdatasource.Property

data class PropertyDTO(
    val propertyName: String,
    val accountId: Long,
    val campaignEnv: String,
    val messageType: String,
    val gdprPmId: String,
    val ccpaPmId: String,
    val authId: String,
    val pmTab: PMTab,
    val messageLanguage: MessageLanguage,
    val gdprEnabled: Boolean,
    val ccpaEnabled: Boolean,
    val property: Property
)

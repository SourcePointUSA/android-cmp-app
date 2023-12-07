package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.model.optimized.MetaDataArg.* //ktlint-disable
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.CcpaStatus.* //ktlint-disable
import com.sourcepoint.cmplibrary.util.check
import com.sourcepoint.cmplibrary.util.extensions.hasTransitionCCPAAuth
import com.sourcepoint.cmplibrary.util.extensions.isIncluded
import kotlinx.serialization.encodeToString

internal fun UsNatArg.createMetadataArg(cm: CampaignManager): UsNatArg {

    var transitionCCPAAuth = this.transitionCCPAAuth
    var optedOut = this.optedOut
    var dateCreated = this.dateCreated
    var usnatUuid = cm.usNatConsentData?.uuid
    val ccpaCS = cm.ccpaConsentStatus

    if (cm.spConfig.isIncluded(CampaignType.USNAT)) {
        if (cm.authId != null &&
            cm.spConfig.hasTransitionCCPAAuth()
        ) {
            transitionCCPAAuth = true
        } else if (ccpaCS != null &&
            cm.usNatConsentData == null &&
            (ccpaCS.status == rejectedSome || ccpaCS.status == rejectedAll)
        ) {
            optedOut = true
            dateCreated = ccpaCS.dateCreated
        }
    }

    return UsNatArg(
        applies = applies,
        groupPmId = groupPmId,
        targetingParams = targetingParams,
        hasLocalData = hasLocalData,
        uuid = usnatUuid,
        dateCreated = dateCreated,
        transitionCCPAAuth = transitionCCPAAuth,
        optedOut = optedOut,
    )
}

internal fun MetaDataParamReq.stringify(): String =
    check { JsonConverter.converter.encodeToString(this) }.getOrNull() ?: "{}"

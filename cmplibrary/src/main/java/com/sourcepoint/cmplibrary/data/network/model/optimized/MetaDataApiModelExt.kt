package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.data.network.model.optimized.MetaDataArg.* //ktlint-disable
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.CcpaStatus.* //ktlint-disable
import com.sourcepoint.cmplibrary.util.extensions.hasTransitionCCPAAuth
import com.sourcepoint.cmplibrary.util.extensions.isIncluded

internal fun UsNatArg.createMetadataArg(campaignManager: CampaignManager): UsNatArg {

    var transitionCCPAAuth = this.transitionCCPAAuth
    var optedOut = this.optedOut
    var dateCreated = this.dateCreated
    var usnatUuid = campaignManager.usNatConsentData?.uuid
    val ccpaCS = campaignManager.ccpaConsentStatus

    if (campaignManager.spConfig.isIncluded(CampaignType.USNAT)) {
        if (campaignManager.authId != null &&
            campaignManager.spConfig.hasTransitionCCPAAuth()
        ) {
            transitionCCPAAuth = true
        } else if (ccpaCS != null &&
            campaignManager.usNatConsentData == null &&
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

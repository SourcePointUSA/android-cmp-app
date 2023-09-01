package com.sourcepoint.cmplibrary.data.network.model.optimized.metaData

import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.data.network.model.optimized.MetaDataResp
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.ChoiceMetaData
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.ChoiceMetaDataArg
import com.sourcepoint.cmplibrary.data.network.model.optimized.consentStatus.ConsentStatusMetaData
import com.sourcepoint.cmplibrary.data.network.model.optimized.consentStatus.ConsentStatusMetaDataArg
import com.sourcepoint.cmplibrary.data.network.model.optimized.messages.MessagesMetaData
import com.sourcepoint.cmplibrary.data.network.model.optimized.messages.MessagesMetaDataArg

/**
 * Method that maps up meta data response to the meta data for /consent-status request
 *
 * @param campaignManager - CampaignManager instance that provides required fields
 */
internal fun MetaDataResp.toConsentStatusMetaData(
    campaignManager: CampaignManager
): ConsentStatusMetaData = ConsentStatusMetaData(
    ccpa = this.ccpa?.let { metaDataCCPAResponse ->
        ConsentStatusMetaDataArg(
            uuid = campaignManager.ccpaConsentStatus?.uuid,
            applies = metaDataCCPAResponse.applies,
            hasLocalData = campaignManager.ccpaConsentStatus != null,
            dateCreated = campaignManager.ccpaConsentStatus?.dateCreated,
        )
    },
    gdpr = this.gdpr?.let { metaDataGDPRResponse ->
        ConsentStatusMetaDataArg(
            uuid = campaignManager.gdprConsentStatus?.uuid,
            applies = metaDataGDPRResponse.applies,
            hasLocalData = campaignManager.gdprConsentStatus != null,
            dateCreated = campaignManager.gdprConsentStatus?.dateCreated,
        )
    },
)

/**
 * Method that maps up meta data response to the meta data for /messages request
 */
internal fun MetaDataResp.toMessagesMetaData(): MessagesMetaData = MessagesMetaData(
    ccpa = this.ccpa?.let { MessagesMetaDataArg(applies = it.applies) },
    gdpr = this.gdpr?.let { MessagesMetaDataArg(applies = it.applies) },
)

/**
 * Method that maps up meta data response to the meta data for /choice request
 */
internal fun MetaDataResp.toChoiceMetaData(): ChoiceMetaData = ChoiceMetaData(
    ccpa = this.ccpa?.let { ChoiceMetaDataArg(applies = it.applies) },
    gdpr = this.gdpr?.let { ChoiceMetaDataArg(applies = it.applies) },
)

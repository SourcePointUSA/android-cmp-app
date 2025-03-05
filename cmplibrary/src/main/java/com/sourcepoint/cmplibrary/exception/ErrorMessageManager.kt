package com.sourcepoint.cmplibrary.exception

import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.data.network.converter.CampaignTypeSerializer
import com.sourcepoint.mobile_core.models.SPCampaignType
import kotlinx.serialization.Serializable

/**
 * Class in charge of building an error message
 */
internal interface ErrorMessageManager {
    /**
     * The build method receives a ConsentLibExceptionK object and build out of it the message to sent to the server.
     * @param exception instance of [ConsentLibExceptionK]
     * @return the message which describe the error occurred
     */
    fun build(exception: RuntimeException): String
}

/**
 * Type of configurable campaigns
 */
@Serializable(with = CampaignTypeSerializer::class)
enum class CampaignType {
    GDPR,
    CCPA,
    USNAT,
    UNKNOWN;

    companion object {
        fun fromCore(type: SPCampaignType) = when(type) {
            SPCampaignType.Gdpr -> GDPR
            SPCampaignType.Ccpa -> CCPA
            SPCampaignType.UsNat -> USNAT
            SPCampaignType.Unknown, SPCampaignType.IOS14 -> UNKNOWN
        }
    }

    fun toCore(): SPCampaignType = when(this) {
        GDPR -> SPCampaignType.Gdpr
        CCPA -> SPCampaignType.Ccpa
        USNAT -> SPCampaignType.UsNat
        UNKNOWN -> SPCampaignType.Unknown
    }
}

/**
 * Class used to group the client information
 */
internal data class ClientInfo(
    val clientVersion: String,
    val osVersion: String,
    val deviceFamily: String
)

/**
 * Factory method for creating an instance of [ErrorMessageManager]
 */
internal fun createErrorManager(
    campaignManager: CampaignManager,
    clientInfo: ClientInfo,
    campaignType: CampaignType = CampaignType.GDPR
): ErrorMessageManager = ErrorMessageManagerImpl(campaignManager, clientInfo, campaignType)

/**
 * Implementation class of [ErrorMessageManager]
 */
private class ErrorMessageManagerImpl(
    val campaignManager: CampaignManager,
    val clientInfo: ClientInfo,
    val campaignType: CampaignType = CampaignType.GDPR
) : ErrorMessageManager {
    override fun build(exception: RuntimeException): String {
        return (exception as? ConsentLibExceptionK)?.let {
            val spConf = campaignManager.spConfig
            """
            {
                "code" : "${exception.code.errorCode}",
                "accountId" : "${spConf.accountId}",
                "propertyId" : "${spConf.propertyId}",
                "propertyHref" : "${spConf.propertyName}",
                "description" : "${exception.description}",
                "clientVersion" : "${clientInfo.clientVersion}",
                "OSVersion" : "${clientInfo.osVersion}",
                "deviceFamily" : "${clientInfo.deviceFamily}",
                "legislation" : "${campaignType.name}"
            }
            """.trimIndent()
        } ?: ""
    }
}

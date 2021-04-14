package com.sourcepoint.cmplibrary.exception

import com.sourcepoint.cmplibrary.campaign.CampaignManager

/**
 * Class in charge of building an error message
 */
internal interface ErrorMessageManager {
    /**
     * The build method receives a ConsentLibExceptionK object and build out of it the message to sent to the server.
     * @param exception instance of [ConsentLibExceptionK]
     * @return the message which describe the error occurred
     */
    fun build(exception: ConsentLibExceptionK): String
}

/**
 * Library types list
 */
enum class Legislation {
    GDPR,
    CCPA
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
    legislation: Legislation = Legislation.GDPR
): ErrorMessageManager = ErrorMessageManagerImpl(campaignManager, clientInfo, legislation)

/**
 * Implementation class of [ErrorMessageManager]
 */
private class ErrorMessageManagerImpl(
    val campaignManager: CampaignManager,
    val clientInfo: ClientInfo,
    val legislation: Legislation = Legislation.GDPR
) : ErrorMessageManager {
    override fun build(exception: ConsentLibExceptionK): String {
        val spConf = campaignManager.spConfig
        return """
            {
                "code" : "${exception.code.code}",
                "accountId" : "${spConf.accountId}",
                "propertyHref" : "${spConf.propertyName}",
                "description" : "${exception.description}",
                "clientVersion" : "${clientInfo.clientVersion}",
                "OSVersion" : "${clientInfo.osVersion}",
                "deviceFamily" : "${clientInfo.deviceFamily}",
                "legislation" : "${legislation.name}"
            }
        """.trimIndent()
    }
}

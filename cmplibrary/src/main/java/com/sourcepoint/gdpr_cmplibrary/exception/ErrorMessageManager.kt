package com.sourcepoint.gdpr_cmplibrary.exception

/**
 * Class in charge of building an error message
 */
interface ErrorMessageManager {
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
data class ClientInfo(
    val clientVersion : String,
    val osVersion : String,
    val deviceFamily : String
)

/**
 * Factory method for creating an instance of [ErrorMessageManager]
 */
fun createErrorManager(
    accountId: Int,
    propertyId: Int,
    propertyHref: String,
    clientInfo: ClientInfo,
    legislation : Legislation = Legislation.GDPR
): ErrorMessageManager = ErrorMessageManagerImpl(accountId, propertyId, propertyHref, clientInfo, legislation)

/**
 * Implementation class of [ErrorMessageManager]
 */
private class ErrorMessageManagerImpl(
    val accountId: Int,
    val propertyId: Int,
    val propertyHref: String,
    val clientInfo: ClientInfo,
    val legislation : Legislation = Legislation.GDPR
) : ErrorMessageManager {
    override fun build(exception: ConsentLibExceptionK): String {
        return """
            {
                "code" : "${exception.code.code}",
                "accountId" : "$accountId",
                "propertyHref" : "$propertyHref",
                "propertyId" : "$propertyId",
                "description" : "${exception.description}",
                "clientVersion" : "${clientInfo.clientVersion}",
                "OSVersion" : "${clientInfo.osVersion}",
                "deviceFamily" : "${clientInfo.deviceFamily}",
                "legislation" : "${legislation.name}"
            }
        """.trimIndent()
    }
}
package com.sourcepoint.gdpr_cmplibrary.exception

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
 * Factory method for creating an instance of [ErrorMessageManager]
 */
internal fun createErrorManager(
    accountId : Int,
    propertyId : Int,
    propertyHref : String
): ErrorMessageManager = ErrorMessageManagerImpl(accountId, propertyId, propertyHref)

/**
 * Implementation class of [ErrorMessageManager]
 */
private class ErrorMessageManagerImpl(
    val accountId : Int,
    val propertyId : Int,
    val propertyHref : String
) : ErrorMessageManager {
    override fun build(exception: ConsentLibExceptionK): String {
        return """
            {
                "code" : "${exception.code}",
                "accountId": "$accountId",
                "propertyHref": "$propertyHref",
                "propertyId": "$propertyId",
                "description": "${exception.description}"
            }
        """.trimIndent()
    }
}
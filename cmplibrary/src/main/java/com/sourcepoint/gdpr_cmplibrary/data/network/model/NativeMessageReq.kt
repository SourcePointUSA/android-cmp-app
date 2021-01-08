package com.sourcepoint.gdpr_cmplibrary.data.network.model

data class NativeMessageReq(
    val accountId: Int,
    val propertyId: Int,
    val propertyHref: String,
    val requestUUID: String,
    val meta: String
) {
    fun toBodyRequest(): String {
        return """
            {
                "accountId": $accountId,
                "propertyId": $propertyId,
                "propertyHref": "$propertyHref",
                "requestUUID": "$requestUUID",
                "meta": "$meta"
            }
        """.trimIndent()
    }
}
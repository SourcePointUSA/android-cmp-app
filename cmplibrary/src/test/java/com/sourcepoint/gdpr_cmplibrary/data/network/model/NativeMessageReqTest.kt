package com.sourcepoint.gdpr_cmplibrary.data.network.model

import com.sourcepoint.gdpr_cmplibrary.assertEquals
import org.junit.Test

class NativeMessageReqTest{

    @Test
    fun `GIVEN a NativeMessageReq object CHECK the request body created `(){
        val sut = NativeMessageReq(
            22,
            7639,
            "https://tcfv2.mobile.webview",
            "test",
            "{}"
        )
        sut.toBodyRequest().assertEquals("""
            {
                "accountId": 22,
                "propertyId": 7639,
                "propertyHref": "https://tcfv2.mobile.webview",
                "requestUUID": "test",
                "meta": "{}"
            }
        """.trimIndent())
    }

}
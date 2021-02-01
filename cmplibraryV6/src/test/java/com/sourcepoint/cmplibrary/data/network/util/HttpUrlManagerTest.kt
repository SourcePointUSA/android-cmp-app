package com.sourcepoint.cmplibrary.data.network.util

import com.sourcepoint.cmplibrary.assertEquals
import org.junit.Test

class HttpUrlManagerTest {

    @Test
    fun `GIVEN an HttpUrl unifiedWrapper CHECK the output URL`() {
        val sut = HttpUrlManagerSingleton.inAppUrlMessage
        val url = sut.toString()
        url.assertEquals("http://localhost:3000/wrapper/v1/unified/message?env=localProd&inApp=true")
    }

    @Test
    fun `GIVEN an HttpUrl nativeMessage CHECK the output URL`() {
        val sut = HttpUrlManagerSingleton.inAppUrlNativeMessage
        val url = sut.toString()
        url.assertEquals("https://cdn.privacy-mgmt.com/wrapper/tcfv2/v1/gdpr/native-message?inApp=true")
    }
}

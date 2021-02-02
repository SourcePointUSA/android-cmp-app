package com.sourcepoint.cmplibrary.data.network.util

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.data.network.model.PmUrlConfig
import com.sourcepoint.cmplibrary.legislation.gdpr.PrivacyManagerTabK
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

    @Test
    fun `GIVEN pmId and `() {
        val config = PmUrlConfig(
            consentUUID = "89b2d14b-70ee-4344-8cc2-1b7b281d0f2d",
            pmTab = PrivacyManagerTabK.DEFAULT,
            messageId = "122058",
            siteId = "7639"
        )
        val sut = HttpUrlManagerSingleton.urlPm(config)
        val url = sut.toString()
        val expected = "https://cdn.privacy-mgmt.com/privacy-manager/index.html?consentLanguage=&consentUUID=89b2d14b-70ee-4344-8cc2-1b7b281d0f2d&site_id=7639&message_id=122058"
        url.assertEquals(expected)
    }

    @Test
    fun `GIVEN an OTT and `() {
        val config = PmUrlConfig(
            consentUUID = "3c7c3e22-0aac-4941-b97d-8e70b73b91c7",
            pmTab = PrivacyManagerTabK.DEFAULT,
            messageId = "122058",
            siteId = "7639"
        )
        val sut = HttpUrlManagerSingleton.ottUrlPm(config)
        val url = sut.toString()
        val expected = "https://cdn.privacy-mgmt.com/privacy-manager-ott/index.html?consentLanguage=&consentUUID=3c7c3e22-0aac-4941-b97d-8e70b73b91c7&site_id=7639&message_id=122058"
        url.assertEquals(expected)
    }
}

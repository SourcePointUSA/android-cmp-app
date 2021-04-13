package com.sourcepoint.cmplibrary.data.network.util

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.data.network.model.PmUrlConfig
import com.sourcepoint.cmplibrary.model.PMTab
import org.junit.Test

class HttpUrlManagerTest {

    @Test
    fun `GIVEN an HttpUrl unifiedWrapper CHECK the output URL`() {
        val sut = HttpUrlManagerSingleton.inAppUrlMessageStage
        val url = sut.toString()
        url.assertEquals("https://cdn.sp-stage.net/wrapper/v2/messages?env=stage")
    }

    @Test
    fun `GIVEN an consent HttpUrl CHECK the output URL`() {
        val sut = HttpUrlManagerSingleton.sendLocalGdprConsentUrl
        val url = sut.toString()
        url.assertEquals("http://192.168.1.11:3000/wrapper/tcfv2/v1/gdpr/consent?env=localProd&inApp=true&sdkVersion=AndroidLocal")
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
            pmTab = PMTab.DEFAULT,
            consentLanguage = "EN",
            consentUUID = "89b2d14b-70ee-4344-8cc2-1b7b281d0f2d",
            siteId = "7639",
            messageId = "122058"
        )
        val sut = HttpUrlManagerSingleton.urlPm(config)
        val url = sut.toString()
        val expected = "https://cdn.privacy-mgmt.com/privacy-manager/index.html?consentLanguage=EN&consentUUID=89b2d14b-70ee-4344-8cc2-1b7b281d0f2d&site_id=7639&message_id=122058"
        url.assertEquals(expected)
    }

    @Test
    fun `GIVEN an OTT and `() {
        val config = PmUrlConfig(
            pmTab = PMTab.DEFAULT,
            consentLanguage = "EN",
            consentUUID = "3c7c3e22-0aac-4941-b97d-8e70b73b91c7",
            siteId = "7639",
            messageId = "122058"
        )
        val sut = HttpUrlManagerSingleton.ottUrlPm(config)
        val url = sut.toString()
        val expected = "https://cdn.privacy-mgmt.com/privacy-manager-ott/index.html?consentLanguage=EN&consentUUID=3c7c3e22-0aac-4941-b97d-8e70b73b91c7&site_id=7639&message_id=122058"
        url.assertEquals(expected)
    }
}

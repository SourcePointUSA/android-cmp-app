package com.sourcepoint.cmplibrary.data.network.util

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertNull
import com.sourcepoint.cmplibrary.assertTrue
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.PmUrlConfig
import org.junit.Test

class HttpUrlManagerTest {

    @Test
    fun `GIVEN a pmConfig RETURN the GDPR URL`() {
        val pmConfig = PmUrlConfig(
            pmTab = PMTab.PURPOSES,
            consentLanguage = "EN",
            uuid = "uuid",
            messageId = "111",
            siteId = "000"
        )
        val sut = HttpUrlManagerSingleton.pmUrl(Env.STAGE, CampaignType.GDPR, pmConfig)
        sut.run {
            toString().contains("notice.sp-stage.net").assertTrue()
            queryParameter("pmTab").assertEquals("purposes")
            queryParameter("message_id").assertEquals("111")
            queryParameter("consentLanguage").assertEquals("EN")
            queryParameter("consentUUID").assertEquals("uuid")
            queryParameter("site_id").assertEquals("000")
        }
    }

    @Test
    fun `GIVEN a pmConfig RETURN the GDPR URL 2`() {
        val pmConfig = PmUrlConfig(
            pmTab = PMTab.FEATURES,
            consentLanguage = null,
            uuid = null,
            messageId = null,
            siteId = null
        )
        val sut = HttpUrlManagerSingleton.pmUrl(Env.STAGE, CampaignType.GDPR, pmConfig)
        sut.run {
            toString().contains("notice.sp-stage.net").assertTrue()
            queryParameter("pmTab").assertEquals("features")
            queryParameter("message_id").assertNull()
            queryParameter("consentLanguage").assertNull()
            queryParameter("consentUUID").assertNull()
            queryParameter("site_id").assertNull()
        }
    }

    @Test
    fun `GIVEN a pmConfig RETURN the CCPA URL`() {
        val pmConfig = PmUrlConfig(
            pmTab = null,
            consentLanguage = null,
            uuid = "uuid",
            messageId = "111",
            siteId = null
        )
        val sut = HttpUrlManagerSingleton.pmUrl(Env.STAGE, CampaignType.CCPA, pmConfig)
        sut.run {
            toString().contains("ccpa-notice.sp-stage.net").assertTrue()
            queryParameter("message_id").assertEquals("111")
            queryParameter("ccpaUUID").assertEquals("uuid")
        }
    }

    @Test
    fun `GIVEN a STAGE env RETURN the stage link`() {
        val sut = HttpUrlManagerSingleton.inAppMessageUrl(Env.STAGE).toString()
        sut.assertEquals("https://cdn.sp-stage.net/wrapper/v2/get_messages?env=stage")
    }

    @Test
    fun `GIVEN a PROD env RETURN the prod link`() {
        val sut = HttpUrlManagerSingleton.inAppMessageUrl(Env.PROD).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/wrapper/v2/get_messages?env=localProd")
    }

    @Test
    fun `GIVEN an OTT and `() {
        val config = PmUrlConfig(
            pmTab = PMTab.DEFAULT,
            consentLanguage = "EN",
            uuid = "3c7c3e22-0aac-4941-b97d-8e70b73b91c7",
            siteId = "7639",
            messageId = "122058"
        )
        val sut = HttpUrlManagerSingleton.ottUrlPm(config, Env.PROD)
        val url = sut.toString()
        val expected = "https://cdn.privacy-mgmt.com/privacy-manager-ott/index.html?consentLanguage=EN&consentUUID=3c7c3e22-0aac-4941-b97d-8e70b73b91c7&site_id=7639&message_id=122058"
        url.assertEquals(expected)
    }
}

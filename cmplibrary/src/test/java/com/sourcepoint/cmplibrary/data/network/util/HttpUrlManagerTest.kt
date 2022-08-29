package com.sourcepoint.cmplibrary.data.network.util

import com.example.cmplibrary.BuildConfig
import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertNull
import com.sourcepoint.cmplibrary.assertTrue
import com.sourcepoint.cmplibrary.data.network.model.v7.MetaDataParamReq
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.PmUrlConfig
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.MessageSubCategory.* //ktlint-disable
import org.json.JSONObject
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
        val sut = HttpUrlManagerSingleton.pmUrl(Env.STAGE, CampaignType.GDPR, pmConfig, TCFv2)
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
        val sut = HttpUrlManagerSingleton.pmUrl(Env.STAGE, CampaignType.GDPR, pmConfig, TCFv2)
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
    fun `GIVEN a NATIVE_OTT sub cat RETURN a Native OTT GDPR URL`() {
        val pmConfig = PmUrlConfig(
            pmTab = PMTab.FEATURES,
            consentLanguage = null,
            uuid = null,
            messageId = null,
            siteId = null
        )
        val sut = HttpUrlManagerSingleton.pmUrl(Env.STAGE, CampaignType.GDPR, pmConfig, NATIVE_OTT)
        sut.run {
            toString().contains("native-ott/index.html").assertTrue()
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
        val sut = HttpUrlManagerSingleton.pmUrl(Env.STAGE, CampaignType.CCPA, pmConfig, TCFv2)
        sut.run {
            toString().contains("ccpa-notice.sp-stage.net").assertTrue()
            queryParameter("message_id").assertEquals("111")
            queryParameter("ccpaUUID").assertEquals("uuid")
            queryParameter("site_id").assertNull()
        }
    }

    @Test
    fun `GIVEN a STAGE env inAppMessageUrl RETURN the stage link`() {
        val sut = HttpUrlManagerSingleton.inAppMessageUrl(Env.STAGE).toString()
        sut.assertEquals("https://cdn.sp-stage.net/wrapper/v2/get_messages?env=${BuildConfig.ENV_QUERY_PARAM}")
    }

    @Test
    fun `GIVEN a PROD env inAppMessageUrl RETURN the prod link`() {
        val sut = HttpUrlManagerSingleton.inAppMessageUrl(Env.PROD).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/wrapper/v2/get_messages?env=prod")
    }

    @Test
    fun `GIVEN a STAGE env sendConsentUrl RETURN the prod link`() {
        val sut = HttpUrlManagerSingleton.sendConsentUrl(ActionType.ACCEPT_ALL, Env.STAGE, CampaignType.GDPR).toString()
        sut.assertEquals("https://cdn.sp-stage.net/wrapper/v2/messages/choice/gdpr/11?env=${BuildConfig.ENV_QUERY_PARAM}")
    }

    @Test
    fun `GIVEN a PROD env sendConsentUrl RETURN the prod link`() {
        val sut = HttpUrlManagerSingleton.sendConsentUrl(ActionType.REJECT_ALL, Env.PROD, CampaignType.CCPA).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/wrapper/v2/messages/choice/ccpa/13?env=prod")
    }

    @Test
    fun `GIVEN a STAGE env sendCustomConsentUrl RETURN the prod link`() {
        val sut = HttpUrlManagerSingleton.sendCustomConsentUrl(Env.STAGE).toString()
        sut.assertEquals("https://cdn.sp-stage.net/wrapper/tcfv2/v1/gdpr/custom-consent?env=${BuildConfig.ENV_QUERY_PARAM}&inApp=true")
    }

    @Test
    fun `GIVEN a PROD env sendCustomConsentUrl RETURN the prod link`() {
        val sut = HttpUrlManagerSingleton.sendCustomConsentUrl(Env.PROD).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/wrapper/tcfv2/v1/gdpr/custom-consent?env=prod&inApp=true")
    }

    @Test
    fun `GIVEN a GDPR OTT property RETURN the ott link`() {
        val config = PmUrlConfig(
            pmTab = null,
            consentLanguage = null,
            uuid = "uuid",
            messageId = "111",
            siteId = null
        )
        val sut = HttpUrlManagerSingleton.pmUrl(Env.PROD, CampaignType.GDPR, config, messSubCat = OTT).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/privacy-manager-ott/index.html?pmTab&site_id&consentUUID=uuid&message_id=111")
    }

    @Test
    fun `GIVEN a GDPR property RETURN the correct pm link`() {
        val config = PmUrlConfig(
            pmTab = null,
            consentLanguage = null,
            uuid = "uuid",
            messageId = "111",
            siteId = null
        )
        val sut = HttpUrlManagerSingleton.pmUrl(Env.PROD, CampaignType.GDPR, config, messSubCat = TCFv2).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/privacy-manager/index.html?pmTab&site_id&consentUUID=uuid&message_id=111")
    }

    @Test
    fun `GIVEN a GDPR NATIVE OTT property RETURN the correct pm link`() {
        val config = PmUrlConfig(
            pmTab = null,
            consentLanguage = null,
            uuid = "uuid",
            messageId = "111",
            siteId = null
        )
        val sut = HttpUrlManagerSingleton.pmUrl(Env.PROD, CampaignType.GDPR, config, messSubCat = NATIVE_OTT).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/native-ott/index.html?pmTab&site_id&consentUUID=uuid&message_id=111")
    }

    @Test
    fun `GIVEN a GDPR OTT property RETURN the correct pm link`() {
        val config = PmUrlConfig(
            pmTab = null,
            consentLanguage = null,
            uuid = "uuid",
            messageId = "111",
            siteId = null
        )
        val sut = HttpUrlManagerSingleton.pmUrl(Env.PROD, CampaignType.GDPR, config, messSubCat = OTT).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/privacy-manager-ott/index.html?pmTab&site_id&consentUUID=uuid&message_id=111")
    }

    @Test
    fun `GIVEN a CCPA property RETURN the correct pm link`() {
        val config = PmUrlConfig(
            pmTab = null,
            consentLanguage = null,
            uuid = "uuid",
            messageId = "111",
            siteId = null
        )
        val sut = HttpUrlManagerSingleton.pmUrl(Env.PROD, CampaignType.CCPA, config, messSubCat = OTT).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/ccpa_ott/index.html?site_id&ccpaUUID=uuid&message_id=111")
    }

    @Test
    fun `GIVEN a CCPA OTT property RETURN the ott link`() {
        val config = PmUrlConfig(
            pmTab = null,
            consentLanguage = null,
            uuid = "uuid",
            messageId = "111",
            siteId = null
        )
        val sut = HttpUrlManagerSingleton.pmUrl(Env.PROD, CampaignType.CCPA, config, messSubCat = TCFv2).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/ccpa_pm/index.html?site_id&ccpaUUID=uuid&message_id=111")
    }

    @Test
    fun `GIVEN a PROD env getMetaData RETURN the prod link`() {
        val param = MetaDataParamReq(
            accountId = 22,
            env = Env.PROD,
            metadata = JSONObject("""{"gdpr": {}, "ccpa": {}}""").toString(),
            propertyId = 17801
        )
        val sut = HttpUrlManagerSingleton.getMetaDataUrl(param).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/wrapper/v2/meta-data?env=prod&accountId=22&propertyId=17801&metadata={%22gdpr%22:{},%22ccpa%22:{}}")
    }
}

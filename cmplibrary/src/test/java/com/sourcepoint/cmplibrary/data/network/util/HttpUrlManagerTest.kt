package com.sourcepoint.cmplibrary.data.network.util

import com.example.cmplibrary.BuildConfig
import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertNull
import com.sourcepoint.cmplibrary.assertTrue
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.model.v7.* // ktlint-disable
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.PmUrlConfig
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.MessageSubCategory.* //ktlint-disable
import com.sourcepoint.cmplibrary.util.file2String
import kotlinx.serialization.decodeFromString
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

    @Test
    fun `GIVEN a PROD env getConsentStatus RETURN the prod link`() {
        val param = ConsentStatusParamReq(
            accountId = 22,
            env = Env.PROD,
            metadata = JSONObject("""{"ccpa":{"applies":true}, "gdpr":{"applies":true, "uuid": "e47e539d-41dd-442b-bb08-5cf52b1e33d4", "hasLocalData": false}}""").toString(),
            propertyId = 17801,
            authId = "user_auth_id"
        )
        val sut = HttpUrlManagerSingleton.getConsentStatusUrl(param).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/wrapper/v2/consent-status?env=prod&accountId=22&propertyId=17801&hasCsp=true&withSiteActions=false&authId=user_auth_id&metadata={%22ccpa%22:{%22applies%22:true},%22gdpr%22:{%22applies%22:true,%22uuid%22:%22e47e539d-41dd-442b-bb08-5cf52b1e33d4%22,%22hasLocalData%22:false}}")
    }

    @Test
    fun `GIVEN a PROD env getConsentStatus with authId NULL RETURN the prod link`() {
        val param = ConsentStatusParamReq(
            accountId = 22,
            env = Env.PROD,
            metadata = JSONObject("""{"ccpa":{"applies":true}, "gdpr":{"applies":true, "uuid": "e47e539d-41dd-442b-bb08-5cf52b1e33d4", "hasLocalData": false}}""").toString(),
            propertyId = 17801,
        )
        val sut = HttpUrlManagerSingleton.getConsentStatusUrl(param).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/wrapper/v2/consent-status?env=prod&accountId=22&propertyId=17801&hasCsp=true&withSiteActions=false&metadata={%22ccpa%22:{%22applies%22:true},%22gdpr%22:{%22applies%22:true,%22uuid%22:%22e47e539d-41dd-442b-bb08-5cf52b1e33d4%22,%22hasLocalData%22:false}}")
    }

    @Test
    fun `GIVEN a PROD env getPvData RETURN the prod link`() {
        val sut = HttpUrlManagerSingleton.getPvDataUrl(Env.PROD).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/wrapper/v2/pv-data?env=prod")
    }

    @Test
    fun `GIVEN a PROD env getMessages RETURN the prod link`() {

        val json = "v7/message_body_cs.json".file2String()
        val cs = JsonConverter.converter.decodeFromString<ConsentStatusResp>(json)

        val body = getMessageBody(
            accountId = 22,
            cs = cs,
            propertyHref = "tests.unified-script.com"
        )

        val param = MessagesParamReq(
            env = Env.PROD,
            nonKeyedLocalState = """{"gdpr":{"_sp_v1_uid":null,"_sp_v1_data":null},"ccpa":{"_sp_v1_uid":null,"_sp_v1_data":null}}""",
            body = body.toString(),
            metadata = """{"ccpa":{"applies":true},"gdpr":{"applies":true}}""",
        )
        val sut = HttpUrlManagerSingleton.getMessagesUrl(param).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/wrapper/v2/messages?env=prod&nonKeyedLocalState={%22gdpr%22:{%22_sp_v1_uid%22:null,%22_sp_v1_data%22:null},%22ccpa%22:{%22_sp_v1_uid%22:null,%22_sp_v1_data%22:null}}&body={%22accountId%22:22,%22includeData%22:{%22TCData%22:{%22type%22:%22RecordString%22},%22campaigns%22:{%22type%22:%22RecordString%22}},%22propertyHref%22:%22https://tests.unified-script.com%22,%22hasCSP%22:true,%22campaigns%22:{%22ccpa%22:{%22hasLocalData%22:false,%22consentStatus%22:{%22hasConsentData%22:false,%22consentedToAll%22:true,%22consentedToAny%22:false,%22rejectedAny%22:false}},%22gdpr%22:{%22hasLocalData%22:false,%22consentStatus%22:{%22hasConsentData%22:false,%22consentedToAll%22:true,%22consentedToAny%22:false,%22rejectedAny%22:false}}}}&metadata={%22ccpa%22:{%22applies%22:true},%22gdpr%22:{%22applies%22:true}}")
    }
}

package com.sourcepoint.cmplibrary.data.network.util

import com.example.cmplibrary.BuildConfig
import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertFalse
import com.sourcepoint.cmplibrary.assertNull
import com.sourcepoint.cmplibrary.assertTrue
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.model.optimized.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.optimized.includeData.IncludeDataGppParam
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.CampaignReqImpl
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.PmUrlConfig
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.MessageType.* // ktlint-disable
import com.sourcepoint.cmplibrary.util.file2String
import kotlinx.serialization.decodeFromString
import org.json.JSONObject
import org.junit.Test

class HttpUrlManagerTest {

    private val initialPmConfig = PmUrlConfig(
        pmTab = PMTab.PURPOSES,
        consentLanguage = "EN",
        uuid = "uuid",
        messageId = "111",
        siteId = "000"
    )

    @Test
    fun `GIVEN a DEFAULT(MOBILE), CCPA config VERIFY the correct url query parameters`() {
        val sut = HttpUrlManagerSingleton.pmUrl(Env.PROD, CampaignType.CCPA, initialPmConfig, MOBILE,)
        sut.run {
            toString().contains("cdn.privacy-mgmt.com").assertTrue()
            queryParameter("site_id").assertEquals("000")
            queryParameter("is_ccpa").assertEquals("true")
            queryParameter("consentLanguage").assertEquals("EN")
            queryParameter("ccpaUUID").assertEquals("uuid")
            queryParameter("message_id").assertEquals("111")
            queryParameter("scriptVersion").assertEquals(BuildConfig.VERSION_NAME)

            pathSegments.contains("ccpa_pm").assertTrue()
            pathSegments.contains("native-ott").assertFalse()
            pathSegments.contains("ccpa_ott").assertFalse()
        }
    }

    @Test
    fun `GIVEN a DEFAULT(MOBILE), GDPR config VERIFY the correct url query parameters`() {
        val sut = HttpUrlManagerSingleton.pmUrl(Env.PROD, CampaignType.GDPR, initialPmConfig, MOBILE,)
        sut.run {
            toString().contains("cdn.privacy-mgmt.com").assertTrue()
            queryParameter("pmTab").assertEquals("purposes")
            queryParameter("site_id").assertEquals("000")
            queryParameter("preload_consent").assertEquals("true")
            queryParameter("consentLanguage").assertEquals("EN")
            queryParameter("consentUUID").assertEquals("uuid")
            queryParameter("message_id").assertEquals("111")
            queryParameter("scriptVersion").assertEquals(BuildConfig.VERSION_NAME)

            pathSegments.contains("privacy-manager").assertTrue()
            pathSegments.contains("privacy-manager-ott").assertFalse()
            pathSegments.contains("native-ott").assertFalse()
        }
    }

    @Test
    fun `GIVEN an OTT, CCPA config VERIFY the correct url query parameters`() {
        val sut = HttpUrlManagerSingleton.pmUrl(Env.PROD, CampaignType.CCPA, initialPmConfig, OTT,)
        sut.run {
            toString().contains("cdn.privacy-mgmt.com").assertTrue()
            queryParameter("site_id").assertEquals("000")
            queryParameter("is_ccpa").assertEquals("true")
            queryParameter("consentLanguage").assertEquals("EN")
            queryParameter("ccpaUUID").assertEquals("uuid")
            queryParameter("message_id").assertEquals("111")
            queryParameter("scriptVersion").assertEquals(BuildConfig.VERSION_NAME)

            pathSegments.contains("native-ott").assertTrue()
            pathSegments.contains("ccpa_pm").assertFalse()
            pathSegments.contains("ccpa_ott").assertFalse()
        }
    }

    @Test
    fun `GIVEN an OTT, GDPR config VERIFY the correct url query parameters`() {
        val sut = HttpUrlManagerSingleton.pmUrl(Env.PROD, CampaignType.GDPR, initialPmConfig, OTT,)
        sut.run {
            toString().contains("cdn.privacy-mgmt.com").assertTrue()
            queryParameter("pmTab").assertEquals("purposes")
            queryParameter("site_id").assertEquals("000")
            queryParameter("preload_consent").assertEquals("true")
            queryParameter("consentLanguage").assertEquals("EN")
            queryParameter("consentUUID").assertEquals("uuid")
            queryParameter("message_id").assertEquals("111")
            queryParameter("scriptVersion").assertEquals(BuildConfig.VERSION_NAME)

            pathSegments.contains("native-ott").assertTrue()
            pathSegments.contains("privacy-manager").assertFalse()
            pathSegments.contains("privacy-manager-ott").assertFalse()
        }
    }

    @Test
    fun `GIVEN a LEGACY_OTT, CCPA config VERIFY the correct url query parameters`() {
        val sut = HttpUrlManagerSingleton.pmUrl(Env.PROD, CampaignType.CCPA, initialPmConfig, LEGACY_OTT,)
        sut.run {
            toString().contains("cdn.privacy-mgmt.com").assertTrue()
            queryParameter("site_id").assertEquals("000")
            queryParameter("is_ccpa").assertEquals("true")
            queryParameter("consentLanguage").assertEquals("EN")
            queryParameter("ccpaUUID").assertEquals("uuid")
            queryParameter("message_id").assertEquals("111")
            queryParameter("scriptVersion").assertEquals(BuildConfig.VERSION_NAME)

            pathSegments.contains("ccpa_ott").assertTrue()
            pathSegments.contains("ccpa_pm").assertFalse()
            pathSegments.contains("native-ott").assertFalse()
        }
    }

    @Test
    fun `GIVEN a LEGACY_OTT, GDPR config VERIFY the correct url query parameters`() {
        val sut = HttpUrlManagerSingleton.pmUrl(Env.PROD, CampaignType.GDPR, initialPmConfig, LEGACY_OTT,)
        sut.run {
            toString().contains("cdn.privacy-mgmt.com").assertTrue()
            queryParameter("pmTab").assertEquals("purposes")
            queryParameter("site_id").assertEquals("000")
            queryParameter("preload_consent").assertEquals("true")
            queryParameter("consentLanguage").assertEquals("EN")
            queryParameter("consentUUID").assertEquals("uuid")
            queryParameter("message_id").assertEquals("111")
            queryParameter("scriptVersion").assertEquals(BuildConfig.VERSION_NAME)

            pathSegments.contains("privacy-manager-ott").assertTrue()
            pathSegments.contains("native-ott").assertFalse()
            pathSegments.contains("privacy-manager").assertFalse()
        }
    }

    @Test
    fun `GIVEN a pmConfig RETURN the GDPR URL`() {
        val pmConfig = PmUrlConfig(
            pmTab = PMTab.PURPOSES,
            consentLanguage = "EN",
            uuid = "uuid",
            messageId = "111",
            siteId = "000"
        )
        val sut = HttpUrlManagerSingleton.pmUrl(Env.STAGE, CampaignType.GDPR, pmConfig, MOBILE,)
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
        val sut = HttpUrlManagerSingleton.pmUrl(Env.STAGE, CampaignType.GDPR, pmConfig, MOBILE,)
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
    fun `GIVEN a OTT sub cat RETURN a Native OTT GDPR URL`() {
        val pmConfig = PmUrlConfig(
            pmTab = PMTab.FEATURES,
            consentLanguage = null,
            uuid = null,
            messageId = null,
            siteId = null
        )
        val sut = HttpUrlManagerSingleton.pmUrl(Env.STAGE, CampaignType.GDPR, pmConfig, OTT,)
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
        val sut = HttpUrlManagerSingleton.pmUrl(Env.STAGE, CampaignType.CCPA, pmConfig, MOBILE,)
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
        sut.assertEquals("https://cdn.sp-stage.net/wrapper/v2/get_messages?env=stage")
    }

    @Test
    fun `GIVEN a PROD env inAppMessageUrl RETURN the prod link`() {
        val sut = HttpUrlManagerSingleton.inAppMessageUrl(Env.PROD).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/wrapper/v2/get_messages?env=prod")
    }

    @Test
    fun `GIVEN a STAGE env sendCustomConsentUrl RETURN the prod link`() {
        val sut = HttpUrlManagerSingleton.sendCustomConsentUrl(Env.STAGE).toString()
        sut.assertEquals("https://cdn.sp-stage.net/wrapper/tcfv2/v1/gdpr/custom-consent?env=stage&inApp=true&scriptType=android&scriptVersion=${BuildConfig.VERSION_NAME}")
    }

    @Test
    fun `GIVEN a PROD env sendCustomConsentUrl RETURN the prod link`() {
        val sut = HttpUrlManagerSingleton.sendCustomConsentUrl(Env.PROD).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/wrapper/tcfv2/v1/gdpr/custom-consent?env=prod&inApp=true&scriptType=android&scriptVersion=${BuildConfig.VERSION_NAME}")
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
        val sut = HttpUrlManagerSingleton.pmUrl(Env.PROD, CampaignType.GDPR, config, messageType = LEGACY_OTT,).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/privacy-manager-ott/index.html?pmTab&site_id&preload_consent=true&consentUUID=uuid&message_id=111&scriptType=android&scriptVersion=${BuildConfig.VERSION_NAME}")
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
        val sut = HttpUrlManagerSingleton.pmUrl(Env.PROD, CampaignType.GDPR, config, messageType = MOBILE,).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/privacy-manager/index.html?pmTab&site_id&preload_consent=true&consentUUID=uuid&message_id=111&scriptType=android&scriptVersion=${BuildConfig.VERSION_NAME}")
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
        val sut = HttpUrlManagerSingleton.pmUrl(Env.PROD, CampaignType.GDPR, config, messageType = OTT,).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/native-ott/index.html?pmTab&site_id&preload_consent=true&consentUUID=uuid&message_id=111&scriptType=android&scriptVersion=${BuildConfig.VERSION_NAME}")
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
        val sut = HttpUrlManagerSingleton.pmUrl(Env.PROD, CampaignType.GDPR, config, messageType = LEGACY_OTT,).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/privacy-manager-ott/index.html?pmTab&site_id&preload_consent=true&consentUUID=uuid&message_id=111&scriptType=android&scriptVersion=${BuildConfig.VERSION_NAME}")
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
        val sut = HttpUrlManagerSingleton.pmUrl(Env.PROD, CampaignType.CCPA, config, messageType = LEGACY_OTT,).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/ccpa_ott/index.html?site_id&preload_consent=true&is_ccpa=true&ccpaUUID=uuid&message_id=111&scriptType=android&scriptVersion=${BuildConfig.VERSION_NAME}")
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
        val sut = HttpUrlManagerSingleton.pmUrl(Env.PROD, CampaignType.CCPA, config, messageType = LEGACY_OTT,).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/ccpa_ott/index.html?site_id&preload_consent=true&is_ccpa=true&ccpaUUID=uuid&message_id=111&scriptType=android&scriptVersion=${BuildConfig.VERSION_NAME}")
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
        sut.assertEquals("https://cdn.privacy-mgmt.com/wrapper/v2/meta-data?env=prod&accountId=22&propertyId=17801&metadata={%22gdpr%22:{},%22ccpa%22:{}}&scriptType=android&scriptVersion=${BuildConfig.VERSION_NAME}")
    }

    @Test
    fun `GIVEN a PROD env getConsentStatus RETURN the prod link`() {
        val param = ConsentStatusParamReq(
            accountId = 22,
            env = Env.PROD,
            metadata = JSONObject("""{"ccpa":{"applies":true}, "gdpr":{"applies":true, "uuid": "e47e539d-41dd-442b-bb08-5cf52b1e33d4", "hasLocalData": false}}""").toString(),
            propertyId = 17801,
            authId = "user_auth_id",
            localState = null
        )
        val sut = HttpUrlManagerSingleton.getConsentStatusUrl(param).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/wrapper/v2/consent-status?env=prod&accountId=22&propertyId=17801&hasCsp=true&withSiteActions=false&includeData=%7B%22TCData%22%3A%20%7B%22type%22%3A%20%22RecordString%22%7D%2C%20%22webConsentPayload%22%3A%20%7B%22type%22%3A%20%22RecordString%22%7D%7D&authId=user_auth_id&metadata={%22ccpa%22:{%22applies%22:true},%22gdpr%22:{%22applies%22:true,%22uuid%22:%22e47e539d-41dd-442b-bb08-5cf52b1e33d4%22,%22hasLocalData%22:false}}&scriptType=android&scriptVersion=${BuildConfig.VERSION_NAME}")
    }

    @Test
    fun `GIVEN a PROD env getGdprChoiceUrl RETURN the prod link`() {
        val param = PostChoiceParamReq(
            env = Env.PROD,
            actionType = ActionType.ACCEPT_ALL
        )
        val sut = HttpUrlManagerSingleton.getGdprChoiceUrl(param).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/wrapper/v2/choice/gdpr/11?env=prod&hasCsp=true&scriptType=android&scriptVersion=${BuildConfig.VERSION_NAME}")
    }

    @Test
    fun `GIVEN a PROD env getCcpaChoiceUrl RETURN the prod link`() {
        val param = PostChoiceParamReq(
            env = Env.PROD,
            actionType = ActionType.ACCEPT_ALL
        )
        val sut = HttpUrlManagerSingleton.getCcpaChoiceUrl(param).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/wrapper/v2/choice/ccpa/11?env=prod&hasCsp=true&scriptType=android&scriptVersion=${BuildConfig.VERSION_NAME}")
    }

    @Test
    fun `GIVEN a PROD env getConsentStatus with authId NULL RETURN the prod link`() {
        val param = ConsentStatusParamReq(
            accountId = 22,
            env = Env.PROD,
            metadata = JSONObject("""{"ccpa":{"applies":true}, "gdpr":{"applies":true, "uuid": "e47e539d-41dd-442b-bb08-5cf52b1e33d4", "hasLocalData": false}}""").toString(),
            propertyId = 17801,
            authId = null,
            localState = null
        )
        val sut = HttpUrlManagerSingleton.getConsentStatusUrl(param).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/wrapper/v2/consent-status?env=prod&accountId=22&propertyId=17801&hasCsp=true&withSiteActions=false&includeData=%7B%22TCData%22%3A%20%7B%22type%22%3A%20%22RecordString%22%7D%2C%20%22webConsentPayload%22%3A%20%7B%22type%22%3A%20%22RecordString%22%7D%7D&metadata={%22ccpa%22:{%22applies%22:true},%22gdpr%22:{%22applies%22:true,%22uuid%22:%22e47e539d-41dd-442b-bb08-5cf52b1e33d4%22,%22hasLocalData%22:false}}&scriptType=android&scriptVersion=${BuildConfig.VERSION_NAME}")
    }

    @Test
    fun `GIVEN a PROD env getPvData RETURN the prod link`() {
        val sut = HttpUrlManagerSingleton.getPvDataUrl(Env.PROD).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/wrapper/v2/pv-data?env=prod&scriptType=android&scriptVersion=${BuildConfig.VERSION_NAME}")
    }

    @Test
    fun `GIVEN a PROD env getMessages RETURN the prod link`() {

        val json = "v7/message_body_cs.json".file2String()
        val cs = JsonConverter.converter.decodeFromString<ConsentStatusResp>(json)

        val list = listOf(
            CampaignReqImpl(
                targetingParams = emptyList(),
                campaignsEnv = CampaignsEnv.PUBLIC,
                campaignType = CampaignType.GDPR,
                groupPmId = null
            )
        )

        val body = getMessageBody(
            accountId = 22,
            gdprConsentStatus = cs.consentStatusData?.gdpr?.consentStatus,
            propertyHref = "tests.unified-script.com",
            campaigns = list,
            ccpaConsentStatus = null,
            consentLanguage = "ES",
            campaignEnv = CampaignsEnv.STAGE,
            includeDataGppParam = IncludeDataGppParam(),
            usNatConsentStatus = null
        )

        val param = MessagesParamReq(
            env = Env.PROD,
            body = body.toString(),
            metadataArg = JsonConverter.converter.decodeFromString("""{"ccpa":{"applies":true},"gdpr":{"applies":true}}"""),
            authId = null,
            accountId = 1212,
            propertyId = 12,
            propertyHref = "asdfasdfasd"
        )
        val sut = HttpUrlManagerSingleton.getMessagesUrl(param)
        sut.run {
            toString().contains("cdn.privacy-mgmt.com").assertTrue()
            queryParameter("env").assertEquals("prod")
            queryParameter("nonKeyedLocalState").assertEquals("{}")
            queryParameter("metadata").assertEquals("""{  "ccpa": {    "applies": true  },  "gdpr": {    "applies": true  }}""")
            queryParameter("scriptVersion").assertEquals(BuildConfig.VERSION_NAME)
            queryParameter("scriptType").assertEquals("android")
            queryParameter("body").assertEquals("""{"accountId":22,"campaignEnv":"stage","includeData":{"TCData":{"type":"RecordString"},"campaigns":{"type":"RecordString"},"webConsentPayload":{"type":"RecordString"},"GPPData":{}},"propertyHref":"https://tests.unified-script.com","hasCSP":true,"campaigns":{"gdpr":{"consentStatus":{"consentedAll":true,"consentedToAny":false,"granularStatus":{"defaultConsent":false,"previousOptInAll":false,"purposeConsent":"ALL","purposeLegInt":"ALL","vendorConsent":"ALL","vendorLegInt":"ALL"},"hasConsentData":false,"rejectedAny":false,"rejectedLI":false},"hasLocalData":true,"targetingParams":{}}},"consentLanguage":"ES","os":{"name":"android","version":"0"}}""")
        }
    }
}

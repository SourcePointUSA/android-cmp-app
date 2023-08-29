package com.sourcepoint.cmplibrary.data.network.util

import com.example.cmplibrary.BuildConfig
import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertNull
import com.sourcepoint.cmplibrary.assertTrue
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.model.optimized.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.ChoiceMetaData
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.ChoiceMetaDataArg
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.ChoiceTypeParam
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.GetChoiceParamReq
import com.sourcepoint.cmplibrary.data.network.model.optimized.ConsentStatusParamReq
import com.sourcepoint.cmplibrary.data.network.model.optimized.ConsentStatusResp
import com.sourcepoint.cmplibrary.data.network.model.optimized.includeData.IncludeData
import com.sourcepoint.cmplibrary.data.network.model.optimized.messages.MessagesBodyReq
import com.sourcepoint.cmplibrary.data.network.model.optimized.messages.OperatingSystemInfoParam
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.gpp.dto.GppData
import com.sourcepoint.cmplibrary.gpp.utils.toIncludeDataGppParam
import com.sourcepoint.cmplibrary.model.CampaignReqImpl
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.MessageSubCategory.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.PmUrlConfig
import com.sourcepoint.cmplibrary.util.file2String
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
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
    fun `GIVEN a STAGE env sendCustomConsentUrl RETURN the prod link`() {
        val sut = HttpUrlManagerSingleton.sendCustomConsentUrl(Env.STAGE).toString()
        sut.assertEquals("https://cdn.sp-stage.net/wrapper/tcfv2/v1/gdpr/custom-consent?env=${BuildConfig.ENV_QUERY_PARAM}&inApp=true&scriptType=android&scriptVersion=${BuildConfig.VERSION_NAME}")
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
        val sut = HttpUrlManagerSingleton.pmUrl(Env.PROD, CampaignType.GDPR, config, messSubCat = OTT).toString()
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
        val sut = HttpUrlManagerSingleton.pmUrl(Env.PROD, CampaignType.GDPR, config, messSubCat = TCFv2).toString()
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
        val sut = HttpUrlManagerSingleton.pmUrl(Env.PROD, CampaignType.GDPR, config, messSubCat = NATIVE_OTT).toString()
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
        val sut = HttpUrlManagerSingleton.pmUrl(Env.PROD, CampaignType.GDPR, config, messSubCat = OTT).toString()
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
        val sut = HttpUrlManagerSingleton.pmUrl(Env.PROD, CampaignType.CCPA, config, messSubCat = OTT).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/ccpa_ott/index.html?site_id&preload_consent=true&ccpaUUID=uuid&message_id=111&scriptType=android&scriptVersion=${BuildConfig.VERSION_NAME}")
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
        sut.assertEquals("https://cdn.privacy-mgmt.com/ccpa_pm/index.html?site_id&preload_consent=true&ccpaUUID=uuid&message_id=111&scriptType=android&scriptVersion=${BuildConfig.VERSION_NAME}")
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
            localState = null,
            hasCsp = false,
            withSiteActions = false,
            includeData = IncludeData.generateIncludeDataForConsentStatus(),
        )
        val sut = HttpUrlManagerSingleton.getConsentStatusUrl(param).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/wrapper/v2/consent-status?env=prod&accountId=22&propertyId=17801&hasCsp=false&withSiteActions=false&includeData=%7B%0A%20%20%22TCData%22%3A%20%7B%0A%20%20%20%20%22type%22%3A%20%22RecordString%22%0A%20%20%7D%2C%0A%20%20%22webConsentPayload%22%3A%20%7B%0A%20%20%20%20%22type%22%3A%20%22RecordString%22%0A%20%20%7D%2C%0A%20%20%22GPPData%22%3A%20%7B%0A%20%20%20%20%22MspaCoveredTransaction%22%3A%20%22no%22%2C%0A%20%20%20%20%22MspaOptOutOptionMode%22%3A%20%22na%22%2C%0A%20%20%20%20%22MspaServiceProviderMode%22%3A%20%22na%22%0A%20%20%7D%0A%7D&authId=user_auth_id&metadata={%22ccpa%22:{%22applies%22:true},%22gdpr%22:{%22applies%22:true,%22uuid%22:%22e47e539d-41dd-442b-bb08-5cf52b1e33d4%22,%22hasLocalData%22:false}}&scriptType=android&scriptVersion=${BuildConfig.VERSION_NAME}")
    }

    @Test
    fun `GIVEN a PROD env getChoiceUrl RETURN the prod link`() {

        val choiceMetaData = ChoiceMetaData(
            ccpa = ChoiceMetaDataArg(
                applies = true,
            ),
            gdpr = ChoiceMetaDataArg(
                applies = true,
            ),
        )

        val param = GetChoiceParamReq(
            choiceType = ChoiceTypeParam.CONSENT_ALL,
            accountId = 22,
            propertyId = 17801,
            env = Env.PROD,
            metadataArg = choiceMetaData,
            includeData = IncludeData.generateIncludeDataForGetChoice(),
            hasCsp = true,
            includeCustomVendorsRes = false,
            withSiteActions = false,
        )
        val sut = HttpUrlManagerSingleton.getChoiceUrl(param).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/wrapper/v2/choice/consent-all?env=prod&accountId=22&propertyId=17801&hasCsp=true&withSiteActions=false&includeCustomVendorsRes=false&metadata={%20%20%22ccpa%22:%20{%20%20%20%20%22applies%22:%20true%20%20},%20%20%22gdpr%22:%20{%20%20%20%20%22applies%22:%20true%20%20}}&includeData=%7B%0A%20%20%22TCData%22%3A%20%7B%0A%20%20%20%20%22type%22%3A%20%22RecordString%22%0A%20%20%7D%2C%0A%20%20%22webConsentPayload%22%3A%20%7B%0A%20%20%20%20%22type%22%3A%20%22RecordString%22%0A%20%20%7D%2C%0A%20%20%22GPPData%22%3A%20%7B%0A%20%20%20%20%22MspaCoveredTransaction%22%3A%20%22no%22%2C%0A%20%20%20%20%22MspaOptOutOptionMode%22%3A%20%22na%22%2C%0A%20%20%20%20%22MspaServiceProviderMode%22%3A%20%22na%22%0A%20%20%7D%0A%7D&scriptType=android&scriptVersion=${BuildConfig.VERSION_NAME}")
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
            localState = null,
            hasCsp = false,
            withSiteActions = false,
            includeData = IncludeData.generateIncludeDataForConsentStatus(),
        )
        val sut = HttpUrlManagerSingleton.getConsentStatusUrl(param).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/wrapper/v2/consent-status?env=prod&accountId=22&propertyId=17801&hasCsp=false&withSiteActions=false&includeData=%7B%0A%20%20%22TCData%22%3A%20%7B%0A%20%20%20%20%22type%22%3A%20%22RecordString%22%0A%20%20%7D%2C%0A%20%20%22webConsentPayload%22%3A%20%7B%0A%20%20%20%20%22type%22%3A%20%22RecordString%22%0A%20%20%7D%2C%0A%20%20%22GPPData%22%3A%20%7B%0A%20%20%20%20%22MspaCoveredTransaction%22%3A%20%22no%22%2C%0A%20%20%20%20%22MspaOptOutOptionMode%22%3A%20%22na%22%2C%0A%20%20%20%20%22MspaServiceProviderMode%22%3A%20%22na%22%0A%20%20%7D%0A%7D&authId=null&metadata={%22ccpa%22:{%22applies%22:true},%22gdpr%22:{%22applies%22:true,%22uuid%22:%22e47e539d-41dd-442b-bb08-5cf52b1e33d4%22,%22hasLocalData%22:false}}&scriptType=android&scriptVersion=${BuildConfig.VERSION_NAME}")
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

        val operatingSystemInfo = OperatingSystemInfoParam()
        val mockGppData = GppData().toIncludeDataGppParam()

        val body = MessagesBodyReq(
            accountId = 22,
            propertyHref = "https://tests.unified-script.com",
            campaigns = list.toMetadataBody(
                gdprConsentStatus = cs.consentStatusData?.gdpr?.consentStatus,
                ccpaConsentStatus = null,
            ),
            campaignEnv = CampaignsEnv.STAGE.env,
            consentLanguage = "ES",
            hasCSP = false,
            includeData = IncludeData.generateIncludeDataForMessages(mockGppData),
            localState = JsonObject(mapOf()),
            operatingSystem = operatingSystemInfo,
        )

        val param = MessagesParamReq(
            env = Env.PROD,
            body = JsonConverter.converter.encodeToString(body),
            metadataArg = JsonConverter.converter.decodeFromString("""{"ccpa":{"applies":true},"gdpr":{"applies":true}}"""),
            authId = null,
            accountId = 1212,
            propertyId = 12,
            propertyHref = "asdfasdfasd"
        )
        val sut = HttpUrlManagerSingleton.getMessagesUrl(param).toString()
        sut.assertEquals("https://cdn.privacy-mgmt.com/wrapper/v2/messages?env=prod&nonKeyedLocalState=%7B%7D&body={%20%20%22accountId%22:%2022,%20%20%22propertyHref%22:%20%22https://tests.unified-script.com%22,%20%20%22campaigns%22:%20{%20%20%20%20%22gdpr%22:%20{%20%20%20%20%20%20%22consentStatus%22:%20{%20%20%20%20%20%20%20%20%22consentedAll%22:%20true,%20%20%20%20%20%20%20%20%22consentedToAny%22:%20false,%20%20%20%20%20%20%20%20%22granularStatus%22:%20{%20%20%20%20%20%20%20%20%20%20%22defaultConsent%22:%20false,%20%20%20%20%20%20%20%20%20%20%22previousOptInAll%22:%20false,%20%20%20%20%20%20%20%20%20%20%22purposeConsent%22:%20%22ALL%22,%20%20%20%20%20%20%20%20%20%20%22purposeLegInt%22:%20%22ALL%22,%20%20%20%20%20%20%20%20%20%20%22vendorConsent%22:%20%22ALL%22,%20%20%20%20%20%20%20%20%20%20%22vendorLegInt%22:%20%22ALL%22%20%20%20%20%20%20%20%20},%20%20%20%20%20%20%20%20%22hasConsentData%22:%20false,%20%20%20%20%20%20%20%20%22rejectedAny%22:%20false,%20%20%20%20%20%20%20%20%22rejectedLI%22:%20false%20%20%20%20%20%20},%20%20%20%20%20%20%22hasLocalData%22:%20true,%20%20%20%20%20%20%22targetingParams%22:%20{%20%20%20%20%20%20}%20%20%20%20}%20%20},%20%20%22campaignEnv%22:%20%22stage%22,%20%20%22consentLanguage%22:%20%22ES%22,%20%20%22hasCSP%22:%20false,%20%20%22includeData%22:%20{%20%20%20%20%22TCData%22:%20{%20%20%20%20%20%20%22type%22:%20%22RecordString%22%20%20%20%20},%20%20%20%20%22campaigns%22:%20{%20%20%20%20%20%20%22type%22:%20%22RecordString%22%20%20%20%20},%20%20%20%20%22webConsentPayload%22:%20{%20%20%20%20%20%20%22type%22:%20%22RecordString%22%20%20%20%20},%20%20%20%20%22GPPData%22:%20{%20%20%20%20%20%20%22MspaCoveredTransaction%22:%20%22no%22,%20%20%20%20%20%20%22MspaOptOutOptionMode%22:%20%22na%22,%20%20%20%20%20%20%22MspaServiceProviderMode%22:%20%22na%22%20%20%20%20}%20%20},%20%20%22localState%22:%20{%20%20},%20%20%22os%22:%20{%20%20%20%20%22name%22:%20%22android%22,%20%20%20%20%22version%22:%20%220%22%20%20}}&metadata={%20%20%22ccpa%22:%20{%20%20%20%20%22applies%22:%20true%20%20},%20%20%22gdpr%22:%20{%20%20%20%20%22applies%22:%20true%20%20}}&scriptType=android&scriptVersion=${BuildConfig.VERSION_NAME}")
    }
}

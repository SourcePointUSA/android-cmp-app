package com.sourcepoint.cmplibrary.data.network.util

import com.example.cmplibrary.BuildConfig
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.model.optimized.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.optimized.ConsentStatusParamReq
import com.sourcepoint.cmplibrary.data.network.model.optimized.MessagesParamReq
import com.sourcepoint.cmplibrary.data.network.model.optimized.MetaDataParamReq
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.GetChoiceParamReq
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.CustomConsentReq
import com.sourcepoint.cmplibrary.model.PmUrlConfig
import com.sourcepoint.cmplibrary.model.exposed.MessageType
import com.sourcepoint.cmplibrary.model.exposed.MessageType.* // ktlint-disable
import kotlinx.serialization.encodeToString
import okhttp3.HttpUrl

/**
 * Component responsible of building and providing the URLs
 */
internal interface HttpUrlManager {
    fun inAppMessageUrl(env: Env): HttpUrl
    fun sendCustomConsentUrl(env: Env): HttpUrl
    fun deleteCustomConsentToUrl(host: String, params: CustomConsentReq): HttpUrl
    fun pmUrl(
        env: Env,
        campaignType: CampaignType,
        pmConfig: PmUrlConfig,
        messageType: MessageType
    ): HttpUrl

    // Optimized
    fun getMetaDataUrl(param: MetaDataParamReq): HttpUrl
    fun getConsentStatusUrl(param: ConsentStatusParamReq): HttpUrl
    fun getChoiceUrl(param: GetChoiceParamReq): HttpUrl
    fun getGdprChoiceUrl(param: PostChoiceParamReq): HttpUrl
    fun getCcpaChoiceUrl(param: PostChoiceParamReq): HttpUrl
    fun postUsNatChoiceUrl(param: PostChoiceParamReq): HttpUrl
    fun getPvDataUrl(env: Env): HttpUrl
    fun getMessagesUrl(param: MessagesParamReq): HttpUrl
}

/**
 * Implementation of the [HttpUrlManager] interface
 */
internal object HttpUrlManagerSingleton : HttpUrlManager {
    private const val scriptType = "android"
    private const val scriptVersion = BuildConfig.VERSION_NAME

    override fun inAppMessageUrl(env: Env): HttpUrl = HttpUrl.Builder()
        .scheme("https")
        .host(env.host)
        .addPathSegments("wrapper/v2/get_messages")
        .addQueryParameter("env", env.queryParam)
        .build()

    override fun pmUrl(
        env: Env,
        campaignType: CampaignType,
        pmConfig: PmUrlConfig,
        messageType: MessageType

    ): HttpUrl {
        return when (campaignType) {
            CampaignType.GDPR -> urlPmGdpr(pmConfig, env, messageType)
            CampaignType.CCPA -> urlPmCcpa(pmConfig, env, messageType)
            CampaignType.USNAT -> urlPmUsNat(pmConfig, env, messageType)
        }
    }

    override fun sendCustomConsentUrl(env: Env): HttpUrl {
        // https://cdn.sp-stage.net/wrapper/tcfv2/v1/gdpr/custom-consent?inApp=true&env=stage
        return HttpUrl.Builder()
            .scheme("https")
            .host(env.host)
            .addPathSegments("wrapper/tcfv2/v1/gdpr/custom-consent")
            .addQueryParameter("env", env.queryParam)
            .addQueryParameter("inApp", "true")
            .addQueryParameter("scriptType", scriptType)
            .addQueryParameter("scriptVersion", scriptVersion)
            .build()
    }

    override fun deleteCustomConsentToUrl(host: String, params: CustomConsentReq): HttpUrl {
        // https://cdn.privacy-mgmt.com/consent/tcfv2/consent/v3/custom/:propertyId?consentUUID={GDPR_UUID}
        return HttpUrl.Builder()
            .scheme("https")
            .host(host)
            .addPathSegments("consent/tcfv2/consent/v3/custom/${params.propertyId}")
            .addQueryParameter("consentUUID", params.consentUUID)
            .addQueryParameter("scriptType", scriptType)
            .addQueryParameter("scriptVersion", scriptVersion)
            .build()
    }

    private fun urlPmGdpr(pmConf: PmUrlConfig, env: Env, messageType: MessageType): HttpUrl {

        val pathSegment = when (messageType) {
            LEGACY_OTT -> "privacy-manager-ott/index.html"
            OTT -> "native-ott/index.html"
            MOBILE -> "privacy-manager/index.html"
        }

        return HttpUrl.Builder()
            // https://notice.sp-stage.net/privacy-manager/index.html?message_id=<PM_ID>
            .scheme("https")
            .host(env.pmHostGdpr)
            .addPathSegments(pathSegment)
            .addQueryParameter("pmTab", pmConf.pmTab?.key)
            .addQueryParameter("site_id", pmConf.siteId)
            .addQueryParameter("preload_consent", true.toString())
            .apply {
                pmConf.consentLanguage?.let { addQueryParameter("consentLanguage", it) }
                pmConf.uuid?.let { addQueryParameter("consentUUID", it) }
                pmConf.siteId?.let { addQueryParameter("site_id", it) }
                pmConf.messageId?.let { addQueryParameter("message_id", it) }
            }
            .addQueryParameter("scriptType", scriptType)
            .addQueryParameter("scriptVersion", scriptVersion)
            .build()
    }

    private fun urlPmCcpa(pmConf: PmUrlConfig, env: Env, messageType: MessageType): HttpUrl {

        val pathSegment = when (messageType) {
            LEGACY_OTT -> "ccpa_ott/index.html"
            OTT -> "native-ott/index.html"
            MOBILE -> "ccpa_pm/index.html"
        }

        return HttpUrl.Builder()
            .scheme("https")
            .host(env.pmHostCcpa)
            .addPathSegments(pathSegment)
            .addQueryParameter("site_id", pmConf.siteId)
            .addQueryParameter("preload_consent", true.toString())
            .addQueryParameter("is_ccpa", true.toString())
            .apply {
                pmConf.consentLanguage?.let { addQueryParameter("consentLanguage", it) }
                pmConf.uuid?.let { addQueryParameter("ccpaUUID", it) }
                pmConf.messageId?.let { addQueryParameter("message_id", it) }
            }
            .addQueryParameter("scriptType", scriptType)
            .addQueryParameter("scriptVersion", scriptVersion)
            .build()
    }

    private fun urlPmUsNat(pmConf: PmUrlConfig, env: Env, messageType: MessageType): HttpUrl {

        val pathSegment = when (messageType) {
            LEGACY_OTT -> "ccpa_ott/index.html"
            OTT -> "native-ott/index.html"
            MOBILE -> "us_pm/index.html"
        }

        return HttpUrl.Builder()
            .scheme("https")
            .host(env.pmHostUSNat)
            .addPathSegments(pathSegment)
            .addQueryParameter("site_id", pmConf.siteId)
            .addQueryParameter("preload_consent", true.toString())
            .apply {
                pmConf.consentLanguage?.let { addQueryParameter("consentLanguage", it) }
                pmConf.uuid?.let { addQueryParameter("uuid", it) }
                pmConf.messageId?.let { addQueryParameter("message_id", it) }
            }
            .addQueryParameter("scriptType", scriptType)
            .addQueryParameter("scriptVersion", scriptVersion)
            .build()
    }

    override fun getMetaDataUrl(param: MetaDataParamReq): HttpUrl {
        // http://localhost:3000/wrapper/v2/meta-data?env=localProd&accountId=22&propertyId=17801&metadata={"gdpr": {}, "ccpa": {}}

        return HttpUrl.Builder()
            .scheme("https")
            .host(param.env.host)
            .addPathSegments("wrapper/v2/meta-data")
            .addQueryParameter("env", param.env.queryParam)
            .addQueryParameter("accountId", param.accountId.toString())
            .addQueryParameter("propertyId", param.propertyId.toString())
            .addEncodedQueryParameter("metadata", param.metadata)
            .addQueryParameter("scriptType", scriptType)
            .addQueryParameter("scriptVersion", scriptVersion)
            .build()
    }

    override fun getConsentStatusUrl(param: ConsentStatusParamReq): HttpUrl {
        // http://localhost:3000/wrapper/v2/consent-status?env=localProd
        // &metadata={"ccpa":{"applies":true}, "gdpr":{"applies":true, "uuid": "e47e539d-41dd-442b-bb08-5cf52b1e33d4", "hasLocalData": false}}
        // &hasCsp=true
        // &withSiteActions=true
        // &includeData={"TCData": {"type": "RecordString"}}
        // &propertyId=17801

        // https://cdn.privacy-mgmt.com/wrapper/v2/consent-status?env=localProd
        // &authId=user_auth_id
        // &metadata={"ccpa":{"applies":true},"gdpr":{"applies":true}}
        // &hasCsp=true
        // &propertyId=17801
        // &localState=
        // &includeData={"TCData": {"type": "RecordString"}}
        // &withSiteActions=true

        return HttpUrl.Builder()
            .scheme("https")
            .host(param.env.host)
            .addPathSegments("wrapper/v2/consent-status")
            .addQueryParameter("env", param.env.queryParam)
            .addQueryParameter("accountId", param.accountId.toString())
            .addQueryParameter("propertyId", param.propertyId.toString())
            .addQueryParameter("hasCsp", true.toString())
            .addQueryParameter("withSiteActions", false.toString())
            .addQueryParameter("includeData", """{"TCData": {"type": "RecordString"}, "webConsentPayload": {"type": "RecordString"}}""")
            .apply { param.authId?.let { p -> addQueryParameter("authId", p) } }
            .addEncodedQueryParameter("metadata", param.metadata)
            .addQueryParameter("scriptType", scriptType)
            .addQueryParameter("scriptVersion", scriptVersion)
            .build()
    }

    override fun getChoiceUrl(param: GetChoiceParamReq): HttpUrl {
        // http://localhost:3000/wrapper/v2/choice
        // /consent-all
        // ?env=localProd
        // &accountId=22
        // &hasCsp=true
        // &propertyId=17801
        // &withSiteActions=false
        // &includeCustomVendorsRes=false
        // &metadata={"ccpa":{"applies":true}, "gdpr":{"applies":true}}

        val metaData: String? = param.metadataArg?.let { JsonConverter.converter.encodeToString(it) }
        val includeData = JsonConverter.converter.encodeToString(param.includeData)

        return HttpUrl.Builder()
            .scheme("https")
            .host(param.env.host)
            .addPathSegments("wrapper/v2/choice")
            .addPathSegments(param.choiceType.type)
            .addQueryParameter("env", param.env.queryParam)
            .addQueryParameter("accountId", param.accountId.toString())
            .addQueryParameter("propertyId", param.propertyId.toString())
            .addQueryParameter("hasCsp", param.hasCsp.toString())
            .addQueryParameter("withSiteActions", param.withSiteActions.toString())
            .addQueryParameter("includeCustomVendorsRes", param.includeCustomVendorsRes.toString())
            .addEncodedQueryParameter("metadata", metaData)
            .addQueryParameter("includeData", includeData)
            .addQueryParameter("scriptType", scriptType)
            .addQueryParameter("scriptVersion", scriptVersion)
            .build()
    }

    override fun getGdprChoiceUrl(param: PostChoiceParamReq): HttpUrl {
        // http://localhost:3000/wrapper/v2/choice/gdpr/11?env=localProd&hasCsp=true
        return HttpUrl.Builder()
            .scheme("https")
            .host(param.env.host)
            .addPathSegments("wrapper/v2/choice/gdpr/${param.actionType.code}")
            .addQueryParameter("env", param.env.queryParam)
            .addQueryParameter("hasCsp", true.toString())
            .addQueryParameter("scriptType", scriptType)
            .addQueryParameter("scriptVersion", scriptVersion)
            .build()
    }
    override fun getCcpaChoiceUrl(param: PostChoiceParamReq): HttpUrl {
        // http://localhost:3000/wrapper/v2/choice/ccpa/11?env=localProd&hasCsp=true
        return HttpUrl.Builder()
            .scheme("https")
            .host(param.env.host)
            .addPathSegments("wrapper/v2/choice/ccpa/${param.actionType.code}")
            .addQueryParameter("env", param.env.queryParam)
            .addQueryParameter("hasCsp", true.toString())
            .addQueryParameter("scriptType", scriptType)
            .addQueryParameter("scriptVersion", scriptVersion)
            .build()
    }

    override fun postUsNatChoiceUrl(param: PostChoiceParamReq): HttpUrl = HttpUrl.Builder()
        .scheme("https")
        .host(param.env.host)
        .addPathSegments("wrapper/v2/choice/usnat/${param.actionType.code}")
        .addQueryParameter("env", param.env.queryParam)
        .addQueryParameter("hasCsp", true.toString())
        .addQueryParameter("scriptType", scriptType)
        .addQueryParameter("scriptVersion", scriptVersion)
        .build()

    override fun getPvDataUrl(env: Env): HttpUrl {
        // http://localhost:3000/wrapper/v2/pv-data?env=localProd
        return HttpUrl.Builder()
            .scheme("https")
            .host(env.host)
            .addPathSegments("wrapper/v2/pv-data")
            .addQueryParameter("env", env.queryParam)
            .addQueryParameter("scriptType", "android")
            .addQueryParameter("scriptVersion", BuildConfig.VERSION_NAME)
            .build()
    }

    override fun getMessagesUrl(param: MessagesParamReq): HttpUrl {
        // http://localhost:3000/wrapper/v2/messages?
        // env=localProd
        // &nonKeyedLocalState={"gdpr":{"_sp_v1_uid":null,"_sp_v1_data":null},"ccpa":{"_sp_v1_uid":null,"_sp_v1_data":null}}
        // &body={"accountId":22,"propertyHref":"https://tests.unified-script.com","hasCSP":true,"campaigns":{"ccpa":{"hasLocalData": false},"gdpr":{"hasLocalData": false, "consentStatus": {}}}, "includeData": {"TCData": {"type": "RecordString"}}}
        // &metadata={"ccpa":{"applies":true},"gdpr":{"applies":true}}
        // &includeData=

        val metaData: String? = param.metadataArg?.let { JsonConverter.converter.encodeToString(it) }

        return HttpUrl.Builder()
            .scheme("https")
            .host(param.env.host)
            .addPathSegments("wrapper/v2/messages")
            .addQueryParameter("env", param.env.queryParam)
            .addQueryParameter("nonKeyedLocalState", param.nonKeyedLocalState.toString())
            .addQueryParameter("localState", param.localState.toString())
            .addQueryParameter("pubData", param.pubData.toString())
            .addEncodedQueryParameter("body", param.body)
            .addEncodedQueryParameter("metadata", metaData)
            .addQueryParameter("scriptType", scriptType)
            .addQueryParameter("scriptVersion", scriptVersion)
            .build()
    }
}

enum class Env(
    val host: String,
    val pmHostGdpr: String,
    val pmHostCcpa: String,
    val pmHostUSNat: String,
    val queryParam: String,
) {
    STAGE(
        "cdn.sp-stage.net",
        "notice.sp-stage.net",
        "ccpa-notice.sp-stage.net",
        "ccpa-notice.sp-stage.net",
        "stage"
    ),
    PRE_PROD(
        "preprod-cdn.privacy-mgmt.com",
        "preprod-cdn.privacy-mgmt.com",
        "preprod-cdn.privacy-mgmt.com",
        "preprod-cdn.privacy-mgmt.com",
        "prod"
    ),
    LOCAL_PROD(
        "cdn.privacy-mgmt.com",
        "cdn.privacy-mgmt.com",
        "cdn.privacy-mgmt.com",
        "cdn.privacy-mgmt.com",
        "localProd"
    ),
    PROD(
        "cdn.privacy-mgmt.com",
        "cdn.privacy-mgmt.com",
        "cdn.privacy-mgmt.com",
        "cdn.privacy-mgmt.com",
        "prod"
    )
}

enum class CampaignsEnv(val env: String) {
    STAGE("stage"),
    PUBLIC("prod")
}

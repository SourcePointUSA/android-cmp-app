package com.sourcepoint.cmplibrary.data.network.util

import com.example.cmplibrary.BuildConfig
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.model.optimized.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.optimized.MessagesParamReq
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.GetChoiceParamReq
import com.sourcepoint.cmplibrary.exception.CampaignType
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
    fun pmUrl(
        env: Env,
        campaignType: CampaignType,
        pmConfig: PmUrlConfig,
        messageType: MessageType
    ): HttpUrl

    // Optimized
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
            .apply {
                pmConf.consentLanguage?.let { addQueryParameter("consentLanguage", it) }
                pmConf.uuid?.let { addQueryParameter("uuid", it) }
                pmConf.messageId?.let { addQueryParameter("message_id", it) }
            }
            .addQueryParameter("scriptType", scriptType)
            .addQueryParameter("scriptVersion", scriptVersion)
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
            .addQueryParameter("nonKeyedLocalState", param.nonKeyedLocalState?.let { JsonConverter.converter.encodeToString(it) })
            .addQueryParameter("localState", param.localState?.let { JsonConverter.converter.encodeToString(it) })
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

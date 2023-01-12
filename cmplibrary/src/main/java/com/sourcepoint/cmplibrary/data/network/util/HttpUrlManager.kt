package com.sourcepoint.cmplibrary.data.network.util

import com.example.cmplibrary.BuildConfig
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.CustomConsentReq
import com.sourcepoint.cmplibrary.model.PmUrlConfig
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.MessageSubCategory
import com.sourcepoint.cmplibrary.model.exposed.MessageSubCategory.* //ktlint-disable
import okhttp3.HttpUrl

/**
 * Component responsible of building and providing the URLs
 */
internal interface HttpUrlManager {
    fun inAppMessageUrl(env: Env): HttpUrl
    fun sendConsentUrl(actionType: ActionType, env: Env, campaignType: CampaignType): HttpUrl
    fun sendCustomConsentUrl(env: Env): HttpUrl
    fun deleteCustomConsentToUrl(host: String, params: CustomConsentReq): HttpUrl
    fun pmUrl(env: Env, campaignType: CampaignType, pmConfig: PmUrlConfig, messSubCat: MessageSubCategory): HttpUrl
}

/**
 * Implementation of the [HttpUrlManager] interface
 */
internal object HttpUrlManagerSingleton : HttpUrlManager {

    override fun inAppMessageUrl(env: Env): HttpUrl = HttpUrl.Builder()
        .scheme("https")
        .host(env.host)
        .addPathSegments("wrapper/v2/get_messages")
        .addQueryParameter("env", env.queryParam)
        .build()

    override fun sendConsentUrl(actionType: ActionType, env: Env, campaignType: CampaignType): HttpUrl {
        return when (campaignType) {
            CampaignType.CCPA -> sendCcpaConsentUrl(actionType = actionType.code, env = env)
            CampaignType.GDPR -> sendGdprConsentUrl(actionType = actionType.code, env = env)
        }
    }

    override fun pmUrl(env: Env, campaignType: CampaignType, pmConfig: PmUrlConfig, messSubCat: MessageSubCategory): HttpUrl {
        return when (campaignType) {
            CampaignType.GDPR -> urlPmGdpr(pmConfig, env, messSubCat)
            CampaignType.CCPA -> urlPmCcpa(pmConfig, env, messSubCat)
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
            .build()
    }

    override fun deleteCustomConsentToUrl(host: String, params: CustomConsentReq): HttpUrl {
        // https://cdn.privacy-mgmt.com/consent/tcfv2/consent/v3/custom/:propertyId?consentUUID={GDPR_UUID}
        return HttpUrl.Builder()
            .scheme("https")
            .host(host)
            .addPathSegments("consent/tcfv2/consent/v3/custom/${params.propertyId}")
            .addQueryParameter("consentUUID", params.consentUUID)
            .build()
    }

    private fun urlPmGdpr(pmConf: PmUrlConfig, env: Env, messSubCat: MessageSubCategory): HttpUrl {

        val urlPostFix = when (messSubCat) {
            OTT -> "privacy-manager-ott/index.html"
            NATIVE_OTT -> "native-ott/index.html"
            else -> "privacy-manager/index.html"
        }

        return HttpUrl.Builder()
            // https://notice.sp-stage.net/privacy-manager/index.html?message_id=<PM_ID>
            .scheme("https")
            .host(env.pmHostGdpr)
            .addPathSegments(urlPostFix)
            .addQueryParameter("pmTab", pmConf.pmTab?.key)
            .apply {
                pmConf.consentLanguage?.let { addQueryParameter("consentLanguage", it) }
                pmConf.uuid?.let { addQueryParameter("consentUUID", it) }
                pmConf.siteId?.let { addQueryParameter("site_id", it.toString()) }
                pmConf.messageId?.let { addQueryParameter("message_id", it) }
            }
            .build()
    }

    private fun urlPmCcpa(pmConf: PmUrlConfig, env: Env, messSubCat: MessageSubCategory): HttpUrl {

        // ott: https://cdn.privacy-mgmt.com/ccpa_ott/index.html?message_id=527843
        //      https://ccpa-notice.sp-stage.net/ccpa_pm/index.html?message_id=14777

        val pathSegment = if (messSubCat == OTT) "ccpa_ott/index.html" else "ccpa_pm/index.html"

        return HttpUrl.Builder()
            .scheme("https")
            .host(env.pmHostCcpa)
            .addPathSegments(pathSegment)
            .apply {
                pmConf.siteId?.let { addQueryParameter("site_id", it.toString()) }
                pmConf.consentLanguage?.let { addQueryParameter("consentLanguage", it) }
                pmConf.uuid?.let { addQueryParameter("ccpaUUID", it) }
                pmConf.messageId?.let { addQueryParameter("message_id", it) }
            }
            .build()
    }

    private fun sendCcpaConsentUrl(actionType: Int, env: Env): HttpUrl {
        // https://<spHost>/wrapper/v2/messages/choice/ccpa/11?env=stage
        return HttpUrl.Builder()
            .scheme("https")
            .host(env.host)
            .addPathSegments("wrapper/v2/messages/choice/ccpa/$actionType")
            .addQueryParameter("env", env.queryParam)
            .build()
    }

    private fun sendGdprConsentUrl(actionType: Int, env: Env): HttpUrl {
        // https://<spHost>/wrapper/v2/messages/choice/gdpr/:actionType?env=stage
        return HttpUrl.Builder()
            .scheme("https")
            .host(env.host)
            .addPathSegments("wrapper/v2/messages/choice/gdpr/$actionType")
            .addQueryParameter("env", env.queryParam)
            .build()
    }
}

enum class Env(
    val host: String,
    val pmHostGdpr: String,
    val pmHostCcpa: String,
    val queryParam: String
) {
    STAGE(
        "cdn.sp-stage.net",
        "notice.sp-stage.net",
        "ccpa-notice.sp-stage.net",
        BuildConfig.ENV_QUERY_PARAM
    ),
    PRE_PROD(
        "preprod-cdn.privacy-mgmt.com",
        "preprod-cdn.privacy-mgmt.com",
        "ccpa-inapp-pm.sp-prod.net",
        "prod"
    ),
    PROD(
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

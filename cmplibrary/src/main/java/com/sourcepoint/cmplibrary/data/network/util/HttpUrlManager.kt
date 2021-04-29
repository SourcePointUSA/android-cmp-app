package com.sourcepoint.cmplibrary.data.network.util

import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.PmUrlConfig
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import okhttp3.HttpUrl

/**
 * Component responsible of building and providing the URLs
 */
internal interface HttpUrlManager {
    fun inAppMessageUrl(env: Env): HttpUrl
    fun sendConsentUrl(actionType: ActionType, env: Env, campaignType: CampaignType): HttpUrl
    fun sendCustomConsentUrl(env: Env): HttpUrl
    fun pmUrl(env: Env, campaignType: CampaignType, pmConfig: PmUrlConfig): HttpUrl
    fun ottUrlPm(pmConf: PmUrlConfig, env: Env): HttpUrl
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

    override fun pmUrl(env: Env, campaignType: CampaignType, pmConfig: PmUrlConfig): HttpUrl {
        return when (campaignType) {
            CampaignType.GDPR -> urlPmGdpr(pmConfig, env)
            CampaignType.CCPA -> urlPmCcpa(pmConfig, env)
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

    override fun ottUrlPm(pmConf: PmUrlConfig, env: Env): HttpUrl = HttpUrl.Builder()
        .scheme("https")
        .host(env.host)
        .addPathSegments("privacy-manager-ott")
        .addPathSegments("index.html")
        .addQueryParameter("consentLanguage", pmConf.consentLanguage)
        .addQueryParameter("consentUUID", pmConf.uuid)
        .apply {
            if (pmConf.pmTab != PMTab.DEFAULT) {
                addQueryParameter("pmTab", pmConf.pmTab?.key)
            }
        }
        .addQueryParameter("site_id", pmConf.siteId)
        .addQueryParameter("message_id", pmConf.messageId)
        .build()

    private fun urlPmGdpr(pmConf: PmUrlConfig, env: Env): HttpUrl = HttpUrl.Builder()
        // https://notice.sp-stage.net/privacy-manager/index.html?message_id=<PM_ID>
        .scheme("https")
        .host(env.pmHostGdpr)
        .addPathSegments("privacy-manager/index.html")
        .addQueryParameter("pmTab", pmConf.pmTab?.key)
        .apply {
            pmConf.consentLanguage?.let { addQueryParameter("consentLanguage", it) }
            pmConf.uuid?.let { addQueryParameter("consentUUID", it) }
            pmConf.siteId?.let { addQueryParameter("site_id", it) }
            pmConf.messageId?.let { addQueryParameter("message_id", it) }
        }
        .build()

    private fun urlPmCcpa(pmConf: PmUrlConfig, env: Env): HttpUrl = HttpUrl.Builder()
        // https://ccpa-notice.sp-stage.net/ccpa_pm/index.html?message_id=14777
        .scheme("https")
        .host(env.pmHostCcpa)
        .addPathSegments("ccpa_pm/index.html")
        .apply {
            pmConf.consentLanguage?.let { addQueryParameter("consentLanguage", it) }
            pmConf.uuid?.let { addQueryParameter("ccpaUUID", it) }
            pmConf.messageId?.let { addQueryParameter("message_id", it) }
        }
        .build()

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

enum class Env(val host: String, val pmHostGdpr: String, val pmHostCcpa: String, val queryParam: String) {
    STAGE("cdn.sp-stage.net", "notice.sp-stage.net", "ccpa-notice.sp-stage.net", "stage"),
    PROD("cdn.privacy-mgmt.com", "cdn.privacy-mgmt.com", "ccpa-inapp-pm.sp-prod.net", "localProd")
}

enum class CampaignEnv(val value: String) {
    STAGE("stage"),
    PUBLIC("prod")
}

package com.sourcepoint.cmplibrary.data.network.util

import com.sourcepoint.cmplibrary.data.network.util.Env.PROD
import com.sourcepoint.cmplibrary.data.network.util.Env.STAGE
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.PmUrlConfig
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import okhttp3.HttpUrl

/**
 * Component responsible of building and providing the URLs
 */
internal interface HttpUrlManager {
    fun inAppMessageUrl(env: Env): HttpUrl
    fun sendConsentUrl(actionType: ActionType, env: Env, legislation: Legislation): HttpUrl
    fun pmUrl(env: Env, legislation: Legislation, pmConfig: PmUrlConfig): HttpUrl
    fun ottUrlPm(pmConf: PmUrlConfig): HttpUrl
}

/**
 * Implementation of the [HttpUrlManager] interface
 */
internal object HttpUrlManagerSingleton : HttpUrlManager {

    private const val spHost = "cdn.privacy-mgmt.com"

    override fun inAppMessageUrl(env: Env): HttpUrl = when (env) {
        STAGE -> inAppUrlMessageStage
        PROD -> inAppUrlMessageProd
    }

    override fun sendConsentUrl(actionType: ActionType, env: Env, legislation: Legislation): HttpUrl {
        return when (legislation) {
            Legislation.CCPA -> {
                when (env) {
                    PROD -> sendCcpaConsentUrlProd(actionType = actionType.code)
                    STAGE -> sendCcpaConsentUrlProd(actionType = actionType.code)
                }
            }
            Legislation.GDPR -> {
                when (env) {
                    PROD -> sendGdprConsentUrlProd(actionType = actionType.code)
                    STAGE -> sendGdprConsentUrlProd(actionType = actionType.code)
                }
            }
        }
    }

    override fun pmUrl(env: Env, legislation: Legislation, pmConfig: PmUrlConfig): HttpUrl = when (legislation) {
        Legislation.GDPR -> urlPmGdpr(pmConfig)
        Legislation.CCPA -> urlPmCcpa(pmConfig)
    }

    private val inAppUrlMessageStage: HttpUrl = HttpUrl.Builder()
        .scheme("https")
        .host("cdn.sp-stage.net")
        .addPathSegments("wrapper/v2/get_messages")
        .addQueryParameter("env", "stage")
        .build()

    private val inAppUrlMessageProd: HttpUrl = HttpUrl.Builder()
        .scheme("https")
        .host("cdn.privacy-mgmt.com")
        .addPathSegments("wrapper/v2/get_messages")
        .addQueryParameter("env", "localProd")
        .build()

    override fun ottUrlPm(pmConf: PmUrlConfig): HttpUrl = HttpUrl.Builder()
        .scheme("https")
        .host(spHost)
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

    private fun urlPmGdpr(pmConf: PmUrlConfig): HttpUrl = HttpUrl.Builder()
        // https://notice.sp-stage.net/privacy-manager/index.html?message_id=<PM_ID>
        .scheme("https")
        .host("notice.sp-stage.net")
        .addPathSegments("privacy-manager/index.html")
        .addQueryParameter("pmTab", pmConf.pmTab?.key)
        .apply {
            pmConf.consentLanguage?.let { addQueryParameter("consentLanguage", it) }
            pmConf.uuid?.let { addQueryParameter("consentUUID", it) }
            pmConf.siteId?.let { addQueryParameter("site_id", it) }
            pmConf.messageId?.let { addQueryParameter("message_id", it) }
        }
        .build()

    private fun urlPmCcpa(pmConf: PmUrlConfig): HttpUrl = HttpUrl.Builder()
        // https://ccpa-notice.sp-stage.net/ccpa_pm/index.html?message_id=14777
        .scheme("https")
        .host("ccpa-notice.sp-stage.net")
        .addPathSegments("ccpa_pm/index.html")
        .apply {
            pmConf.consentLanguage?.let { addQueryParameter("consentLanguage", it) }
            pmConf.uuid?.let { addQueryParameter("ccpaUUID", it) }
            pmConf.messageId?.let { addQueryParameter("message_id", it) }
        }
        .build()

    private fun sendCcpaConsentUrlProd(actionType: Int): HttpUrl {
        // https://cdn.sp-stage.net/wrapper/v2/messages/choice/ccpa/11?env=stage
        return HttpUrl.Builder()
            .scheme("https")
            .host("cdn.sp-stage.net")
            .addPathSegments("wrapper/v2/messages/choice/ccpa/$actionType")
            .addQueryParameter("env", "stage")
            .build()
    }

    private fun sendGdprConsentUrlProd(actionType: Int): HttpUrl {
        // https://cdn.sp-stage.net/wrapper/v2/messages/choice/gdpr/:actionType?env=stage
        return HttpUrl.Builder()
            .scheme("https")
            .host("cdn.sp-stage.net")
            .addPathSegments("wrapper/v2/messages/choice/gdpr/$actionType")
            .addQueryParameter("env", "stage")
            .build()
    }
}

enum class Env {
    STAGE,
    PROD
}

enum class CampaignEnv(val value: String) {
    STAGE("stage"),
    PUBLIC("prod")
}

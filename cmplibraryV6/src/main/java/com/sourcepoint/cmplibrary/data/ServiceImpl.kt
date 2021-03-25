package com.sourcepoint.cmplibrary.data

import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.consent.ConsentManager
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.NetworkClient
import com.sourcepoint.cmplibrary.data.network.converter.toCCPAUserConsent
import com.sourcepoint.cmplibrary.data.network.converter.toGDPRUserConsent
import com.sourcepoint.cmplibrary.data.network.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.consent.ConsentResp
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManager
import com.sourcepoint.cmplibrary.exception.Legislation.*
import com.sourcepoint.cmplibrary.model.getMap
import com.sourcepoint.cmplibrary.model.toTreeMap
import com.sourcepoint.cmplibrary.util.executeOnLeft
import com.sourcepoint.cmplibrary.util.getOrNull
import okhttp3.HttpUrl

/**
 * Factory method to create an instance of a [Service] using its implementation
 * @param nc is an instance of [NetworkClient]
 * @param ds is an instance of [DataStorage]
 * @param campaignManager is an instance of [CampaignManager]
 * @param consentManager is an instance of [ConsentManager]
 * @return an instance of the [ServiceImpl] implementation
 */
internal fun Service.Companion.create(
    nc: NetworkClient,
    campaignManager: CampaignManager,
    consentManager: ConsentManager,
    urlManager: HttpUrlManager
): Service = ServiceImpl(nc, campaignManager, consentManager, urlManager)

/**
 * Implementation os the [Service] interface
 */
private class ServiceImpl(
    private val nc: NetworkClient,
    private val campaignManager: CampaignManager,
    private val consentManager: ConsentManager,
    private val urlManager: HttpUrlManager
) : Service, NetworkClient by nc, CampaignManager by campaignManager {

    override fun getMessage1203(messageReq: MessageReq, pSuccess: (UnifiedMessageResp1203) -> Unit, pError: (Throwable) -> Unit) {
        nc.getMessage1203(
            messageReq,
            { messageResp ->
                campaignManager.saveUnifiedMessageResp1203(messageResp)
                pSuccess(messageResp)
            },
            pError
        )
    }

    override fun getMessage(messageReq: MessageReq, pSuccess: (UnifiedMessageResp) -> Unit, pError: (Throwable) -> Unit) {
        nc.getMessage(
            messageReq,
            { messageResp ->
                messageResp.campaigns.forEach { cr ->
                    when (cr) {
                        is Gdpr -> {
                            campaignManager.saveGdpr(cr)
                            cr.userConsent?.let { uc -> campaignManager.saveGDPRConsent(uc) }
                        }
                        is Ccpa -> {
                            campaignManager.saveCcpa(cr)
                            cr.userConsent.let { uc -> campaignManager.saveCCPAConsent(uc) }
                        }
                    }
                }
                pSuccess(messageResp)
            },
            pError
        )
    }

    override fun getNativeMessage(messageReq: MessageReq, success: (NativeMessageResp) -> Unit, error: (Throwable) -> Unit) {
        nc.getNativeMessage(
            messageReq,
            { nativeMessageResp ->
                success(nativeMessageResp)
                nativeMessageResp.msgJSON
                // TODO save the data into the local storage
            },
            error
        )
    }

    override fun getNativeMessageK(messageReq: MessageReq, success: (NativeMessageRespK) -> Unit, error: (Throwable) -> Unit) {
        nc.getNativeMessageK(
            messageReq,
            { nativeMessageResp ->
                success(nativeMessageResp)
                nativeMessageResp.msg
                // TODO save the data into the local storage
            },
            error
        )
    }

    override fun sendConsent(action: ConsentAction, success: (ConsentResp) -> Unit, error: (Throwable) -> Unit) {

        val url: HttpUrl = when (action.legislation) {
            CCPA -> urlManager.sendCcpaConsentUrl(actionType = action.actionType.code)
            GDPR -> urlManager.sendGdprConsentUrl
        }

        val request = consentManager.buildConsentReq(action)
            .executeOnLeft { error(it) }
            .getOrNull() ?: return

        nc.sendConsent(
            request,
            { consentResp ->
                success(consentResp.copy(legislation = action.legislation))
                when (action.legislation) {
                    GDPR -> {
                        consentResp.content
                            .toTreeMap()
                            .getMap("userConsent")
                            ?.toGDPRUserConsent()
                            ?.let { campaignManager.saveGDPRConsent(it) }
                    }
                    CCPA -> {
                        consentResp.content
                            .toTreeMap()
                            .getMap("userConsent")
                            ?.toCCPAUserConsent()
                            ?.let { campaignManager.saveCCPAConsent(it) }
                    }
                }
            },
            error,
            url,

        )
    }
}

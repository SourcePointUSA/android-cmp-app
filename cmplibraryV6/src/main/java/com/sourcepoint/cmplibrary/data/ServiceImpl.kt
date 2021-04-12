package com.sourcepoint.cmplibrary.data

import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.consent.ConsentManagerUtils
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.executeOnLeft
import com.sourcepoint.cmplibrary.core.flatMap
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.NetworkClient
import com.sourcepoint.cmplibrary.data.network.converter.toCCPAUserConsent
import com.sourcepoint.cmplibrary.data.network.converter.toGDPRUserConsent
import com.sourcepoint.cmplibrary.data.network.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.consent.ConsentResp
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManager
import com.sourcepoint.cmplibrary.exception.Legislation.CCPA
import com.sourcepoint.cmplibrary.exception.Legislation.GDPR
import com.sourcepoint.cmplibrary.model.getMap
import com.sourcepoint.cmplibrary.model.toTreeMap

/**
 * Factory method to create an instance of a [Service] using its implementation
 * @param nc is an instance of [NetworkClient]
 * @param ds is an instance of [DataStorage]
 * @param campaignManager is an instance of [CampaignManager]
 * @param consentManagerUtils is an instance of [ConsentManagerUtils]
 * @return an instance of the [ServiceImpl] implementation
 */
internal fun Service.Companion.create(
    nc: NetworkClient,
    campaignManager: CampaignManager,
    consentManagerUtils: ConsentManagerUtils,
    urlManager: HttpUrlManager
): Service = ServiceImpl(nc, campaignManager, consentManagerUtils, urlManager)

/**
 * Implementation os the [Service] interface
 */
private class ServiceImpl(
    private val nc: NetworkClient,
    private val campaignManager: CampaignManager,
    private val consentManagerUtils: ConsentManagerUtils,
    private val urlManager: HttpUrlManager
) : Service, NetworkClient by nc, CampaignManager by campaignManager {

    override fun getUnifiedMessage(
        messageReq: UnifiedMessageRequest,
        pSuccess: (UnifiedMessageResp1203) -> Unit,
        pError: (Throwable) -> Unit,
        env: Env
    ) {
        nc.getUnifiedMessage(
            messageReq,
            pSuccess = { messageResp ->
                campaignManager.saveUnifiedMessageResp1203(messageResp)
                pSuccess(messageResp)
            },
            pError = pError,
            env = env
        )
    }

    override fun getNativeMessage(
        messageReq: MessageReq,
        success: (NativeMessageResp) -> Unit,
        error: (Throwable) -> Unit
    ) {
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

    override fun getNativeMessageK(
        messageReq: MessageReq,
        success: (NativeMessageRespK) -> Unit,
        error: (Throwable) -> Unit
    ) {
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

    override fun sendConsent(
        localState: String,
        consentAction: ConsentAction,
        env: Env,
        pmId: String?
    ): Either<ConsentResp> {
        return consentManagerUtils.buildConsentReq(consentAction, localState, pmId)
            .flatMap {
                nc.sendConsent(it, env, consentAction)
            }
    }

    override fun sendConsent(
        localState: String,
        consentAction: ConsentAction,
        success: (ConsentResp) -> Unit,
        error: (Throwable) -> Unit,
        env: Env,
        pmId: String?
    ) {

        val request = consentManagerUtils.buildConsentReq(consentAction, localState, pmId)
            .executeOnLeft { error(it) }
            .getOrNull() ?: return

        nc.sendConsent(
            consentAction = consentAction,
            consentReq = request,
            success = { consentResp ->
                success(consentResp.copy(legislation = consentAction.legislation))
                when (consentAction.legislation) {
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
            error = error,
            env = env
        )
    }
}

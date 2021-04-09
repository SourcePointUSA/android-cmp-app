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

//    override fun getMessage1203(
//        messageReq: MessageReq,
//        pSuccess: (UnifiedMessageResp1203) -> Unit,
//        pError: (Throwable) -> Unit
//    ) {
//        nc.getMessage1203(
//            messageReq,
//            { messageResp ->
//                campaignManager.saveUnifiedMessageResp1203(messageResp)
//                pSuccess(messageResp)
//            },
//            pError
//        )
//    }

//    override fun getMessage(
//        messageReq: MessageReq,
//        pSuccess: (UnifiedMessageResp) -> Unit,
//        pError: (Throwable) -> Unit,
//        stage: Env
//    ) {
//        nc.getMessage(
//            messageReq,
//            pSuccess = { messageResp ->
//                messageResp.campaigns.forEach { cr ->
//                    when (cr) {
//                        is Gdpr -> {
//                            campaignManager.saveGdpr(cr)
//                            cr.userConsent?.let { uc -> campaignManager.saveGDPRConsent(uc) }
//                        }
//                        is Ccpa -> {
//                            campaignManager.saveCcpa(cr)
//                            cr.userConsent.let { uc -> campaignManager.saveCCPAConsent(uc) }
//                        }
//                    }
//                }
//                pSuccess(messageResp)
//            },
//            pError = pError,
//            stage = stage
//        )
//    }

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
        env: Env
    ): Either<ConsentResp> {
        return consentManagerUtils.buildConsentReq(consentAction, localState)
            .flatMap {
                nc.sendConsent(it, env, consentAction)
            }
            .executeOnLeft { error(it) }
    }

    override fun sendConsent(
        consentAction: ConsentAction,
        success: (ConsentResp) -> Unit,
        error: (Throwable) -> Unit,
        env: Env
    ) {

        val request = consentManagerUtils.buildConsentReq(consentAction)
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

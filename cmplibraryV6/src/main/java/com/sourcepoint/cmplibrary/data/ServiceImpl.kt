package com.sourcepoint.cmplibrary.data

import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.consent.ConsentManagerUtils
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.executeOnRight
import com.sourcepoint.cmplibrary.core.flatMap
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.NetworkClient
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.Legislation.CCPA
import com.sourcepoint.cmplibrary.exception.Legislation.GDPR
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.ConsentResp
import com.sourcepoint.cmplibrary.model.MessageReq
import com.sourcepoint.cmplibrary.model.UnifiedMessageRequest
import com.sourcepoint.cmplibrary.model.UnifiedMessageResp

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
    dataStorage: DataStorage,
    logger: Logger
): Service = ServiceImpl(nc, campaignManager, consentManagerUtils, dataStorage, logger)

/**
 * Implementation os the [Service] interface
 */
private class ServiceImpl(
    private val nc: NetworkClient,
    private val campaignManager: CampaignManager,
    private val consentManagerUtils: ConsentManagerUtils,
    private val dataStorage: DataStorage,
    private val logger: Logger
) : Service, NetworkClient by nc, CampaignManager by campaignManager {

    override fun getUnifiedMessage(
        messageReq: UnifiedMessageRequest,
        pSuccess: (UnifiedMessageResp) -> Unit,
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
            .executeOnRight {
                when (it.legislation) {
                    GDPR -> {
                        dataStorage.saveGdprConsentResp(it.content.toString())
                        dataStorage.saveGdprConsentUuid(it.uuid)
                    }
                    CCPA -> {
                        dataStorage.saveCcpaConsentResp(it.content.toString())
                        dataStorage.saveCcpaConsentUuid(it.uuid)
                    }
                }
                logger.d(this::class.java.name, "uuid[$it]")
            }
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
}

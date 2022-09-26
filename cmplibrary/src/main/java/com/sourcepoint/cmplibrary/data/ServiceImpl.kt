package com.sourcepoint.cmplibrary.data

import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.consent.ConsentManagerUtils
import com.sourcepoint.cmplibrary.core.* // ktlint-disable
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.executeOnRight
import com.sourcepoint.cmplibrary.core.flatMap
import com.sourcepoint.cmplibrary.core.map
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.NetworkClient
import com.sourcepoint.cmplibrary.data.network.converter.genericFail
import com.sourcepoint.cmplibrary.data.network.model.v7.ChoiceParamReq
import com.sourcepoint.cmplibrary.data.network.model.v7.MessagesParamReq
import com.sourcepoint.cmplibrary.data.network.model.v7.MessagesResp
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.CampaignType.CCPA
import com.sourcepoint.cmplibrary.exception.CampaignType.GDPR
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.ConsentResp
import com.sourcepoint.cmplibrary.model.UnifiedMessageRequest
import com.sourcepoint.cmplibrary.model.UnifiedMessageResp
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.util.check
import org.json.JSONObject

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
                campaignManager.saveUnifiedMessageResp(messageResp)
                messageResp.campaigns
                pSuccess(messageResp)
            },
            pError = pError,
            env = env
        )
    }

    override fun sendConsent(
        localState: String,
        consentActionImpl: ConsentActionImpl,
        env: Env,
        pmId: String?
    ): Either<ConsentResp> = check {
        consentManagerUtils.buildConsentReq(consentActionImpl, localState, pmId)
            .flatMap {
                nc.sendConsent(it, env, consentActionImpl)
            }
            .executeOnRight {
                dataStorage.run {
                    saveLocalState(it.localState)
                    savedConsent = true
                }
                when (it.campaignType) {
                    GDPR -> {
                        dataStorage.saveGdprConsentResp(it.userConsent ?: "")
                        dataStorage.saveGdprConsentUuid(it.uuid)
                    }
                    CCPA -> {
                        dataStorage.saveCcpaConsentResp(it.userConsent ?: "")
                        dataStorage.saveCcpaConsentUuid(it.uuid)
                    }
                }
            }
            .fold(
                { throwable -> throw throwable },
                { consentResp: ConsentResp -> consentResp }
            )
    }

    override fun sendCustomConsentServ(customConsentReq: CustomConsentReq, env: Env): Either<SPConsents?> = check {
        nc.sendCustomConsent(customConsentReq, env)
            .map {
                if (dataStorage.getGdprConsentResp() == null) {
                    genericFail("CustomConsent cannot be executed. Consent is missing!!!")
                }
                val existingConsent = JSONObject(dataStorage.getGdprConsentResp())
                existingConsent.put("grants", it.content.get("grants"))
                dataStorage.saveGdprConsentResp(existingConsent.toString())
            }
        consentManagerUtils.getSpConsent()
    }

    override fun deleteCustomConsentToServ(customConsentReq: CustomConsentReq, env: Env): Either<SPConsents?> = check {
        nc.deleteCustomConsentTo(customConsentReq, env)
            .map {
                if (dataStorage.getGdprConsentResp() == null) {
                    genericFail("CustomConsent cannot be executed. Consent is missing!!!")
                }
                val existingConsent = JSONObject(dataStorage.getGdprConsentResp())
                existingConsent.put("grants", it.content.get("grants"))
                dataStorage.saveGdprConsentResp(existingConsent.toString())
            }
        consentManagerUtils.getSpConsent()
    }

    override fun getMessages(
        messageReq: MessagesParamReq,
        pSuccess: (MessagesResp) -> Unit,
        pError: (Throwable) -> Unit
    ) {
        if(campaignManager.shouldCallMessages){
            if(consentManagerUtils.shoulTriggerTheFlow){
                // pv data
            }
            getChoice(ChoiceParamReq())

        }else{
            // pvData
            pSuccess(campaignManager.messagesV7)
        }
    }
}

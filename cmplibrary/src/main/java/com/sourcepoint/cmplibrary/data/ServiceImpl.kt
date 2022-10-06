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
import com.sourcepoint.cmplibrary.data.network.converter.failParam
import com.sourcepoint.cmplibrary.data.network.converter.genericFail
import com.sourcepoint.cmplibrary.data.network.model.v7.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.v7.ChoiceParamReq
import com.sourcepoint.cmplibrary.data.network.model.v7.MessagesParamReq
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
    logger: Logger,
    execManager: ExecutorManager
): Service = ServiceImpl(nc, campaignManager, consentManagerUtils, dataStorage, logger, execManager)

/**
 * Implementation os the [Service] interface
 */
private class ServiceImpl(
    private val nc: NetworkClient,
    private val campaignManager: CampaignManager,
    private val consentManagerUtils: ConsentManagerUtils,
    private val dataStorage: DataStorage,
    private val logger: Logger,
    private val execManager: ExecutorManager
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

//    override fun getMetaData(param: MetaDataParamReq): Either<MetaDataResp> {
//        return nc.getMetaData(param)
//            .executeOnRight { campaignManager.metaDataResp = it }
//    }

//    override fun getConsentStatus(param: ConsentStatusParamReq): Either<ConsentStatusResp> {
//        return nc.getConsentStatus(param)
//            .executeOnRight { campaignManager.consentStatusResponse = it }
//    }

    override fun getMessages(
        messageReq: MessagesParamReq,
        pSuccess: (MessagesResp) -> Unit,
        pError: (Throwable) -> Unit
    ) {

        execManager.executeOnWorkerThread {
            val md = MetaDataParamReq(
                env = messageReq.env,
                accountId = messageReq.accountId,
                propertyId = messageReq.propertyId,
                metadata = messageReq.metadata
            )

            val meta = this.getMetaData(md)
                .executeOnRight {
                    campaignManager.metaDataResp = it
                }
                .executeOnLeft {
                    println()
                }
                .getOrNull() ?: failParam("MessageData call")
            val gdprUUID = consentStatusResponse?.consentStatusData?.gdpr?.uuid
            val ccpaUUID = consentStatusResponse?.consentStatusData?.ccpa?.uuid

            if (messageReq.authId != null || ((gdprUUID == null || ccpaUUID == null) && consentStatusResponse == null)) {
                val csParam = ConsentStatusParamReq(
                    env = messageReq.env,
                    accountId = messageReq.accountId,
                    propertyId = messageReq.propertyId,
                    metadata = messageReq.metadata,
                    authId = messageReq.authId
                )
                campaignManager.run {
                    getConsentStatus(csParam).executeOnRight {
                        consentStatusResponse = it
                        gdprConsentStatus = it.consentStatusData?.gdpr?.consentStatus
                    }
                }
            }

            val gdprConsentStatus = campaignManager.gdprConsentStatus

            val additionsChangeDate = meta.gdpr?.additionsChangeDate ?: failParam("additionsChangeDate")
            val legalBasisChangeDate = meta.gdpr.legalBasisChangeDate ?: failParam("legalBasisChangeDate")
            val dataRecordedConsent = campaignManager.dataRecordedConsent

            if (dataRecordedConsent != null && gdprConsentStatus != null) {
                val creationLessThanAdditions = dataRecordedConsent.epochSecond < additionsChangeDate.epochSecond
                val creationLessThanLegalBasis = dataRecordedConsent.epochSecond < legalBasisChangeDate.epochSecond

                if (creationLessThanAdditions) {
                    gdprConsentStatus.vendorListAdditions = true
                }
                if (creationLessThanLegalBasis) {
                    gdprConsentStatus.legalBasisChanges = true
                }
                if (creationLessThanAdditions || creationLessThanLegalBasis) {
                    if (gdprConsentStatus.consentedAll == true) {
                        gdprConsentStatus.granularStatus?.previousOptInAll = true
                    } else {
                        gdprConsentStatus.consentedAll = false
                    }
                }
            }

            // update consentStatus
            campaignManager.gdprConsentStatus = gdprConsentStatus

            if (campaignManager.shouldCallMessages) {

                val body = getMessageBody(
                    accountId = messageReq.accountId,
                    propertyHref = messageReq.propertyHref,
                    cs = consentStatusResponse
                )

                val messagesParamReq = MessagesParamReq(
                    accountId = messageReq.accountId,
                    propertyId = messageReq.propertyId,
                    authId = messageReq.authId,
                    propertyHref = messageReq.propertyHref,
                    env = messageReq.env,
                    body = body.toString(),
                    metadata = meta.toString(),
                    nonKeyedLocalState = ""
                )

                getMessages(messagesParamReq).executeOnRight {
                    pSuccess(it)
                }

                getChoice(ChoiceParamReq())
            } else {
                // pvData
                campaignManager.messagesV7?.let(pSuccess)
            }
        }
    }
}

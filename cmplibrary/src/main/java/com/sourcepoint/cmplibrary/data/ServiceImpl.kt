package com.sourcepoint.cmplibrary.data

import com.example.cmplibrary.BuildConfig
import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.consent.ConsentManager
import com.sourcepoint.cmplibrary.consent.ConsentManagerUtils
import com.sourcepoint.cmplibrary.core.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.NetworkClient
import com.sourcepoint.cmplibrary.data.network.converter.genericFail
import com.sourcepoint.cmplibrary.data.network.model.v7.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.CampaignType.CCPA
import com.sourcepoint.cmplibrary.exception.CampaignType.GDPR
import com.sourcepoint.cmplibrary.exception.InvalidConsentResponse
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.util.check
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.json.JSONObject
import java.security.InvalidParameterException

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
                        dataStorage.gdprConsentUuid = it.uuid
                    }
                    CCPA -> {
                        dataStorage.saveCcpaConsentResp(it.userConsent ?: "")
                        dataStorage.ccpaConsentUuid = it.uuid
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
        showConsent: () -> Unit,
        pError: (Throwable) -> Unit
    ) {
        execManager.executeOnWorkerThread {

            val meta = this.getMetaData(messageReq.toMetaDataParamReq())
                .executeOnLeft {
                    execManager.executeOnMain { showConsent() }
                    return@executeOnWorkerThread
                }
                .executeOnRight { campaignManager.metaDataResp = it }

            if (messageReq.authId != null || campaignManager.shouldCallConsentStatus) {

                val csParams = messageReq.toConsentStatusParamReq(
                    gdprUuid = campaignManager.gdprUuid,
                    ccpaUuid = campaignManager.ccpaUuid,
                    localState = campaignManager.messagesV7LocalState
                )

                getConsentStatus(csParams)
                    .executeOnLeft {
                        execManager.executeOnMain { showConsent() }
                        return@executeOnWorkerThread
                    }
                    .executeOnRight {
                        campaignManager.apply {
                            messagesV7LocalState = it.localState
                            it.consentStatusData?.let { csd ->
                                // GDPR
                                gdprConsentStatus = csd.gdpr
                                consentStatus = csd.gdpr?.consentStatus
                                gdprDateCreated = csd.gdpr?.dateCreated
                                gdprUuid = csd.gdpr?.uuid
                                gdprDateCreated = csd.gdpr?.dateCreated
                                // CCPA
                                ccpaConsentStatus = csd.ccpa
                                ccpaDateCreated = csd.ccpa?.dateCreated
                                ccpaUuid = csd.ccpa?.uuid
                                ccpaDateCreated = csd.ccpa?.dateCreated
                            }
                        }
                    }
            }

            val gdprConsentStatus = campaignManager.consentStatus
            val additionsChangeDate = meta.getOrNull()?.gdpr?.additionsChangeDate
            val legalBasisChangeDate = meta.getOrNull()?.gdpr?.legalBasisChangeDate
            val dataRecordedConsent = campaignManager.gdprDateCreated

            if (dataRecordedConsent != null &&
                gdprConsentStatus != null &&
                additionsChangeDate != null &&
                legalBasisChangeDate != null
            ) {
                val consentStatus = consentManagerUtils.updateGdprConsentV7(
                    dataRecordedConsent = dataRecordedConsent,
                    gdprConsentStatus = gdprConsentStatus,
                    additionsChangeDate = additionsChangeDate,
                    legalBasisChangeDate = legalBasisChangeDate
                )
                campaignManager.consentStatus = consentStatus
            }

            if (campaignManager.shouldCallMessages) {

                val body = getMessageBody(
                    accountId = messageReq.accountId,
                    propertyHref = messageReq.propertyHref,
                    cs = gdprConsentStatus,
                    ccpaStatus = campaignManager.ccpaConsentStatus?.status?.name,
                    localState = campaignManager.messagesV7LocalState?.jsonObject ?: JsonObject(emptyMap()),
                    campaigns = campaignManager.campaigns4Config
                )

                val messagesParamReq = MessagesParamReq(
                    accountId = messageReq.accountId,
                    propertyId = messageReq.propertyId,
                    authId = messageReq.authId,
                    propertyHref = messageReq.propertyHref,
                    env = messageReq.env,
                    body = body.toString(),
                    metadataArg = meta.getOrNull()?.toMetaDataArg(),
                    nonKeyedLocalState = ""
                )

                val statusResp = getMessages(messagesParamReq)
                    .executeOnLeft {
                        execManager.executeOnMain { showConsent() }
                        return@executeOnWorkerThread
                    }
                    .executeOnRight {
                        campaignManager.messagesV7LocalState = it.localState
                        it.campaigns?.gdpr?.messageMetaData?.let { gmd -> campaignManager.gdprMessageMetaData = gmd }
                        it.campaigns?.ccpa?.messageMetaData?.let { cmd -> campaignManager.ccpaMessageMetaData = cmd }
                        dataStorage.tcDataV7 = it.campaigns?.gdpr?.TCData
                        campaignManager.gdprDateCreated = it.campaigns?.gdpr?.dateCreated
                        execManager.executeOnMain { pSuccess(it) }
                    }

                if (statusResp.getOrNull() != null) {
                    val choiceBody = campaignManager.getChoiceBody()
                    getChoice(
                        ChoiceParamReq(
                            env = messageReq.env,
                            body = choiceBody,
                            choiceType = ChoiceTypeParam.CONSENT_ALL,
                            propertyId = messageReq.propertyId,
                            accountId = messageReq.accountId,
                            metadataArg = messageReq.metadataArg
                        )
                    )
                        .executeOnLeft {
                            execManager.executeOnMain { showConsent() }
                            return@executeOnWorkerThread
                        }
                        .executeOnRight { campaignManager.choiceResp = it }
                }

                if ((campaignManager.gdprConsentStatus != null || campaignManager.ccpaConsentStatus != null) &&
                    consentManagerUtils.shouldTriggerBySample
                ) {

                    val pvParams = PvDataParamReq(
                        env = messageReq.env,
                        body = campaignManager.getPvDataBody(messageReq)
                    )

                    savePvData(pvParams)
                        .executeOnLeft {
                            execManager.executeOnMain { showConsent() }
                            return@executeOnWorkerThread
                        }
                        .executeOnRight {
                            campaignManager.pvDataResp = it
                        }
                }
            } else {
                execManager.executeOnMain { showConsent() }
                // pvData
            }
        }
    }

    override fun sendConsentV7(
        consentActionImpl: ConsentActionImpl,
        env: Env,
        sPConsentsSuccess: ((SPConsents) -> Unit)?,
        pmId: String?
    ): Either<ChoiceResp> {
        return when (consentActionImpl.campaignType) {
            GDPR -> {
                sendConsentGdprV7(
                    consentActionImpl,
                    env,
                    sPConsentsSuccess
                ).map { ChoiceResp(gdpr = it) }
            }
            CCPA -> {
                sendConsentCcpaV7(
                    consentActionImpl,
                    env,
                    sPConsentsSuccess
                ).map { ChoiceResp(ccpa = it) }
            }
        }
    }

    fun sendConsentGdprV7(
        consentActionImpl: ConsentActionImpl,
        env: Env,
        sPConsentsSuccess: ((SPConsents) -> Unit)?
    ): Either<GdprCS> = check {

        var getResp: ChoiceResp? = null

        val at = consentActionImpl.actionType
        if (at == ActionType.ACCEPT_ALL || at == ActionType.REJECT_ALL) {

            val choiceBody = campaignManager.getChoiceBody()

            val gcParam = ChoiceParamReq(
                choiceType = consentActionImpl.actionType.toChoiceTypeParam(),
                accountId = spConfig.accountId.toLong(),
                propertyId = spConfig.propertyId!!.toLong(),
                env = env,
                metadataArg = campaignManager.metaDataResp?.copy(ccpa = null)?.toMetaDataArg(),
                body = choiceBody
            )

            getResp = nc.getChoice(gcParam)
                .executeOnRight { r ->
                    r.gdpr?.let {
                        campaignManager.gdprConsentStatus = it
                        campaignManager.consentStatus = it.consentStatus
                    }
                }
                .executeOnRight {
                    it.gdpr?.let { g ->
                        val cr = ConsentManager.responseConsentHandler(g, consentManagerUtils)
                        sPConsentsSuccess?.invoke(cr)
                    }
                }
                .getOrNull()
        }

        if (spConfig.propertyId == null) throw InvalidParameterException("PropertyId cannot be null!!!")
        val messageId: Long = campaignManager.gdprMessageMetaData?.messageId?.toLong()
            ?: throw InvalidParameterException("Gdpr messageId cannot be null!!!")

        val body = postChoiceGdprBody(
            sampleRate = BuildConfig.SAMPLE_RATE,
            propertyId = spConfig.propertyId.toLong(),
            messageId = messageId,
            granularStatus = campaignManager.consentStatus?.granularStatus,
            consentAllRef = getResp?.gdpr?.consentAllRef,
            vendorListId = getResp?.gdpr?.vendorListId,
            saveAndExitVariables = consentActionImpl.saveAndExitVariablesV7,
            uuid = campaignManager.gdprUuid,
            authid = authId
        )

        val pcParam = PostChoiceParamReq(
            env = env,
            actionType = consentActionImpl.actionType,
            body = body
        )

        nc.storeGdprChoice(pcParam)
            .executeOnRight {
                campaignManager.gdprUuid = it.uuid
                if (at != ActionType.ACCEPT_ALL && at != ActionType.REJECT_ALL) {
                    campaignManager.gdprConsentStatus = it
                    campaignManager.consentStatus = it.consentStatus
                }
            }
            .executeOnRight {
                if (at != ActionType.ACCEPT_ALL && at != ActionType.REJECT_ALL) {
                    val cr = ConsentManager.responseConsentHandler(it, consentManagerUtils)
                    sPConsentsSuccess?.invoke(cr)
                }
            }

        campaignManager.gdprConsentStatus ?: throw InvalidConsentResponse(
            cause = null,
            "The GDPR consent object cannot be null!!!"
        )
    }

    fun sendConsentCcpaV7(
        consentActionImpl: ConsentActionImpl,
        env: Env,
        sPConsentsSuccess: ((SPConsents) -> Unit)?
    ): Either<CcpaCS> {
        val at = consentActionImpl.actionType
        if (at == ActionType.ACCEPT_ALL || at == ActionType.REJECT_ALL) {

            val gcParam = ChoiceParamReq(
                choiceType = at.toChoiceTypeParam(),
                accountId = spConfig.accountId.toLong(),
                propertyId = spConfig.propertyId!!.toLong(),
                env = env,
                metadataArg = campaignManager.metaDataResp?.copy(gdpr = null)?.toMetaDataArg()
            )
            nc.getChoice(gcParam)
                .executeOnRight { r ->
                    r.ccpa?.let {
                        campaignManager.ccpaConsentStatus = it
                        val cr = ConsentManager.responseConsentHandler(it, consentManagerUtils)
                        sPConsentsSuccess?.invoke(cr)
                    }
                }
        }

        if (spConfig.propertyId == null) return Either.Left(InvalidParameterException("PropertyId cannot be null!!!"))
        val messageId: Long = campaignManager.ccpaMessageMetaData?.messageId?.toLong()
            ?: return Either.Left(InvalidParameterException("Ccpa messageId cannot be null!!!"))

        val body = postChoiceCcpaBody(
            sampleRate = BuildConfig.SAMPLE_RATE,
            propertyId = spConfig.propertyId.toLong(),
            messageId = messageId,
            saveAndExitVariables = consentActionImpl.saveAndExitVariablesV7,
            authid = authId,
            uuid = campaignManager.ccpaUuid
        )

        val pcParam = PostChoiceParamReq(
            env = env,
            actionType = at,
            body = body
        )

        return nc.storeCcpaChoice(pcParam)
            .executeOnRight {
                campaignManager.ccpaUuid = it.uuid
                campaignManager.ccpaConsentStatus = it
            }.executeOnRight {
                if (at != ActionType.ACCEPT_ALL && at != ActionType.REJECT_ALL) {
                    val cr = ConsentManager.responseConsentHandler(it, consentManagerUtils)
                    sPConsentsSuccess?.invoke(cr)
                }
            }
    }
}

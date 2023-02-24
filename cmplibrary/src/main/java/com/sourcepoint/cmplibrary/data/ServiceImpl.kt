package com.sourcepoint.cmplibrary.data

import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.consent.ConsentManager
import com.sourcepoint.cmplibrary.consent.ConsentManagerUtils
import com.sourcepoint.cmplibrary.core.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.NetworkClient
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.converter.genericFail
import com.sourcepoint.cmplibrary.data.network.converter.toMapOfAny
import com.sourcepoint.cmplibrary.data.network.model.optimized.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.CampaignType.CCPA
import com.sourcepoint.cmplibrary.exception.CampaignType.GDPR
import com.sourcepoint.cmplibrary.exception.InvalidConsentResponse
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.GDPRPurposeGrants
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.util.check
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
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

    override fun sendCustomConsentServ(customConsentReq: CustomConsentReq, env: Env): Either<GdprCS> = check {
        nc.sendCustomConsent(customConsentReq, env)
            .map {
                if (campaignManager.gdprConsentStatus == null) {
                    genericFail("CustomConsent cannot be executed. Consent is missing!!!")
                }
                val grantsString: String = (it.content.get("grants") as JSONObject).toString()
                val grants = JsonConverter.converter.decodeFromString<Map<String, GDPRPurposeGrants>>(grantsString)
                val updatedGrants = campaignManager.gdprConsentStatus?.copy(grants = grants)
                campaignManager.gdprConsentStatus = updatedGrants
            }
        campaignManager.gdprConsentStatus!!
    }

    override fun deleteCustomConsentToServ(customConsentReq: CustomConsentReq, env: Env): Either<GdprCS> = check {
        nc.deleteCustomConsentTo(customConsentReq, env)
            .map {
                if (campaignManager.gdprConsentStatus == null) {
                    genericFail("CustomConsent cannot be executed. Consent is missing!!!")
                }
                val grantsString: String = (it.content.get("grants") as JSONObject).toString()
                val grants = JsonConverter.converter.decodeFromString<Map<String, GDPRPurposeGrants>>(grantsString)
                val updatedGrants = campaignManager.gdprConsentStatus?.copy(grants = grants)
                campaignManager.gdprConsentStatus = updatedGrants
            }
        campaignManager.gdprConsentStatus!!
    }

    var pMessageReq: MessagesParamReq? = null
    override fun getMessages(
        messageReq: MessagesParamReq,
        pSuccess: (MessagesResp) -> Unit,
        showConsent: () -> Unit,
        pError: (Throwable) -> Unit
    ) {
        pMessageReq = messageReq
        execManager.executeOnWorkerThread {

            campaignManager.authId = messageReq.authId

            val meta = this.getMetaData(messageReq.toMetaDataParamReq())
                .executeOnLeft {
                    pError(it)
                    return@executeOnWorkerThread
                }
                .executeOnRight { handleMetaDataLogic(it) }

            if (messageReq.authId != null || campaignManager.shouldCallConsentStatus) {
                triggerConsentStatus(messageReq)
                    .executeOnLeft {
                        pError(it)
                        return@executeOnWorkerThread
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
                val consentStatus = consentManagerUtils.updateGdprConsentOptimized(
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
                    localState = campaignManager.messagesOptimizedLocalState?.jsonObject ?: JsonObject(emptyMap()),
                    campaigns = campaignManager.campaigns4Config,
                    consentLanguage = campaignManager.messageLanguage.value
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

                getMessages(messagesParamReq)
                    .executeOnLeft {
                        pError(it)
                        return@executeOnWorkerThread
                    }
                    .executeOnRight {
                        campaignManager.messagesOptimizedLocalState = it.localState
                        it.campaigns?.gdpr?.messageMetaData?.let { gmd -> campaignManager.gdprMessageMetaData = gmd }
                        it.campaigns?.ccpa?.messageMetaData?.let { cmd -> campaignManager.ccpaMessageMetaData = cmd }

                        if (!campaignManager.hasLocalData) {
                            it.campaigns?.gdpr?.TCData?.let { tc -> dataStorage.tcData = tc.toMapOfAny() }
                            it.campaigns?.gdpr?.dateCreated?.let { dc -> campaignManager.gdprDateCreated = dc }

                            campaignManager.run {
                                if (!(messageReq.authId != null || campaignManager.shouldCallConsentStatus)) {
                                    // GDPR
                                    this.gdprConsentStatus = it.campaigns?.gdpr?.toGdprCS()
                                    consentStatus = it.campaigns?.gdpr?.consentStatus
                                    gdprDateCreated = it.campaigns?.gdpr?.dateCreated
                                    // CCPA
                                    ccpaConsentStatus = it.campaigns?.ccpa?.toCcpaCS()
                                    ccpaDateCreated = it.campaigns?.ccpa?.dateCreated
                                }
                            }
                        }

                        execManager.executeOnMain { pSuccess(it) }
                    }
            } else {
                execManager.executeOnMain { showConsent() }
            }

            val isGdprInConfig = spConfig.campaigns.find { it.campaignType == GDPR } != null

            logger.computation(
                tag = "PvData condition GdprSample",
                msg = """
                    isGdprInConfig[$isGdprInConfig]
                    shouldTriggerByGdprSample[${consentManagerUtils.shouldTriggerByGdprSample}]
                    res[${consentManagerUtils.shouldTriggerByGdprSample && isGdprInConfig}]
                """.trimIndent()
            )

            if (consentManagerUtils.shouldTriggerByGdprSample && isGdprInConfig) {

                val pvParams = PvDataParamReq(
                    env = messageReq.env,
                    body = campaignManager.getGdprPvDataBody(messageReq),
                    campaignType = GDPR
                )

                savePvData(pvParams)
                    .executeOnLeft {
                        pError(it)
                        return@executeOnWorkerThread
                    }
                    .executeOnRight {
                        campaignManager.pvDataResp = it
                    }
            }

            val isCcpaInConfig = spConfig.campaigns.find { it.campaignType == CCPA } != null

            logger.computation(
                tag = "PvData condition CcpaSample",
                msg = """
                    isCcpaInConfig[$isCcpaInConfig]
                    shouldTriggerByCcpaSample[${consentManagerUtils.shouldTriggerByCcpaSample}]
                    res[${consentManagerUtils.shouldTriggerByCcpaSample && isCcpaInConfig}]
                """.trimIndent()
            )

            if (consentManagerUtils.shouldTriggerByCcpaSample && isCcpaInConfig) {

                val pvParams = PvDataParamReq(
                    env = messageReq.env,
                    body = campaignManager.getCcpaPvDataBody(messageReq),
                    campaignType = CCPA
                )

                savePvData(pvParams)
                    .executeOnLeft {
                        pError(it)
                        return@executeOnWorkerThread
                    }
                    .executeOnRight {
                        campaignManager.pvDataResp = it
                    }
            }
        }
    }

    override fun sendConsentOptimized(
        consentActionImpl: ConsentActionImpl,
        env: Env,
        sPConsentsSuccess: ((SPConsents) -> Unit)?,
        pmId: String?
    ): Either<ChoiceResp> {
        return when (consentActionImpl.campaignType) {
            GDPR -> {
                sendConsentGdprOptimized(
                    consentActionImpl,
                    env,
                    sPConsentsSuccess
                ).map { ChoiceResp(gdpr = it) }
            }
            CCPA -> {
                sendConsentCcpaOptimized(
                    consentActionImpl,
                    env,
                    sPConsentsSuccess
                ).map { ChoiceResp(ccpa = it) }
            }
        }
    }

    fun sendConsentGdprOptimized(
        consentActionImpl: ConsentActionImpl,
        env: Env,
        sPConsentsSuccess: ((SPConsents) -> Unit)?
    ): Either<GdprCS> = check {

        var getResp: ChoiceResp? = null

        val at = consentActionImpl.actionType
        if (at == ActionType.ACCEPT_ALL || at == ActionType.REJECT_ALL) {

            val gcParam = ChoiceParamReq(
                choiceType = consentActionImpl.actionType.toChoiceTypeParam(),
                accountId = spConfig.accountId.toLong(),
                propertyId = spConfig.propertyId.toLong(),
                env = env,
                metadataArg = campaignManager.metaDataResp?.toMetaDataArg()?.copy(ccpa = null),
            )

            getResp = nc.getChoice(gcParam)
                .executeOnRight { r ->
                    r.gdpr?.let {
                        campaignManager.gdprConsentStatus = it
                        campaignManager.consentStatus = it.consentStatus
                    }
                }
                .executeOnRight {
                    val cr = ConsentManager.responseConsentHandler(it.gdpr, consentManagerUtils)
                    sPConsentsSuccess?.invoke(cr)
                }
                .getOrNull()
        }

        val messageId: Long? = campaignManager.gdprMessageMetaData?.messageId?.toLong()

        val body = postChoiceGdprBody(
            sampleRate = dataStorage.gdprSamplingValue,
            propertyId = spConfig.propertyId.toLong(),
            messageId = messageId,
            granularStatus = campaignManager.consentStatus?.granularStatus,
            consentAllRef = getResp?.gdpr?.consentAllRef,
            vendorListId = getResp?.gdpr?.vendorListId,
            saveAndExitVariables = consentActionImpl.saveAndExitVariablesOptimized,
            authid = authId,
            uuid = campaignManager.gdprUuid,
            sendPvData = dataStorage.gdprSamplingResult
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

//        pMessageReq?.apply { authId?.let{ triggerConsentStatus(this) } }

        campaignManager.gdprConsentStatus ?: throw InvalidConsentResponse(
            cause = null,
            "The GDPR consent object cannot be null!!!"
        )
    }

    fun sendConsentCcpaOptimized(
        consentActionImpl: ConsentActionImpl,
        env: Env,
        sPConsentsSuccess: ((SPConsents) -> Unit)?
    ): Either<CcpaCS> = check {
        val at = consentActionImpl.actionType
        if (at == ActionType.ACCEPT_ALL || at == ActionType.REJECT_ALL) {

            val gcParam = ChoiceParamReq(
                choiceType = at.toChoiceTypeParam(),
                accountId = spConfig.accountId.toLong(),
                propertyId = spConfig.propertyId.toLong(),
                env = env,
                metadataArg = campaignManager.metaDataResp?.toMetaDataArg()?.copy(gdpr = null),
            )
            nc.getChoice(gcParam)
                .executeOnRight { r ->
                    campaignManager.ccpaConsentStatus = r.ccpa
                    val cr = ConsentManager.responseConsentHandler(r.ccpa, consentManagerUtils)
                    sPConsentsSuccess?.invoke(cr)
                }
        }

        val messageId: Long? = campaignManager.ccpaMessageMetaData?.messageId?.toLong()

        val body = postChoiceCcpaBody(
            sampleRate = dataStorage.ccpaSamplingValue,
            propertyId = spConfig.propertyId.toLong(),
            messageId = messageId,
            saveAndExitVariables = consentActionImpl.saveAndExitVariablesOptimized,
            authid = authId,
            uuid = campaignManager.ccpaUuid,
            sendPvData = dataStorage.ccpaSamplingResult
        )

        val pcParam = PostChoiceParamReq(
            env = env,
            actionType = at,
            body = body
        )

        nc.storeCcpaChoice(pcParam)
            .executeOnRight {
                campaignManager.ccpaUuid = it.uuid
                campaignManager.ccpaConsentStatus = it
            }.executeOnRight {
                if (at != ActionType.ACCEPT_ALL && at != ActionType.REJECT_ALL) {
                    val cr = ConsentManager.responseConsentHandler(it, consentManagerUtils)
                    sPConsentsSuccess?.invoke(cr)
                }
            }

        campaignManager.ccpaConsentStatus ?: throw InvalidConsentResponse(
            cause = null,
            "The CCPA consent object cannot be null!!!"
        )
    }

    private fun triggerConsentStatus(messageReq: MessagesParamReq): Either<ConsentStatusResp> {
        val csParams = messageReq.toConsentStatusParamReq(
            gdprUuid = campaignManager.gdprUuid,
            ccpaUuid = campaignManager.ccpaUuid,
            localState = campaignManager.messagesOptimizedLocalState
        )

        return getConsentStatus(csParams)
            .executeOnRight {
                campaignManager.apply {
                    campaignManager.handleOldLocalData()
                    messagesOptimizedLocalState = it.localState
                    it.consentStatusData?.let { csd ->
                        // GDPR
                        gdprConsentStatus = csd.gdpr
                        consentStatus = csd.gdpr?.consentStatus
                        gdprUuid = csd.gdpr?.uuid
                        gdprDateCreated = csd.gdpr?.dateCreated
                        // CCPA
                        ccpaConsentStatus = csd.ccpa
                        ccpaUuid = csd.ccpa?.uuid
                        ccpaDateCreated = csd.ccpa?.dateCreated
                    }
                }
            }
    }
}

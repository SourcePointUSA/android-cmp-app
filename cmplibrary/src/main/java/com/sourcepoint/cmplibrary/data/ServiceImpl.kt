package com.sourcepoint.cmplibrary.data

import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.consent.ConsentManager
import com.sourcepoint.cmplibrary.consent.ConsentManagerUtils
import com.sourcepoint.cmplibrary.core.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.NetworkClient
import com.sourcepoint.cmplibrary.data.network.connection.ConnectionManager
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.converter.genericFail
import com.sourcepoint.cmplibrary.data.network.model.optimized.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.ChoiceResp
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.GetChoiceParamReq
import com.sourcepoint.cmplibrary.data.network.model.optimized.includeData.IncludeData
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.* //ktlint-disable
import com.sourcepoint.cmplibrary.exception.CampaignType.* //ktlint-disable
import com.sourcepoint.cmplibrary.exception.CampaignType.CCPA
import com.sourcepoint.cmplibrary.exception.CampaignType.GDPR
import com.sourcepoint.cmplibrary.exception.ConsentLibExceptionK
import com.sourcepoint.cmplibrary.exception.InvalidConsentResponse
import com.sourcepoint.cmplibrary.exception.NoInternetConnectionException
import com.sourcepoint.cmplibrary.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.GDPRPurposeGrants
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.util.check
import com.sourcepoint.cmplibrary.util.extensions.extractIncludeGppDataParamIfEligible
import com.sourcepoint.cmplibrary.util.extensions.isIncluded
import com.sourcepoint.cmplibrary.util.extensions.toJsonObject
import com.sourcepoint.cmplibrary.util.extensions.toMapOfAny
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.jsonObject
import org.json.JSONArray
import org.json.JSONObject

/**
 * Factory method to create an instance of a [Service] using its implementation
 * @param nc is an instance of [NetworkClient]
 * @param dataStorage is an instance of [DataStorage]
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
    execManager: ExecutorManager,
    connectionManager: ConnectionManager,
): Service = ServiceImpl(nc, campaignManager, consentManagerUtils, dataStorage, logger, execManager, connectionManager)

/**
 * Implementation os the [Service] interface
 */
private class ServiceImpl(
    private val networkClient: NetworkClient,
    private val campaignManager: CampaignManager,
    private val consentManagerUtils: ConsentManagerUtils,
    private val dataStorage: DataStorage,
    private val logger: Logger,
    private val execManager: ExecutorManager,
    private val connectionManager: ConnectionManager,
) : Service, NetworkClient by networkClient, CampaignManager by campaignManager {

    private fun JSONArray.toArrayList(): ArrayList<String> {
        val list = arrayListOf<String>()
        for (i in 0 until this.length()) {
            list.add(this.getString(i))
        }
        return list
    }

    override fun sendCustomConsentServ(customConsentReq: CustomConsentReq, env: Env): Either<GdprCS> = check {
        if (connectionManager.isConnected.not()) throw NoInternetConnectionException()

        networkClient.sendCustomConsent(customConsentReq, env)
            .map {
                if (campaignManager.gdprConsentStatus == null) {
                    genericFail("CustomConsent cannot be executed. Consent is missing!!!")
                }

                val categories: List<String> = (it.content.get("categories") as JSONArray).toArrayList()
                val vendors: List<String> = (it.content.get("vendors") as JSONArray).toArrayList()
                val legIntCategories: List<String> = (it.content.get("legIntCategories") as JSONArray).toArrayList()
                val specialFeatures: List<String> = (it.content.get("specialFeatures") as JSONArray).toArrayList()

                val grantsString: String = (it.content.get("grants") as JSONObject).toString()
                val grants = JsonConverter.converter.decodeFromString<Map<String, GDPRPurposeGrants>>(grantsString)
                val updatedGrants = campaignManager.gdprConsentStatus?.copy(
                    grants = grants,
                    categories = categories,
                    vendors = vendors,
                    legIntCategories = legIntCategories,
                    specialFeatures = specialFeatures
                )
                campaignManager.gdprConsentStatus = updatedGrants
            }
        campaignManager.gdprConsentStatus!!
    }

    override fun deleteCustomConsentToServ(customConsentReq: CustomConsentReq, env: Env): Either<GdprCS> = check {
        if (connectionManager.isConnected.not()) throw NoInternetConnectionException()

        networkClient.deleteCustomConsentTo(customConsentReq, env)
            .map {
                if (campaignManager.gdprConsentStatus == null) {
                    genericFail("CustomConsent cannot be executed. Consent is missing!!!")
                }

                val categories: List<String> = (it.content.get("categories") as JSONArray).toArrayList()
                val vendors: List<String> = (it.content.get("vendors") as JSONArray).toArrayList()
                val legIntCategories: List<String> = (it.content.get("legIntCategories") as JSONArray).toArrayList()
                val specialFeatures: List<String> = (it.content.get("specialFeatures") as JSONArray).toArrayList()

                val grantsString: String = (it.content.get("grants") as JSONObject).toString()
                val grants = JsonConverter.converter.decodeFromString<Map<String, GDPRPurposeGrants>>(grantsString)
                campaignManager.gdprConsentStatus = campaignManager.gdprConsentStatus?.copy(
                    grants = grants,
                    categories = categories,
                    vendors = vendors,
                    legIntCategories = legIntCategories,
                    specialFeatures = specialFeatures
                )
            }
        campaignManager.gdprConsentStatus!!
    }

    override fun getMessages(
        messageReq: MessagesParamReq,
        onSuccess: (MessagesResp) -> Unit,
        showConsent: () -> Unit,
        onFailure: (Throwable, Boolean) -> Unit,
    ) {
        execManager.executeOnWorkerThread {

            if (connectionManager.isConnected.not()) {
                val noInternetConnectionException = NoInternetConnectionException()
                onFailure(noInternetConnectionException, true)
                return@executeOnWorkerThread
            }

            // check whether the authId or propertyId changed, and handle the flow accordingly
            campaignManager.handleAuthIdOrPropertyIdChange(
                newAuthId = messageReq.authId,
                newPropertyId = spConfig.propertyId,
            )

            campaignManager.deleteExpiredConsents()

            val metadataResponse = this.getMetaData(messageReq.toMetaDataParamReq(campaigns4Config))
                .executeOnLeft { metaDataError ->
                    onFailure(metaDataError, true)
                    return@executeOnWorkerThread
                }
                .executeOnRight { metaDataResponse -> handleMetaDataResponse(metaDataResponse) }

            if (messageReq.authId != null || campaignManager.shouldCallConsentStatus) {
                triggerConsentStatus(
                    messageReq = messageReq,
                    gdprApplies = metadataResponse.getOrNull()?.gdpr?.applies,
                    ccpaApplies = metadataResponse.getOrNull()?.ccpa?.applies,
                    usNatApplies = metadataResponse.getOrNull()?.usNat?.applies,
                )
                    .executeOnLeft { consentStatusError ->
                        onFailure(consentStatusError, true)
                        return@executeOnWorkerThread
                    }
            }

            val gdprConsentStatus = campaignManager.gdprConsentStatus?.consentStatus
            val additionsChangeDate = metadataResponse.getOrNull()?.gdpr?.additionsChangeDate
            val legalBasisChangeDate = metadataResponse.getOrNull()?.gdpr?.legalBasisChangeDate
            val dataRecordedConsent = campaignManager.gdprConsentStatus?.dateCreated

            if (dataRecordedConsent != null &&
                gdprConsentStatus != null &&
                additionsChangeDate != null &&
                legalBasisChangeDate != null
            ) {
                val consentStatus = consentManagerUtils.updateGdprConsent(
                    dataRecordedConsent = dataRecordedConsent,
                    gdprConsentStatus = gdprConsentStatus,
                    additionsChangeDate = additionsChangeDate,
                    legalBasisChangeDate = legalBasisChangeDate
                )
                campaignManager.gdprConsentStatus = campaignManager.gdprConsentStatus?.copy(
                    consentStatus = consentStatus,
                    applies = metaDataResp?.gdpr?.applies
                )
            }

            if (campaignManager.shouldCallMessages) {

                val body = getMessageBody(
                    accountId = messageReq.accountId,
                    propertyHref = messageReq.propertyHref,
                    gdprConsentStatus = campaignManager.gdprConsentStatus?.consentStatus,
                    ccpaConsentStatus = campaignManager.ccpaConsentStatus?.status?.name,
                    usNatConsentStatus = campaignManager.usNatConsentData?.consentStatus,
                    campaigns = campaignManager.campaigns4Config,
                    consentLanguage = campaignManager.messageLanguage.value,
                    campaignEnv = campaignManager.spConfig.campaignsEnv,
                    includeDataGppParam = spConfig.extractIncludeGppDataParamIfEligible(),
                )

                val messagesParamReq = MessagesParamReq(
                    accountId = messageReq.accountId,
                    propertyId = messageReq.propertyId,
                    authId = messageReq.authId,
                    propertyHref = messageReq.propertyHref,
                    env = messageReq.env,
                    body = body.toString(),
                    metadataArg = metadataResponse.getOrNull()?.toMetaDataArg(),
                    nonKeyedLocalState = campaignManager.nonKeyedLocalState?.jsonObject,
                    localState = campaignManager.messagesOptimizedLocalState?.jsonObject,
                )

                getMessages(messagesParamReq)
                    .executeOnLeft { messagesError ->
                        onFailure(messagesError, true)
                        return@executeOnWorkerThread
                    }
                    .executeOnRight {
                        it.campaigns?.gdpr?.expirationDate?.let { exDate -> dataStorage.gdprExpirationDate = exDate }
                        it.campaigns?.ccpa?.expirationDate?.let { exDate -> dataStorage.ccpaExpirationDate = exDate }
                        it.campaigns?.usNat?.let { usNat ->
                            campaignManager.usNatConsentData
                                ?.let {
                                    campaignManager.usNatConsentData = campaignManager.usNatConsentData?.copy(
                                        message = usNat.message,
                                        messageMetaData = usNat.messageMetaData,
                                        url = usNat.url,
                                        type = usNat.type
                                    )
                                } ?: kotlin.run { campaignManager.usNatConsentData = usNat }
                        }
                        campaignManager.also { _ ->
                            messagesOptimizedLocalState = it.localState
                            nonKeyedLocalState = it.nonKeyedLocalState
                            gdprMessageMetaData = it.campaigns?.gdpr?.messageMetaData
                            ccpaMessageMetaData = it.campaigns?.ccpa?.messageMetaData
                        }

                        if (campaignManager.hasLocalData.not()) {

                            // save tc data in the data storage
                            it.campaigns?.gdpr?.TCData?.let { tcData ->
                                dataStorage.tcData = tcData.toMapOfAny()
                            }

                            dataStorage.gppData = it.campaigns?.ccpa?.gppData?.toMapOfAny()

                            campaignManager.run {
                                if ((messageReq.authId != null || campaignManager.shouldCallConsentStatus).not()) {
                                    this.gdprConsentStatus = it.campaigns?.gdpr?.toGdprCS(metadataResponse.getOrNull()?.gdpr?.applies)
                                    this.ccpaConsentStatus = it.campaigns?.ccpa?.toCcpaCS(metadataResponse.getOrNull()?.ccpa?.applies)
                                    this.usNatConsentData = usNatConsentData?.copy(applies = metadataResponse.getOrNull()?.usNat?.applies)
                                }
                            }
                        }

                        execManager.executeOnMain { onSuccess(it) }
                    }
            } else {
                execManager.executeOnMain { showConsent() }
            }

            val isGdprInConfig = spConfig.isIncluded(GDPR)

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

                postPvData(pvParams)
                    .executeOnLeft { gdprPvDataError ->
                        onFailure(gdprPvDataError, false)
                        return@executeOnWorkerThread
                    }
                    .executeOnRight { pvDataResponse ->
                        campaignManager.gdprConsentStatus = campaignManager.gdprConsentStatus?.copy(
                            uuid = pvDataResponse.gdpr?.uuid
                        )
                    }
            }

            val isCcpaInConfig = spConfig.isIncluded(CCPA)

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

                postPvData(pvParams)
                    .executeOnLeft { ccpaPvDataError ->
                        onFailure(ccpaPvDataError, false)
                        return@executeOnWorkerThread
                    }
                    .executeOnRight { pvDataResponse ->
                        campaignManager.ccpaConsentStatus = campaignManager.ccpaConsentStatus?.copy(
                            uuid = pvDataResponse.ccpa?.uuid
                        )
                    }
            }

            val isUsNatInConfig = spConfig.isIncluded(USNAT)

            logger.computation(
                tag = "PvData condition UsNatSample",
                msg = """
                    isUsNatInConfig[$isUsNatInConfig]
                    shouldTriggerByUsNatSample[${consentManagerUtils.shouldTriggerByUsNatSample}]
                    res[${consentManagerUtils.shouldTriggerByUsNatSample && isUsNatInConfig}]
                """.trimIndent()
            )

            if (consentManagerUtils.shouldTriggerByUsNatSample && isUsNatInConfig) {
                val pvParams = PvDataParamReq(
                    env = messageReq.env,
                    body = campaignManager.getUsNatPvDataBody(messageReq),
                    campaignType = USNAT
                )

                postPvData(pvParams)
                    .executeOnLeft { usNatPvDataError ->
                        onFailure(usNatPvDataError, false)
                        return@executeOnWorkerThread
                    }
                    .executeOnRight { pvDataResponse ->
                        campaignManager.usNatConsentData = campaignManager.usNatConsentData?.copy(
                            uuid = pvDataResponse.usnat?.uuid
                        )
                    }
            }
        }
    }

    override fun sendConsent(
        consentActionImpl: ConsentActionImpl,
        env: Env,
        sPConsentsSuccess: ((SPConsents) -> Unit)?,
        pmId: String?
    ): Either<ChoiceResp> {
        return when (consentActionImpl.campaignType) {
            GDPR -> {
                sendConsentGdpr(
                    consentActionImpl,
                    env,
                    sPConsentsSuccess
                ).map { ChoiceResp(gdpr = it) }
            }
            CCPA -> {
                sendConsentCcpa(
                    consentActionImpl,
                    env,
                    sPConsentsSuccess
                ).map { ChoiceResp(ccpa = it) }
            }
            USNAT -> {
                Either.Left(RuntimeException())
            }
        }
    }

    fun sendConsentGdpr(
        consentActionImpl: ConsentActionImpl,
        env: Env,
        sPConsentsSuccess: ((SPConsents) -> Unit)?
    ): Either<GdprCS> = check {

        var getResp: ChoiceResp? = null

        val actionType = consentActionImpl.actionType
        if (actionType == ActionType.ACCEPT_ALL || actionType == ActionType.REJECT_ALL) {

            val getChoiceParamReq = GetChoiceParamReq(
                choiceType = consentActionImpl.actionType.toChoiceTypeParam(),
                accountId = spConfig.accountId.toLong(),
                propertyId = spConfig.propertyId.toLong(),
                env = env,
                metadataArg = campaignManager.metaDataResp?.toMetaDataArg()?.copy(ccpa = null),
                includeData = IncludeData.generateIncludeDataForGetChoice(
                    gppData = spConfig.extractIncludeGppDataParamIfEligible(),
                ),
                hasCsp = true,
                includeCustomVendorsRes = false,
                withSiteActions = false,
            )

            getResp = networkClient.getChoice(getChoiceParamReq)
                .executeOnRight { response ->
                    response.gdpr?.let { responseGdpr ->
                        campaignManager.gdprConsentStatus = responseGdpr.copy(uuid = campaignManager.gdprConsentStatus?.uuid)
                    }
                    val consentHandler = ConsentManager.responseConsentHandler(
                        response.gdpr?.copy(uuid = campaignManager.gdprConsentStatus?.uuid, applies = dataStorage.gdprApplies),
                        consentManagerUtils
                    )
                    sPConsentsSuccess?.invoke(consentHandler)
                }
                .executeOnLeft { error ->
                    (error as? ConsentLibExceptionK)?.let { logger.error(error) }
                }
                .getOrNull()
        }

        val messageId: Long? = campaignManager.gdprMessageMetaData?.messageId?.toLong()

        val body = postChoiceGdprBody(
            sampleRate = dataStorage.gdprSamplingValue,
            propertyId = spConfig.propertyId.toLong(),
            messageId = messageId,
            granularStatus = campaignManager.gdprConsentStatus?.consentStatus?.granularStatus,
            consentAllRef = getResp?.gdpr?.consentAllRef,
            vendorListId = getResp?.gdpr?.vendorListId,
            saveAndExitVariables = consentActionImpl.saveAndExitVariablesOptimized,
            authid = authId,
            uuid = campaignManager.gdprConsentStatus?.uuid,
            sendPvData = dataStorage.gdprSamplingResult,
            pubData = consentActionImpl.pubData.toJsonObject(),
        )

        val postConsentParams = PostChoiceParamReq(
            env = env,
            actionType = consentActionImpl.actionType,
            body = body
        )

        networkClient.storeGdprChoice(postConsentParams)
            .executeOnRight { postConsentResponse ->
                campaignManager.gdprUuid = postConsentResponse.uuid

                // don't overwrite gdpr consents if the action is accept all or reject all
                // because the response from those endpoints does not contain a full consent
                // object.
                if (actionType != ActionType.ACCEPT_ALL && actionType != ActionType.REJECT_ALL) {
                    campaignManager.gdprConsentStatus = postConsentResponse
                    val cr = ConsentManager.responseConsentHandler(postConsentResponse.copy(applies = dataStorage.gdprApplies), consentManagerUtils)
                    sPConsentsSuccess?.invoke(cr)
                }
            }
            .executeOnLeft { error ->
                (error as? ConsentLibExceptionK)?.let { logger.error(error) }
            }

        campaignManager.gdprConsentStatus ?: throw InvalidConsentResponse(
            cause = null,
            "The GDPR consent object cannot be null!!!"
        )
    }

    fun sendConsentCcpa(
        consentActionImpl: ConsentActionImpl,
        env: Env,
        sPConsentsSuccess: ((SPConsents) -> Unit)?
    ): Either<CcpaCS> = check {
        val at = consentActionImpl.actionType
        if (at == ActionType.ACCEPT_ALL || at == ActionType.REJECT_ALL) {

            val getChoiceParamReq = GetChoiceParamReq(
                choiceType = at.toChoiceTypeParam(),
                accountId = spConfig.accountId.toLong(),
                propertyId = spConfig.propertyId.toLong(),
                env = env,
                metadataArg = campaignManager.metaDataResp?.toMetaDataArg()?.copy(gdpr = null),
                includeData = IncludeData.generateIncludeDataForGetChoice(
                    gppData = spConfig.extractIncludeGppDataParamIfEligible(),
                ),
                hasCsp = true,
                includeCustomVendorsRes = false,
                withSiteActions = false,
            )

            networkClient.getChoice(getChoiceParamReq)
                .executeOnRight { ccpaResponse ->
                    campaignManager.ccpaConsentStatus = ccpaResponse.ccpa?.copy(uuid = campaignManager.ccpaConsentStatus?.uuid)
                    val consentHandler = ConsentManager.responseConsentHandler(
                        ccpaResponse.ccpa?.copy(uuid = campaignManager.ccpaConsentStatus?.uuid, applies = dataStorage.ccpaApplies),
                        consentManagerUtils
                    )
                    sPConsentsSuccess?.invoke(consentHandler)
                }
                .executeOnLeft { error ->
                    (error as? ConsentLibExceptionK)?.let { logger.error(error) }
                }
        }

        val messageId: Long? = campaignManager.ccpaMessageMetaData?.messageId?.toLong()

        val body = postChoiceCcpaBody(
            sampleRate = dataStorage.ccpaSamplingValue,
            propertyId = spConfig.propertyId.toLong(),
            messageId = messageId,
            saveAndExitVariables = consentActionImpl.saveAndExitVariablesOptimized,
            authid = authId,
            uuid = campaignManager.ccpaConsentStatus?.uuid,
            sendPvData = dataStorage.ccpaSamplingResult,
            pubData = consentActionImpl.pubData.toJsonObject(),
        )

        val postConsentParams = PostChoiceParamReq(
            env = env,
            actionType = at,
            body = body
        )

        networkClient.storeCcpaChoice(postConsentParams)
            .executeOnRight { postConsentResponse ->
                campaignManager.ccpaUuid = postConsentResponse.uuid
                campaignManager.ccpaConsentStatus =
                    if (postConsentResponse.webConsentPayload != null) {
                        postConsentResponse
                    } else {
                        postConsentResponse.copy(
                            webConsentPayload = campaignManager.ccpaConsentStatus?.webConsentPayload
                        )
                    }

                // don't overwrite gdpr consents if the action is accept all or reject all
                // because the response from those endpoints does not contain a full consent
                // object.
                if (at != ActionType.ACCEPT_ALL && at != ActionType.REJECT_ALL) {
                    val consentHandler = ConsentManager.responseConsentHandler(postConsentResponse.copy(applies = dataStorage.ccpaApplies), consentManagerUtils)
                    sPConsentsSuccess?.invoke(consentHandler)
                }
            }
            .executeOnLeft { error ->
                (error as? ConsentLibExceptionK)?.let { logger.error(error) }
            }

        campaignManager.ccpaConsentStatus ?: throw InvalidConsentResponse(
            cause = null,
            "The CCPA consent object cannot be null!!!"
        )
    }

    private fun triggerConsentStatus(
        messageReq: MessagesParamReq,
        gdprApplies: Boolean?,
        ccpaApplies: Boolean?,
        usNatApplies: Boolean?,
    ): Either<ConsentStatusResp> {
        val csParams = messageReq.toConsentStatusParamReq(
            gdprUuid = campaignManager.gdprUuid,
            ccpaUuid = campaignManager.ccpaUuid,
            usNatUuid = campaignManager.usNatConsentData?.uuid,
            localState = campaignManager.messagesOptimizedLocalState,
        )

        return getConsentStatus(csParams)
            .executeOnRight {
                dataStorage.updateLocalDataVersion()
                campaignManager.apply {
                    campaignManager.handleOldLocalData()
                    messagesOptimizedLocalState = it.localState
                    it.consentStatusData?.let { csd ->
                        // GDPR
                        gdprConsentStatus = csd.gdpr?.copy(applies = gdprApplies)
                        gdprUuid = csd.gdpr?.uuid
                        gdprDateCreated = csd.gdpr?.dateCreated
                        csd.gdpr?.expirationDate?.let { exDate -> dataStorage.gdprExpirationDate = exDate }

                        // CCPA
                        ccpaConsentStatus = csd.ccpa?.copy(applies = ccpaApplies)
                        ccpaUuid = csd.ccpa?.uuid
                        ccpaDateCreated = csd.ccpa?.dateCreated
                        csd.ccpa?.expirationDate?.let { exDate -> dataStorage.ccpaExpirationDate = exDate }

                        // USnat
                        usNatConsentData = csd.usnat?.copy(applies = usNatApplies)
                    }
                }
            }
    }
}

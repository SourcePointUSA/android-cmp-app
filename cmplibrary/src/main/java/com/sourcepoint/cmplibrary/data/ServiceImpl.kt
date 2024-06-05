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
import com.sourcepoint.cmplibrary.data.network.model.optimized.includeData.buildIncludeData
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.* //ktlint-disable
import com.sourcepoint.cmplibrary.exception.CampaignType.* //ktlint-disable
import com.sourcepoint.cmplibrary.exception.CampaignType.CCPA
import com.sourcepoint.cmplibrary.exception.CampaignType.GDPR
import com.sourcepoint.cmplibrary.exception.ConsentLibExceptionK
import com.sourcepoint.cmplibrary.exception.InvalidConsentResponse
import com.sourcepoint.cmplibrary.exception.NoInternetConnectionException
import com.sourcepoint.cmplibrary.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.GDPRPurposeGrants
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.util.check
import com.sourcepoint.cmplibrary.util.extensions.* //ktlint-disable
import com.sourcepoint.cmplibrary.util.extensions.isIncluded
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

            campaignManager.handleAuthIdOrPropertyIdChange(
                newAuthId = messageReq.authId,
                newPropertyId = spConfig.propertyId,
            )

            campaignManager.deleteExpiredConsents()

            val metadataResponse = this.getMetaData(messageReq.toMetaDataParamReq(campaigns4Config))
                .executeOnRight { metaDataResponse -> handleMetaDataResponse(metaDataResponse) }
                .executeOnLeft { metaDataError ->
                    onFailure(metaDataError, true)
                    return@executeOnWorkerThread
                }

            campaignManager.consentStatusLog(messageReq.authId)
            if (campaignManager.shouldCallConsentStatus(messageReq.authId)) {
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

            campaignManager.reConsentGdpr(
                additionsChangeDate = campaignManager.gdprAdditionsChangeDate,
                legalBasisChangeDate = campaignManager.gdprLegalBasisChangeDate,
            )
                ?.let {
                    campaignManager.gdprConsentStatus = campaignManager.gdprConsentStatus?.copy(consentStatus = it)
                }

            campaignManager.reConsentUsnat(
                additionsChangeDate = campaignManager.usnatAdditionsChangeDate,
            )
                ?.let { campaignManager.usNatConsentData = campaignManager.usNatConsentData?.copy(consentStatus = it) }

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
                    includeData = buildIncludeData(gppDataValue = campaignManager.spConfig.getGppCustomOption())
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

                            if (spConfig.isIncluded(CCPA)) {
                                dataStorage.gppData = it.campaigns?.ccpa?.gppData?.toMapOfAny()
                            }
                            if (spConfig.isIncluded(USNAT)) {
                                dataStorage.gppData = it.campaigns?.usNat?.gppData?.toMapOfAny()
                            }

                            campaignManager.run {
                                if ((messageReq.authId != null || campaignManager.shouldCallConsentStatus(messageReq.authId)).not()) {
                                    if (spConfig.isIncluded(GDPR)) {
                                        this.gdprConsentStatus = it.campaigns?.gdpr?.toGdprCS(metadataResponse.getOrNull()?.gdpr?.applies)
                                    }
                                    if (spConfig.isIncluded(CCPA)) {
                                        this.ccpaConsentStatus = it.campaigns?.ccpa?.toCcpaCS(metadataResponse.getOrNull()?.ccpa?.applies)
                                    }
                                    if (spConfig.isIncluded(USNAT)) {
                                        this.usNatConsentData = usNatConsentData?.copy(applies = metadataResponse.getOrNull()?.usNat?.applies)
                                    }
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
        env: Env,
        consentAction: ConsentActionImpl,
        onSpConsentsSuccess: ((SPConsents) -> Unit)?,
    ): Either<ChoiceResp> {
        return if (connectionManager.isConnected) {
            when (consentAction.campaignType) {
                GDPR -> {
                    sendConsentGdpr(
                        env = env,
                        consentAction = consentAction,
                        onSpConsentsSuccess = onSpConsentsSuccess,
                    ).map { gdpr -> ChoiceResp(gdpr = gdpr) }
                }
                CCPA -> {
                    sendConsentCcpa(
                        env = env,
                        consentAction = consentAction,
                        onSpConsentsSuccess = onSpConsentsSuccess,
                    ).map { ccpa -> ChoiceResp(ccpa = ccpa) }
                }
                USNAT -> {
                    sendConsentUsNat(
                        env = env,
                        consentAction = consentAction,
                        onSpConsentSuccess = onSpConsentsSuccess,
                    ).map { usNat -> ChoiceResp(usNat = usNat) }
                }
            }
        } else {
            consentManagerUtils
                .spStoredConsent
                .executeOnRight { onSpConsentsSuccess?.invoke(it) }
                .map { campaignManager.storeChoiceResp }
        }
    }

    fun sendConsentGdpr(
        env: Env,
        consentAction: ConsentActionImpl,
        onSpConsentsSuccess: ((SPConsents) -> Unit)?
    ): Either<GdprCS> = check {
        var getResp: ChoiceResp? = null
        if (consentAction.actionType.isAcceptOrRejectAll()) {
            val getChoiceParamReq = GetChoiceParamReq(
                choiceType = consentAction.actionType.toChoiceTypeParam(),
                accountId = spConfig.accountId.toLong(),
                propertyId = spConfig.propertyId.toLong(),
                env = env,
                metadataArg = campaignManager.metaDataResp?.toMetaDataArg()?.copy(ccpa = null, usNat = null),
                includeData = buildIncludeData(gppDataValue = campaignManager.spConfig.getGppCustomOption())
            )

            getResp = networkClient.getChoice(getChoiceParamReq)
                .executeOnRight { response ->
                    response.gdpr?.let { responseGdpr ->
                        campaignManager.gdprConsentStatus = responseGdpr.copy(uuid = campaignManager.gdprConsentStatus?.uuid)
                        val spConsents = ConsentManager.responseConsentHandler(
                                gdpr = responseGdpr.copy(
                                        uuid = campaignManager.gdprConsentStatus?.uuid,
                                        applies = dataStorage.gdprApplies,
                                ),
                                consentManagerUtils = consentManagerUtils,
                        )
                        onSpConsentsSuccess?.invoke(spConsents)
                    }
                }
                .executeOnLeft { error ->
                    (error as? ConsentLibExceptionK)?.let { logger.error(error) }
                    val spConsents = ConsentManager.responseConsentHandler(
                        gdpr = campaignManager.gdprConsentStatus?.copy(applies = dataStorage.gdprApplies),
                        consentManagerUtils = consentManagerUtils,
                    )
                    onSpConsentsSuccess?.invoke(spConsents)
                }
                .getOrNull()
        }

        val shouldWaitForPost = consentAction.actionType.isAcceptOrRejectAll().not() || getResp?.gdpr == null

        networkClient.storeGdprChoice(PostChoiceParamReq(
            env = env,
            actionType = consentAction.actionType,
            body = postChoiceGdprBody(
                sampleRate = dataStorage.gdprSamplingValue,
                propertyId = spConfig.propertyId.toLong(),
                messageId = campaignManager.gdprMessageMetaData?.messageId?.toLong(),
                granularStatus = campaignManager.gdprConsentStatus?.consentStatus?.granularStatus,
                consentAllRef = getResp?.gdpr?.consentAllRef,
                vendorListId = getResp?.gdpr?.vendorListId,
                saveAndExitVariables = consentAction.saveAndExitVariablesOptimized,
                authid = authId,
                uuid = campaignManager.gdprConsentStatus?.uuid,
                sendPvData = dataStorage.gdprSamplingResult,
                pubData = consentAction.pubData.toJsonObject(),
                includeData = buildIncludeData(gppDataValue = campaignManager.spConfig.getGppCustomOption())
            )
        ))
            .executeOnRight { postConsentResponse ->
                campaignManager.gdprUuid = postConsentResponse.uuid
                campaignManager.gdprConsentStatus = campaignManager.gdprConsentStatus
                    ?.copy(uuid = postConsentResponse.uuid)

                // only overwrite the consent object on Save & Exit.
                // since the consent object was already saved on the response of the GET choice
                // when accepting / rejecting all
                if (shouldWaitForPost) {
                    campaignManager.gdprConsentStatus = postConsentResponse
                }
            }
            .executeOnLeft { error ->
                (error as? ConsentLibExceptionK)?.let { logger.error(error) }
            }

        if (shouldWaitForPost) {
            val spConsents = ConsentManager.responseConsentHandler(
                gdpr = campaignManager.gdprConsentStatus?.copy(applies = dataStorage.gdprApplies),
                consentManagerUtils = consentManagerUtils,
            )
            onSpConsentsSuccess?.invoke(spConsents)
        }

        campaignManager.gdprConsentStatus ?: throw InvalidConsentResponse(
            cause = null,
            description = "The GDPR consent object cannot be null!!!",
        )
    }

    fun sendConsentCcpa(
        env: Env,
        consentAction: ConsentActionImpl,
        onSpConsentsSuccess: ((SPConsents) -> Unit)?
    ): Either<CcpaCS> = check {
        var getResp: ChoiceResp? = null
        if (consentAction.actionType.isAcceptOrRejectAll()) {
            getResp = networkClient.getChoice(GetChoiceParamReq(
                choiceType = consentAction.actionType.toChoiceTypeParam(),
                accountId = spConfig.accountId.toLong(),
                propertyId = spConfig.propertyId.toLong(),
                env = env,
                metadataArg = campaignManager.metaDataResp?.toMetaDataArg()?.copy(gdpr = null, usNat = null),
                includeData = buildIncludeData(gppDataValue = campaignManager.spConfig.getGppCustomOption())
            ))
                .executeOnRight { response ->
                    response.ccpa?.let { ccpaResponse ->
                        campaignManager.ccpaConsentStatus = ccpaResponse.copy(uuid = campaignManager.ccpaConsentStatus?.uuid)
                        onSpConsentsSuccess?.invoke(ConsentManager.responseConsentHandler(
                                ccpa = ccpaResponse.copy(
                                        uuid = campaignManager.ccpaConsentStatus?.uuid,
                                        applies = dataStorage.ccpaApplies,
                                ),
                                consentManagerUtils = consentManagerUtils,
                        ))
                    }
                }
                .executeOnLeft { error ->
                    (error as? ConsentLibExceptionK)?.let { logger.error(error) }
                    val spConsents = ConsentManager.responseConsentHandler(
                        gdpr = campaignManager.gdprConsentStatus?.copy(applies = dataStorage.gdprApplies),
                        consentManagerUtils = consentManagerUtils,
                    )
                    onSpConsentsSuccess?.invoke(spConsents)
                }
                .getOrNull()
        }

        val shouldWaitForPost = consentAction.actionType.isAcceptOrRejectAll().not() || getResp?.ccpa == null

        networkClient.storeCcpaChoice(PostChoiceParamReq(
                env = env,
                actionType = consentAction.actionType,
                body = postChoiceCcpaBody(
                        sampleRate = dataStorage.ccpaSamplingValue,
                        propertyId = spConfig.propertyId.toLong(),
                        messageId = campaignManager.ccpaMessageMetaData?.messageId?.toLong(),
                        saveAndExitVariables = consentAction.saveAndExitVariablesOptimized,
                        authid = authId,
                        uuid = campaignManager.ccpaConsentStatus?.uuid,
                        sendPvData = dataStorage.ccpaSamplingResult,
                        pubData = consentAction.pubData.toJsonObject(),
                        includeData = buildIncludeData(gppDataValue = campaignManager.spConfig.getGppCustomOption())
                )
        ))
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
            }
            .executeOnLeft { error ->
                (error as? ConsentLibExceptionK)?.let { logger.error(error) }
            }

        // don't overwrite gdpr consents if the action is accept all or reject all
        // because the response from those endpoints does not contain a full consent
        // object.
        if (shouldWaitForPost) {
            val spConsents = ConsentManager.responseConsentHandler(
                ccpa = campaignManager.ccpaConsentStatus?.copy(applies = dataStorage.ccpaApplies),
                consentManagerUtils = consentManagerUtils,
            )
            onSpConsentsSuccess?.invoke(spConsents)
        }

        campaignManager.ccpaConsentStatus ?: throw InvalidConsentResponse(
            cause = null,
            description = "The CCPA consent object cannot be null!!!",
        )
    }

    fun sendConsentUsNat(
        env: Env,
        consentAction: ConsentActionImpl,
        onSpConsentSuccess: ((SPConsents) -> Unit)?,
    ): Either<USNatConsentData> = check {
        networkClient.storeUsNatChoice(PostChoiceParamReq(
            env = env,
            actionType = consentAction.actionType,
            body = postChoiceUsNatBody(
                granularStatus = campaignManager.usNatConsentData?.consentStatus?.granularStatus,
                messageId = campaignManager.usNatConsentData?.messageMetaData?.messageId?.toLong(),
                saveAndExitVariables = consentAction.saveAndExitVariablesOptimized,
                propertyId = spConfig.propertyId.toLong(),
                pubData = consentAction.pubData.toJsonObject(),
                sendPvData = dataStorage.usNatSamplingResult,
                sampleRate = dataStorage.usNatSamplingValue,
                uuid = campaignManager.usNatConsentData?.uuid,
                vendorListId = campaignManager.metaDataResp?.usNat?.vendorListId,
                includeData = buildIncludeData(gppDataValue = campaignManager.spConfig.getGppCustomOption()),
                authId = campaignManager.authId
            ),
        ))
            .executeOnRight { postChoiceUsNatResponse ->
                campaignManager.usNatConsentData = postChoiceUsNatResponse
            }
            .executeOnLeft { error ->
                (error as? ConsentLibExceptionK)?.let { logger.error(error) }
            }

        onSpConsentSuccess?.invoke(ConsentManager.responseConsentHandler(
            usNat = campaignManager.usNatConsentData?.copy(applies = dataStorage.usNatApplies),
            consentManagerUtils = consentManagerUtils,
        ))

        campaignManager.usNatConsentData ?: throw InvalidConsentResponse(
            cause = null,
            description = "The UsNat consent data cannot be null!!!",
        )
    }

    private fun triggerConsentStatus(
        messageReq: MessagesParamReq,
        gdprApplies: Boolean?,
        ccpaApplies: Boolean?,
        usNatApplies: Boolean?,
    ): Either<ConsentStatusResp> = getConsentStatus(messageReq.toConsentStatusParamReq(campaignManager))
        .executeOnRight {
            campaignManager.apply {
                campaignManager.handleOldLocalData()
                messagesOptimizedLocalState = it.localState
                it.consentStatusData?.let { csd ->
                    if (spConfig.isIncluded(GDPR)) {
                        gdprConsentStatus = csd.gdpr?.copy(applies = gdprApplies)
                        gdprUuid = csd.gdpr?.uuid
                        gdprDateCreated = csd.gdpr?.dateCreated
                        csd.gdpr?.expirationDate?.let { exDate -> dataStorage.gdprExpirationDate = exDate }
                    }

                    if (spConfig.isIncluded(CCPA)) {
                        ccpaConsentStatus = csd.ccpa?.copy(applies = ccpaApplies)
                        ccpaUuid = csd.ccpa?.uuid
                        ccpaDateCreated = csd.ccpa?.dateCreated
                        csd.ccpa?.expirationDate?.let { exDate -> dataStorage.ccpaExpirationDate = exDate }
                    }

                    if (spConfig.isIncluded(USNAT)) {
                        usNatConsentData = csd.usnat?.copy(applies = usNatApplies)
                    }
                }
            }
        }
    }

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
import com.sourcepoint.cmplibrary.data.network.model.optimized.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.optimized.сonsentStatus.CcpaCS
import com.sourcepoint.cmplibrary.data.network.model.optimized.сonsentStatus.GdprCS
import com.sourcepoint.cmplibrary.data.network.model.optimized.сonsentStatus.USNatConsentData
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.ChoiceResp
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
import com.sourcepoint.cmplibrary.model.exposed.CcpaStatus.rejectedAll
import com.sourcepoint.cmplibrary.model.exposed.CcpaStatus.rejectedSome
import com.sourcepoint.cmplibrary.model.exposed.GDPRPurposeGrants
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.model.exposed.toJsonElement
import com.sourcepoint.cmplibrary.util.check
import com.sourcepoint.cmplibrary.util.extensions.* //ktlint-disable
import com.sourcepoint.cmplibrary.util.extensions.isIncluded
import com.sourcepoint.cmplibrary.util.extensions.toMapOfAny
import com.sourcepoint.mobile_core.models.SPActionType
import com.sourcepoint.mobile_core.models.SPIDFAStatus
import com.sourcepoint.mobile_core.network.requests.ChoiceAllMetaDataRequest
import com.sourcepoint.mobile_core.network.requests.ConsentStatusRequest
import com.sourcepoint.mobile_core.network.requests.IncludeData
import com.sourcepoint.mobile_core.network.requests.MetaDataRequest
import com.sourcepoint.mobile_core.network.requests.PvDataRequest
import com.sourcepoint.mobile_core.network.requests.USNatChoiceRequest
import com.sourcepoint.mobile_core.network.responses.ChoiceAllResponse
import com.sourcepoint.mobile_core.network.responses.MetaDataResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.jsonObject

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
internal class ServiceImpl(
    private val networkClient: NetworkClient,
    private val campaignManager: CampaignManager,
    private val consentManagerUtils: ConsentManagerUtils,
    private val dataStorage: DataStorage,
    private val logger: Logger,
    private val execManager: ExecutorManager,
    private val connectionManager: ConnectionManager,
) : Service, NetworkClient by networkClient, CampaignManager by campaignManager {

    private val transitionCCPAAuth: Boolean get() = spConfig.hasTransitionCCPAAuth() && campaignManager.authId != null
    private val transitionCCPAOptedOut: Boolean get() = ccpaConsentStatus != null &&
        campaignManager.usNatConsentData == null &&
        (ccpaConsentStatus?.status == rejectedSome || ccpaConsentStatus?.status == rejectedAll)
    private val transitionCCPAUSnatDateCreated: String? get() =
        if (transitionCCPAOptedOut) ccpaConsentStatus?.dateCreated else usNatConsentData?.dateCreated

    override fun sendCustomConsentServ(
        consentUUID: String,
        propertyId: Int,
        vendors: List<String>,
        categories: List<String>,
        legIntCategories: List<String>
    ): Either<GdprCS> = check {

        if (connectionManager.isConnected.not()) throw NoInternetConnectionException()

        try {
            val response = networkClient.sendCustomConsent(
                consentUUID = consentUUID,
                propertyId = propertyId,
                vendors = vendors,
                categories = categories,
                legIntCategories = legIntCategories
            )
            if (campaignManager.gdprConsentStatus == null) {
                throw IllegalStateException("CustomConsent cannot be executed. Consent is missing!!!")
            }
            val grants = response.grants.map {
                it.key to GDPRPurposeGrants(it.value.vendorGrant, it.value.purposeGrants)
            }.toMap()
            campaignManager.gdprConsentStatus = campaignManager.gdprConsentStatus?.copy(
                grants = grants,
                categories = response.categories,
                vendors = response.vendors,
                legIntCategories = response.legIntCategories,
                specialFeatures = response.specialFeatures
            )
            return@check campaignManager.gdprConsentStatus!!
        } catch (error: Throwable) {
            throw error
        }
    }

    override fun deleteCustomConsentToServ(
        consentUUID: String,
        propertyId: Int,
        vendors: List<String>,
        categories: List<String>,
        legIntCategories: List<String>
    ): Either<GdprCS> = check {
        if (connectionManager.isConnected.not()) throw NoInternetConnectionException()

        try {
            val response = networkClient.deleteCustomConsentTo(
                consentUUID = consentUUID,
                propertyId = propertyId,
                vendors = vendors,
                categories = categories,
                legIntCategories = legIntCategories
            )
            if (campaignManager.gdprConsentStatus == null) {
                throw IllegalStateException("CustomConsent cannot be executed. Consent is missing!!!")
            }
            val grants = response.grants.map {
                it.key to GDPRPurposeGrants(it.value.vendorGrant, it.value.purposeGrants)
            }.toMap()
            campaignManager.gdprConsentStatus = campaignManager.gdprConsentStatus?.copy(
                grants = grants,
                categories = response.categories,
                vendors = response.vendors,
                legIntCategories = response.legIntCategories,
                specialFeatures = response.specialFeatures
            )
            return@check campaignManager.gdprConsentStatus!!
        } catch (error: Throwable) {
            throw error
        }
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

            val metadataResponse: MetaDataResponse

            try {
                metadataResponse = networkClient.getMetaData(
                    campaigns = MetaDataRequest.Campaigns(
                        gdpr = campaigns4Config.firstOrNull { it.campaignType == GDPR }?.let {
                            MetaDataRequest.Campaigns.Campaign(groupPmId = it.groupPmId)
                        },
                        usnat = campaigns4Config.firstOrNull { it.campaignType == USNAT }?.let {
                            MetaDataRequest.Campaigns.Campaign(groupPmId = it.groupPmId)
                        },
                        ccpa = campaigns4Config.firstOrNull { it.campaignType == CCPA }?.let {
                            MetaDataRequest.Campaigns.Campaign(groupPmId = it.groupPmId)
                        }
                    )
                )
                handleMetaDataResponse(metadataResponse)
            } catch (error: Throwable) {
                onFailure(error, true)
                return@executeOnWorkerThread
            }

            if (campaignManager.shouldCallConsentStatus(messageReq.authId)) {
                try {
                    triggerConsentStatus(
                        messageReq = messageReq,
                        gdprApplies = metadataResponse.gdpr?.applies,
                        ccpaApplies = metadataResponse.ccpa?.applies,
                        usNatApplies = metadataResponse.usnat?.applies,
                    )
                } catch (error: Throwable) {
                    onFailure(error, true)
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
                val messagesParamReq = MessagesParamReq(
                    accountId = messageReq.accountId,
                    propertyId = messageReq.propertyId,
                    authId = messageReq.authId,
                    propertyHref = messageReq.propertyHref,
                    env = messageReq.env,
                    body = getMessageBody(
                        accountId = messageReq.accountId,
                        propertyHref = messageReq.propertyHref,
                        gdprConsentStatus = campaignManager.gdprConsentStatus?.consentStatus,
                        ccpaConsentStatus = campaignManager.ccpaConsentStatus?.status?.name,
                        usNatConsentStatus = campaignManager.usNatConsentData?.consentStatus,
                        campaigns = campaignManager.campaigns4Config,
                        consentLanguage = campaignManager.messageLanguage.value,
                        campaignEnv = campaignManager.spConfig.campaignsEnv,
                        includeData = buildIncludeData(gppDataValue = campaignManager.spConfig.getGppCustomOption())
                    ).toString(),
                    metadataArg = MetaDataArg(
                        gdpr = metadataResponse.gdpr?.let { gdpr ->
                            MetaDataArg.GdprArg(
                                applies = gdpr.applies,
                                hasLocalData = gdprUuid != null,
                                groupPmId = getGroupId(GDPR),
                                targetingParams = campaigns4Config.firstOrNull { it.campaignType == GDPR }?.targetingParams?.toJsonElement(),
                                uuid = gdprUuid
                            )
                        },
                        usNat = metadataResponse.usnat?.let { usnat ->
                            MetaDataArg.UsNatArg(
                                applies = usnat.applies,
                                hasLocalData = usNatConsentData?.uuid != null,
                                groupPmId = getGroupId(USNAT),
                                targetingParams = campaigns4Config.firstOrNull { it.campaignType == USNAT }?.targetingParams?.toJsonElement(),
                                uuid = usNatConsentData?.uuid,
                                transitionCCPAAuth = transitionCCPAAuth,
                                optedOut = transitionCCPAOptedOut,
                                dateCreated = transitionCCPAUSnatDateCreated
                            )
                        },
                        ccpa = metadataResponse.ccpa?.let { ccpa ->
                            MetaDataArg.CcpaArg(
                                applies = ccpa.applies,
                                hasLocalData = ccpaUuid != null,
                                groupPmId = getGroupId(CCPA),
                                targetingParams = campaigns4Config.firstOrNull { it.campaignType == CCPA }?.targetingParams?.toJsonElement(),
                                uuid = ccpaUuid
                            )
                        },
                    ),
                    nonKeyedLocalState = campaignManager.nonKeyedLocalState?.jsonObject,
                    localState = campaignManager.messagesOptimizedLocalState?.let {
                        JsonConverter.converter.decodeFromString(it)
                    }
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
                            messagesOptimizedLocalState = JsonConverter.converter.encodeToString(it.localState)
                            nonKeyedLocalState = it.nonKeyedLocalState
                            gdprMessageMetaData = it.campaigns?.gdpr?.messageMetaData
                            ccpaMessageMetaData = it.campaigns?.ccpa?.messageMetaData
                        }

                        if (campaignManager.hasLocalData.not()) {
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
                                        this.gdprConsentStatus = it.campaigns?.gdpr?.toGdprCS(metadataResponse.gdpr?.applies)
                                    }
                                    if (spConfig.isIncluded(CCPA)) {
                                        this.ccpaConsentStatus = it.campaigns?.ccpa?.toCcpaCS(metadataResponse.ccpa?.applies)
                                    }
                                    if (spConfig.isIncluded(USNAT)) {
                                        this.usNatConsentData = usNatConsentData?.copy(applies = metadataResponse.usnat?.applies)
                                    }
                                }
                            }
                        }

                        execManager.executeOnMain { onSuccess(it) }
                    }
            } else {
                execManager.executeOnMain { showConsent() }
            }

            pvData(messageReq, onFailure)
        }
    }

    fun pvData(messageReq: MessagesParamReq, onFailure: (Throwable, Boolean) -> Unit) {
        if (spConfig.isIncluded(GDPR)) {
            dataStorage.gdprSampled = sampleAndPvData(
                wasSampled = dataStorage.gdprSampled,
                rate = dataStorage.gdprSampleRate,
                pvDataRequest = PvDataRequest(
                    gdpr = campaignManager.getGdprPvDataBody(messageReq),
                    ccpa = null,
                    usnat = null
                ),
                onFailure = onFailure
            )
        }

        if (spConfig.isIncluded(CCPA)) {
            dataStorage.ccpaSampled = sampleAndPvData(
                wasSampled = dataStorage.ccpaSampled,
                rate = dataStorage.ccpaSampleRate,
                pvDataRequest = PvDataRequest(
                    gdpr = null,
                    ccpa = campaignManager.getCcpaPvDataBody(messageReq),
                    usnat = null
                ),
                onFailure = onFailure
            )
        }

        if (spConfig.isIncluded(USNAT)) {
            dataStorage.usnatSampled = sampleAndPvData(
                wasSampled = dataStorage.usnatSampled,
                rate = dataStorage.usnatSampleRate,
                pvDataRequest = PvDataRequest(
                    gdpr = null,
                    ccpa = null,
                    usnat = campaignManager.getUsNatPvDataBody(messageReq)
                ),
                onFailure = onFailure
            )
        }
    }

    private fun sampleAndPvData(
        wasSampled: Boolean?,
        rate: Double,
        pvDataRequest: PvDataRequest,
        onFailure: (Throwable, Boolean) -> Unit
    ): Boolean {
        if (wasSampled == false) return false

        val sampled = wasSampled == true || consentManagerUtils.sample(rate)
        if (sampled) {
            try {
                val response = postPvData(pvDataRequest)
                response.usnat?.let {
                    usNatConsentData = usNatConsentData?.copy(uuid = it.uuid)
                }
                response.gdpr?.let {
                    gdprConsentStatus = gdprConsentStatus?.copy(uuid = it.uuid)
                }
                response.ccpa?.let {
                    ccpaConsentStatus = ccpaConsentStatus?.copy(uuid = it.uuid)
                }
            }
            catch (error: Throwable) {
                onFailure(error, false)
            }
        }
        return sampled
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

    private fun sendConsentGdpr(
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
                metadataArg = GetChoiceParamReq.MetaData(
                    gdpr = GetChoiceParamReq.MetaData.Campaign(applies = metaDataResp?.gdpr?.applies ?: false)
                ),
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

        networkClient.storeGdprChoice(
            PostChoiceParamReq(
                env = env,
                actionType = consentAction.actionType,
                body = postChoiceGdprBody(
                    sampleRate = dataStorage.gdprSampleRate,
                    propertyId = spConfig.propertyId.toLong(),
                    messageId = campaignManager.gdprMessageMetaData?.messageId?.toLong(),
                    granularStatus = campaignManager.gdprConsentStatus?.consentStatus?.granularStatus,
                    consentAllRef = getResp?.gdpr?.consentAllRef,
                    vendorListId = getResp?.gdpr?.vendorListId,
                    saveAndExitVariables = consentAction.saveAndExitVariablesOptimized,
                    authid = authId,
                    uuid = campaignManager.gdprConsentStatus?.uuid,
                    sendPvData = dataStorage.gdprSampled,
                    pubData = consentAction.pubData.toJsonObject(),
                    includeData = buildIncludeData(gppDataValue = campaignManager.spConfig.getGppCustomOption())
                )
            )
        )
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

    private fun sendConsentCcpa(
        env: Env,
        consentAction: ConsentActionImpl,
        onSpConsentsSuccess: ((SPConsents) -> Unit)?
    ): Either<CcpaCS> = check {
        var getResp: ChoiceResp? = null
        if (consentAction.actionType.isAcceptOrRejectAll()) {
            getResp = networkClient.getChoice(
                GetChoiceParamReq(
                    choiceType = consentAction.actionType.toChoiceTypeParam(),
                    accountId = spConfig.accountId.toLong(),
                    propertyId = spConfig.propertyId.toLong(),
                    env = env,
                    metadataArg = GetChoiceParamReq.MetaData(
                        ccpa = GetChoiceParamReq.MetaData.Campaign(applies = metaDataResp?.ccpa?.applies ?: false)
                    ),
                    includeData = buildIncludeData(gppDataValue = campaignManager.spConfig.getGppCustomOption())
                )
            )
                .executeOnRight { response ->
                    response.ccpa?.let { ccpaResponse ->
                        campaignManager.ccpaConsentStatus = ccpaResponse.copy(uuid = campaignManager.ccpaConsentStatus?.uuid)
                        onSpConsentsSuccess?.invoke(
                            ConsentManager.responseConsentHandler(
                                ccpa = ccpaResponse.copy(
                                    uuid = campaignManager.ccpaConsentStatus?.uuid,
                                    applies = dataStorage.ccpaApplies,
                                ),
                                consentManagerUtils = consentManagerUtils,
                            )
                        )
                    }
                }
                .executeOnLeft { error ->
                    (error as? ConsentLibExceptionK)?.let { logger.error(error) }
                    val spConsents = ConsentManager.responseConsentHandler(
                        ccpa = campaignManager.ccpaConsentStatus?.copy(applies = dataStorage.ccpaApplies),
                        consentManagerUtils = consentManagerUtils,
                    )
                    onSpConsentsSuccess?.invoke(spConsents)
                }
                .getOrNull()
        }

        val shouldWaitForPost = consentAction.actionType.isAcceptOrRejectAll().not() || getResp?.ccpa == null

        networkClient.storeCcpaChoice(
            PostChoiceParamReq(
                env = env,
                actionType = consentAction.actionType,
                body = postChoiceCcpaBody(
                    sampleRate = dataStorage.ccpaSampleRate,
                    propertyId = spConfig.propertyId.toLong(),
                    messageId = campaignManager.ccpaMessageMetaData?.messageId?.toLong(),
                    saveAndExitVariables = consentAction.saveAndExitVariablesOptimized,
                    authid = authId,
                    uuid = campaignManager.ccpaConsentStatus?.uuid,
                    sendPvData = dataStorage.ccpaSampled,
                    pubData = consentAction.pubData.toJsonObject(),
                    includeData = buildIncludeData(gppDataValue = campaignManager.spConfig.getGppCustomOption())
                )
            )
        )
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

        // don't overwrite ccpa consents if the action is accept all or reject all
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

    private fun sendConsentUsNat(
        env: Env,
        consentAction: ConsentActionImpl,
        onSpConsentSuccess: ((SPConsents) -> Unit)?,
    ): Either<USNatConsentData> = check {
        var getResp: ChoiceAllResponse? = null
        if (consentAction.actionType.isAcceptOrRejectAll()) {
            try {
                getResp = networkClient.getChoice(
                    actionType = SPActionType.entries.first { it.type == consentAction.actionType.code },
                    accountId = spConfig.accountId,
                    propertyId = spConfig.propertyId,
                    idfaStatus = SPIDFAStatus.Unknown,
                    metadata = ChoiceAllMetaDataRequest(
                        gdpr = ChoiceAllMetaDataRequest.Campaign(false),
                        ccpa = ChoiceAllMetaDataRequest.Campaign(false),
                        usnat = ChoiceAllMetaDataRequest.Campaign(metaDataResp?.usnat?.applies ?: false)
                    ),
                    includeData = IncludeData(
                        gppData = IncludeData.GPPConfig(
                            MspaCoveredTransaction = IncludeData.MspaBinaryFlag.entries.first { it.value == campaignManager.spConfig.spGppConfig?.coveredTransaction?.type },
                            MspaOptOutOptionMode = IncludeData.MspaTernaryFlag.entries.first { it.value == campaignManager.spConfig.spGppConfig?.optOutOptionMode?.type },
                            MspaServiceProviderMode = IncludeData.MspaTernaryFlag.entries.first { it.value == campaignManager.spConfig.spGppConfig?.serviceProviderMode?.type },
                            uspString = campaignManager.spConfig.hasSupportForLegacyUSPString()?.uspString
                        )
                    )
                )

                getResp.usnat?.let { usnatResponse ->
                    campaignManager.usNatConsentData = USNatConsentData()
                        .copyingFrom(usnatResponse,metaDataResp?.usnat?.applies ?: false)
                        .copy(uuid = campaignManager.usNatConsentData?.uuid)
                    val spConsents = ConsentManager.responseConsentHandler(
                        usNat = USNatConsentData()
                            .copyingFrom(usnatResponse, dataStorage.usNatApplies)
                            .copy(uuid = campaignManager.usNatConsentData?.uuid),
                        consentManagerUtils = consentManagerUtils,
                    )
                    onSpConsentSuccess?.invoke(spConsents)
                }
            }
            catch (error: Throwable) {
                (error as? ConsentLibExceptionK)?.let { logger.error(error) }
                val spConsents = ConsentManager.responseConsentHandler(
                    usNat = campaignManager.usNatConsentData?.copy(applies = dataStorage.usNatApplies),
                    consentManagerUtils = consentManagerUtils,
                )
                onSpConsentSuccess?.invoke(spConsents)
            }
        }

        val shouldWaitForPost = consentAction.actionType.isAcceptOrRejectAll().not() || getResp?.usnat == null

        try {
            val postChoiceUsNatResponse = networkClient.storeUsNatChoice(
                actionType = SPActionType.entries.first { it.type == consentAction.actionType.code },
                request = USNatChoiceRequest(
                    authId = campaignManager.authId,
                    uuid = campaignManager.usNatConsentData?.uuid,
                    messageId = campaignManager.usNatConsentData?.messageMetaData?.messageId.toString(),
                    vendorListId = campaignManager.metaDataResp?.usnat?.vendorListId,
                    pubData = consentAction.pubData.toJsonObject(),
                    pmSaveAndExitVariables = consentAction.saveAndExitVariablesOptimized.toString(),
                    sendPVData = dataStorage.usnatSampled ?: false,
                    propertyId = spConfig.propertyId,
                    sampleRate = dataStorage.usnatSampleRate.toFloat(),
                    idfaStatus = SPIDFAStatus.Unknown,
                    granularStatus = campaignManager.usNatConsentData?.consentStatus?.granularStatus?.toCoreConsentStatusGranularStatus(),
                    includeData = IncludeData(
                        gppData = IncludeData.GPPConfig(
                            MspaCoveredTransaction = IncludeData.MspaBinaryFlag.entries.first { it.value == campaignManager.spConfig.spGppConfig?.coveredTransaction?.type },
                            MspaOptOutOptionMode = IncludeData.MspaTernaryFlag.entries.first { it.value == campaignManager.spConfig.spGppConfig?.optOutOptionMode?.type },
                            MspaServiceProviderMode = IncludeData.MspaTernaryFlag.entries.first { it.value == campaignManager.spConfig.spGppConfig?.serviceProviderMode?.type },
                            uspString = campaignManager.spConfig.hasSupportForLegacyUSPString()?.uspString
                        )
                    )
                )
            )

            campaignManager.usNatConsentData = USNatConsentData().copyingFrom(postChoiceUsNatResponse, metaDataResp?.usnat?.applies ?: false)
        }
        catch (error: Throwable) {
            (error as? ConsentLibExceptionK)?.let { logger.error(error) }
        }

        // don't overwrite usNat consents if the action is accept all or reject all
        // because the response from those endpoints does not contain a full consent
        // object.
        if (shouldWaitForPost) {
            val spConsents = ConsentManager.responseConsentHandler(
                usNat = campaignManager.usNatConsentData?.copy(applies = dataStorage.usNatApplies),
                consentManagerUtils = consentManagerUtils,
            )
            onSpConsentSuccess?.invoke(spConsents)
        }

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
    ) {
        val statusMetadata = ConsentStatusRequest.MetaData(
            gdpr = campaigns4Config.firstOrNull { it.campaignType == GDPR }?.let {
                ConsentStatusRequest.MetaData.Campaign(
                    applies = gdprApplies ?: false,
                    dateCreated = gdprConsentStatus?.dateCreated,
                    uuid = gdprConsentStatus?.uuid,
                    hasLocalData = false
                )
            },
            usnat = campaigns4Config.firstOrNull { it.campaignType == USNAT }?.let {
                ConsentStatusRequest.MetaData.USNatCampaign(
                    applies = usNatApplies ?: false,
                    dateCreated = transitionCCPAUSnatDateCreated,
                    uuid = usNatConsentData?.uuid,
                    hasLocalData = false,
                    transitionCCPAAuth = transitionCCPAAuth,
                    optedOut = transitionCCPAOptedOut
                )
            },
            ccpa = campaigns4Config.firstOrNull { it.campaignType == CCPA }?.let {
                ConsentStatusRequest.MetaData.Campaign(
                    applies = ccpaApplies ?: false,
                    dateCreated = ccpaConsentStatus?.dateCreated,
                    uuid = ccpaConsentStatus?.uuid,
                    hasLocalData = false,
                )
            }
        )
        val response = getConsentStatus(
            authId = messageReq.authId,
            metadata = statusMetadata
        )
        campaignManager.handleOldLocalData()
        messagesOptimizedLocalState = response.localState
        if (spConfig.isIncluded(GDPR)) {
            gdprConsentStatus = (gdprConsentStatus ?: GdprCS()).copyingFrom(core = response.consentStatusData.gdpr, applies = gdprApplies)
            gdprUuid = response.consentStatusData.gdpr?.uuid
            gdprDateCreated = response.consentStatusData.gdpr?.dateCreated
            response.consentStatusData.gdpr?.expirationDate?.let { exDate -> dataStorage.gdprExpirationDate = exDate }
        }

        if (spConfig.isIncluded(CCPA)) {
            ccpaConsentStatus = (ccpaConsentStatus ?: CcpaCS()).copyingFrom(core = response.consentStatusData.ccpa, applies = ccpaApplies)
            ccpaUuid = response.consentStatusData.ccpa?.uuid
            ccpaDateCreated = response.consentStatusData.ccpa?.dateCreated
            response.consentStatusData.ccpa?.expirationDate?.let { exDate -> dataStorage.ccpaExpirationDate = exDate }
        }

        if (spConfig.isIncluded(USNAT)) {
            usNatConsentData = (usNatConsentData ?: USNatConsentData()).copyingFrom(core = response.consentStatusData.usnat, applies = usNatApplies)
        }
    }
}

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
import com.sourcepoint.cmplibrary.data.network.model.optimized.сonsentStatus.USNatCS
import com.sourcepoint.cmplibrary.data.network.model.optimized.includeData.buildIncludeData
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.* //ktlint-disable
import com.sourcepoint.cmplibrary.exception.CampaignType.* //ktlint-disable
import com.sourcepoint.cmplibrary.exception.CampaignType.CCPA
import com.sourcepoint.cmplibrary.exception.CampaignType.GDPR
import com.sourcepoint.cmplibrary.exception.ConsentLibExceptionK
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
import com.sourcepoint.mobile_core.Coordinator
import com.sourcepoint.mobile_core.models.consents.State
import com.sourcepoint.mobile_core.network.requests.ChoiceAllRequest
import com.sourcepoint.mobile_core.network.requests.ConsentStatusRequest
import com.sourcepoint.mobile_core.network.requests.IncludeData
import com.sourcepoint.mobile_core.network.requests.MetaDataRequest
import com.sourcepoint.mobile_core.network.requests.PvDataRequest
import com.sourcepoint.mobile_core.network.responses.ChoiceAllResponse
import com.sourcepoint.mobile_core.network.responses.MetaDataResponse
import kotlinx.coroutines.runBlocking
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
    coreCoordinator: Coordinator
): Service = ServiceImpl(
        nc,
        campaignManager,
        consentManagerUtils,
        dataStorage,
        logger,
        execManager,
        connectionManager,
        coreCoordinator
    )

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
    private val coreCoordinator: Coordinator,
) : Service, NetworkClient by networkClient, CampaignManager by campaignManager {

    private val transitionCCPAAuth: Boolean get() = spConfig.hasTransitionCCPAAuth() && campaignManager.authId != null
    private val transitionCCPAOptedOut: Boolean get() = ccpaConsentStatus != null &&
        campaignManager.usNatCS == null &&
        (ccpaConsentStatus?.status == rejectedSome || ccpaConsentStatus?.status == rejectedAll)
    private val transitionCCPAUSnatDateCreated: String? get() =
        if (transitionCCPAOptedOut) ccpaConsentStatus?.dateCreated else usNatCS?.dateCreated

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
                ?.let { campaignManager.usNatCS = campaignManager.usNatCS?.copy(consentStatus = it) }

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
                        usNatConsentStatus = campaignManager.usNatCS?.consentStatus,
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
                                hasLocalData = usNatCS?.uuid != null,
                                groupPmId = getGroupId(USNAT),
                                targetingParams = campaigns4Config.firstOrNull { it.campaignType == USNAT }?.targetingParams?.toJsonElement(),
                                uuid = usNatCS?.uuid,
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
                            campaignManager.usNatCS
                                ?.let {
                                    campaignManager.usNatCS = campaignManager.usNatCS?.copy(
                                        message = usNat.message,
                                        messageMetaData = usNat.messageMetaData,
                                        url = usNat.url,
                                        type = usNat.type
                                    )
                                } ?: kotlin.run { campaignManager.usNatCS = usNat }
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
                                        this.usNatCS = usNatCS?.copy(applies = metadataResponse.usnat?.applies)
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
                    usNatCS = usNatCS?.copy(uuid = it.uuid)
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

    private fun buildChoiceAllCampaigns(consentAction: ConsentActionImpl): ChoiceAllRequest.ChoiceAllCampaigns{
        var gdprApplies: Boolean? = null
        var ccpaApplies: Boolean? = null
        var usnatApplies: Boolean? = null
        when (consentAction.campaignType) {
            GDPR -> { gdprApplies = metaDataResp?.gdpr?.applies }
            CCPA -> { ccpaApplies = metaDataResp?.ccpa?.applies }
            USNAT -> { usnatApplies = metaDataResp?.usnat?.applies }
        }
        return ChoiceAllRequest.ChoiceAllCampaigns(
            gdpr = ChoiceAllRequest.ChoiceAllCampaigns.Campaign(gdprApplies ?: false),
            ccpa = ChoiceAllRequest.ChoiceAllCampaigns.Campaign(ccpaApplies ?: false),
            usnat = ChoiceAllRequest.ChoiceAllCampaigns.Campaign(usnatApplies ?: false)
        )
    }

    private fun updateCoreConsentStatus() {
        coreCoordinator.authId = authId
        coreCoordinator.idfaStatus = null
        coreCoordinator.includeData = IncludeData(gppData = campaignManager.spConfig.gppCustomOptionToCore())
        coreCoordinator.state = State(
            gdpr = campaignManager.gdprConsentStatus?.toCoreGDPRConsent(),
            ccpa = campaignManager.ccpaConsentStatus?.toCoreCCPAConsent(),
            usNat = campaignManager.usNatCS?.toCoreUSNatConsent(),
            gdprMetaData = State.GDPRMetaData(
                additionsChangeDate = "",
                legalBasisChangeDate = null,
                sampleRate = dataStorage.gdprSampleRate.toFloat(),
                wasSampled = dataStorage.gdprSampled,
                wasSampledAt = null
            ),
            ccpaMetaData = State.CCPAMetaData(
                sampleRate = dataStorage.ccpaSampleRate.toFloat(),
                wasSampled = dataStorage.ccpaSampled,
                wasSampledAt = null
            ),
            usNatMetaData = State.UsNatMetaData(
                additionsChangeDate = "",
                sampleRate = dataStorage.usnatSampleRate.toFloat(),
                wasSampled = dataStorage.usnatSampled,
                wasSampledAt = null,
                vendorListId = "",
                applicableSections = emptyList()
            )
        )
    }

    private fun updateConsentStatusFromCore() {
        campaignManager.gdprConsentStatus = campaignManager.gdprConsentStatus?.copyingFrom(coreCoordinator.state.gdpr, metaDataResp?.gdpr?.applies)
        campaignManager.ccpaConsentStatus = campaignManager.ccpaConsentStatus?.copyingFrom(coreCoordinator.state.ccpa, metaDataResp?.ccpa?.applies)
        campaignManager.usNatCS = campaignManager.usNatCS?.copyingFrom(coreCoordinator.state.usNat, metaDataResp?.usnat?.applies)
        campaignManager.gdprUuid = coreCoordinator.state.gdpr?.uuid
        campaignManager.ccpaUuid = coreCoordinator.state.ccpa?.uuid
        campaignManager.usnatUuid = coreCoordinator.state.usNat?.uuid
    }

    override fun sendConsent(
        env: Env,
        consentAction: ConsentActionImpl,
        onSpConsentsSuccess: ((SPConsents) -> Unit)?,
    ) {
        if (connectionManager.isConnected) {
            updateCoreConsentStatus()
            var getResp: ChoiceAllResponse? = null
            try {
                getResp = runBlocking { coreCoordinator.getChoiceAll(consentAction.toCoreSPAction(), buildChoiceAllCampaigns(consentAction)) }
            }
            catch (error: Throwable) {
                (error as? ConsentLibExceptionK)?.let { logger.error(error) }
            }
            when (consentAction.campaignType) {
                GDPR -> {
                    try {
                        runBlocking { coreCoordinator.reportGDPRAction(consentAction.toCoreSPAction(), getResp) }
                    }
                    catch (error: Throwable) {
                        (error as? ConsentLibExceptionK)?.let { logger.error(error) }
                    }
                    updateConsentStatusFromCore()
                    onSpConsentsSuccess?.invoke(ConsentManager.responseConsentHandler(
                            gdpr = campaignManager.gdprConsentStatus,
                            consentManagerUtils = consentManagerUtils
                    ))
                }

                CCPA -> {
                    try {
                        runBlocking { coreCoordinator.reportCCPAAction(consentAction.toCoreSPAction(), getResp) }
                    }
                    catch (error: Throwable) {
                        (error as? ConsentLibExceptionK)?.let { logger.error(error) }
                    }
                    updateConsentStatusFromCore()
                    onSpConsentsSuccess?.invoke(ConsentManager.responseConsentHandler(
                            ccpa = campaignManager.ccpaConsentStatus,
                            consentManagerUtils = consentManagerUtils
                    ))
                }

                USNAT -> {
                    try {
                        runBlocking { coreCoordinator.reportUSNatAction(consentAction.toCoreSPAction(), getResp) }
                    }
                    catch (error: Throwable) {
                        (error as? ConsentLibExceptionK)?.let { logger.error(error) }
                    }
                    updateConsentStatusFromCore()
                    onSpConsentsSuccess?.invoke(ConsentManager.responseConsentHandler(
                            usNat = campaignManager.usNatCS,
                            consentManagerUtils = consentManagerUtils
                    ))
                }
            }
        } else {
            consentManagerUtils
                .spStoredConsent
                .executeOnRight { onSpConsentsSuccess?.invoke(it) }
        }
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
                    uuid = usNatCS?.uuid,
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
            usNatCS = (usNatCS ?: USNatCS()).copyingFrom(core = response.consentStatusData.usnat, applies = usNatApplies)
        }
    }
}

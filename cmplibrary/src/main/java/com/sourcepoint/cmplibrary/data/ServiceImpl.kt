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
import com.sourcepoint.cmplibrary.util.extensions.toJsonObject
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
    execManager: ExecutorManager
): Service = ServiceImpl(nc, campaignManager, consentManagerUtils, dataStorage, logger, execManager)

/**
 * Implementation os the [Service] interface
 */
private class ServiceImpl(
    private val networkClient: NetworkClient,
    private val campaignManager: CampaignManager,
    private val consentManagerUtils: ConsentManagerUtils,
    private val dataStorage: DataStorage,
    private val logger: Logger,
    private val execManager: ExecutorManager
) : Service, NetworkClient by networkClient, CampaignManager by campaignManager {

    private fun JSONArray.toArrayList(): ArrayList<String> {
        val list = arrayListOf<String>()
        for (i in 0 until this.length()) {
            list.add(this.getString(i))
        }
        return list
    }

    override fun sendCustomConsentServ(customConsentReq: CustomConsentReq, env: Env): Either<GdprCS> = check {
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

    override fun getMessages(
        messageReq: MessagesParamReq,
        pSuccess: (MessagesResp) -> Unit,
        showConsent: () -> Unit,
        pError: (Throwable) -> Unit
    ) {
        execManager.executeOnWorkerThread {
            campaignManager.authId = messageReq.authId

            val metadataResponse = this.getMetaData(messageReq.toMetaDataParamReq())
                .executeOnLeft {
                    pError(it)
                    return@executeOnWorkerThread
                }
                .executeOnRight { response ->
                    handleMetaDataLogic(response)
                }

            if (messageReq.authId != null || campaignManager.shouldCallConsentStatus) {
                triggerConsentStatus(messageReq)
                    .executeOnLeft {
                        pError(it)
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
                campaignManager.gdprConsentStatus = campaignManager.gdprConsentStatus?.copy(consentStatus = consentStatus)
            }

            if (campaignManager.shouldCallMessages) {

                val body = getMessageBody(
                    accountId = messageReq.accountId,
                    propertyHref = messageReq.propertyHref,
                    cs = campaignManager.gdprConsentStatus?.consentStatus,
                    ccpaStatus = campaignManager.ccpaConsentStatus?.status?.name,
                    campaigns = campaignManager.campaigns4Config,
                    consentLanguage = campaignManager.messageLanguage.value,
                    campaignEnv = campaignManager.spConfig.campaignsEnv
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
                    .executeOnLeft {
                        pError(it)
                        return@executeOnWorkerThread
                    }
                    .executeOnRight {
                        campaignManager.also { _ ->
                            messagesOptimizedLocalState = it.localState
                            nonKeyedLocalState = it.nonKeyedLocalState
                            gdprMessageMetaData = it.campaigns?.gdpr?.messageMetaData
                            ccpaMessageMetaData = it.campaigns?.ccpa?.messageMetaData
                        }

                        if (!campaignManager.hasLocalData) {
                            it.campaigns?.gdpr?.TCData?.let { tc -> dataStorage.tcData = tc.toMapOfAny() }

                            campaignManager.run {
                                if (!(messageReq.authId != null || campaignManager.shouldCallConsentStatus)) {
                                    // GDPR
                                    this.gdprConsentStatus = it.campaigns?.gdpr?.toGdprCS()
                                    // CCPA
                                    ccpaConsentStatus = it.campaigns?.ccpa?.toCcpaCS()
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

                postPvData(pvParams)
                    .executeOnLeft {
                        pError(it)
                        return@executeOnWorkerThread
                    }
                    .executeOnRight { pvDataResponse ->
                        campaignManager.gdprConsentStatus = campaignManager.gdprConsentStatus?.copy(
                            uuid = pvDataResponse.gdpr?.uuid
                        )
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

                postPvData(pvParams)
                    .executeOnLeft {
                        pError(it)
                        return@executeOnWorkerThread
                    }
                    .executeOnRight { pvDataResponse ->
                        campaignManager.ccpaConsentStatus = campaignManager.ccpaConsentStatus?.copy(
                            uuid = pvDataResponse.ccpa?.uuid
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
        }
    }

    fun sendConsentGdpr(
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

            getResp = networkClient.getChoice(gcParam)
                .executeOnRight { response ->
                    response.gdpr?.let { responseGdpr ->
                        campaignManager.gdprConsentStatus = responseGdpr.copy(uuid = campaignManager.gdprUuid)
                    }
                    val consentHandler = ConsentManager.responseConsentHandler(
                        response.gdpr?.copy(uuid = campaignManager.gdprUuid),
                        consentManagerUtils
                    )
                    sPConsentsSuccess?.invoke(consentHandler)
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
            uuid = campaignManager.gdprUuid,
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
                if (at != ActionType.ACCEPT_ALL && at != ActionType.REJECT_ALL) {
                    campaignManager.gdprConsentStatus = gdprConsentStatus
                    val cr = ConsentManager.responseConsentHandler(gdprConsentStatus, consentManagerUtils)
                    sPConsentsSuccess?.invoke(cr)
                }
            }

//        pMessageReq?.apply { authId?.let{ triggerConsentStatus(this) } }

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

            val getConsentParams = ChoiceParamReq(
                choiceType = at.toChoiceTypeParam(),
                accountId = spConfig.accountId.toLong(),
                propertyId = spConfig.propertyId.toLong(),
                env = env,
                metadataArg = campaignManager.metaDataResp?.toMetaDataArg()?.copy(gdpr = null),
            )
            networkClient.getChoice(getConsentParams)
                .executeOnRight { ccpaResponse ->
                    campaignManager.ccpaConsentStatus = ccpaResponse.ccpa?.copy(uuid = campaignManager.ccpaConsentStatus?.uuid)
                    val consentHandler = ConsentManager.responseConsentHandler(
                        ccpaResponse.ccpa?.copy(uuid = campaignManager.ccpaConsentStatus?.uuid),
                        consentManagerUtils
                    )
                    sPConsentsSuccess?.invoke(consentHandler)
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
                campaignManager.ccpaConsentStatus?.uuid = postConsentResponse.uuid
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
                    val consentHandler = ConsentManager.responseConsentHandler(postConsentResponse, consentManagerUtils)
                    sPConsentsSuccess?.invoke(consentHandler)
                }
            }

        campaignManager.ccpaConsentStatus ?: throw InvalidConsentResponse(
            cause = null,
            "The CCPA consent object cannot be null!!!"
        )
    }

    private fun triggerConsentStatus(messageReq: MessagesParamReq): Either<ConsentStatusResp> {
        val csParams = messageReq.toConsentStatusParamReq(
            gdprUuid = campaignManager.gdprConsentStatus?.uuid,
            ccpaUuid = campaignManager.ccpaConsentStatus?.uuid,
            localState = campaignManager.messagesOptimizedLocalState
        )

        return getConsentStatus(csParams)
            .executeOnRight { consentStatusResponse ->
                campaignManager.apply {
                    handleOldLocalData()
                    messagesOptimizedLocalState = consentStatusResponse.localState
                    consentStatusResponse.consentStatusData?.let { consentStatusData ->
                        gdprConsentStatus = consentStatusData.gdpr
                        ccpaConsentStatus = consentStatusData.ccpa
                    }
                }
            }
    }
}

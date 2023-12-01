package com.sourcepoint.cmplibrary.campaign

import com.example.cmplibrary.BuildConfig
import com.sourcepoint.cmplibrary.campaign.CampaignManager.Companion.SIMPLE_DATE_FORMAT_PATTERN
import com.sourcepoint.cmplibrary.campaign.CampaignManager.Companion.selectPmId
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.creation.validPattern
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.LOCAL_STATE
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.LOCAL_STATE_OLD
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.KEY_GDPR_APPLIES_OLD
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.KEY_GDPR_MESSAGE_SUBCATEGORY_OLD
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.converter.fail
import com.sourcepoint.cmplibrary.data.network.model.optimized.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.ChoiceResp
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.* //ktlint-disable
import com.sourcepoint.cmplibrary.exception.CampaignType.CCPA
import com.sourcepoint.cmplibrary.exception.CampaignType.GDPR
import com.sourcepoint.cmplibrary.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.* //ktlint-disable
import com.sourcepoint.cmplibrary.util.check
import com.sourcepoint.cmplibrary.util.updateCcpaUspString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.* //ktlint-disable

internal interface CampaignManager {
    val spConfig: SpConfig
    val messageLanguage: MessageLanguage
    val campaigns4Config: List<CampaignReq>
    val ccpaMessageSubCategory: MessageSubCategory
    val gdprMessageSubCategory: MessageSubCategory
    fun addCampaign(campaignType: CampaignType, campaign: CampaignTemplate)

    fun getAppliedCampaign(): Either<Pair<CampaignType, CampaignTemplate>>
    fun getCampaignTemplate(campaignType: CampaignType): Either<CampaignTemplate>

    fun getPmConfig(
        campaignType: CampaignType,
        pmId: String?,
        pmTab: PMTab?,
        useGroupPmIfAvailable: Boolean,
        groupPmId: String?
    ): Either<PmUrlConfig>

    fun getPmConfig(
        campaignType: CampaignType,
        pmId: String?,
        pmTab: PMTab?
    ): Either<PmUrlConfig>

    fun getMessageOptimizedReq(authId: String?, pubData: JSONObject?): MessagesParamReq

    fun getGroupId(campaignType: CampaignType): String?

    fun clearConsents()

    // Optimized
    val shouldCallMessages: Boolean
    val shouldCallConsentStatus: Boolean
    var gdprMessageMetaData: MessageMetaData?
    var ccpaMessageMetaData: MessageMetaData?

    // Consent Status
    var gdprConsentStatus: GdprCS?
    var ccpaConsentStatus: CcpaCS?
    var usNatConsentData: USNatConsentData?
    var messagesOptimizedLocalState: JsonElement?
    var nonKeyedLocalState: JsonElement?
    var gdprUuid: String?
    var ccpaUuid: String?
    val hasLocalData: Boolean
    val isGdprExpired: Boolean
    val isCcpaExpired: Boolean
    val isUsnatExpired: Boolean

    // dateCreated
    var gdprDateCreated: String?
    var ccpaDateCreated: String?

    var metaDataResp: MetaDataResp?
    var choiceResp: ChoiceResp?
    var dataRecordedConsent: Instant?
    var authId: String?
    var propertyId: Int

    fun handleAuthIdOrPropertyIdChange(newAuthId: String?, newPropertyId: Int)
    fun handleMetaDataResponse(response: MetaDataResp?)
    fun handleOldLocalData()
    fun getGdprPvDataBody(messageReq: MessagesParamReq): JsonObject
    fun getCcpaPvDataBody(messageReq: MessagesParamReq): JsonObject
    fun getUsNatPvDataBody(messageReq: MessagesParamReq): JsonObject
    fun deleteExpiredConsents()
    fun clearConsentIfVendorListIdChanged(gdprVendorListId: String?, usNatVendorListId: String?)

    companion object {
        const val SIMPLE_DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        fun selectPmId(userPmId: String?, childPmId: String?, useGroupPmIfAvailable: Boolean): String {
            return when {
                useGroupPmIfAvailable && !childPmId.isNullOrEmpty() && childPmId.isNotBlank() -> childPmId
                else -> userPmId ?: ""
            }
        }
    }
}

internal fun CampaignManager.Companion.create(
    dataStorage: DataStorage,
    spConfig: SpConfig
): CampaignManager = CampaignManagerImpl(dataStorage, spConfig)

private class CampaignManagerImpl(
    val dataStorage: DataStorage,
    override val spConfig: SpConfig
) : CampaignManager {

    override val messageLanguage: MessageLanguage = spConfig.messageLanguage

    private val mapTemplate = mutableMapOf<String, CampaignTemplate>()
    val logger: Logger? = spConfig.logger

    init {
        if (!spConfig.propertyName.contains(validPattern)) {
            throw InvalidArgumentException(
                description = """
                PropertyName can only include letters, numbers, '.', ':', '-' and '/'. (string) passed is invalid
                """.trimIndent()
            )
        }
        spConfig.also { spp ->
            spp.campaigns.forEach {
                when (it.campaignType) {
                    GDPR -> {
                        addCampaign(
                            it.campaignType,
                            CampaignTemplate(
                                spp.campaignsEnv,
                                it.targetingParams.filter { tp -> tp.key != "campaignEnv" },
                                it.campaignType,
                                it.groupPmId
                            )
                        )
                    }

                    CampaignType.USNAT -> {
                        addCampaign(
                            it.campaignType,
                            CampaignTemplate(
                                spp.campaignsEnv,
                                it.targetingParams.filter { tp -> tp.key != "campaignEnv" },
                                it.campaignType,
                                it.groupPmId
                            )
                        )
                    }

                    CCPA -> {
                        addCampaign(
                            it.campaignType,
                            CampaignTemplate(
                                spp.campaignsEnv,
                                it.targetingParams.filter { tp -> tp.key != "campaignEnv" },
                                it.campaignType,
                                it.groupPmId
                            )
                        )
                    }
                }
            }
        }
    }

    override val campaigns4Config: List<CampaignReq>
        get() {
            val campaigns = mutableListOf<CampaignReq>()
            mapTemplate[GDPR.name]
                ?.let {
                    it.toCampaignReqImpl(
                        targetingParams = it.targetingParams,
                        campaignsEnv = it.campaignsEnv,
                        groupPmId = it.groupPmId
                    )
                }
                ?.let { campaigns.add(it) }
            mapTemplate[CCPA.name]
                ?.let {
                    it.toCampaignReqImpl(
                        targetingParams = it.targetingParams,
                        campaignsEnv = it.campaignsEnv
                    )
                }
                ?.let { campaigns.add(it) }
            mapTemplate[CampaignType.USNAT.name]
                ?.let {
                    it.toCampaignReqImpl(
                        targetingParams = it.targetingParams,
                        campaignsEnv = it.campaignsEnv
                    )
                }
                ?.let { campaigns.add(it) }
            return campaigns
        }

    override val ccpaMessageSubCategory: MessageSubCategory
        get() = ccpaMessageMetaData?.subCategoryId ?: MessageSubCategory.TCFv2

    override val gdprMessageSubCategory: MessageSubCategory
        get() = gdprMessageMetaData?.subCategoryId ?: MessageSubCategory.TCFv2

    override fun addCampaign(campaignType: CampaignType, campaign: CampaignTemplate) {
        mapTemplate[campaignType.name] = campaign
    }

    override fun getCampaignTemplate(campaignType: CampaignType): Either<CampaignTemplate> = check {
        mapTemplate[campaignType.name] ?: fail("${campaignType.name} Campain is missing!!!")
    }

    override fun getPmConfig(
        campaignType: CampaignType,
        pmId: String?,
        pmTab: PMTab?,
        useGroupPmIfAvailable: Boolean,
        groupPmId: String?
    ): Either<PmUrlConfig> {
        return when (campaignType) {
            GDPR -> getGdprPmConfig(pmId, pmTab ?: PMTab.PURPOSES, useGroupPmIfAvailable, groupPmId)
            CCPA -> getCcpaPmConfig(pmId)
            CampaignType.USNAT -> getUsNatPmConfig(pmId, groupPmId)
        }
    }

    override fun getPmConfig(
        campaignType: CampaignType,
        pmId: String?,
        pmTab: PMTab?
    ): Either<PmUrlConfig> {
        return when (campaignType) {
            GDPR -> getGdprPmConfig(pmId, pmTab ?: PMTab.PURPOSES, false, null)
            CCPA -> getCcpaPmConfig(pmId)
            CampaignType.USNAT -> getUsNatPmConfig(pmId)
        }
    }

    private fun getGdprPmConfig(
        pmId: String?,
        pmTab: PMTab,
        useGroupPmIfAvailable: Boolean,
        groupPmId: String?
    ): Either<PmUrlConfig> = check {
        val childPmId: String? = dataStorage.gdprChildPmId
        val isChildPmIdAbsent: Boolean = childPmId == null
        val hasGroupPmId: Boolean = groupPmId != null

        val usedPmId = selectPmId(pmId, childPmId, useGroupPmIfAvailable)

        if (hasGroupPmId && useGroupPmIfAvailable && isChildPmIdAbsent) {
            logger?.error(
                ChildPmIdNotFound(
                    description = """
                              childPmId not found!!!
                              GroupPmId[$groupPmId]
                              useGroupPmIfAvailable [true] 
                    """.trimIndent()
                )
            )
            logger?.e(
                tag = "The childPmId is missing",
                msg = """
                              childPmId [null]
                              GroupPmId[$groupPmId]
                              useGroupPmIfAvailable [true] 
                """.trimIndent()
            )
        }

        logger?.computation(
            tag = "Property group - GDPR PM",
            msg = """
                pmId[$pmId]
                childPmId[$childPmId]
                useGroupPmIfAvailable [$useGroupPmIfAvailable] 
                Query Parameter pmId[$usedPmId]
            """.trimIndent()
        )

        PmUrlConfig(
            pmTab = pmTab,
            consentLanguage = spConfig.messageLanguage.value,
            uuid = gdprUuid,
            siteId = spConfig.propertyId.toString(),
            messageId = usedPmId
        )
    }

    private fun getCcpaPmConfig(pmId: String?): Either<PmUrlConfig> = check {
        val childPmId: String? = dataStorage.ccpaChildPmId
        val isChildPmIdAbsent: Boolean = childPmId == null
        val hasGroupPmId = false // feature not yet implemented
        val useGroupPmIfAvailable = false // feature not yet implemented

        if (hasGroupPmId && useGroupPmIfAvailable && isChildPmIdAbsent) {
            logger?.error(
                ChildPmIdNotFound(
                    description = """
                              childPmId not found!!!
                              GroupPmId[groupPmId]
                              useGroupPmIfAvailable [true] 
                    """.trimIndent()
                )
            )
        }

        val usedPmId = childPmId ?: pmId

        PmUrlConfig(
            consentLanguage = spConfig.messageLanguage.value,
            uuid = ccpaUuid,
            siteId = spConfig.propertyId.toString(),
            messageId = usedPmId
        )
    }

    private fun getUsNatPmConfig(
        pmId: String?,
        groupPmId: String? = null,
        useGroupPmIfAvailable: Boolean = false
    ): Either<PmUrlConfig> = check {
        val childPmId: String? = dataStorage.usnatChildPmId
        val isChildPmIdAbsent: Boolean = childPmId == null
        val hasGroupPmId: Boolean = groupPmId != null

        val usedPmId = selectPmId(pmId, childPmId, useGroupPmIfAvailable)

        if (hasGroupPmId && useGroupPmIfAvailable && isChildPmIdAbsent) {
            logger?.error(
                ChildPmIdNotFound(
                    description = """
                              childPmId not found!!!
                              GroupPmId[$groupPmId]
                              useGroupPmIfAvailable [true] 
                    """.trimIndent()
                )
            )
            logger?.e(
                tag = "The childPmId is missing",
                msg = """
                              childPmId [null]
                              GroupPmId[$groupPmId]
                              useGroupPmIfAvailable [true] 
                """.trimIndent()
            )
        }

        logger?.computation(
            tag = "Property group - USNAT PM",
            msg = """
                pmId[$pmId]
                childPmId[$childPmId]
                useGroupPmIfAvailable [$useGroupPmIfAvailable] 
                Query Parameter pmId[$usedPmId]
            """.trimIndent()
        )

        PmUrlConfig(
            consentLanguage = spConfig.messageLanguage.value,
            uuid = usNatConsentData?.uuid,
            siteId = spConfig.propertyId.toString(),
            messageId = pmId
        )
    }

    override fun getAppliedCampaign(): Either<Pair<CampaignType, CampaignTemplate>> = check {
        when {
            dataStorage
                .getGdprMessage().isNotBlank() -> Pair(GDPR, mapTemplate[GDPR.name]!!)
            dataStorage
                .getCcpaMessage().isNotBlank() -> Pair(CCPA, mapTemplate[CCPA.name]!!)
            else -> throw MissingPropertyException(description = "Inconsistent Legislation!!!")
        }
    }

    override fun getMessageOptimizedReq(authId: String?, pubData: JSONObject?): MessagesParamReq {
        val campaigns = mutableListOf<CampaignReq>()
        mapTemplate[GDPR.name]
            ?.let {
                it.toCampaignReqImpl(
                    targetingParams = it.targetingParams,
                    campaignsEnv = it.campaignsEnv,
                    groupPmId = it.groupPmId
                )
            }
            ?.let { campaigns.add(it) }
        mapTemplate[CCPA.name]
            ?.let {
                it.toCampaignReqImpl(
                    targetingParams = it.targetingParams,
                    campaignsEnv = it.campaignsEnv
                )
            }
            ?.let { campaigns.add(it) }
        mapTemplate[CampaignType.USNAT.name]
            ?.let {
                it.toCampaignReqImpl(
                    targetingParams = it.targetingParams,
                    campaignsEnv = it.campaignsEnv
                )
            }
            ?.let { campaigns.add(it) }

        return MessagesParamReq(
            metadataArg = campaigns.toMetadataArgs(),
            body = "",
            env = Env.values().find { it.name == BuildConfig.SDK_ENV } ?: Env.PROD,
            propertyHref = spConfig.propertyName,
            accountId = spConfig.accountId.toLong(),
            authId = authId,
            propertyId = spConfig.propertyId.toLong(),
            pubData = pubData?.toString()
                ?.let { check { JsonConverter.converter.decodeFromString<JsonObject>(it) }.getOrNull() }
                ?: JsonObject(mapOf())
        )
    }

    override fun getGroupId(campaignType: CampaignType): String? {
        return spConfig.campaigns.find { it.campaignType == campaignType }?.groupPmId
    }

    override fun clearConsents() {
        dataStorage.clearGdprConsent()
        dataStorage.clearCcpaConsent()
    }

    override var authId: String?
        get() = dataStorage.getAuthId()
        set(value) {
            dataStorage.saveAuthId(value)
        }

    override var propertyId: Int
        get() = dataStorage.propertyId
        set(value) {
            dataStorage.propertyId = value
        }

    // Optimized Implementation below

    val isNewUser: Boolean
        get() {
            val localStateSize = messagesOptimizedLocalState?.jsonObject?.size ?: 0
            return messagesOptimizedLocalState == null || localStateSize == 0 || (
                gdprUuid == null &&
                    (ccpaConsentStatus?.newUser == null || ccpaConsentStatus?.newUser == true)
                )
        }

    override val shouldCallMessages: Boolean
        get() {

            val gdprToBeCompleted: Boolean = spConfig.campaigns.find { it.campaignType == GDPR }
                ?.let {
                    dataStorage.gdprApplies && (gdprConsentStatus?.consentStatus?.consentedAll != true)
                }
                ?: false

            val ccpaToBeCompleted: Boolean = spConfig.campaigns.find { it.campaignType == CCPA }
                ?.let { true }
                ?: false

            val usNatToBeCompleted: Boolean = spConfig.campaigns.find { it.campaignType == CampaignType.USNAT }
                ?.let { true }
                ?: false

            val res = (isNewUser || ccpaToBeCompleted || gdprToBeCompleted || usNatToBeCompleted)

            logger?.computation(
                tag = "shouldCallMessages",
                msg = """
                isNewUser[$isNewUser]
                ccpaToBeCompleted[$ccpaToBeCompleted]
                gdprToBeCompleted[$gdprToBeCompleted]
                usNatToBeCompleted[$usNatToBeCompleted]
                shouldCallMessages[$res]  
                """.trimIndent()
            )

            return res
        }

    override val shouldCallConsentStatus: Boolean
        get() {
            val isGdprPresent = dataStorage.gdprConsentUuid != null
            val isCcpaUuidPresent = dataStorage.ccpaConsentUuid != null
            val isUsNatUuidPresent = usNatConsentData?.uuid != null
            val isLocalStateEmpty = messagesOptimizedLocalState?.jsonObject?.isEmpty() == true
            val isV6LocalStatePresent = dataStorage.preference.all.containsKey(LOCAL_STATE)
            val isV6LocalStatePresent2 = dataStorage.preference.all.containsKey(LOCAL_STATE_OLD)
            val hasNonEligibleLocalDataVersion =
                dataStorage.localDataVersion != DataStorage.LOCAL_DATA_VERSION_HARDCODED_VALUE

            val isEligibleForCallingConsentStatus =
                ((isGdprPresent || isCcpaUuidPresent || isUsNatUuidPresent) && isLocalStateEmpty) ||
                    isV6LocalStatePresent ||
                    isV6LocalStatePresent2 ||
                    hasNonEligibleLocalDataVersion

            logger?.computation(
                tag = "shouldCallConsentStatus",
                msg = """
                isGdprPresent[$isGdprPresent] - isCcpaUuidPresent[$isCcpaUuidPresent]
                isUSNatUuidPresent[$isUsNatUuidPresent] - isLocalStateEmpty[$isLocalStateEmpty]
                isV6LocalStatePresent[$isV6LocalStatePresent] - isV6LocalStatePresent2[$isV6LocalStatePresent2]
                hasDataVersion[$hasNonEligibleLocalDataVersion] - shouldCallConsentStatus[$isEligibleForCallingConsentStatus]
                """.trimIndent()
            )

            return isEligibleForCallingConsentStatus
        }

    override var gdprMessageMetaData: MessageMetaData?
        get() {
            return dataStorage.gdprMessageMetaData?.let { JsonConverter.converter.decodeFromString<MessageMetaData>(it) }
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.gdprMessageMetaData = serialised
        }

    override var ccpaMessageMetaData: MessageMetaData?
        get() {
            return dataStorage.ccpaMessageMetaData?.let { JsonConverter.converter.decodeFromString<MessageMetaData>(it) }
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.ccpaMessageMetaData = serialised
        }

    override var gdprConsentStatus: GdprCS?
        get() {
            return dataStorage.gdprConsentStatus
                ?.let { JsonConverter.converter.decodeFromString<GdprCS>(it) }
                ?.copy(applies = dataStorage.gdprApplies)
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.apply {
                gdprConsentStatus = serialised
                value?.TCData
                    ?.let { this.tcData = it }
                    ?: run { clearTCData() }
            }
        }

    override var ccpaConsentStatus: CcpaCS?
        get() {
            return dataStorage.ccpaConsentStatus
                ?.let { JsonConverter.converter.decodeFromString<CcpaCS>(it) }
                ?.copy(applies = metaDataResp?.ccpa?.applies)
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.run {
                ccpaConsentStatus = serialised
                gppData = value?.gppData
                uspstring = value?.uspstring
            }
        }

    override var usNatConsentData: USNatConsentData?
        get() {
            return dataStorage.usNatConsentData
                ?.let { JsonConverter.converter.decodeFromString<USNatConsentData>(it) }
                ?.copy(applies = metaDataResp?.usNat?.applies)
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.run {
                usNatConsentData = serialised
            }
        }

    override var messagesOptimizedLocalState: JsonElement?
        get() {
            return dataStorage.messagesOptimizedLocalState?.let {
                JsonConverter.converter.decodeFromString<JsonElement>(
                    it
                )
            }
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.messagesOptimizedLocalState = serialised
        }

    override var nonKeyedLocalState: JsonElement?
        get() {
            return dataStorage.nonKeyedLocalState?.let {
                JsonConverter.converter.decodeFromString<JsonElement>(
                    it
                )
            }
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.nonKeyedLocalState = serialised
        }

    override var gdprUuid: String?
        get() {
            return dataStorage.gdprConsentUuid
        }
        set(value) {
            dataStorage.gdprConsentUuid = value
        }

    override var ccpaUuid: String?
        get() {
            return dataStorage.ccpaConsentUuid
        }
        set(value) {
            dataStorage.ccpaConsentUuid = value
        }

    override val hasLocalData: Boolean
        get() = dataStorage.gdprConsentStatus != null || dataStorage.ccpaConsentStatus != null

    /**
     * The method that checks if the authId or propertyId was changed, if so it will flush all the
     * data. At the end, it will update the authId and propertyId with the corresponding values.
     */
    override fun handleAuthIdOrPropertyIdChange(
        newAuthId: String?,
        newPropertyId: Int,
    ) {
        // flush local data if proper authId or propertyId change was detected
        val isNewAuthId = newAuthId != null && newAuthId != authId
        val isNewPropertyId = newPropertyId != propertyId
        val hasPreviousPropertyId = propertyId != 0
        if (isNewAuthId || isNewPropertyId && hasPreviousPropertyId) {
            dataStorage.clearAll()
        }

        // update stored values of authId and propertyId
        authId = newAuthId
        propertyId = newPropertyId
    }

    override fun handleMetaDataResponse(response: MetaDataResp?) {

        // remove consent if vendorListId of a specific campaign changed
        clearConsentIfVendorListIdChanged(
            gdprVendorListId = response?.gdpr?.vendorListId,
            usNatVendorListId = response?.usNat?.vendorListId,
        )

        // update meta data response in the data storage
        metaDataResp = response

        if (response == null) return

        // handle ccpa
        response.ccpa?.apply {
            applies?.let { i ->
                ccpaConsentStatus?.let { ccpaCS ->
                    val updatedCcpaCS = ccpaCS.copy(applies = i)
                    // update the new uspstring value based on the applies
                    val uspstring = updateCcpaUspString(updatedCcpaCS, logger)
                    ccpaConsentStatus = updatedCcpaCS.copy(uspstring = uspstring)
                }
            }

            sampleRate?.let { i ->
                if (i != dataStorage.ccpaSamplingValue) {
                    dataStorage.ccpaSamplingValue = i
                    dataStorage.ccpaSamplingResult = null
                }
            }
        }

        // handle gdpr
        response.gdpr?.apply {
            applies?.let { gdprApplies ->
                gdprConsentStatus?.let { gdprCS ->
                    val updatedGdprConsentStatus = gdprCS.copy(applies = gdprApplies)
                    gdprConsentStatus = updatedGdprConsentStatus
                }
            }

            childPmId?.let { i -> dataStorage.gdprChildPmId = i }
            sampleRate?.let { i ->
                if (i != dataStorage.gdprSamplingValue) {
                    dataStorage.gdprSamplingValue = i
                    dataStorage.gdprSamplingResult = null
                }
            }
        }
    }

    /**
     * Method that clears out local consent if the vendor list id of a specific campaign changed
     */
    override fun clearConsentIfVendorListIdChanged(
        gdprVendorListId: String?,
        usNatVendorListId: String?
    ) {

        // delete GDPR consent if GDPR vendor list id changed
        if (hasGdprVendorListIdChanged(gdprVendorListId)) {
            dataStorage.deleteGdprConsent()
        }

        // delete USNAT consent if USNAT vendor list id changed
        if (hasUsNatVendorListIdChanged(usNatVendorListId)) {
            dataStorage.deleteUsNatConsent()
        }
    }

    /**
     * Method that verifies if vendorListId of GDPR changed qualitatively, basically a string value
     * to a new string value
     *
     * @param newGdprVendorListId - vendor list id of GDPR from a new /meta-data response
     */
    private fun hasGdprVendorListIdChanged(newGdprVendorListId: String?): Boolean {
        val storedGdprVendorListId = metaDataResp?.gdpr?.vendorListId
        return newGdprVendorListId != null && storedGdprVendorListId != null &&
            storedGdprVendorListId != newGdprVendorListId
    }

    /**
     * Method that verifies if vendorListId of USNAT changed qualitatively, basically a string value
     * to a new string value
     *
     * @param newUsNatVendorListId - vendor list id of USNAT from a new /meta-data response
     */
    private fun hasUsNatVendorListIdChanged(newUsNatVendorListId: String?): Boolean {
        val storedUsNatVendorListId = metaDataResp?.usNat?.vendorListId
        return newUsNatVendorListId != null && storedUsNatVendorListId != null &&
            storedUsNatVendorListId != newUsNatVendorListId
    }

    override fun handleOldLocalData() {
        if (dataStorage.preference.contains(LOCAL_STATE) || dataStorage.preference.contains(LOCAL_STATE_OLD)) {
            dataStorage.preference
                .edit().apply {
                    remove(LOCAL_STATE)
                    remove(LOCAL_STATE_OLD)
                    remove(KEY_GDPR_APPLIES_OLD)
                    remove(KEY_GDPR_MESSAGE_SUBCATEGORY_OLD)
                    remove("key_property_id")
                    remove("key_ccpa")
                    remove("key_gdpr")
                    remove("ccpa_consent_resp")
                    remove("gdpr_consent_resp")
                }
                .apply()
        }
    }

    override var gdprDateCreated: String?
        get() = dataStorage.gdprDateCreated
        set(value) {
            dataStorage.gdprDateCreated = value
        }

    override var ccpaDateCreated: String?
        get() = dataStorage.ccpaDateCreated
        set(value) {
            dataStorage.ccpaDateCreated = value
        }

    override var metaDataResp: MetaDataResp?
        get() {
            return dataStorage.metaDataResp?.let { JsonConverter.converter.decodeFromString<MetaDataResp>(it) }
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.metaDataResp = serialised
        }

    override var choiceResp: ChoiceResp?
        get() {
            return dataStorage.choiceResp?.let { JsonConverter.converter.decodeFromString<ChoiceResp>(it) }
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.choiceResp = serialised
        }

    override var dataRecordedConsent: Instant?
        get() {
            return dataStorage.dataRecordedConsent?.let { Instant.parse(it) }
        }
        set(value) {
            dataStorage.dataRecordedConsent = value?.toString()
        }

    override fun getGdprPvDataBody(messageReq: MessagesParamReq): JsonObject {
        return toPvDataBody(
            accountId = messageReq.accountId,
            propertyId = messageReq.propertyId,
            pubData = messageReq.pubData,
            metaDataResp = metaDataResp,
            gdprMessageMetaData = gdprMessageMetaData,
            gdprCs = gdprConsentStatus,
            ccpaMessageMetaData = null,
            ccpaCS = null,
            usNatCS = null,
        )
    }

    override fun getCcpaPvDataBody(messageReq: MessagesParamReq): JsonObject {
        return toPvDataBody(
            accountId = messageReq.accountId,
            propertyId = messageReq.propertyId,
            pubData = messageReq.pubData,
            metaDataResp = metaDataResp,
            ccpaMessageMetaData = ccpaMessageMetaData,
            ccpaCS = ccpaConsentStatus,
            usNatCS = null,
            gdprMessageMetaData = null,
            gdprCs = null,
        )
    }

    override fun getUsNatPvDataBody(messageReq: MessagesParamReq): JsonObject {
        return toPvDataBody(
            accountId = messageReq.accountId,
            propertyId = messageReq.propertyId,
            pubData = messageReq.pubData,
            metaDataResp = metaDataResp,
            usNatCS = usNatConsentData,
            ccpaMessageMetaData = null,
            gdprMessageMetaData = null,
            ccpaCS = null,
            gdprCs = null,
        )
    }

    override val isGdprExpired: Boolean
        get() {
            val formatter = SimpleDateFormat(SIMPLE_DATE_FORMAT_PATTERN, Locale.getDefault())
            val gdprExpirationDate = dataStorage.gdprExpirationDate?.let { formatter.parse(it) } ?: return false
            val currentDate = Date()
            return currentDate.after(gdprExpirationDate)
        }

    override val isCcpaExpired: Boolean
        get() {
            val formatter = SimpleDateFormat(SIMPLE_DATE_FORMAT_PATTERN, Locale.getDefault())
            val ccpaExpirationDate = dataStorage.ccpaExpirationDate?.let { formatter.parse(it) } ?: return false
            val currentDate = Date()
            return currentDate.after(ccpaExpirationDate)
        }

    override val isUsnatExpired: Boolean
        get() {
            val formatter = SimpleDateFormat(SIMPLE_DATE_FORMAT_PATTERN, Locale.getDefault())
            val usnatExpirationDate = usNatConsentData?.expirationDate?.let { formatter.parse(it) } ?: return false
            val currentDate = Date()
            return currentDate.after(usnatExpirationDate)
        }

    override fun deleteExpiredConsents() {
        if (isUsnatExpired) dataStorage.deleteUsNatConsent()
        if (isCcpaExpired) dataStorage.deleteCcpaConsent()
        if (isGdprExpired) dataStorage.deleteGdprConsent()

        logger?.computation(
            tag = "Expiration Date",
            msg = """
                isGdprExpired[$isGdprExpired] 
                isCcpaExpired[$isCcpaExpired] 
                isUsnatExpired[$isUsnatExpired] 
            """.trimIndent()
        )
    }
}

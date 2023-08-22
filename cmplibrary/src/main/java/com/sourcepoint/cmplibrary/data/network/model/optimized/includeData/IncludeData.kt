package com.sourcepoint.cmplibrary.data.network.model.optimized.includeData

import com.sourcepoint.cmplibrary.gpp.dto.GppData
import com.sourcepoint.cmplibrary.gpp.utils.toIncludeDataGppParam
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class IncludeData(
    @SerialName("localState")
    val localState: IncludeDataParam? = null,
    @SerialName("TCData")
    val tcData: IncludeDataParam? = null,
    @SerialName("campaigns")
    val campaigns: IncludeDataParam? = null,
    @SerialName("customVendorsResponse")
    val customVendorsResponse: IncludeDataParam? = null,
    @SerialName("messageMetaData")
    val messageMetaData: IncludeDataParam? = null,
    @SerialName("webConsentPayload")
    val webConsentPayload: IncludeDataParam? = null,
    @SerialName("GPPData")
    val gppData: IncludeDataGppParam? = null
) {

    companion object {
        /**
         * Method that generates include data for /consent-status request.
         */
        fun generateIncludeDataForConsentStatus(
            includeDataGppParam: IncludeDataGppParam = GppData().toIncludeDataGppParam(),
        ): IncludeData = IncludeData(
            tcData = IncludeDataParam(IncludeDataParamType.RECORD_STRING.type),
            webConsentPayload = IncludeDataParam(IncludeDataParamType.RECORD_STRING.type),
            gppData = includeDataGppParam,
        )

        /**
         * Method that generates include data for /messages request.
         */
        fun generateIncludeDataForMessages(
            includeDataGppParam: IncludeDataGppParam = GppData().toIncludeDataGppParam(),
        ): IncludeData = IncludeData(
            tcData = IncludeDataParam(IncludeDataParamType.RECORD_STRING.type),
            campaigns = IncludeDataParam(IncludeDataParamType.RECORD_STRING.type),
            webConsentPayload = IncludeDataParam(IncludeDataParamType.RECORD_STRING.type),
            gppData = includeDataGppParam,
        )

        /**
         * Method that generates include data for GET /choice request. This method pick out proper
         * params to add to the IncludeData param of the request.
         */
        fun generateIncludeDataForGetChoice(
            includeDataGppParam: IncludeDataGppParam = GppData().toIncludeDataGppParam(),
        ): IncludeData = IncludeData(
            tcData = IncludeDataParam(IncludeDataParamType.RECORD_STRING.type),
            webConsentPayload = IncludeDataParam(IncludeDataParamType.RECORD_STRING.type),
            gppData = includeDataGppParam,
        )
    }
}

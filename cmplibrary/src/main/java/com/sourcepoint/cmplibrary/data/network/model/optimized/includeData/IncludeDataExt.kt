package com.sourcepoint.cmplibrary.data.network.model.optimized.includeData

/**
 * Method that generates include data for /messages request. This method pick out proper
 * params to add to the IncludeData param of the request.
 */
fun generateIncludeDataForMessages(): IncludeData = IncludeData(
    tcData = IncludeDataParam(IncludeDataParamType.RECORD_STRING.type),
    campaigns = IncludeDataParam(IncludeDataParamType.RECORD_STRING.type),
    webConsentPayload = IncludeDataParam(IncludeDataParamType.RECORD_STRING.type),
)

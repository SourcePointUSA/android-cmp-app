package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.model.getFieldValue
import com.sourcepoint.cmplibrary.model.getMap
import org.json.JSONObject

internal fun Map<String, Any?>.toMetaDataResp(): MetaDataResp {

    val ccpaV7 = getMap("ccpa")?.toCcpaV7()
    val gdprV7 = getMap("gdpr")?.toGdprV7()

    return MetaDataResp(
        gdpr = gdprV7,
        ccpa = ccpaV7,
        thisContent = JSONObject(this)
    )
}

internal fun Map<String, Any?>.toCcpaV7(): CcpaMD {
    val applies = getFieldValue<Boolean>("applies") ?: false
    return CcpaMD(
        applies = applies,
        thisContent = JSONObject(this)
    )
}

internal fun Map<String, Any?>.toGdprV7(): GdprMD {
    val applies = getFieldValue<Boolean>("applies") ?: false
    val _id = getFieldValue<String>("_id")
    val additionsChangeDate = getFieldValue<String>("additionsChangeDate")
    val legalBasisChangeDate = getFieldValue<String>("legalBasisChangeDate")
    val version = getFieldValue<Int>("version")
    val getMessageAlways = getFieldValue<Boolean>("getMessageAlways") ?: false
    return GdprMD(
        applies = applies,
        _id = _id,
        additionsChangeDate = additionsChangeDate,
        getMessageAlways = getMessageAlways,
        legalBasisChangeDate = legalBasisChangeDate,
        version = version,
        thisContent = JSONObject(this)
    )
}

package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.data.network.util.Env
import org.json.JSONObject

internal class MetaDataResp(
    val thisContent: JSONObject,
    val gdpr: GdprMD?,
    val ccpa: CcpaMD?
)

internal abstract class BaseModelResp {
    abstract val thisContent: JSONObject
    abstract val applies: Boolean
}

internal data class GdprMD(
    override val thisContent: JSONObject,
    override val applies: Boolean,
    val _id: String?,
    val additionsChangeDate: String?,
    val legalBasisChangeDate: String?,
    val version: Int?,
    val getMessageAlways: Boolean
) : BaseModelResp()

internal data class MetaDataParamReq(
    val env: Env,
    val propertyId: Int,
    val accountId: Int,
    val metadata: String
)

internal data class CcpaMD(
    override val thisContent: JSONObject,
    override val applies: Boolean
) : BaseModelResp()

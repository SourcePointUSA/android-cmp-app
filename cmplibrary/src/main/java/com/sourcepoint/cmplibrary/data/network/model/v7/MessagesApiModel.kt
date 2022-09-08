package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.model.exposed.GDPRPurposeGrants
import okhttp3.HttpUrl
import org.json.JSONObject

internal data class MessagesResp(
    val thisContent: JSONObject,
    val propertyId: Int,
    val campaigns: List<MessagesCampaign> = emptyList(),
    val localState: JSONObject
)

internal abstract class MessagesCampaign {
    abstract val thisContent: JSONObject
    abstract val type: String
    abstract val url: HttpUrl?
    abstract val message: JSONObject?
    abstract val messageMetaData: JSONObject?
    abstract val dateCreated: String
}

internal data class GdprMess(
    override val thisContent: JSONObject,
    override val dateCreated: String,
    override val message: JSONObject?,
    override val messageMetaData: JSONObject?,
    override val type: String,
    override val url: HttpUrl?,
    var grants: Map<String, GDPRPurposeGrants>,
    val consentStatusCS: ConsentStatusCS,
    val hasLocalData: Boolean,
    val addtlConsent: String,
    val childPmId: String? = null,
) : MessagesCampaign()

internal data class CcpaMess(
    override val thisContent: JSONObject,
    override val dateCreated: String,
    override val message: JSONObject?,
    override val messageMetaData: JSONObject?,
    override val type: String,
    override val url: HttpUrl?
) : MessagesCampaign()

internal data class MessagesParamReq(
    val env: Env,
    val metadata: String,
    val body: String,
    val nonKeyedLocalState: String
)

package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.model.exposed.CcpaStatus
import com.sourcepoint.cmplibrary.model.exposed.GDPRPurposeGrants
import com.sourcepoint.cmplibrary.model.exposed.MessageSubCategory
import okhttp3.HttpUrl
import org.json.JSONObject

internal data class MessagesResp(
    val thisContent: JSONObject,
    val propertyId: Int,
    val campaigns: List<MessagesCampaign> = emptyList(),
    val localState: JSONObject?
)

internal abstract class MessagesCampaign {
    abstract val thisContent: JSONObject
    abstract val type: String
    abstract val dateCreated: String?
    abstract val url: HttpUrl?
    abstract val message: JSONObject?
    abstract val messageMetaData: MessageMetaData?
    abstract val messageSubCategory: MessageSubCategory
}

internal data class GdprMessage(
    override val thisContent: JSONObject,
    override val dateCreated: String?,
    override val message: JSONObject?,
    override val messageMetaData: MessageMetaData?,
    override val type: String,
    override val url: HttpUrl?,
    override val messageSubCategory: MessageSubCategory,
    var grants: Map<String, GDPRPurposeGrants>,
    val consentStatusCS: ConsentStatusCS?,
    val hasLocalData: Boolean,
    val addtlConsent: String?,
    val euconsent: String?,
    val customVendorsResponse: JSONObject?,
    val childPmId: String? = null,

) : MessagesCampaign()

internal data class CcpaMessage(
    override val thisContent: JSONObject,
    override val dateCreated: String?,
    override val message: JSONObject?,
    override val messageMetaData: MessageMetaData?,
    override val type: String,
    override val url: HttpUrl?,
    override val messageSubCategory: MessageSubCategory,
    val newUser: Boolean,
    val consentedAll: Boolean,
    val rejectedAll: Boolean,
    val signedLspa: Boolean,
    val rejectedCategories: List<String> = listOf(),
    val rejectedVendors: List<String> = listOf(),
    val status: CcpaStatus,
    val uspstring: String?,
    val applies: Boolean = false,
) : MessagesCampaign()

internal data class MessagesParamReq(
    val env: Env,
    val metadata: String,
    val body: String,
    val nonKeyedLocalState: String
)

data class MessageMetaData(
    val thisContent: JSONObject,
    val bucket: Int?,
    val categoryId: Int?,
    val messageId: Int?,
    val msgDescription: String?,
    val prtnUUID: String?,
    val subCategoryId: MessageSubCategory
)

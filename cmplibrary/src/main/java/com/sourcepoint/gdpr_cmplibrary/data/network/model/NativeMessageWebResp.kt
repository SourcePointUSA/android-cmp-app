package com.sourcepoint.gdpr_cmplibrary.data.network.model

data class NativeMessageWebResp(
    val actions: List<Action>? = null,
    val addtlConsent: String? = null,
    val bucket: Int? = null,
    val categoryId: Int? = null,
    val customVendorsResponse: CustomVendorsResponse? = null,
    val euconsent: String? = null,
    val gdprApplies: Boolean? = null,
    val grants: String? = null,
    val messageId: Int? = null,
    val msgDescription: String? = null,
    val propertyId: Int? = null,
    val prtnUUID: String? = null,
    val stackInfo: StackInfo? = null,
    val subCategoryId: Int? = null,
    val uuid: String? = null,
    val msgJSON: String? = null
)
package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.model.exposed.CcpaStatus
import com.sourcepoint.cmplibrary.model.exposed.GDPRPurposeGrants
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class GDPRPostChoiceResponse(
    val uuid: String,
    val dateCreated: String,
    val TCData: JsonObject,
    val euconsent: String?,
    val grants: Map<String, GDPRPurposeGrants>?,
    val webConsentPayload: JsonObject?,
    val vendors: List<String>?,
    val categories: List<String>?
)

@Serializable
data class CCPAPostChoiceResponse(
    val uuid: String,
    val dateCreated: String,
    val consentedAll: Boolean,
    val rejectedAll: Boolean,
    val status: CcpaStatus,
    val rejectedVendors: List<String>,
    val rejectedCategories: List<String>,
    val webConsentPayload: JsonObject,
    val GPPData: JsonObject
)

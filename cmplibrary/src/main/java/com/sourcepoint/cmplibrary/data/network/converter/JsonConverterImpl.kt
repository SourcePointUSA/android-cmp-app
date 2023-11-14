package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.layout.model.NativeMessageDto
import com.sourcepoint.cmplibrary.core.layout.model.toNativeMessageDto
import com.sourcepoint.cmplibrary.data.network.model.optimized.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.ChoiceResp
import com.sourcepoint.cmplibrary.data.network.model.toConsentAction
import com.sourcepoint.cmplibrary.exception.ApiRequestPostfix
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.ConsentActionImpl
import com.sourcepoint.cmplibrary.model.ConsentResp
import com.sourcepoint.cmplibrary.model.getMap
import com.sourcepoint.cmplibrary.model.toTreeMap
import com.sourcepoint.cmplibrary.util.check
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONObject

/**
 * Factory method to create an instance of a [JsonConverter] using its implementation
 * @return an instance of the [JsonConverterImpl] implementation
 */
internal fun JsonConverter.Companion.create(): JsonConverter = JsonConverterImpl()
internal val JsonConverter.Companion.converter: Json by lazy {
    Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        isLenient = true
        allowStructuredMapKeys = true
        explicitNulls = false
        prettyPrint = true
        prettyPrintIndent = "  "
        coerceInputValues = true
        useArrayPolymorphism = true
        allowSpecialFloatingPointValues = true
    }
}

/**
 * Implementation of the [JsonConverter] interface
 */
private class JsonConverterImpl : JsonConverter {

    override fun toConsentAction(body: String): Either<ConsentActionImpl> = check {
        body.toConsentAction()
    }

    override fun toNativeMessageRespK(body: String): Either<NativeMessageRespK> = check {
        val map: Map<String, Any?> = JSONObject(body).toTreeMap()
        val bean: NativeMessageDto = map.getMap("msgJSON")!!.toNativeMessageDto()
        NativeMessageRespK(msg = bean)
    }

    override fun toConsentResp(body: String, campaignType: CampaignType): Either<ConsentResp> = check {
        val obj = JSONObject(body)
        val map: Map<String, Any?> = JSONObject(body).toTreeMap()
        val localState = map.getMap("localState")?.toJSONObj() ?: JSONObject()
        val uuid = map.getFieldValue<String>("uuid") ?: "invalid"
        obj.get("userConsent")
        ConsentResp(
            content = JSONObject(body),
            localState = localState.toString(),
            uuid = uuid,
            userConsent = obj["userConsent"].toString(),
            campaignType = campaignType
        )
    }

    override fun toCustomConsentResp(body: String): Either<CustomConsentResp> = check {
        val obj = JSONObject(body)
        CustomConsentResp(obj)
    }

    override fun toNativeMessageDto(body: String): Either<NativeMessageDto> = check {
        JSONObject(body).toTreeMap().toNativeMessageDto()
    }

    override fun toMetaDataRespResp(body: String): Either<MetaDataResp> = check(ApiRequestPostfix.META_DATA) {
        JsonConverter.converter.decodeFromString(body)
    }

    override fun toConsentStatusResp(body: String): Either<ConsentStatusResp> = check(ApiRequestPostfix.CONSENT_STATUS) {
        JsonConverter.converter.decodeFromString(body)
    }

    override fun toChoiceResp(body: String): Either<ChoiceResp> = check(ApiRequestPostfix.GET_CHOICE) {
        JsonConverter.converter.decodeFromString(body)
    }

    override fun toGdprPostChoiceResp(body: String): Either<GdprCS> = check(ApiRequestPostfix.POST_CHOICE_GDPR) {
        JsonConverter.converter.decodeFromString(body)
    }

    override fun toCcpaPostChoiceResp(body: String): Either<CcpaCS> = check(ApiRequestPostfix.POST_CHOICE_CCPA) {
        JsonConverter.converter.decodeFromString(body)
    }

    override fun toUsNatPostChoiceResp(body: String): Either<USNatConsentData> = check(ApiRequestPostfix.POST_CHOICE_USNAT) {
        JsonConverter.converter.decodeFromString(body)
    }

    override fun toPvDataResp(body: String): Either<PvDataResp> = check(ApiRequestPostfix.PV_DATA) {
        JsonConverter.converter.decodeFromString(body)
    }

    override fun toMessagesResp(body: String): Either<MessagesResp> = check(ApiRequestPostfix.MESSAGES) {
        JsonConverter.converter.decodeFromString(body)
    }
}

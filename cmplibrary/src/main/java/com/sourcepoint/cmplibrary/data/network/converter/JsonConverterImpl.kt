package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.layout.model.NativeMessageDto
import com.sourcepoint.cmplibrary.core.layout.model.toNativeMessageDto
import com.sourcepoint.cmplibrary.data.network.model.optimized.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.optimized.сonsentStatus.CcpaCS
import com.sourcepoint.cmplibrary.data.network.model.optimized.сonsentStatus.GdprCS
import com.sourcepoint.cmplibrary.data.network.model.optimized.сonsentStatus.USNatCS
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.ChoiceResp
import com.sourcepoint.cmplibrary.data.network.model.toConsentAction
import com.sourcepoint.cmplibrary.exception.ApiRequestPostfix
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.ConsentActionImpl
import com.sourcepoint.cmplibrary.model.getMap
import com.sourcepoint.cmplibrary.model.toTreeMap
import com.sourcepoint.cmplibrary.util.check
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
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
internal class JsonConverterImpl : JsonConverter {

    override fun toConsentAction(body: String): Either<ConsentActionImpl> = check {
        body.toConsentAction()
    }

    override fun toNativeMessageRespK(body: String): Either<NativeMessageRespK> = check {
        val map: Map<String, Any?> = JSONObject(body).toTreeMap()
        val bean: NativeMessageDto = map.getMap("msgJSON")!!.toNativeMessageDto()
        NativeMessageRespK(msg = bean)
    }

    override fun toNativeMessageDto(body: String): Either<NativeMessageDto> = check {
        JSONObject(body).toTreeMap().toNativeMessageDto()
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

    override fun toUsNatPostChoiceResp(body: String): Either<USNatCS> = check(ApiRequestPostfix.POST_CHOICE_USNAT) {
        JsonConverter.converter.decodeFromString(body)
    }

    override fun toMessagesResp(body: String): Either<MessagesResp> = check(ApiRequestPostfix.MESSAGES) {
        JsonConverter.converter.decodeFromString(body)
    }

    override fun toJsonObject(body: String) = JsonConverter.converter.parseToJsonElement(body).jsonObject
}

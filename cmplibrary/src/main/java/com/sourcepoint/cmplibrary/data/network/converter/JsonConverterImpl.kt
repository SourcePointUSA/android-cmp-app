package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.layout.model.NativeMessageDto
import com.sourcepoint.cmplibrary.core.layout.model.toNativeMessageDto
import com.sourcepoint.cmplibrary.data.network.model.toConsentAction
import com.sourcepoint.cmplibrary.data.network.model.toUnifiedMessageRespDto
import com.sourcepoint.cmplibrary.data.network.model.v7.CcpaMD
import com.sourcepoint.cmplibrary.data.network.model.v7.GdprMD
import com.sourcepoint.cmplibrary.data.network.model.v7.MetaDataResp
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.InvalidResponseWebMessageException
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.util.check
import org.json.JSONObject

/**
 * Factory method to create an instance of a [JsonConverter] using its implementation
 * @return an instance of the [JsonConverterImpl] implementation
 */
internal fun JsonConverter.Companion.create(): JsonConverter = JsonConverterImpl()

/**
 * Implementation of the [JsonConverter] interface
 */
private class JsonConverterImpl : JsonConverter {

    override fun toUnifiedMessageResp(body: String): Either<UnifiedMessageResp> = check {
        body.toUnifiedMessageRespDto()
    }

    override fun toConsentAction(body: String): Either<ConsentActionImpl> = check {
        body.toConsentAction()
    }

    override fun toNativeMessageResp(body: String): Either<NativeMessageResp> = check {
        val map: Map<String, Any?> = JSONObject(body).toTreeMap()
        val msgJSON = map.getMap("msgJSON") ?: fail("msgJSON")
        NativeMessageResp(msgJSON = JSONObject(msgJSON))
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

    override fun toMetaDataRespResp(body: String): Either<MetaDataResp> = check {
        JSONObject(body).toTreeMap().toMetaDataResp()
    }

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

    /**
     * Util method to throws a [ConsentLibExceptionK] with a custom message
     * @param param name of the null object
     */
    private fun fail(param: String): Nothing {
        throw InvalidResponseWebMessageException(description = "$param object is null")
    }
}

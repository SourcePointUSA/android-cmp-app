package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.model.exposed.MessageCategory
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object MessageCategorySerializer : KSerializer<MessageCategory> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("MessageCategory", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): MessageCategory {
        val code = decoder.decodeInt()
        return MessageCategory.values()
            .find { m -> m.code == code }
            ?: MessageCategory.GDPR
    }

    override fun serialize(encoder: Encoder, value: MessageCategory) {
        encoder.encodeInt(value.code)
    }
}

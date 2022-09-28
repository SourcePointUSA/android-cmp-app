package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.model.exposed.MessageSubCategory
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object MessageSubCategorySerializer : KSerializer<MessageSubCategory> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("MessageSubCategory", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): MessageSubCategory {
        val code = decoder.decodeInt()
        return MessageSubCategory.values()
            .find { m -> m.code == code }
            ?: MessageSubCategory.TCFv2
    }

    override fun serialize(encoder: Encoder, value: MessageSubCategory) {
        encoder.encodeInt(value.code)
    }
}

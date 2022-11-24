package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.model.exposed.ActionType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ActionTypeSerializer : KSerializer<ActionType> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ActionType", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ActionType {
        val code = decoder.decodeInt()
        return ActionType.values()
            .find { m -> m.code == code }
            ?: ActionType.UNKNOWN
    }

    override fun serialize(encoder: Encoder, value: ActionType) {
        encoder.encodeInt(value.code)
    }
}

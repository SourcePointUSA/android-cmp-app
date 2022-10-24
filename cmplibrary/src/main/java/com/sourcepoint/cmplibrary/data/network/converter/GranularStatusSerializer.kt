package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.data.network.model.v7.GranularState
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object GranularStateSerializer : KSerializer<GranularState> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("GranularState", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): GranularState {
        val code = decoder.decodeString()
        return GranularState.values()
            .find { m -> m.name == code }
            ?: GranularState.NONE
    }

    override fun serialize(encoder: Encoder, value: GranularState) {
        encoder.encodeString(value.name)
    }
}

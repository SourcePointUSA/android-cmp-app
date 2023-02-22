package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.model.exposed.CcpaStatus
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object CcpaStatusSerializer : KSerializer<CcpaStatus> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("CcpaStatus", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): CcpaStatus {
        val code = decoder.decodeString()
        return CcpaStatus.values()
            .find { m -> m.name == code }
            ?: CcpaStatus.unknown
    }

    override fun serialize(encoder: Encoder, value: CcpaStatus) {
        encoder.encodeString(value.name)
    }
}

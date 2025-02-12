package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.data.network.model.optimized.сonsentStatus.GCMStatus
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object SpConsentStatusSerializer : KSerializer<GCMStatus> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SpConsentStatus", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): GCMStatus {
        val code = decoder.decodeString()
        return GCMStatus.values()
            .find { m -> m.status == code }
            ?: GCMStatus.DENIED
    }

    override fun serialize(encoder: Encoder, value: GCMStatus) {
        encoder.encodeString(value.status)
    }
}

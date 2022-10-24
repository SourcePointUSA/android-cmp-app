package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.exception.CampaignType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object CampaignTypeSerializer : KSerializer<CampaignType> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("CampaignType", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): CampaignType {
        val code = decoder.decodeString()
        return CampaignType.values()
            .find { m -> m.name == code }
            ?: CampaignType.GDPR
    }

    override fun serialize(encoder: Encoder, value: CampaignType) {
        encoder.encodeString(value.name)
    }
}

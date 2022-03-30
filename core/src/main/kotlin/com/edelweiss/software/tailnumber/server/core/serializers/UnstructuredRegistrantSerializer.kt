package com.edelweiss.software.tailnumber.server.core.serializers

import com.edelweiss.software.tailnumber.server.core.registration.UnstructuredRegistrant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object UnstructuredRegistrantSerializer : KSerializer<UnstructuredRegistrant> {
    override val descriptor = PrimitiveSerialDescriptor("UnstructuredRegistrant", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder) =
        UnstructuredRegistrant(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: UnstructuredRegistrant) {
        encoder.encodeString(value.value)
    }
}
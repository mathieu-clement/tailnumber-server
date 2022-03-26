package com.edelweiss.software.tailnumber.server.core.serializers

import com.edelweiss.software.tailnumber.server.core.registration.RegistrationId
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object RegistrationIdSerializer : KSerializer<RegistrationId> {
    override val descriptor = PrimitiveSerialDescriptor("RegistrationId", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): RegistrationId =
        RegistrationId.fromTailNumber(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: RegistrationId) {
        encoder.encodeString(value.id)
    }
}
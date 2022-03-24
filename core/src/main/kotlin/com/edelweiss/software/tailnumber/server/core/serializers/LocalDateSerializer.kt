package com.edelweiss.software.tailnumber.server.core.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder) =
        LocalDate.parse(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE))
    }
}
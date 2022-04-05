package com.edelweiss.software.tailnumber.server.core.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object LocalDateTimeSerializer : KSerializer<LocalDateTime?> {
    override val descriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LocalDateTime? = try {
        val string = decoder.decodeString()
        LocalDateTime.parse(string)
    } catch (oob: IndexOutOfBoundsException) {
        null
    }

    override fun serialize(encoder: Encoder, value: LocalDateTime?) {
        if (value != null) encoder.encodeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
    }
}
package com.edelweiss.software.tailnumber.server.importer.ch.models.response

import com.edelweiss.software.tailnumber.server.core.serializers.LocalDateTimeSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime

object NAorLocalDateTimeSerializer : KSerializer<LocalDateTime?> {
    override val descriptor = PrimitiveSerialDescriptor("NAorLocalDateTime", PrimitiveKind.STRING)

    private val delegate = LocalDateTimeSerializer

    override fun deserialize(decoder: Decoder): LocalDateTime? {
        val s = decoder.decodeString()
        return if (s == "N/A") {
            null
        } else {
            delegate.deserialize(decoder)
        }
    }

    override fun serialize(encoder: Encoder, value: LocalDateTime?) {
        if (value != null) delegate.serialize(encoder, value)
    }
}
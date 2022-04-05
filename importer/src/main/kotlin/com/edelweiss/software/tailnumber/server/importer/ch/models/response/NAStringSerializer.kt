package com.edelweiss.software.tailnumber.server.importer.ch.models.response

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object NAStringSerializer : KSerializer<String?> {
    override val descriptor = PrimitiveSerialDescriptor("NAString", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): String? {
        val s = decoder.decodeString()
        return if (s == "N/A") {
            null
        } else {
            s
        }
    }

    override fun serialize(encoder: Encoder, value: String?) {
        if (value != null) encoder.encodeString(value)
    }
}
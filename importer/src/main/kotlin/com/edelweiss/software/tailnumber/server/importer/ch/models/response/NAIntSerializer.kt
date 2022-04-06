package com.edelweiss.software.tailnumber.server.importer.ch.models.response

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object NAIntSerializer : KSerializer<Int?> {
    override val descriptor = PrimitiveSerialDescriptor("NAInt", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Int? {
        val s = decoder.decodeString()
        return if (s == "N/A") {
            null
        } else {
            s.toInt()
        }
    }

    override fun serialize(encoder: Encoder, value: Int?) {
        if (value != null) encoder.encodeString(value.toString())
    }
}
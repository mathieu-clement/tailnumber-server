package com.edelweiss.software.tailnumber.server.core.serializers

import com.edelweiss.software.tailnumber.server.core.registration.TransponderCode
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

object TransponderCodeSerializer : KSerializer<TransponderCode> {
    val stringDescriptor = PrimitiveSerialDescriptor("TransponderCodeString", PrimitiveKind.STRING)
    val longDescriptor = PrimitiveSerialDescriptor("TransponderCodeLong", PrimitiveKind.LONG)
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("TransponderCode") {
            element("code", longDescriptor)
            element("octal", stringDescriptor)
            element("hex", stringDescriptor)
        }

    override fun deserialize(decoder: Decoder): TransponderCode =
        decoder.decodeStructure(descriptor) {
            decodeElementIndex(descriptor)
            TransponderCode(decodeSerializableElement(descriptor, 0, Long.serializer()))
        }

    override fun serialize(encoder: Encoder, value: TransponderCode) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, Long.serializer(), value.code)
            encodeSerializableElement(descriptor, 1, String.serializer(), value.octal())
            encodeSerializableElement(descriptor, 2, String.serializer(), value.hex())
        }
    }
}
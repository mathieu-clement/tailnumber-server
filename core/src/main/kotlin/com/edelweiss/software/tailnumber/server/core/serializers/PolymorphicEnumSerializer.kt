package com.edelweiss.software.tailnumber.server.core.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

class PolymorphicEnumSerializer<T : Enum<T>>(
    private val enumSerializer: KSerializer<T>,
    private val passThrough: Boolean = false
) :
    KSerializer<T> {

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor(enumSerializer.descriptor.serialName) {
            element("value", enumSerializer.descriptor)
        }

    override fun deserialize(decoder: Decoder): T =
        decoder.decodeStructure(descriptor) {
            decodeElementIndex(descriptor)
            return decodeSerializableElement(descriptor, 0, enumSerializer)
        }

    override fun serialize(encoder: Encoder, value: T) =
        if (passThrough)
            encoder.encodeSerializableValue(enumSerializer, value)
        else {
            encoder.encodeStructure(descriptor) {
                encodeSerializableElement(descriptor, 0, enumSerializer, value)
            }
        }
}


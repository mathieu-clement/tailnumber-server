package com.edelweiss.software.tailnumber.server.core.serializers

import com.edelweiss.software.tailnumber.server.core.aircraft.Weight
import com.edelweiss.software.tailnumber.server.core.aircraft.WeightCategory
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

object WeightCategorySerializer : KSerializer<WeightCategory> {
    val stringDescriptor = PrimitiveSerialDescriptor("WeightCategoryName", PrimitiveKind.STRING)
    val weightSerializer = Weight.serializer()
    val weightDescriptor = weightSerializer.descriptor
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("WeightCategory") {
            element("class", stringDescriptor)
            element("minWeight", weightDescriptor)
            element("maxWeight", weightDescriptor)
        }

    override fun deserialize(decoder: Decoder): WeightCategory =
        decoder.decodeStructure(descriptor) {
            decodeElementIndex(descriptor)
            val className = decodeSerializableElement(descriptor, 0, String.serializer())
            WeightCategory.valueOf(className)
        }

    override fun serialize(encoder: Encoder, value: WeightCategory) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, String.serializer(), value.name)
            encodeSerializableElement(descriptor, 1, weightSerializer, value.minWeight)
            value.maxWeight?.let {
                encodeSerializableElement(descriptor, 2, weightSerializer, it)
            }
        }
    }
}
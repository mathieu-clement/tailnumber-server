package com.edelweiss.software.tailnumber.server.importer.ch.models.response

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.IntArraySerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate

// [2007, 5, 14] <-> LocalDate
object ArrayDateSerializer : KSerializer<LocalDate> {
    private val delegate = IntArraySerializer()

    override val descriptor = buildClassSerialDescriptor("ArrayDate", delegate.descriptor)

    override fun deserialize(decoder: Decoder): LocalDate {
        val (year, month, day) = decoder.decodeSerializableValue(delegate)
        return LocalDate.of(year, month, day)
    }

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeSerializableValue(delegate,
            intArrayOf(value.year, value.monthValue, value.dayOfMonth)
        )
    }

}
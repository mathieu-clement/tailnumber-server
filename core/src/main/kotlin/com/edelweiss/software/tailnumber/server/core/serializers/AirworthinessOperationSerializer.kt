package com.edelweiss.software.tailnumber.server.core.serializers

import com.edelweiss.software.tailnumber.server.core.airworthiness.AirworthinessOperation
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement

object AirworthinessOperationSerializer : JsonContentPolymorphicSerializer<AirworthinessOperation>(AirworthinessOperation::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out AirworthinessOperation> {
        TODO("Not yet implemented")
    }
}
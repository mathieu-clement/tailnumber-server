package com.edelweiss.software.tailnumber.server.search.elastic.serializers

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.serializer

object StringFieldValueUnwrapperSerializer : JsonTransformingSerializer<String>(serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        val array = (element as JsonArray)
        return if (array.isEmpty()) JsonNull else array[0]
    }
}

object IntFieldValueUnwrapperSerializer : JsonTransformingSerializer<Int>(serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        val array = (element as JsonArray)
        return if (array.isEmpty()) JsonNull else array[0]
    }
}
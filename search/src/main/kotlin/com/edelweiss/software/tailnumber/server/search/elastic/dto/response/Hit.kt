package com.edelweiss.software.tailnumber.server.search.elastic.dto.response

@kotlinx.serialization.Serializable
data class Hit(
    val fields : FieldsResponse? = null
)

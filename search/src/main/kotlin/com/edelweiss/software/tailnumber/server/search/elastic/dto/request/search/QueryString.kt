package com.edelweiss.software.tailnumber.server.search.elastic.dto.request.search

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class QueryString(
    val query: String,
    val fields: List<String>,
    @EncodeDefault @SerialName("default_operator") val operator: String? = "and"
)

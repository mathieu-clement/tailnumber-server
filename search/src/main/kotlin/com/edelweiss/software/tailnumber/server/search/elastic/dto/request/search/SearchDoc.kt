package com.edelweiss.software.tailnumber.server.search.elastic.dto.request.search

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SearchDoc(
    val query: QueryDoc,
    val fields: Set<String>,
    @EncodeDefault @SerialName("_source") val includeSource: Boolean = false,
    @EncodeDefault val from: Int = 0,
    @EncodeDefault val size: Int = 100
)

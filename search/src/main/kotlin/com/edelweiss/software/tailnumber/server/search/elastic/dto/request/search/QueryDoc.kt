package com.edelweiss.software.tailnumber.server.search.elastic.dto.request.search

@kotlinx.serialization.Serializable
data class QueryDoc(
    val bool: BooleanQuery
)

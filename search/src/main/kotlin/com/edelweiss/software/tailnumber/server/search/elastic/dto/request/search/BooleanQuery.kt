package com.edelweiss.software.tailnumber.server.search.elastic.dto.request.search

@kotlinx.serialization.Serializable
data class BooleanQuery(
    val must: Set<MustQuery>? = null,
    val should: Set<MustQuery>? = null,
    // val minimum_should_match = 1
)

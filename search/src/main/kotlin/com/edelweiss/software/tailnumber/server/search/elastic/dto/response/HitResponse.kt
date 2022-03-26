package com.edelweiss.software.tailnumber.server.search.elastic.dto.response

@kotlinx.serialization.Serializable
data class HitResponse(
    val hits: List<Hit>
)

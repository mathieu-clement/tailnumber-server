package com.edelweiss.software.tailnumber.server.search.elastic.dto.request.search

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class BooleanQuery(
    val must: Set<MustQuery>? = null,
    val should: Set<MustQuery>? = null,
    @EncodeDefault @SerialName("minimum_should_match") val minimumShouldMatch : Int = 1
)

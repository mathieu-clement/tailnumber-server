package com.edelweiss.software.tailnumber.server.search.elastic.dto.request.search

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class MustQuery(
    val match: MatchQuery? = null,
    @SerialName("query_string") val queryString: QueryString? = null
)

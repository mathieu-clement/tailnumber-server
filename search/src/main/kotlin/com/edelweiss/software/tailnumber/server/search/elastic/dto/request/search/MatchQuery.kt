package com.edelweiss.software.tailnumber.server.search.elastic.dto.request.search

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class MatchQuery(
    @SerialName("registrant.name") val registrantName: String? = null
)
